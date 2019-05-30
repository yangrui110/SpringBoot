package com.demo.web.core.crud.sql.service.impl;

import com.demo.config.advice.BaseException;
import com.demo.web.core.crud.centity.CEntity;
import com.demo.web.core.crud.centity.COrderBy;
import com.demo.web.core.crud.centity.ConditionEntity;
import com.demo.web.core.crud.sql.dao.SqlAjaxCrudDao;
import com.demo.web.core.crud.sql.service.SqlAjaxCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/5/18 17:28
 */
@Service
public class SqlAjaxCrudServiceImpl implements SqlAjaxCrudService {

    @Autowired
    private SqlAjaxCrudDao sqlAjaxCrudDao;

    @Override
    public List findAll(String entityName, List<CEntity>  condition, int start, int end, List<COrderBy> orderBy, ConditionEntity entity) {
        //1.处理查询条件，让每个表名都带上[]
        entity.setStart(start);
        entity.setEnd(start+end);
        if(entity.getOrderBy()==null)
            throw new BaseException(304, "必须传入排序字段");
        return sqlAjaxCrudDao.findAll(entity);
    }

    @Override
    public void insert(String entityName, Map<String, Object> data) {
        sqlAjaxCrudDao.insert(entityName, data);
    }

    @Override
    public void update(String entityName, Map<String, Object> data, List<CEntity> whereCondition) {
        whereCondition=whereCondition==null?new ArrayList<>():whereCondition;
        sqlAjaxCrudDao.update(entityName, data, whereCondition);
    }

    @Override
    public void delete(String entityName, List<CEntity> whereCondition) {
        whereCondition=whereCondition==null?new ArrayList<>():whereCondition;
        sqlAjaxCrudDao.delete(entityName, whereCondition);
    }

}
