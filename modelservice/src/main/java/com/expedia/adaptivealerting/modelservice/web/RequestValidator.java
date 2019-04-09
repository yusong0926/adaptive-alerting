package com.expedia.adaptivealerting.modelservice.web;

import com.expedia.adaptivealerting.modelservice.model.Expression;
import com.expedia.adaptivealerting.modelservice.model.Operand;
import com.expedia.adaptivealerting.modelservice.model.Operator;
import com.expedia.adaptivealerting.modelservice.model.Detector;
import com.expedia.adaptivealerting.modelservice.model.User;
import com.expedia.adaptivealerting.modelservice.dao.es.DetectorMappingEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Component
public class RequestValidator {

    public void validateUser(User user) {
        Assert.notNull(user, "subscription user can't null");
        Assert.isTrue(!StringUtils.isEmpty(user.getId()), "subscription userId can't empty");
        Assert.isTrue(!StringUtils.containsWhitespace(user.getId()), "subscription userId can't " +
            "contain whitespaces");
    }

    public void validateExpression(Expression expression) {
        Assert.notNull(expression, "subscription expression can't null");
        //Only AND operator is supported now
        Assert.isTrue(Operator.AND.equals(expression.getOperator()), "Only AND operator is supported now");
        Assert.notEmpty(expression.getOperands(), "Operands can't be empty");
        expression.getOperands().forEach(operand -> {
            validateOperand(operand);
        });
    }

    private void validateOperand(Operand operand) {
        //Nested conditions are not supported now
        Assert.isNull(operand.getExpression(), "Nested expressions are not supported");
        Assert.notNull(operand.getField(), "Operands can't be empty");
        Assert.isTrue(!StringUtils.isEmpty(operand.getField().getKey()), "Invalid operand field key");
        Assert.isTrue(!StringUtils.isEmpty(operand.getField().getValue()), "Invalid operand field value");
        Assert.isTrue(!operand.getField().getKey().startsWith(DetectorMappingEntity.AA_PREFIX),
            String.format("Invalid operand field key '%s'. %s is a reserved prefix",
                operand.getField().getKey(), DetectorMappingEntity.AA_PREFIX));
    }
    
    public void validateDetector(Detector detector) {
        Assert.notNull(detector, "Detector can't be null");
        Assert.notNull(detector.getId(), "Detector id can't be null");
    }
}
