package com.expedia.adaptivealerting.modelservice.util;

import com.expedia.adaptivealerting.modelservice.model.Expression;
import com.expedia.adaptivealerting.modelservice.model.Field;
import com.expedia.adaptivealerting.modelservice.model.Operand;
import com.expedia.adaptivealerting.modelservice.model.Operator;
import com.expedia.adaptivealerting.modelservice.dao.es.BoolCondition;
import com.expedia.adaptivealerting.modelservice.dao.es.MustCondition;
import com.expedia.adaptivealerting.modelservice.dao.es.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QueryUtil {

    private QueryUtil() {}

    public static Expression buildExpression(Query query) {
        Expression expression = new Expression();
        //TODO - derive operator from query. for now hardcoding to AND as this is the only operator supported now.
        expression.setOperator(Operator.AND);
        List<Operand> operands = query.getBool().getMust().stream()
            .map(mustCondition -> {
                Operand operand = new Operand();
                Field field = mustCondition.getMatch().entrySet().stream()
                    .map(match -> new Field(match.getKey(), match.getValue()))
                    .collect(Collectors.toList()).get(0);
                operand.setField(field);
                return operand;
            }).collect(Collectors.toList());
        expression.setOperands(operands);
        return expression;
    }

    public static Query buildQuery(Expression expression) {
        List<MustCondition> mustConditions = expression.getOperands().stream()
            .map(operand -> {
                Map<String, String> condition = new HashMap<>();
                condition.put(operand.getField().getKey(), operand.getField().getValue());
                return new MustCondition(condition);
            })
            .collect(Collectors.toList());
        BoolCondition boolCondition = new BoolCondition(mustConditions);
        return new Query(boolCondition);
    }
}
