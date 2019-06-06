package com.demo.web.core.aspect.mysql;

import com.demo.config.advice.BaseException;
import com.demo.web.core.crud.centity.CEntity;
import com.demo.web.core.crud.centity.COrderBy;
import com.demo.web.core.crud.centity.ConditionEntity;
import com.demo.web.core.crud.centity.FindEntity;
import com.demo.web.core.xmlEntity.EntityMap;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/5/11 16:01
 */
@Component
@Aspect
public class AspectCrud {

    /***
     * 切面拦截mysql操作
     */
    @Around(value="execution(public * com.demo.web.core.crud.service.*.*(..))")
    public Object doCheck(ProceedingJoinPoint point) throws Throwable {
        Object[] args=point.getArgs();
        System.out.println("方法名："+point.getSignature().getName());
        if("findAll".equals(point.getSignature().getName())) {
            for (Object a : args) {
                if (a instanceof FindEntity) {
                    String orign = ((FindEntity) a).getEntityName();
                    List<CEntity> conditions = ((FindEntity) a).getCondition();
                    if (conditions.size() > 0) {
                        Map map = new HashMap();
                        conditions.forEach((k) -> {
                            map.put(k.getLeft(), "");
                        });
                        EntityMap.yanzhengMap((Map<String, Object>) map, orign);
                    }
                    ConditionEntity entity = EntityMap.readEntityToCondition(orign, conditions, ((FindEntity) a).getOrderBy());
                    args[1] = entity;
                }
            }
        }else {
            //验证更新、删除、插入
            for(Object arg: args){
                if(arg instanceof FindEntity){
                    List<CEntity> conditions = ((FindEntity) arg).getCondition();
                    String tableAlias=((FindEntity) arg).getEntityName();
                    if(EntityMap.judeIsViewEntity(tableAlias)){
                        throw new BaseException(304,"视图表"+tableAlias+"不允许增删改操作");
                    }
                    EntityMap.yanzhengMap(((FindEntity) arg).getData(), tableAlias);
                    if (conditions.size() > 0) {
                        Map map = new HashMap();
                        conditions.forEach((k) -> {
                            map.put(k.getLeft(), "");
                        });
                        EntityMap.yanzhengMap((Map<String, Object>) map, tableAlias);
                    }
                    //((FindEntity) arg).setEntityName(EntityMap.getTableName(tableAlias));
                }
            }
        }
        Object os= point.proceed(args);
        return os;
    }

    /***
     * 切面拦截sql操作
     * */


}
