package com.expedia.adaptivealerting.modelservice.dao.es;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Query {
    private BoolCondition bool;
}
