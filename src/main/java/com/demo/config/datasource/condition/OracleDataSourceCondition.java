package com.demo.config.datasource.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @autor 杨瑞
 * @date 2019/6/6 13:32
 */
public class OracleDataSourceCondition implements Condition {
    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        boolean s=conditionContext.getEnvironment().containsProperty("spring.datasource.oracle.url");
        return s;
    }
}
