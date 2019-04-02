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
package com.expedia.adaptivealerting.core.data;

import com.expedia.metrics.MetricDefinition;
import com.expedia.metrics.TagCollection;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.*;

/**
 * {@link MetricFrameLoader} unit test.
 */
@Slf4j
public class MetricFrameLoaderTest {
    private static final String DEF_FILENAME = "datasets/cal-inflow-metric-def.json";
    private static final String DATA_FILENAME = "datasets/cal-inflow.csv";
    private static final double TOLERANCE = 0.001;

    @Test
    public void testLoadCsv_files() throws IOException {
        val defFilename = ClassLoader.getSystemResource(DEF_FILENAME).getFile();
        val dataFilename = ClassLoader.getSystemResource(DATA_FILENAME).getFile();
        val defFile = new File(defFilename);
        val dataFile = new File(dataFilename);
        log.info("defFile={}, dataFile={}", defFile, dataFile);
        val frame = MetricFrameLoader.loadCsv(defFile, dataFile, true);
        assertEquals(5040, frame.getNumRows());
        assertEquals(9.0, frame.getMetricData().get(118).getValue(), 0.001);
    }

    @Test
    public void testLoadCsv() throws IOException {
        val metric = new MetricDefinition(new TagCollection(new HashMap<String, String>() {{
            put("unit", "dummy");
            put("mtype", "dummy");
        }}));
        val is = ClassLoader.getSystemResourceAsStream(DATA_FILENAME);
        val frame = MetricFrameLoader.loadCsv(metric, is, true);
        assertNotNull(frame);
        assertTrue(frame.getNumRows() > 0);
        assertEquals(0.0, frame.getMetricDataPoint(0).getValue(), TOLERANCE);
        assertEquals(3.0, frame.getMetricDataPoint(15).getValue(), TOLERANCE);
    }
}
