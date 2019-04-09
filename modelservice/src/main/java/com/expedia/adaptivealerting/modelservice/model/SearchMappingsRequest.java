package com.expedia.adaptivealerting.modelservice.model;

import lombok.Data;

import java.util.UUID;

@Data
public class SearchMappingsRequest {
    private String userId;
    private UUID detectorUuid;
}
