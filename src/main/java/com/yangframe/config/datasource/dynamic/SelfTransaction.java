package com.yangframe.config.datasource.dynamic;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * @autor 杨瑞
 * @date 2019/6/12 12:49
 */
public class SelfTransaction {

    /**
     * 获取transactionStatus变量
     * */
    public static TransactionStatus begin(DataSourceTransactionManager manager){
        TransactionStatus transaction = manager.getTransaction(new DefaultTransactionDefinition());
        return transaction;
    }

    /**
     * 提交事务
     * */
    public static void commit(DataSourceTransactionManager manager,TransactionStatus status){
        if(!status.hasSavepoint()) manager.commit(status);
    }

    /**
     * 事务回滚
     * */
    public static void rollBack(DataSourceTransactionManager manager,TransactionStatus status){
        manager.rollback(status);
    }
}
