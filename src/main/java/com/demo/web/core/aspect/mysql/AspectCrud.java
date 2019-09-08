package com.demo.web.core.aspect.mysql;

import com.demo.config.advice.BaseException;
import com.demo.config.datasource.dynamic.SelfTransaction;
import com.demo.config.datasource.type.DataSourceType;
import com.demo.web.core.crud.centity.ConditionEntity;
import com.demo.web.core.crud.centity.FindEntity;
import com.demo.web.core.crud.centity.PageMake;
import com.demo.web.core.xmlEntity.EntityMap;
import com.demo.web.core.xmlEntity.InfoOfEntity;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/5/11 16:01
 */
@Component
@Aspect
public class AspectCrud implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    /***
     * 切面拦截mysql操作
     */
    @Around(value="execution(public * com.demo.web.core.crud.service.BaseServiceImpl.*(..))")
    public Object doCheck(ProceedingJoinPoint point) throws Throwable {
        Object[] args=point.getArgs();
        System.out.println("方法名："+point.getSignature().getName());
        if("findAll".equals(point.getSignature().getName())||"findAllNoPage".equals(point.getSignature().getName())||"totalNum".equals(point.getSignature().getName())) {
            for (Object a : args) {
                if (a instanceof FindEntity) {
                    String orign = ((FindEntity) a).getEntityName();
                    Map conditions = ((FindEntity) a).getCondition();
                    if (conditions.size() > 0) {
                        EntityMap.yanzhengConditionKey((Map<String, Object>) conditions, orign);
                    }
                    ConditionEntity entity = EntityMap.readEntityToCondition(orign, conditions, ((FindEntity) a).getOrderBy());
                    entity.setStart(((FindEntity)a).getStart());
                    entity.setEnd(((FindEntity)a).getEnd());
                    args[1] = entity;
                    //构造分页条件
                    PageMake.makePage(entity, EntityMap.getAndJugeNotEmpty(((FindEntity) a).getEntityName()).getConfig().getSourceType());
                }
            }
        }else {
            //验证更新、删除、插入
            for(Object arg: args){
                if(arg instanceof FindEntity){
                    FindEntity entity=(FindEntity) arg;
                    Map conditions = entity.getCondition();
                    String tableAlias=entity.getEntityName();
                    if(EntityMap.judeIsViewEntity(tableAlias)){
                        throw new BaseException(304,"视图表"+tableAlias+"不允许增删改操作");
                    }
                    //处理其它的操作
                    EntityMap.dealUpCondition(entity);
                    EntityMap.yanzhengDataKey(entity.getData(), entity.getEntityName());
                    EntityMap.yanzhengConditionKey(entity.getCondition(), entity.getEntityName());
                }
            }
        }
        String entityName = "";
        if(args[0] instanceof String){
            entityName= (String) args[0];
        }else if(args[0] instanceof FindEntity){
            entityName=((FindEntity) args[0]).getEntityName();
        }
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
    }

    /***
     * 切面拦截sql操作
     * */

    private static void changeTableName(ConditionEntity entity,String sourceType){
        if(DataSourceType.ORACLE.equals(sourceType))
            entity.setMainTable("\""+entity.getMainTable()+"\"");

    }

}
