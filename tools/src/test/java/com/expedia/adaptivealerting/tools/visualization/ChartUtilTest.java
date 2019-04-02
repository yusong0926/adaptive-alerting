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
package com.expedia.adaptivealerting.tools.visualization;

import lombok.val;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class ChartUtilTest {

    @Test(expected = IllegalAccessException.class)
    public void testPrivateConstructor() throws Exception {
        ChartUtil.class.newInstance();
    }

    @Test
    public void testCreateChart() {
        val chartSeries = new ChartSeries();
        val chart = ChartUtil.createChart("My Chart", chartSeries);
        assertEquals("My Chart", chart.getTitle().getText());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateChart_nullTitle() {
        ChartUtil.createChart(null, new ChartSeries());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateChart_nullSeries() {
        ChartUtil.createChart("My Chart", null);
    }

    // N.B. Travis CI fails when running this since Travis CI is headless.
    @Test
    @Ignore
    public void testCreateChartFrame() {
        val chartSeries = new ChartSeries();
        val chart = ChartUtil.createChart("My Chart", chartSeries);
        val frame = ChartUtil.createChartFrame("My Frame", chart);
        assertEquals("My Frame", frame.getTitle());
    }

    @Test
    public void testCreateChartPanel() {
        val chartSeries = new ChartSeries();
        val chart = ChartUtil.createChart("My Chart", chartSeries);
        val panel = ChartUtil.createChartPanel(chart);
        assertEquals(1, panel.getComponents().length);
    }
}
