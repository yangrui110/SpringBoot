package com.yangframe.web.dao.mysql;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ZhiyuDao {
    List<Map<String,Object>> findZhiyuUser();

    List<Map<String,Object>> findProducts();

    List<Map<String,Object>> findNetValue();

    List<Map<String,Object>> findNetValueByName(@Param("name")String name);

    List<Map<String,Object>> findProFile();

    List<Map<String,Object>> findProFileByName(@Param("name")String name);

    List<Map<String,Object>> findOrder();

    List<Map<String,Object>> findOrderRecard();
}
