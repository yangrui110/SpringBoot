package com.demo.web.core.crud.dao.oracle;

import com.demo.web.core.crud.centity.ConditionEntity;
import com.demo.web.core.crud.dao.BaseDao;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface OracleAjaxCrudDao extends BaseDao {

    Map<String,Object> findByPK(@Param("entityName") String entityName, @Param("mapData") Map<String, Object> mapData);

    int totalNum(@Param("condition") ConditionEntity entity);
    /**
     * @param entity 查询的条件
     * */
    List<Map<String,Object>> findAll(@Param("condition") ConditionEntity entity);

    /**
     * @param entityName 实体文件中定义的表名称
     * @param mapData 待插入的数据
     * */
    void insert(@Param("entityName") String entityName, @Param("data") Map<String, Object> mapData);

    /**
     * @param entityName 实体文件中定义的表名称
     * @param mapData 待更新的数据集
     * @param cons 主键条件
     * */
    void update(@Param("entityName") String entityName, @Param("data") Map<String, Object> mapData, @Param("cons")Map<String,Object> cons);
    /**
     * @param entityName 实体文件中定义的表名称
     * @param  cons 主键
     * */
    void delete(@Param("entityName") String entityName, @Param("cons")Map<String,Object> cons);
}
