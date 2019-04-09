package com.expedia.adaptivealerting.modelservice.model;

import com.expedia.adaptivealerting.modelservice.model.Detector;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class MatchingDetectorsResponse {
    private Map<Integer, List<Detector>> groupedDetectorsBySearchIndex;
    private long lookupTimeInMillis;

}