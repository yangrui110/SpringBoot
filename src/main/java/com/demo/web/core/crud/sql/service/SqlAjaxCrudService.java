package com.demo.web.core.crud.sql.service;

import com.demo.web.core.crud.centity.CEntity;
import com.demo.web.core.crud.centity.COrderBy;
import com.demo.web.core.crud.centity.ConditionEntity;
import org.dom4j.DocumentException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/5/18 17:27
 */
public interface SqlAjaxCrudService {

    /**
     * @param entityName 传入的实体名称，对应tableAlias属性
     * @param condition 传入的查询条件
     * @param start 开始行数
     * @param end 从start偏移的行数
     * @param orderBy 排序字段，在sql查询中是必传的参数
     * @param entity 在AOP拦截中，将所有的查询条件全部都放置在该参数中
     * */
    List findAll(String entityName, List<CEntity> condition, int start, int end, List<COrderBy> orderBy, ConditionEntity entity);

    void insert(String entityName,Map<String,Object> data);

    void update(String entityName,Map<String,Object> data,List<CEntity> whereCondition);

    void delete(String entityName,List<CEntity> whereCondition);
}
