package com.expedia.adaptivealerting.modelservice.model;

import lombok.Data;

@Data
public class CreateDetectorMappingRequest {
    private Expression expression;
    private Detector detector;
    private User user;
}


