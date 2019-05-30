package com.demo.web.dao.mysql;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/5/11 15:15
 */
public interface UserDao {
    List<Map> finds();

    List findAll(@Param("columns") String columns, @Param("entityName") String entityName,@Param("conditions") Map<String, Object> condition);
}
