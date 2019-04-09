package com.expedia.adaptivealerting.modelservice.model;

import lombok.Data;

@Data
public class Operand {
    private Field field;
    private Expression expression;
}
