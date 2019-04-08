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
package com.expedia.adaptivealerting.anomdetect.forecast;

import com.expedia.adaptivealerting.anomdetect.forecast.interval.IntervalForecast;
import com.expedia.adaptivealerting.anomdetect.forecast.interval.IntervalForecaster;
import com.expedia.adaptivealerting.anomdetect.forecast.point.PointForecaster;
import com.expedia.adaptivealerting.core.anomaly.AnomalyType;
import com.expedia.metrics.MetricData;
import com.expedia.metrics.MetricDefinition;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.when;

public class ForecastingDetectorTest {
    private ForecastingDetector detectorUnderTest;

    @Mock
    private PointForecaster pointForecaster;

    @Mock
    private IntervalForecaster intervalForecaster;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        initDependencies();
        this.detectorUnderTest = new ForecastingDetector(
                UUID.randomUUID(),
                pointForecaster,
                intervalForecaster,
                AnomalyType.TWO_TAILED);
    }

    @Test
    public void testClassify() {
        val metricDef = new MetricDefinition("my-metric");
        val metricData = new MetricData(metricDef, 100.0, Instant.now().getEpochSecond());
        val result = detectorUnderTest.classify(metricData);
        assertNotNull(result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testClassify_nullMetricData() {
        detectorUnderTest.classify(null);
    }

    private void initDependencies() {
        when(pointForecaster.forecast(any(MetricData.class)))
                .thenReturn(50.0);
        when(intervalForecaster.forecast(any(MetricData.class), anyDouble()))
                .thenReturn(new IntervalForecast(100.0, 90.0, 20.0, 10.0));
    }
}