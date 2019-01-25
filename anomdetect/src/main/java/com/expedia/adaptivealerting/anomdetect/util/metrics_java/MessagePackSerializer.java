package com.expedia.adaptivealerting.anomdetect.util.metrics_java;

/**
 * @author shsethi
 */
/*
 * Copyright 2018 Expedia Group, Inc.
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

import com.expedia.metrics.MetricDefinition;

/**
 * This serializer reads and writes the Metrictank Kafka-mdm MetricData format
 *
 * @see <a href="https://github.com/grafana/metrictank/blob/master/docs/inputs.md#metricdata">MetricData</a>
 */
public class MessagePackSerializer  {
    // Defaults chosen to match MetricTank Prometheus input
    // https://github.com/grafana/metrictank/blob/b1c2c1a877d08b75d28f150b9b9b68ec90d7db73/input/prometheus/prometheus.go#L105
    private static final int DEFAULT_ORG_ID = 1;
    private static final int DEFAULT_INTERVAL = 15;
    private static final String DEFAULT_UNIT = "unknown";
    private static final String DEFAULT_MTYPE = "gauge";

    private final MetricTankIdFactory idFactory = new MetricTankIdFactory();

    static int getOrgId(MetricDefinition metric) {

        return DEFAULT_ORG_ID;
    }

    static int getInterval(MetricDefinition metric) {

        return DEFAULT_INTERVAL;
    }

    static String getUnit(MetricDefinition metric) {

        return DEFAULT_UNIT;
    }

    static String getMtype(MetricDefinition metric) {

        return DEFAULT_MTYPE;
    }

}