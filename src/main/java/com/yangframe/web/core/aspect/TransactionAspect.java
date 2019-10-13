package com.yangframe.web.core.aspect;

import com.alibaba.druid.pool.DruidDataSource;
import com.yangframe.config.datasource.dynamic.SelfTransaction;
import com.yangframe.config.util.ApplicationContextUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

@Component
@Aspect
@Order(9)
public class TransactionAspect {

    @Around(value = "execution(public * com.yangframe.chat.service.ChatService.*(..))")
    public Object aspectMysqlTransactionManager(ProceedingJoinPoint point) throws Throwable {
        DataSourceTransactionManager transactionManger = (DataSourceTransactionManager) ApplicationContextUtil.applicationContext.getBean("mysqlDataSource"+"manager");
        return aspect(transactionManger,point);
    }
    /*@Around(value = "")
    public Object aspectOracleTransactionManager(ProceedingJoinPoint point) throws Throwable {
        DataSourceTransactionManager transactionManger = (DataSourceTransactionManager) ApplicationContextUtil.applicationContext.getBean("oracleDatasource"+"manager");
        return aspect(transactionManger,point);
    }
    @Around(value = "")
    public Object aspectH2TransactionManager(ProceedingJoinPoint point) throws Throwable {
        DataSourceTransactionManager transactionManger = (DataSourceTransactionManager) ApplicationContextUtil.applicationContext.getBean("h2Datasource"+"manager");
        return aspect(transactionManger,point);
    }
    @Around(value = "")
    public Object aspectSqlTransactionManager(ProceedingJoinPoint point) throws Throwable {
        DataSourceTransactionManager transactionManger = (DataSourceTransactionManager) ApplicationContextUtil.applicationContext.getBean("sqlDatasource"+"manager");
        return aspect(transactionManger,point);
    }*/

    private Object aspect(DataSourceTransactionManager transactionManager, ProceedingJoinPoint point) throws Throwable {
        TransactionStatus status = SelfTransaction.begin(transactionManager);

        Object os= null;
        try {
            os = point.proceed(point.getArgs());
        } catch (Throwable throwable) {
            if(status.isNewTransaction())
                SelfTransaction.rollBack(transactionManager, status);
            throwable.printStackTrace();
            throw throwable;
        }
        if(status.isNewTransaction())
            SelfTransaction.commit(transactionManager, status);
        return os;
    }
}
