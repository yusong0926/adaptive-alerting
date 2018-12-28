package com.expedia.adaptivealerting.modelservice.repo;

import com.expedia.adaptivealerting.modelservice.entity.MetricTagMapping;
import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Created by tbahl on 12/20/18.
 */
public interface MetricTagMappingRepository extends PagingAndSortingRepository<MetricTagMapping, Long> {

}
