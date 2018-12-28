package com.expedia.adaptivealerting.modelservice.service;

import com.expedia.adaptivealerting.modelservice.repo.MetricRepository;
import com.expedia.adaptivealerting.modelservice.util.JpaConverterJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */

@Service
@Slf4j
public class OnboardServiceImpl implements OnboardService {

    @Autowired
    private JpaConverterJson jpaConverterJson;

    @Autowired
    private MetricRepository metricRepo;

    @Override
    public void isOnboarded(String tags) {
        log.info("tags:{}", tags);
        Object tagsMap = jpaConverterJson.convertToEntityAttribute((String) tags);
        System.out.println(tagsMap);
    }
}
