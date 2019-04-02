/*
 * Copyright 2018-2019 Expedia Group, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.expedia.adaptivealerting.anomdetect.detector;

import com.expedia.adaptivealerting.core.anomaly.AnomalyLevel;
import com.expedia.adaptivealerting.core.anomaly.AnomalyType;
import com.expedia.adaptivealerting.core.util.MathUtil;
import com.expedia.metrics.MetricData;
import com.expedia.metrics.MetricDefinition;
import com.opencsv.bean.CsvToBeanBuilder;
import junit.framework.TestCase;
import lombok.val;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStreamReader;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static junit.framework.TestCase.assertEquals;

public class CusumDetectorTest {
    private static final double WEAK_SIGMAS = 3.0;
    private static final double STRONG_SIGMAS = 4.0;
    private static final double TOLERANCE = 0.01;
    private static final int WARMUP_PERIOD = 25;

    private UUID detectorUuid;
    private MetricDefinition metricDefinition;
    private long epochSecond;
    private static List<CusumTestRow> data;

    @BeforeClass
    public static void setUpClass() {
        readDataFromCsv();
    }

    @Before
    public void setUp() {
        this.detectorUuid = UUID.randomUUID();
        this.metricDefinition = new MetricDefinition("some-key");
        this.epochSecond = Instant.now().getEpochSecond();
    }

    @Test
    public void testClassify_leftTailed() {
        val anomalyType = AnomalyType.LEFT_TAILED;

        val params = new CusumParams()
                .setType(anomalyType)
                .setTargetValue(1000.0)
                .setWeakSigmas(WEAK_SIGMAS)
                .setStrongSigmas(STRONG_SIGMAS)
                .setSlackParam(0.5)
                .setInitMeanEstimate(1000.0)
                .setWarmUpPeriod(WARMUP_PERIOD);

        val testRows = new CusumTestRow[]{
                new CusumTestRow(1020.0, AnomalyLevel.NORMAL.name()),
                new CusumTestRow(994.0, AnomalyLevel.WEAK.name()),
                new CusumTestRow(990.0, AnomalyLevel.STRONG.name()),
        };

        testClassify(params, anomalyType, testRows);
    }

    @Test
    public void testClassify_rightTailed() {
        val testRows = data.listIterator();
        val testRow0 = testRows.next();

        val anomalyType = AnomalyType.RIGHT_TAILED;

        val params = new CusumParams()
                .setType(anomalyType)
                .setTargetValue(0.16)
                .setWeakSigmas(WEAK_SIGMAS)
                .setStrongSigmas(STRONG_SIGMAS)
                .setSlackParam(0.5)
                .setInitMeanEstimate(testRow0.getObserved())
                .setWarmUpPeriod(WARMUP_PERIOD);

        val detector = new CusumDetector();
        detector.init(detectorUuid, params, anomalyType);

        int numDataPoints = 1;

        while (testRows.hasNext()) {
            val testRow = testRows.next();
            val observed = testRow.getObserved();
            val metricData = new MetricData(metricDefinition, observed, epochSecond);
            val level = detector.classify(metricData).getAnomalyLevel();

            assertEquals(AnomalyLevel.valueOf(testRow.getLevel()), level);

            // TODO Why not apply these assertions to all rows? [WLW]
            if (numDataPoints >= WARMUP_PERIOD) {
                assertApproxEqual(testRow.getSh(), detector.getSumHigh());
                assertApproxEqual(testRow.getSl(), detector.getSumLow());
            }

            numDataPoints++;
        }
    }

    @Test
    public void testClassify_twoTailed() {
        val anomalyType = AnomalyType.TWO_TAILED;

        val params = new CusumParams()
                .setType(anomalyType)
                .setTargetValue(1000.0)
                .setWeakSigmas(WEAK_SIGMAS)
                .setStrongSigmas(STRONG_SIGMAS)
                .setSlackParam(0.5)
                .setInitMeanEstimate(1000.0)
                .setWarmUpPeriod(5);

        val testRows = new CusumTestRow[]{
                new CusumTestRow(1000.0, AnomalyLevel.NORMAL.name()),
                new CusumTestRow(1020.0, AnomalyLevel.STRONG.name()),
                new CusumTestRow(994.0, AnomalyLevel.NORMAL.name()),
                new CusumTestRow(960.0, AnomalyLevel.WEAK.name()),
        };

        testClassify(params, anomalyType, testRows);
    }

    private void testClassify(CusumParams params, AnomalyType anomalyType, CusumTestRow[] testRows) {
        val detector = new CusumDetector();
        detector.init(detectorUuid, params, anomalyType);

        // FIXME Hack to handle an off-by-one bug in the CusumDetector. [WLW]
        val adjWarmupPeriod = params.getWarmUpPeriod() - 1;

        for (int i = 0; i < adjWarmupPeriod; i++) {
            val metricData = new MetricData(metricDefinition, 1000.0, 1000 * i);
            val anomalyResult = detector.classify(metricData);
            val anomalyLevel = anomalyResult.getAnomalyLevel();
            assertEquals(AnomalyLevel.MODEL_WARMUP, anomalyLevel);
        }

        for (int i = 0; i < testRows.length; i++) {
            val testRow = testRows[i];
            val value = testRow.getObserved();
            val timestamp = (adjWarmupPeriod + i) * 1000L;
            val metricData = new MetricData(metricDefinition, value, timestamp);
            val anomalyResult = detector.classify(metricData);
            val anomalyLevel = anomalyResult.getAnomalyLevel();
            assertEquals(AnomalyLevel.valueOf(testRow.getLevel()), anomalyLevel);
        }
    }

    private static void readDataFromCsv() {
        val is = ClassLoader.getSystemResourceAsStream("tests/cusum-sample-input.csv");
        data = new CsvToBeanBuilder<CusumTestRow>(new InputStreamReader(is))
                .withType(CusumTestRow.class)
                .build()
                .parse();
    }

    private static void assertApproxEqual(double d1, double d2) {
        TestCase.assertTrue(MathUtil.isApproximatelyEqual(d1, d2, TOLERANCE));
    }
}
