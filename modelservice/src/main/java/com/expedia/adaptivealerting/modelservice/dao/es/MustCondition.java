package com.expedia.adaptivealerting.modelservice.dao.es;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class MustCondition {
    Map<String, String> match;
}
