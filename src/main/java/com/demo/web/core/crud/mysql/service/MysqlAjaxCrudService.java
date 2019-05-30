package com.demo.web.core.crud.mysql.service;

import com.demo.web.core.crud.centity.CEntity;
import com.demo.web.core.crud.centity.COrderBy;
import com.demo.web.core.crud.centity.ConditionEntity;

import java.util.List;
import java.util.Map;

public interface MysqlAjaxCrudService {

    List findAll(String entityName, List<CEntity> condition, Integer start, Integer end, COrderBy orderBy, ConditionEntity entity);

    void insert(String entityName, Map<String,Object> data);

    void update(String entityName,Map<String,Object> data,List<CEntity> whereCondition);

    void delete(String entityName,List<CEntity> whereCondition);

}
