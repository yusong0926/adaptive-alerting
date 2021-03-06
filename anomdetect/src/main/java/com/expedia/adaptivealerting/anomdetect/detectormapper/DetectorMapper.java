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
package com.expedia.adaptivealerting.anomdetect.detectormapper;


import com.expedia.adaptivealerting.anomdetect.comp.DetectorSource;
import com.expedia.metrics.MetricDefinition;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Detector mapper finds matching detectors for each incoming {@link MetricDefinition}
 */
@Slf4j
public class DetectorMapper {
    private DetectorMapperCache cache;
    private AtomicLong lastElasticLookUpLatency = new AtomicLong(-1);
    @Getter
    @NonNull
    private DetectorSource detectorSource;


    public DetectorMapper(DetectorSource detectorSource) {
        assert detectorSource != null;
        this.detectorSource = detectorSource;
        this.cache = new DetectorMapperCache();
    }

    public List<Detector> getDetectorsFromCache(MetricDefinition metricDefinition) {
        String cacheKey = CacheUtil.getKey(metricDefinition.getTags().getKv());
        return cache.get(cacheKey);
    }

    public boolean isSuccessfulDetectorMappingLookup(List<Map<String, String>> cacheMissedMetricTags) {

        log.info("Mapping-Cache: lookup for {} metrics", cacheMissedMetricTags.size());
        DetectorMatchResponse matchingDetectorMappings = detectorSource.findMatchingDetectorMappings(cacheMissedMetricTags);

        if (matchingDetectorMappings != null) {

            lastElasticLookUpLatency.set(matchingDetectorMappings.getLookupTimeInMillis());
            Map<Integer, List<Detector>> groupedDetectorsByIndex = matchingDetectorMappings.getGroupedDetectorsBySearchIndex();

            //populate cache and result map
            groupedDetectorsByIndex.forEach((index, detectors) -> {
                String cacheKey = CacheUtil.getKey(cacheMissedMetricTags.get(index));
                if (!detectors.isEmpty()) {
                    cache.put(cacheKey, detectors);
                }
            });

            Set<Integer> searchIndexes = groupedDetectorsByIndex.keySet();

//For metrics with no matching detectors, set matching detectors to empty in cache to avoid repeated cache miss
            int i = 0;
            for (Map<String, String> tags : cacheMissedMetricTags) {
                if (!searchIndexes.contains(i)) {
                    String cacheKey = CacheUtil.getKey(tags);
                    cache.put(cacheKey, Collections.EMPTY_LIST);
                }
                i++;
            }

        } else {
            lastElasticLookUpLatency.set(-2);
        }
        return matchingDetectorMappings != null;
    }

    //TODO - make batch size configureable
    public int optimalBatchSize() {
        if (lastElasticLookUpLatency.longValue() == -1L || lastElasticLookUpLatency.longValue() > 100L) {
            return 80;
        }
        return 0;
    }

}

