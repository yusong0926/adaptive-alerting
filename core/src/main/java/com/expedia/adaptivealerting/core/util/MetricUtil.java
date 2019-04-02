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
package com.expedia.adaptivealerting.core.util;

import com.expedia.metrics.MetricData;
import com.expedia.metrics.MetricDefinition;
import com.expedia.metrics.TagCollection;
import lombok.val;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static com.expedia.adaptivealerting.core.util.AssertUtil.notNull;

/**
 * Metric utilities.
 */
public final class MetricUtil {

    /**
     * Prevent instantiation.
     */
    private MetricUtil() {
    }

    public static Map<String, String> defaultKvTags() {
        val kvTags = new HashMap<String, String>();
        kvTags.put(MetricDefinition.UNIT, "");
        kvTags.put(MetricDefinition.MTYPE, "gauge");
        return kvTags;
    }

    public static Set<String> defaultVTags() {
        return new HashSet<>();
    }

    /**
     * Convenience method to create a new metric definition from the given tags. Provides defaults for null values.
     *
     * @param kvTags Key/value tags. Sets unit=[empty string] and mtype=gauge if null.
     * @param vTags  Value tags. Uses empty set if null.
     * @return Metric definition.
     */
    public static MetricDefinition metricDefinition(Map<String, String> kvTags, Set<String> vTags) {
        if (kvTags == null) {
            kvTags = defaultKvTags();
        }
        if (vTags == null) {
            vTags = defaultVTags();
        }
        return new MetricDefinition(new TagCollection(kvTags, vTags));
    }

    public static MetricData metricData(MetricDefinition metricDef) {
        notNull(metricDef, "metricDef can't be null");
        return metricData(metricDef, 0.0);
    }

    /**
     * Convenience method to create a new {@link MetricData} from the given definition and value. Sets the timestamp to
     * the current epoch second.
     *
     * @param metricDef Metric definition.
     * @param value     Value.
     * @return Metric data.
     */
    public static MetricData metricData(MetricDefinition metricDef, double value) {
        return new MetricData(metricDef, value, Instant.now().getEpochSecond());
    }
}
