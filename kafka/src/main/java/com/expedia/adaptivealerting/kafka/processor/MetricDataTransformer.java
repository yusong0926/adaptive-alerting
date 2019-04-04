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
package com.expedia.adaptivealerting.kafka.processor;

import com.expedia.adaptivealerting.anomdetect.DetectorMapper;
import com.expedia.adaptivealerting.anomdetect.mapper.Detector;
import com.expedia.adaptivealerting.anomdetect.mapper.MapperResult;
import com.expedia.metrics.MetricData;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author shsethi
 */
@Slf4j
@Data
@RequiredArgsConstructor
class MetricDataTransformer implements Transformer<String, MetricData, KeyValue<String, MapperResult>> {

    private ProcessorContext context;
    private KeyValueStore<String, MetricData> kvStore;

    @NonNull
    private DetectorMapper detectorMapper;
    @NonNull
    private final String stateStoreName;


    private static String addSalt(String key) {
        return key.concat(":").concat(UUID.randomUUID().toString());
    }

    private static String removeSalt(String key) {
        if (key.contains(":"))
            return key.substring(0, key.indexOf(":"));
        else
            return key;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void init(ProcessorContext context) {
        this.context = context;
        //TODO remove hardcode
        this.kvStore = (KeyValueStore<String, MetricData>) context.getStateStore("es-request-buffer");

        //TODO decide PUNCTUATION time
        this.context.schedule(2000, PunctuationType.WALL_CLOCK_TIME, (timestamp) -> {

            if (kvStore.approximateNumEntries() >= detectorMapper.optimalBatchSize()) {
                KeyValueIterator<String, MetricData> iter = this.kvStore.all();
                Map<String, MetricData> cacheMissedMetrics = new HashMap<>();

                while (iter.hasNext()) {
                    KeyValue<String, MetricData> entry = iter.next();
                    cacheMissedMetrics.put(entry.key, entry.value);
                    kvStore.delete(entry.key);
                }
                iter.close();

                List<Map<String, String>> cacheMissedMetricTags = cacheMissedMetrics.values().stream().map(value -> value.getMetricDefinition().getTags().getKv()).collect(Collectors.toList());
                if (detectorMapper.fetchDetectorMapping(cacheMissedMetricTags)) {
                    cacheMissedMetrics.forEach((originalKey, metricData) -> {
                        List<Detector> detectors = detectorMapper.getDetectorsFromCache(metricData);
                        if (!detectors.isEmpty()) {
                            context.forward(removeSalt(originalKey), new MapperResult(metricData, detectors));
                        }
                    });
                }

            } else {
                log.info("ES lookup skipped , as batch size is not optimum");
            }

            // commit the current processing progress
            context.commit();
        });

    }

    @Override
    public KeyValue<String, MapperResult> transform(String key, MetricData metricData) {

        List<Detector> detectors = detectorMapper.getDetectorsFromCache(metricData);

        if (detectors.isEmpty()) {
            //adding salt to key to prevent incoming records with same key being over-ridden
            this.kvStore.put(addSalt(key), metricData);
        } else {
            return new KeyValue<>(key, new MapperResult(metricData, detectors));
        }
        return null;

    }

    @Override
    public KeyValue<String, MapperResult> punctuate(long timestamp) {
        return null;
    }


    @Override
    public void close() {
    }
}