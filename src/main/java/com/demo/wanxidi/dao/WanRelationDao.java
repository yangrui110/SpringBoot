package com.demo.wanxidi.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/7/8 0:13
 */
public interface WanRelationDao {

    List<Map> findRelation(@Param("condition")Map<String,Object> condition, @Param("start") int start, @Param("end") int end);

    int countRelation(@Param("condition") Map<String,Object> condition);
}
