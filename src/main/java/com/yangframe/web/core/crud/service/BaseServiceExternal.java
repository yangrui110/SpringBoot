package com.yangframe.web.core.crud.service;

import com.yangframe.config.advice.BaseException;
import com.yangframe.config.advice.ResultEntity;
import com.yangframe.config.advice.ResultEnum;
import com.yangframe.config.util.MapUtil;
import com.yangframe.web.core.crud.centity.ConditionEntity;
import com.yangframe.web.core.crud.centity.FindEntity;
import com.yangframe.web.core.crud.centity.Operator;
import com.yangframe.web.core.util.MakeConditionUtil;
import com.yangframe.web.core.xmlEntity.ColumnProperty;
import com.yangframe.web.core.xmlEntity.EntityMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @autor 杨瑞
 * @date 2019/9/23 16:33
 * 对于baseService的补充方法
 */
@Service
public class BaseServiceExternal {

    @Autowired
    private BaseServiceImpl baseService;

    /**
     * 存储，包括更新和插入
     * */
    public void storeAll(String entityName, List<Map<String,Object>> mapData){
        //首先获取当前实体的主键
        Map<String, ColumnProperty> primaryKey = EntityMap.getPrimaryKey(entityName);
        //根据传递进来的参数，给每个主键赋值
        Map<String,Object> result =new HashMap<>();
        primaryKey.forEach((k,v)->{
            result.put(k, null);
        });
        setDataForAlias(result, mapData);
        List<Map<String, Object>> allNoPage = baseService.findAllNoPage(FindEntity.newInstance().makeEntityName(entityName).makeData(MakeConditionUtil.makeCondition(result, Operator.IN)), new ConditionEntity());
        //收集到需要更新的数据
        List<Map<String, Object>> updateData = collectUpdateData(mapData, allNoPage,primaryKey);
        //收集到需要插入的数据
        if(updateData.size()>0)
            baseService.updateAll(entityName, updateData);
        List<Map<String, Object>> insertData = collectInsertData(mapData, allNoPage,primaryKey);
        if(insertData.size()>0)
            baseService.insertAll(entityName, insertData);
    }
    /**
     * 更新一对多的关联表
     * */
    public void updateManyToManyTable(String entityName,List<Map<String,Object>> data,List<String> mainColumns){
        //首先查找到这些记录信息
        Map<String, ColumnProperty> primaryKey = EntityMap.getPrimaryKey(entityName);
        Map<String, Object> objectMap = data.get(0);
        Map<String,Object> pks = new HashMap<>();
        for(String k : mainColumns){
            if(objectMap.get(k)==null)
                throw new BaseException(509,"列"+k+"不能为空");
            pks.put(k, objectMap.get(k));
        }
        List<Map<String, Object>> mapList = baseService.findAllNoPage(FindEntity.newInstance().makeEntityName(entityName).makeData(MakeConditionUtil.makeCondition(pks)), new ConditionEntity());
        //收集到不在其中的行
        List<Map<String, Object>> collectInsertData = collectInsertData(mapList, data, primaryKey);
        //进行删除
        if(collectInsertData.size()>0)
        baseService.delSelect(entityName, collectInsertData);
        //然后更新存储
        storeAll(entityName, data);
    }

    /**
     * 携带关联表查询
     * */
    public List<Map<String, Object>> referFindAll(FindEntity findEntity){
        Map<String, ColumnProperty> source = EntityMap.getPrimaryKey(findEntity.getEntityName());
        Map<String, ColumnProperty> referAllColumns = EntityMap.getAllColumns(findEntity.getEntityName());
        List<Map<String, Object>> allNoPage = baseService.findAll(findEntity, new ConditionEntity());
        //获取到两个表之间的关联
        Map<String, Object> alias = getRefreColumn(source, referAllColumns);
        //给关联列设置查到的值
        setDataForAlias(alias,allNoPage);
        //根据上面的条件，从关联表查询数据
        Map<String, Object> objectMap = MakeConditionUtil.makeCondition(alias, Operator.IN);
        List<Map<String, Object>> serviceAllNoPage = baseService.findAllNoPage(FindEntity.newInstance().makeEntityName(findEntity.getReferTableName()).makeData(objectMap), new ConditionEntity());
        //将查询到的关联表结果，赋值到主表的查询结果中去
        parseReferDataToMain(allNoPage,source,serviceAllNoPage,alias);

        return allNoPage;
    }

    List<Map<String, Object>> collectUpdateData(List<Map<String, Object>> allData,List<Map<String, Object>> selectData,Map<String,ColumnProperty> primaryKey){
        List<Map<String, Object>> collect = allData.stream().filter((k) -> {
            boolean jude = false;
            for (Map<String, Object> mapOne : selectData) {
                int count = 0;
                Iterator<String> iterator = primaryKey.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    if (k.get(key).equals(mapOne.get(key))) {
                        count++;
                    }
                }
                if (count == primaryKey.size()) {
                    jude = true;
                }
            }
            return jude;
        }).collect(Collectors.toList());

        return collect;
    }
    List<Map<String, Object>> collectInsertData(List<Map<String, Object>> allData,List<Map<String, Object>> selectData,Map primaryKey){
        List<Map<String, Object>> collect = allData.stream().filter((k) -> {
            boolean jude = false;
            for (Map<String, Object> mapOne : selectData) {
                int count = 0;
                Iterator<String> iterator = primaryKey.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    if (k.get(key).equals(mapOne.get(key))) {
                        count++;
                    }
                }
                if (count == primaryKey.size()) {
                    jude = true;
                }
            }
            return !jude;
        }).collect(Collectors.toList());
        return collect;
    }
    void setDataForAlias(Map<String,Object> alias,List<Map<String,Object>> allNoPage){
        alias.forEach((k,v)->{
            List<Object> ls =new ArrayList<>();
            for(Map<String,Object> one :allNoPage){
                //开始处理
                if(one.containsKey(k))
                    ls.add(one.get(k));
            }
            if(ls.size()>0)
                alias.put(k, ls);
        });

    }

    private Map<String,Object> getRefreColumn(Map<String,ColumnProperty> source,Map<String,ColumnProperty> referAllColumns){
        Map<String,Object> alias = new HashMap<>();
        source.forEach((k,v)->{
            referAllColumns.forEach((m,n)->{
                if(n.getColumn().equals(v.getColumn())&&n.getTableName().equals(v.getTableName())){
                    alias.put(n.getAlias(), null);
                }
            });
        });
        return alias;
    }


    void parseReferDataToMain(List<Map<String,Object>> allNoPage,Map<String,ColumnProperty> mainPrimaryKey,List<Map<String,Object>> referData,Map<String,Object> referAlias){
        for(Map<String,Object> one:allNoPage){
            StringBuilder builder = new StringBuilder();
            mainPrimaryKey.forEach((m,n)->{
                builder.append(one.get(m)).append("_");
            });
            String key = builder.substring(0, builder.length()-1);
            List<Object> ls =new ArrayList<>();
            for(Map<String,Object> service : referData){

                StringBuilder builders = new StringBuilder();
                referAlias.forEach((m,n)->{
                    builders.append(service.get(m)).append("_");
                });
                String referKey = builders.substring(0, builders.length()-1);
                if(key.equals(referKey)){
                    ls.add(service);
                }
            }
            one.put("referAll", ls);
        }

    }

}
