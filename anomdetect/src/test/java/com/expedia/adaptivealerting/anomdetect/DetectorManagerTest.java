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
package com.expedia.adaptivealerting.anomdetect;

import com.expedia.adaptivealerting.anomdetect.comp.DetectorSource;
import com.expedia.adaptivealerting.anomdetect.detector.Detector;
import com.expedia.adaptivealerting.core.anomaly.AnomalyResult;
import com.expedia.adaptivealerting.core.data.MappedMetricData;
import com.expedia.metrics.MetricData;
import com.expedia.metrics.MetricDefinition;
import com.typesafe.config.Config;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * {@link DetectorManager} unit test.
 */
public final class DetectorManagerTest {
    private static final String DETECTOR_TYPE = "ewma-detector";
    private final int detectorRefreshPeriod = 1;
    private final int badDetectorRefreshPeriod = 0;

    private DetectorManager managerUnderTest;

    @Mock
    private DetectorSource detectorSource;

    private UUID mappedUuid;
    private UUID unmappedUuid;
    private List<UUID> updatedDetectors;

    // "Good" just means the detector source can find a detector for the MMD.
    private MetricDefinition goodDefinition;
    private MetricData goodMetricData;
    private MappedMetricData goodMappedMetricData;

    // "Bad" just means that the detector source can't find a detector for the MMD.
    private MetricDefinition badDefinition;
    private MetricData badMetricData;
    private MappedMetricData badMappedMetricData;

    @Mock
    private Detector detector;

    @Mock
    private AnomalyResult anomalyResult;

    @Mock
    private Config config;

    @Mock
    private Config badConfig;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        initTestObjects();
        initDependencies();
        this.managerUnderTest = new DetectorManager(detectorSource, config);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadConfig() {
        new DetectorManager(detectorSource, badConfig);
    }

    @Test
    public void testGetDetectorSource() {
        // Purely for code coverage :D
        managerUnderTest.getDetectorSource();
    }

    @Test
    public void testGetDetectorTypes() {
        val detectorTypes = managerUnderTest.getDetectorTypes();
        assertTrue(detectorTypes.contains(DETECTOR_TYPE));
    }

    @Test
    public void testHasDetectorType() {
        assertTrue(managerUnderTest.hasDetectorType(DETECTOR_TYPE));
    }

    @Test
    public void testDoesNotHaveDetectorType() {
        assertFalse(managerUnderTest.hasDetectorType("some-nonexistent-type"));
    }

    @Test
    public void testDetectorRefresh() {
        val result = managerUnderTest.detectorMapRefresh();
        assertNotNull(result);
        assertEquals(updatedDetectors, result);
    }

    @Test
    public void testClassify() {
        val result = managerUnderTest.classify(goodMappedMetricData);
        assertNotNull(result);
        assertSame(anomalyResult, result);
    }

    @Test
    public void testClassify_getCached() {
        managerUnderTest.classify(goodMappedMetricData);

        // This one grabs the cached detector
        // TODO Come up with some way to actually prove this. E.g. mock the cache and verify().
        //  For now I just put a log.trace() in there. [WLW]
        managerUnderTest.classify(goodMappedMetricData);
    }

    @Test
    public void testClassifyMetricThatCantBeFound() {
        val result = managerUnderTest.classify(badMappedMetricData);
        assertNull(result);
    }

    private void initTestObjects() {
        this.mappedUuid = UUID.randomUUID();
        this.unmappedUuid = UUID.randomUUID();

        this.goodDefinition = new MetricDefinition("good-definition");
        this.goodMetricData = new MetricData(goodDefinition, 100.0, Instant.now().getEpochSecond());
        this.goodMappedMetricData = new MappedMetricData(goodMetricData, mappedUuid);

        this.badDefinition = new MetricDefinition("bad-definition");
        this.badMetricData = new MetricData(badDefinition, 100.0, Instant.now().getEpochSecond());
        this.badMappedMetricData = new MappedMetricData(badMetricData, unmappedUuid);

        val detectorUuid = UUID.fromString("7629c28a-5958-4ca7-9aaa-49b95d3481ff");
        this.updatedDetectors = Collections.singletonList(detectorUuid);

    }

    private void initDependencies() {
        when(detector.classify(goodMetricData)).thenReturn(anomalyResult);

        when(detectorSource.findDetectorTypes()).thenReturn(Collections.singleton(DETECTOR_TYPE));
        when(detectorSource.findDetector(mappedUuid)).thenReturn(detector);
        when(detectorSource.findDetector(unmappedUuid)).thenReturn(null);
        when(detectorSource.findUpdatedDetectors(detectorRefreshPeriod)).thenReturn(updatedDetectors);

        when(config.getInt("detector-refresh-period")).thenReturn(detectorRefreshPeriod);
        when(badConfig.getInt("detector-refresh-period")).thenReturn(badDetectorRefreshPeriod);
    }
}
