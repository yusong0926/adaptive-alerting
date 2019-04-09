package com.expedia.adaptivealerting.modelservice.model;

import lombok.Data;

import java.util.List;

/**
 * The type Detector mapping.
 *
 * searchIndexes: index of matching metric-tag in request batch of metric-tags
 */
@Data
public class DetectorMapping {
    private String id;
    private Detector detector;
    private Expression expression;
    private User user;
    private long lastModifiedTimeInMillis;
    private long createdTimeInMillis;
    private boolean isEnabled;
    private List<Integer> searchIndexes;
}
