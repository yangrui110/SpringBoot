package com.demo.web.core.crud.mysql.service.impl;

import com.demo.web.core.crud.centity.CEntity;
import com.demo.web.core.crud.centity.COrderBy;
import com.demo.web.core.crud.centity.ConditionEntity;
import com.demo.web.core.crud.mysql.dao.MysqlAjaxCrudDao;
import com.demo.web.core.crud.mysql.service.MysqlAjaxCrudService;
import com.demo.web.core.xmlEntity.EntityMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/5/18 12:59
 */
@Service
public class MysqlAjaxCrudServiceImpl implements MysqlAjaxCrudService {

    @Autowired
    private MysqlAjaxCrudDao ajaxCrudDao;

    @Override
    public List findAll(String entityName, List<CEntity> condition, Integer start, Integer end, COrderBy orderBy, ConditionEntity entity){
        entity.setStart(start);
        entity.setEnd(end);
        return ajaxCrudDao.findAll(entity);
    }

    @Override
    public void insert(String entityName, Map<String, Object> data) {
        ajaxCrudDao.insert(entityName, data);
    }

    @Override
    public void update(String entityName, Map<String, Object> data,List<CEntity> whereCondition) {
        whereCondition=whereCondition==null?new ArrayList<>():whereCondition;
        ajaxCrudDao.update(entityName, data,whereCondition);
    }

    @Override
    public void delete(String entityName, List<CEntity> whereCondition) {
        whereCondition=whereCondition==null?new ArrayList<>():whereCondition;
        ajaxCrudDao.delete(entityName,  whereCondition);
    }

}
