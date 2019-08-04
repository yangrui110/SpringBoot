package com.demo.web.core.crud.service;

import com.demo.config.datasource.type.DataSourceType;
import com.demo.web.core.crud.centity.ConditionEntity;
import com.demo.web.core.crud.centity.FindEntity;
import com.demo.web.core.crud.dao.BaseDao;
import com.demo.web.core.xmlEntity.EntityMap;
import com.demo.web.core.xmlEntity.InfoOfEntity;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/6/6 21:56
 * @describetion 通用的增删改查service类
 */
@Service
public class BaseServiceImpl implements ApplicationContextAware {

    WebApplicationContext currentWebApplicationContext;

    public List findAll(FindEntity findEntity, ConditionEntity entity){
        InfoOfEntity entity1 = EntityMap.getAndJugeNotEmpty(findEntity.getEntityName());
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(entity1.getConfig().getDaoBaseClassName());
        List<Map<String, Object>> all = baseDao.findAll(entity);
        all.forEach((k)->{
            if(k.containsKey("RowNumber"))
                k.remove("RowNumber");
        });
        return all;
    }

    public List<Map<String, Object>> findAllNoPage(FindEntity findEntity, ConditionEntity entity){
        InfoOfEntity entity1 = EntityMap.getAndJugeNotEmpty(findEntity.getEntityName());
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(entity1.getConfig().getDaoBaseClassName());
        List<Map<String, Object>> all = baseDao.findAllNoPage(entity);
        return all;
    }

    public void update(FindEntity entity){
        InfoOfEntity entity1 = EntityMap.getAndJugeNotEmpty(entity.getEntityName());
        String entityName=EntityMap.getTableName(entity.getEntityName());
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(entity1.getConfig().getDaoBaseClassName());
        if(DataSourceType.ORACLE.equals(entity1.getConfig().getSourceType())){
            EntityMap.makeOracleData(entity);
            baseDao.update("\""+entityName+"\"",entity.getData() ,entity.getCons());
        }else baseDao.update(entityName,entity.getData() ,entity.getCons());
    }
    public void insert(FindEntity entity){
        InfoOfEntity entity1 = EntityMap.getAndJugeNotEmpty(entity.getEntityName());
        String entityName=EntityMap.getTableName(entity.getEntityName());
        EntityMap.yanzhengPKIsEmpty(entity); //验证主键的值是否已经传入
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(entity1.getConfig().getDaoBaseClassName());
        if(DataSourceType.ORACLE.equals(entity1.getConfig().getSourceType())){
            EntityMap.makeOracleData(entity);
            baseDao.insert("\""+entityName+"\"",entity.getData());
        }else baseDao.insert(entityName,entity.getData());
    }
    public void delete(FindEntity entity){
        InfoOfEntity entity1 = EntityMap.getAndJugeNotEmpty(entity.getEntityName());
        String entityName=EntityMap.getTableName(entity.getEntityName());
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(entity1.getConfig().getDaoBaseClassName());
        if(DataSourceType.ORACLE.equals(entity1.getConfig().getSourceType())){
            EntityMap.makeOracleData(entity);
            baseDao.delete("\""+entityName+"\"",entity.getCons());
        }else baseDao.delete(entityName,entity.getCons());
    }

    public int totalNum(FindEntity findEntity,ConditionEntity entity){
        InfoOfEntity entity1 = EntityMap.getAndJugeNotEmpty(findEntity.getEntityName());
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(entity1.getConfig().getDaoBaseClassName());
        return baseDao.totalNum(entity);
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        currentWebApplicationContext= (WebApplicationContext) applicationContext;
    }
}
