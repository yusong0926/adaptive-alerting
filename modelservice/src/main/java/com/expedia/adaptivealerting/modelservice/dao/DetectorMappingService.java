package com.expedia.adaptivealerting.modelservice.dao;

import com.expedia.adaptivealerting.modelservice.model.CreateDetectorMappingRequest;
import com.expedia.adaptivealerting.modelservice.model.DetectorMapping;
import com.expedia.adaptivealerting.modelservice.model.MatchingDetectorsResponse;
import com.expedia.adaptivealerting.modelservice.model.SearchMappingsRequest;
import com.expedia.adaptivealerting.modelservice.model.UpdateDetectorMappingRequest;

import java.util.List;
import java.util.Map;

public interface DetectorMappingService {

    MatchingDetectorsResponse findMatchingDetectorMappings(List<Map<String, String>> tagsList);
    String createDetectorMapping(CreateDetectorMappingRequest createDetectorMappingRequest);
    void updateDetectorMapping(UpdateDetectorMappingRequest updateDetectorMappingRequest);
    void deleteDetectorMapping(String id);
    DetectorMapping findDetectorMapping(String id);
    List<DetectorMapping> search(SearchMappingsRequest searchMappingsRequest);
    List<DetectorMapping> findLastUpdated(int timeInSeconds);
    void disableDetectorMapping(String id);
}
