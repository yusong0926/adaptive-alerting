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

import lombok.Data;
import org.jfree.data.time.TimeSeries;

/**
 * Time series container.
 */
@Data
public class ChartSeries {
    private final TimeSeries observed = new TimeSeries("observed");
    private final TimeSeries predicted = new TimeSeries("predicted");
    private final TimeSeries weakThresholdUpper = new TimeSeries("weakThresholdUpper");
    private final TimeSeries weakThresoldLower = new TimeSeries("weakThresholdLower");
    private final TimeSeries strongThresholdUpper = new TimeSeries("strongThresholdUpper");
    private final TimeSeries strongThresholdLower = new TimeSeries("strongThresholdLower");
    private final TimeSeries strongOutlier = new TimeSeries("strongOutlier");
    private final TimeSeries weakOutlier = new TimeSeries("weakOutlier");
}
