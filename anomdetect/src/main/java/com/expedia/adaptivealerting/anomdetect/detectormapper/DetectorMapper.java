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
import com.typesafe.config.Config;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Detector mapper finds matching detectors for each incoming {@link MetricDefinition}
 */
@Slf4j
public class DetectorMapper {
    private static final String CK_DETECTOR_CACHE_UPDATE_PERIOD = "detector-mapping-cache-update-period";
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private AtomicLong lastElasticLookUpLatency = new AtomicLong(-1);

    @Getter
    @NonNull
    private DetectorSource detectorSource;
    private DetectorMapperCache cache;
    private int detectorCacheUpdateTimePeriod;

    public DetectorMapper(DetectorSource detectorSource, DetectorMapperCache cache, int detectorCacheUpdateTimePeriod) {
        assert detectorSource != null;
        this.detectorSource = detectorSource;
        this.cache = cache;
        this.detectorCacheUpdateTimePeriod = detectorCacheUpdateTimePeriod;
    }

    public DetectorMapper(DetectorSource detectorSource, Config config) {
        this(detectorSource, new DetectorMapperCache(), config.getInt(CK_DETECTOR_CACHE_UPDATE_PERIOD));
        this.initScheduler();
    }

    private void initScheduler() {
        scheduler.scheduleWithFixedDelay(() -> {
            try {
                log.trace("Updating detector mapping cache");
                this.detectorCacheUpdate();
            } catch (Exception e) {
                log.error("Error updating detectors mapping cache", e);
            }
        }, 1, detectorCacheUpdateTimePeriod, TimeUnit.MINUTES);
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


    void detectorCacheUpdate() {

        List<DetectorMapping> detectorMappings = detectorSource.findUpdatedDetectorMappings(detectorCacheUpdateTimePeriod);

        List<DetectorMapping> disabledDetectorMappings = detectorMappings.stream()
                .filter(dt -> !dt.isEnabled())
                .collect(Collectors.toList());
        if (!disabledDetectorMappings.isEmpty()) {
            cache.removeDisabledDetectorMappings(disabledDetectorMappings);
        }
        List<DetectorMapping> newDetectorMappings = detectorMappings.stream()
                .filter(DetectorMapping::isEnabled)
                .collect(Collectors.toList());
        if (!newDetectorMappings.isEmpty()) {
            cache.updateCache(newDetectorMappings);
        }
    }

}

