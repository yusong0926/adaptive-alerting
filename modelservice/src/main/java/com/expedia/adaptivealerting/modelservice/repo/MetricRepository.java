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
package com.expedia.adaptivealerting.modelservice.repo;

import com.expedia.adaptivealerting.modelservice.entity.Metric;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data repository for metrics.
 */
public interface MetricRepository extends PagingAndSortingRepository<Metric, Long> {

    /**
     * Finds a metric by its unique key, if any.
     *
     * @param key Unique key.
     * @return Metric identified by the unique key.
     */
    Metric findByKey(@Param("key") String key);

    /**
     * Finds a metric by its unique hash.
     *
     * @param hash Unique hash.
     * @return Metric identified by the unique hash.
     */
    Metric findByHash(@Param("hash") String hash);

    /**
     * Indicates whether a metric with the given hash exists.
     *
     * @param hash metric hash
     * @return boolean indicating whether a corresponding metric exists
     */
    boolean existsByHash(@Param("hash") String hash);

    /**
     * Finds a list of metrics by its user.
     *
     * @param user Detector user.
     * @return List of metrics by its user
     */
    @Query(nativeQuery = true, value = "SELECT * FROM metric WHERE id IN (SELECT metric_id FROM metric_detector_mapping WHERE detector_id IN (SELECT id FROM detector WHERE created_by=:user))")
    List<Metric> findByCreatedBy(@Param("user") String user);

    /**
     * Finds a list of metrics by its matching key.
     *
     * @param key      Matching key.
     * @param pageable paging params
     * @return List of metrics by its matching key
     */
    @Query(nativeQuery = true, value = "SELECT * FROM metric m WHERE m.ukey LIKE :key order by m.ukey", countQuery = "SELECT count(*) FROM metric m WHERE m.ukey LIKE :key")
    Page<Metric> findByKeyContaining(@Param("key") String key, Pageable pageable);

    /**
     * Finds a list of metrics by its matching tag. Tags has json data type and this query works only for json values and not keys.
     *
     * @param tag Matching tag value.
     * @return List of metrics by its matching tag
     */
    @Query(nativeQuery = true, value = "SELECT * FROM (SELECT id, ukey, hash, tags, JSON_SEARCH(tags, \"all\", :tag) as tag_result FROM metric) as new_metric WHERE tag_result IS NOT NULL")
    List<Metric> findByTagsContaining(@Param("tag") String tag);

    /**
     * Finds a list of metrics attached to a given detector
     *
     * @param uuid Detector uuid.
     * @return List of metrics attached to the provided detector uuid
     */
    @Query("select mmm.metric from MetricDetectorMapping mmm where mmm.detector.uuid = :uuid")
    List<Metric> findByDetectorUuid(@Param("uuid") String uuid);
}
