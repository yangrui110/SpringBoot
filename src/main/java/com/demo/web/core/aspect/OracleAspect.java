package com.demo.web.core.aspect;

import com.demo.config.datasource.type.DataSourceType;
import com.demo.web.core.crud.centity.ConditionEntity;
import com.demo.web.core.crud.centity.FindEntity;
import com.demo.web.core.util.OracleParser;
import com.demo.web.core.xmlEntity.EntityMap;
import com.demo.web.core.xmlEntity.InfoOfEntity;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @see com.demo.web.core.crud.dao.BaseDao
 * 拦截BaseDao类，当执行其中的方法时，就会被调用
 * */
@Component
@Aspect
public class OracleAspect {

    @Around(value = "execution( public * com.demo.web.core.crud.dao.BaseDao.findByPK(..))")
    public Object aspectPoint(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        aspectCommon(args);
        return point.proceed(args);
    }


    @Around(value = "execution( public * com.demo.web.core.crud.dao.BaseDao.findAll*(..))")
    public Object aspectFindAll(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        aspectConditon((ConditionEntity) args[0]);
        return point.proceed(args);
    }

    @Around(value = "execution( public * com.demo.web.core.crud.dao.BaseDao.update(..))")
    public Object aspectUpdate(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        aspectUpdate(args);
        return point.proceed(args);
    }
    @Around(value = "execution( public * com.demo.web.core.crud.dao.BaseDao.insert(..))")
    public Object aspectInsert(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        aspectCommon(args);
        return point.proceed(args);
    }
    @Around(value = "execution( public * com.demo.web.core.crud.dao.BaseDao.totalNum(..))")
    public Object aspectTotalNum(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        aspectConditon((ConditionEntity) args[0]);
        return point.proceed(args);
    }
    @Around(value = "execution( public * com.demo.web.core.crud.dao.BaseDao.delete(..))")
    public Object aspectDelete(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        aspectCommon(args);
        return point.proceed(args);
    }

    private void aspectFindEntity(FindEntity entity){
        InfoOfEntity infoOfEntity = EntityMap.getMainTable(entity.getEntityName()).getInfoOfEntity();
        entity.setEntityName(infoOfEntity.getEntityName());
        if(DataSourceType.ORACLE.equals(infoOfEntity.getConfig().getSourceType())){
            entity.setCondition(OracleParser.parseMap(entity.getCondition()));
            entity.setData(OracleParser.parseMap(entity.getData()));
        }
    }
    private void aspectConditon(ConditionEntity entity){
        InfoOfEntity infoOfEntity = EntityMap.getMainTable(entity.getMainTable()).getInfoOfEntity();
        entity.setMainTable(infoOfEntity.getEntityName());
        if(DataSourceType.ORACLE.equals(infoOfEntity.getConfig().getSourceType())){
            OracleParser.parseConditionEntity(entity);
        }
    }

    private void aspectUpdate(Object[] args){
        //1、实体名 2、PK数据
        InfoOfEntity infoOfEntity = EntityMap.getMainTable((String) args[0]).getInfoOfEntity();
        args[0] = infoOfEntity.getEntityName();
        if(DataSourceType.ORACLE.equals(infoOfEntity.getConfig().getSourceType())){
            args[0]= OracleParser.parseEntityName((String) args[0]);
            args[1] =OracleParser.parseMap((Map<String, Object>) args[1]);
            args[2] =OracleParser.parseMap((Map<String, Object>) args[2]);
        }
    }
    private void aspectCommon(Object[] args){
        //1、实体名 2、PK数据
        InfoOfEntity infoOfEntity = EntityMap.getMainTable((String) args[0]).getInfoOfEntity();
        args[0] = infoOfEntity.getEntityName();
        if(DataSourceType.ORACLE.equals(infoOfEntity.getConfig().getSourceType())){
            args[0]= OracleParser.parseEntityName((String) args[0]);
            args[1] =OracleParser.parseMap((Map<String, Object>) args[1]);
        }
    }

    private void aspectName(Object[] args){
        //1、实体名 2、PK数据
        InfoOfEntity infoOfEntity = EntityMap.getMainTable((String) args[0]).getInfoOfEntity();
        args[0] = infoOfEntity.getEntityName();
        if(DataSourceType.ORACLE.equals(infoOfEntity.getConfig().getSourceType())){
            args[0]= OracleParser.parseEntityName((String) args[0]);
        }
    }
}
