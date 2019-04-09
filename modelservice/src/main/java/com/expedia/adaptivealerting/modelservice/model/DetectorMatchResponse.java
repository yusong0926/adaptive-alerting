package com.expedia.adaptivealerting.modelservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DetectorMatchResponse {
    private List<DetectorMapping> detectorMappings;
    private long lookupTimeInMillis;
}
