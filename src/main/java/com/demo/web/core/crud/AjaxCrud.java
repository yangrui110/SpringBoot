package com.demo.web.core.crud;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/5/14 19:57
 * 通用的增删改查操作
 */
@Service
public class AjaxCrud {

    /**
     * 通过预设置的PK查找一个数据
     * @param entityName 预先定义的实体名
     * */
    public Map<String,Object> findByPk(String entityName){
        return null;
    }
    /**
     * 查找所有的数据
     * @param entityName 预先定义的实体名
     * @param mapData 传入的查询数据
     * @param start 开始页数
     * @param offset 偏移量
     * */
    public List<Map<String,Object>> findAll(String entityName,Map<String,Object> mapData,int start,int offset){
        return null;
    }
    /**
     * 更新数据,主键是在xml实体中预先定义的PK
     * @param entityName 实体名
     * */
    public void update(String entityName){

    }

    /**
     * 插入一条数据，判断主键是否为空，如果为空，则会调用getUUID方法进行设置
     * @param entityName 实体名
     * */
    public void insert(String entityName){

    }
    /**
     * 删除一条数据，通过预先设置的PK
     * @param entityName 预先定义的实体名
     * */
    public void delete(String entityName){

    }
}
