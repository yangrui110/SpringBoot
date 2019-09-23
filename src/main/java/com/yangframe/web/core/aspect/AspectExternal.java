package com.yangframe.web.core.aspect;

import com.yangframe.config.datasource.dynamic.SelfTransaction;
import com.yangframe.web.core.crud.centity.FindEntity;
import com.yangframe.web.core.xmlEntity.EntityMap;
import com.yangframe.web.core.xmlEntity.InfoOfEntity;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

/**
 * @autor 杨瑞
 * @date 2019/9/23 20:00
 */
@Component
@Aspect
public class AspectExternal {

    private static ApplicationContext applicationContext;

    @Around(value="execution(public * com.yangframe.web.core.crud.service.BaseServiceExternal.*(..))")
    public Object doCheck(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        String entityName = null;
        if(args[0] instanceof FindEntity)
            entityName=((FindEntity)args[0]).getEntityName();
        else if(args[0] instanceof String)
            entityName = (String) args[0];
        InfoOfEntity entity1 = EntityMap.getAndJugeNotEmpty(entityName);
        DataSourceTransactionManager manager = (DataSourceTransactionManager) applicationContext.getBean(entity1.getConfig().getSourceBeanName()+"manager");
        TransactionStatus status = SelfTransaction.begin(manager);

        Object os= null;
        try {
            os = point.proceed(args);
        } catch (Throwable throwable) {
            SelfTransaction.rollBack(manager, status);
            throwable.printStackTrace();
            throw throwable;
        }
        SelfTransaction.commit(manager, status);
        return os;
    }

    public static void setApplicationContext(ApplicationContext applicationContext1) throws BeansException {
        applicationContext=applicationContext1;
    }
}
