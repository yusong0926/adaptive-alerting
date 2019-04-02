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
import com.expedia.metrics.metrictank.MessagePackSerializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class MetricDataMessagePackSerde implements Serde<MetricData> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public void close() {
    }

    @Override
    public Serializer<MetricData> serializer() {
        return new Ser();
    }

    @Override
    public Deserializer<MetricData> deserializer() {
        return new Deser();
    }

    public static class Ser implements Serializer<MetricData> {

        // N.B. This is a MetricTank-specific serializer. We don't want that for the long term.
        private static final MessagePackSerializer mps = new MessagePackSerializer();

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {
        }

        @Override
        public byte[] serialize(String topic, MetricData metricData) {
            try {
                return mps.serialize(metricData);
            } catch (IOException e) {
                throw new SerializationException(e);
            }
        }

        @Override
        public void close() {
        }
    }

    public static class Deser implements Deserializer<MetricData> {

        // N.B. This is a MetricTank-specific serializer. We don't want that for the long term.
        private static final MessagePackSerializer mps = new MessagePackSerializer();

        @Override
        public void configure(Map<String, ?> configs, boolean isKey) {
        }

        @Override
        public MetricData deserialize(String topic, byte[] metricDataBytes) {
            try {
                return mps.deserialize(metricDataBytes);
            } catch (IOException e) {
                log.error("Deserialization error", e);
                return null;
            }
        }

        @Override
        public void close() {
        }
    }
}
