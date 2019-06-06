package com.demo.web.core.crud.service;

import com.demo.config.datasource.type.DataSourceType;
import com.demo.web.core.crud.centity.ConditionEntity;
import com.demo.web.core.crud.centity.FindEntity;
import com.demo.web.core.crud.dao.BaseDao;
import com.demo.web.core.crud.dao.mysql.MysqlAjaxCrudDao;
import com.demo.web.core.crud.dao.sql.SqlAjaxCrudDao;
import com.demo.web.core.xmlEntity.EntityMap;
import com.demo.web.core.xmlEntity.InfoOfEntity;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

/**
 * @autor 杨瑞
 * @date 2019/6/6 21:56
 */
@Service
public class BaseServiceImpl implements ApplicationContextAware {

    WebApplicationContext currentWebApplicationContext;

    public Object findAll(FindEntity findEntity, ConditionEntity entity){
        entity.setStart(findEntity.getStart());
        entity.setEnd(findEntity.getEnd());
        BaseDao baseDao;
        InfoOfEntity entity1 = EntityMap.tables.get(findEntity.getEntityName());
        if(DataSourceType.MYSQL.equals(entity1.getSource())){
            baseDao= currentWebApplicationContext.getBean(MysqlAjaxCrudDao.class);
            return baseDao.findAll(entity);
        }else if(DataSourceType.ORACLE.equals(entity1.getSource())){
            //currentWebApplicationContext.getBean()
        }else if(DataSourceType.SQL.equals(entity1.getSource())){
            baseDao= currentWebApplicationContext.getBean(SqlAjaxCrudDao.class);
            return baseDao.findAll(entity);
        }
        return null;
    }

    public void update(FindEntity entity){
        InfoOfEntity entity1 = EntityMap.tables.get(entity.getEntityName());
        String entityName=EntityMap.getTableName(entity.getEntityName());
        BaseDao baseDao;
        if(DataSourceType.MYSQL.equals(entity1.getSource())){
            baseDao = currentWebApplicationContext.getBean(MysqlAjaxCrudDao.class);
            baseDao.update(entityName,entity.getData() ,entity.getCondition());
        }else if(DataSourceType.ORACLE.equals(entity1.getSource())){
            //currentWebApplicationContext.getBean()
        }else if(DataSourceType.SQL.equals(entity1.getSource())){
            baseDao= currentWebApplicationContext.getBean(SqlAjaxCrudDao.class);
            baseDao.update(entityName,entity.getData(),entity.getCondition());
        }
    }
    public void insert(FindEntity entity){
        InfoOfEntity entity1 = EntityMap.tables.get(entity.getEntityName());
        String entityName=EntityMap.getTableName(entity.getEntityName());
        BaseDao baseDao;
        if(DataSourceType.MYSQL.equals(entity1.getSource())){
            baseDao = currentWebApplicationContext.getBean(MysqlAjaxCrudDao.class);
            baseDao.insert(entityName,entity.getData());
        }else if(DataSourceType.ORACLE.equals(entity1.getSource())){
            //currentWebApplicationContext.getBean()
        }else if(DataSourceType.SQL.equals(entity1.getSource())){
            baseDao= currentWebApplicationContext.getBean(SqlAjaxCrudDao.class);
            baseDao.insert(entityName,entity.getData());
        }
    }
    public void delete(FindEntity entity){
        InfoOfEntity entity1 = EntityMap.tables.get(entity.getEntityName());
        String entityName=EntityMap.getTableName(entity.getEntityName());
        BaseDao baseDao;
        if(DataSourceType.MYSQL.equals(entity1.getSource())){
            baseDao = currentWebApplicationContext.getBean(MysqlAjaxCrudDao.class);
            baseDao.delete(entityName,entity.getCondition());
        }else if(DataSourceType.ORACLE.equals(entity1.getSource())){
            //currentWebApplicationContext.getBean()
        }else if(DataSourceType.SQL.equals(entity1.getSource())){
            baseDao= currentWebApplicationContext.getBean(SqlAjaxCrudDao.class);
            baseDao.delete(entityName,entity.getCondition());
        }
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        currentWebApplicationContext= (WebApplicationContext) applicationContext;
    }
}
