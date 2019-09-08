package com.demo.web.core.crud.service;

import com.demo.config.advice.BaseException;
import com.demo.config.advice.ExceptionEntity;
import com.demo.config.datasource.type.DataSourceType;
import com.demo.web.core.crud.centity.ConditionEntity;
import com.demo.web.core.crud.centity.FindEntity;
import com.demo.web.core.crud.centity.MainTableInfo;
import com.demo.web.core.crud.centity.Operator;
import com.demo.web.core.crud.dao.BaseDao;
import com.demo.web.core.util.MakeConditionUtil;
import com.demo.web.core.xmlEntity.ColumnProperty;
import com.demo.web.core.xmlEntity.EntityMap;
import com.demo.web.core.xmlEntity.InfoOfEntity;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/6/6 21:56
 * @describetion 通用的增删改查service类
 */
@Service
public class BaseServiceImpl implements ApplicationContextAware {

    WebApplicationContext currentWebApplicationContext;

    public List findAll(FindEntity findEntity, ConditionEntity entity){
        InfoOfEntity entity1 = EntityMap.getAndJugeNotEmpty(findEntity.getEntityName());
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(entity1.getConfig().getDaoBaseClassName());
        List<Map<String, Object>> all = baseDao.findAll(entity);
        all.forEach((k)->{
            if(k.containsKey("RowNumber"))
                k.remove("RowNumber");
        });
        return all;
    }

    public List<Map<String, Object>> findAllNoPage(FindEntity findEntity, ConditionEntity entity){
        InfoOfEntity entity1 = EntityMap.getAndJugeNotEmpty(findEntity.getEntityName());
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(entity1.getConfig().getDaoBaseClassName());
        List<Map<String, Object>> all = baseDao.findAllNoPage(entity);
        return all;
    }

    public void update(FindEntity entity){
        InfoOfEntity entity1 = EntityMap.getAndJugeNotEmpty(entity.getEntityName());
        String entityName=EntityMap.getTableName(entity.getEntityName());
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(entity1.getConfig().getDaoBaseClassName());
        if(DataSourceType.ORACLE.equals(entity1.getConfig().getSourceType())){
            EntityMap.makeOracleData(entity);
            baseDao.update("\""+entityName+"\"",entity.getData() ,entity.getCons());
        }else baseDao.update(entityName,entity.getData() ,entity.getCons());
    }
    public void insert(FindEntity entity){
        InfoOfEntity entity1 = EntityMap.getAndJugeNotEmpty(entity.getEntityName());
        String entityName=EntityMap.getTableName(entity.getEntityName());
        EntityMap.yanzhengPKIsEmpty(entity); //验证主键的值是否已经传入
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(entity1.getConfig().getDaoBaseClassName());
        if(DataSourceType.ORACLE.equals(entity1.getConfig().getSourceType())){
            EntityMap.makeOracleData(entity);
            baseDao.insert("\""+entityName+"\"",entity.getData());
        }else baseDao.insert(entityName,entity.getData());
    }
    public void delete(FindEntity entity){
        InfoOfEntity entity1 = EntityMap.getAndJugeNotEmpty(entity.getEntityName());
        String entityName=EntityMap.getTableName(entity.getEntityName());
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(entity1.getConfig().getDaoBaseClassName());
        if(DataSourceType.ORACLE.equals(entity1.getConfig().getSourceType())){
            EntityMap.makeOracleData(entity);
            baseDao.delete("\""+entityName+"\"",entity.getCons());
        }else baseDao.delete(entityName,entity.getCons());
    }

    public int totalNum(FindEntity findEntity,ConditionEntity entity){
        InfoOfEntity entity1 = EntityMap.getAndJugeNotEmpty(findEntity.getEntityName());
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(entity1.getConfig().getDaoBaseClassName());
        return baseDao.totalNum(entity);
    }

    public void insertAll(String entityName,List<Map<String,Object>> mapDatas){
        //根据entityName，将数据转换为完整的列集合
        InfoOfEntity infoOfEntity = EntityMap.getAndJugeNotEmpty(entityName);
        if(infoOfEntity.isView()){
            throw new BaseException(new ExceptionEntity(506,"视图表不允许增改操作"));
        }
        Map<String, ColumnProperty> allColumns = EntityMap.getAllColumns(entityName);
        //将条件中的列转换为满列格式,保持数据格式的统一性
        List<Map<String,Object>> result=new ArrayList<>();
        mapDatas.forEach((v)->{
            Map<String,Object> one=new HashMap<>();
            allColumns.forEach((key,value)->{
                one.put(key, v.get(key));
            });
            result.add(one);
        });

        List<String> keys=new ArrayList<>();  //键值
        allColumns.forEach((key,value)->{
            keys.add(key);
        });

        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(infoOfEntity.getConfig().getDaoBaseClassName());

        baseDao.insertAll(infoOfEntity.getEntityName(),keys, result);
    }

    public void delSelect(String entityName,List<Map<String,Object>> mapDatas){
        InfoOfEntity infoOfEntity = EntityMap.getAndJugeNotEmpty(entityName);
        if(infoOfEntity.isView()){
            infoOfEntity = EntityMap.getMainTable(entityName).getInfoOfEntity();
        }
        Map<String, List<Object>> result = getResult(infoOfEntity, mapDatas);
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(infoOfEntity.getConfig().getDaoBaseClassName());

        baseDao.deleteAll(infoOfEntity.getEntityName(), result);
        //baseDao.(entityName,keys, result);
    }

    //获取主键对应的值集合
    private Map<String,List<Object>> getResult(InfoOfEntity infoOfEntity,List<Map<String,Object>> mapDatas){
        Map<String, ColumnProperty> pks = EntityMap.getPrimaryKey(infoOfEntity.getEntityAlias());
        //构造数据格式为：Map<String,List<Object>>格式，存储key和value的对应关系
        Map<String,List<Object>> result=new HashMap<>();

        pks.forEach((k,v)->{
            result.put(k, new ArrayList<>());
        });
        mapDatas.forEach((map)->{
            pks.forEach((key,value)->{
                result.get(key).add(map.get(key));
            });
        });
        return result;
    }
    public void updateAll(String entityName,List<Map<String,Object>> mapDatas){
        InfoOfEntity infoOfEntity = EntityMap.getAndJugeNotEmpty(entityName);
        if(infoOfEntity.isView()){
            throw new BaseException(new ExceptionEntity(506,"视图表不允许增改操作"));
        }
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(infoOfEntity.getConfig().getDaoBaseClassName());

        //获取所有的键
        Map<String, ColumnProperty> columns = EntityMap.getAllColumns(entityName);
        //筛选出主键
        Map<String, List<Object>> result = getResult(infoOfEntity, mapDatas);
        List<Object> pks =new ArrayList<>();
        result.forEach((k,v)->{
            pks.add(k);
        });

        //将待更新的列生成一个Map集合
        Map<String,String> condition=new HashMap<>();
        columns.forEach((k,v)->{
            if(!result.containsKey(k)){
                condition.put(k, null);
            }
        });
        List<Map<String,Object>> allRecodes=getAllRecords(entityName,result,mapDatas);

        //遍历所有的记录集，补全更新的条件
        for(Map<String,Object> outer:allRecodes){
            for(Map<String,Object> inner:mapDatas){
                int count=0;
                for(Object os:pks){
                    if(outer.get(os).equals(inner.get(os))){
                        count++;
                    }
                }
                if(count==pks.size()){
                    //表示是相同的一条记录,将inner的内部值存入ouer中
                    outer.forEach((ks,kv)->{
                        if(!inner.containsKey(ks))
                            inner.put(ks, kv);
                    });
                }
            }
        }
        //遍历condition,给每个key都加上待更新的值
        condition.forEach((key,value)->{
            StringBuilder builder=new StringBuilder();
            for(Map<String,Object> mapOne:mapDatas) {
                if (mapOne.containsKey(key)) {
                    builder.append(" WHEN ").append(makePkString(result, mapOne)).append(" THEN ").append("'").append(mapOne.get(key)).append("'");
                }
            }
            if(!StringUtils.isEmpty(builder.toString())){
                builder.insert(0, " CASE ").append(" END ");
                condition.put(key, builder.toString());
            }
        });

        baseDao.updateAll(infoOfEntity.getEntityName(), result,condition);

    }

    private List<Map<String,Object>> getAllRecords(String entityName,Map pks,List<Map<String,Object>> mapDatas){

        //先查出所有的记录集
        List<Map<String,Object>> cons=new ArrayList<>();
        pks.forEach((key,value)->{
            List ls=new ArrayList();
            mapDatas.forEach((mapOne)->{
                if(mapOne.containsKey(key))
                    ls.add(mapOne.get(key));
            });
            if (ls.size()<0){
                throw new BaseException(506, "为传入完整的主键："+key);
            }
            cons.add(MakeConditionUtil.parseOne((String) key, ls, Operator.IN));
        });
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(entityName);
        ConditionEntity conditionEntity = EntityMap.readEntityLabel(entityName, MakeConditionUtil.parseLast(cons), EntityMap.makeDefaulOrderBy(entityName, null));
        List<Map<String, Object>> allRecodes =baseDao.findAllNoPage(conditionEntity);
        return allRecodes;
    }
    /**
     * @param pks 主键的列名作为key值
     * @param values 每个更新记录的值
     * */
    private String makePkString(Map pks,Map<String,Object> values){
        StringBuilder builder=new StringBuilder();
        pks.forEach((k,v)->{
            builder.append(" ").append(k).append(" = '").append(values.get(k)).append("' and ");
        });
        String result = builder.substring(0, builder.length() - 4);
        return result;
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        currentWebApplicationContext= (WebApplicationContext) applicationContext;
    }
}
