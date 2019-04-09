package com.expedia.adaptivealerting.modelservice.dao.es;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BoolCondition {
    private List<MustCondition> must;
}
