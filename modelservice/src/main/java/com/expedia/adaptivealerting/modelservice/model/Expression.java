package com.expedia.adaptivealerting.modelservice.model;

import lombok.Data;

import java.util.List;

@Data
public class Expression {
    private Operator operator;
    List<Operand> operands;
}
