package com.expedia.adaptivealerting.modelservice.model;

import lombok.Data;

@Data
public class UpdateDetectorMappingRequest {
    private String id;
    private Expression expression;
    private Detector detector;
}
