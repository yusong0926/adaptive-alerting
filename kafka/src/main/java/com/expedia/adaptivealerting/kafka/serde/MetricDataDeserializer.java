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
package com.expedia.adaptivealerting.kafka.serde;

import com.expedia.metrics.MetricData;
import com.expedia.metrics.MetricDefinition;
import com.expedia.metrics.TagCollection;
import com.expedia.metrics.metrictank.MessagePackSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Deserializer;
import org.msgpack.core.MessagePack;
import org.msgpack.core.MessageUnpacker;
import org.msgpack.value.Value;
import org.msgpack.value.ValueFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class MetricDataDeserializer implements Deserializer<MetricData> {
    private final static MessagePackSerializer mps = new MessagePackSerializer();

    private String metricKey = "Metric";
    private String valueKey = "Value";
    private String timeKey = "Time";
    private String tagsKey = "Tags";

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public MetricData deserialize(String topic, byte[] metricDataBytes) {
        try {
            MessageUnpacker unpacker = MessagePack.newDefaultUnpacker(metricDataBytes);
            Map<Value, Value> metricData = unpacker.unpackValue().asMapValue().map();
            String key = metricData.get(ValueFactory.newString(metricKey)).asStringValue().asString();
            Map<String, String> tags = createTags(metricData);
            MetricDefinition metricDefinition = new MetricDefinition(key, new TagCollection(tags), TagCollection.EMPTY);
            return new MetricData(metricDefinition, metricData.get(ValueFactory.newString(valueKey)).asFloatValue().toDouble(),
                    metricData.get(ValueFactory.newString(timeKey)).asIntegerValue().toLong());
        } catch (Exception ex) {

            log.error("Failed to deserialize due to error: {}", ex);
            return null;
        }
    }


    private Map<String, String> createTags(Map<Value, Value> metricData) {
        List<Value> list = metricData.get(ValueFactory.newString(tagsKey)).asArrayValue().list();
        return list.stream()
                .map(tag -> tag.toString().split("="))
                .collect(Collectors.toMap(kvPairs -> kvPairs[0], kvPairs -> kvPairs[1]));
    }

    @Override
    public void close() {
    }
}
