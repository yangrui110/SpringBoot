package com.yangframe.web.core.crud.service;

import com.yangframe.config.advice.BaseException;
import com.yangframe.config.advice.ExceptionEntity;
import com.yangframe.config.datasource.type.DataSourceType;
import com.yangframe.web.core.crud.centity.ConditionEntity;
import com.yangframe.web.core.crud.centity.FindEntity;
import com.yangframe.web.core.crud.centity.Operator;
import com.yangframe.web.core.crud.dao.BaseDao;
import com.yangframe.web.core.util.MakeConditionUtil;
import com.yangframe.web.core.xmlEntity.ColumnProperty;
import com.yangframe.web.core.xmlEntity.EntityMap;
import com.yangframe.web.core.xmlEntity.InfoOfEntity;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;

import java.util.*;

/**
 * @autor 杨瑞
 * @date 2019/6/6 21:56
 * @describetion 通用的增删改查service类,调用dao层的增删改查方法时，统一传入的entityName是entityAlias
 *
 */
@Service
public class BaseServiceImpl implements ApplicationContextAware {

    WebApplicationContext currentWebApplicationContext;

    public Map<String,Object> findByPK(String entityName,Map<String,Object> pkDatas){
        InfoOfEntity entity1 = EntityMap.getAndJugeNotEmpty(entityName);
        EntityMap.yanzhengPKIsEmpty(entityName,pkDatas);
        Map<String, ColumnProperty> primaryKey = EntityMap.getPrimaryKey(entityName);
        Map<String,Object> pks=new HashMap<>();
        primaryKey.forEach((k,v)->{
            if(pkDatas.get(k)!=null){
                pks.put(v.getColumn(), pkDatas.get(k));
            }
        });
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(entity1.getConfig().getDaoBaseClassName());
        Map<String, Object> pk = baseDao.findByPK(entityName, pks);
        return pk;
    }

    public List findAll(FindEntity findEntity, ConditionEntity entity){
        InfoOfEntity entity1 = EntityMap.getAndJugeNotEmpty(findEntity.getEntityName());
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(entity1.getConfig().getDaoBaseClassName());
        entity.setMainTable(findEntity.getEntityName());
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
        entity.setMainTable(findEntity.getEntityName());
        List<Map<String, Object>> all = baseDao.findAllNoPage(entity);
        all.forEach((k)->{
            if(k.containsKey("RowNumber"))
                k.remove("RowNumber");
        });
        return all;
    }

    public void update(FindEntity entity){
        InfoOfEntity entity1 = EntityMap.getAndJugeNotEmpty(entity.getEntityName());
        Map<String, ColumnProperty> columns = EntityMap.getAllColumns(entity.getEntityName());
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(entity1.getConfig().getDaoBaseClassName());
        Map parseData = new HashMap();
        entity.getData().forEach((k,v)->{
            parseData.put(columns.get(k).getColumn(), v);
        });
        baseDao.update(entity.getEntityName(),parseData,entity.getCondition());
    }
    public void insert(FindEntity entity){
        InfoOfEntity entity1 = EntityMap.getAndJugeNotEmpty(entity.getEntityName());
        Map<String, ColumnProperty> columns = EntityMap.getAllColumns(entity.getEntityName());
        Map<String, Object> pkData = EntityMap.yanzhengPKIsEmpty(entity.getEntityName(),entity.getData());//验证主键的值是否已经传入
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(entity1.getConfig().getDaoBaseClassName());
        Map<String,Object> record = findByPK(entity.getEntityName(),pkData);
        if(record!=null)
            throw new BaseException(504, "主键已经存在于数据库中");
        Map parseData = new HashMap();
        entity.getData().forEach((k,v)->{
            parseData.put(columns.get(k).getColumn(), v);
        });
        baseDao.insert(entity.getEntityName(),parseData);
    }
    public void insertOrUpdate(FindEntity entity){
        InfoOfEntity entity1 = EntityMap.getAndJugeNotEmpty(entity.getEntityName());
        Map<String, ColumnProperty> columns = EntityMap.getAllColumns(entity.getEntityName());
        Map<String, Object> pkData = EntityMap.yanzhengPKIsEmpty(entity.getEntityName(),entity.getData());//验证主键的值是否已经传入
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(entity1.getConfig().getDaoBaseClassName());
        Map<String,Object> record = findByPK(entity.getEntityName(),pkData);
        Map parseData = new HashMap();
        entity.getData().forEach((k,v)->{
            parseData.put(columns.get(k).getColumn(), v);
        });
        if(record==null){
            baseDao.insert(entity.getEntityName(),parseData);
        }else{
            baseDao.update(entity.getEntityName(),parseData,entity.getCondition());
        }
    }
    public void delete(String entityName,Map<String,Object> mapDatas){
        InfoOfEntity entity1 = EntityMap.getAndJugeNotEmpty(entityName);
        Map<String, ColumnProperty> primaryKey = EntityMap.getPrimaryKey(entityName);
        Map result =new HashMap<>();
        mapDatas.forEach((k,v)->{
            if(primaryKey.containsKey(k)){
                result.put(primaryKey.get(k).getColumn(), v);
            }
        });
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(entity1.getConfig().getDaoBaseClassName());
        if(result.size()<=0){
            throw new BaseException(505, "删除条件不能为空");
        }
        baseDao.delete(entityName,result);
    }

    public int totalNum(FindEntity findEntity,ConditionEntity entity){
        InfoOfEntity entity1 = EntityMap.getAndJugeNotEmpty(findEntity.getEntityName());
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(entity1.getConfig().getDaoBaseClassName());
        entity.setMainTable(findEntity.getEntityName());
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
        List<List<Object>> result=new ArrayList<>();
        List<String> aliasKeys=new ArrayList<>();  //键值
        List<String> keys=new ArrayList<>();  //键值
        allColumns.forEach((key,value)->{
            aliasKeys.add(key);
            keys.add(value.getColumn());
        });
        for(Map<String,Object> v :mapDatas){
            List<Object> one =new ArrayList<>();
            for(String key : aliasKeys){
                one.add( v.get(key));
            }
            result.add(one);
        }

        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(infoOfEntity.getConfig().getDaoBaseClassName());

        baseDao.insertAll(entityName,keys, result);
    }

    public void delSelect(String entityName,List<Map<String,Object>> mapDatas){
        InfoOfEntity infoOfEntity = EntityMap.getAndJugeNotEmpty(entityName);
        Map<String, ColumnProperty> columns = EntityMap.getAllColumns(entityName);
        if(infoOfEntity.isView()){
            infoOfEntity = EntityMap.getMainTable(entityName).getInfoOfEntity();
        }
        Map<String, ColumnProperty> primaryKey = EntityMap.getPrimaryKey(entityName);
        //剔除掉不是主键中的列
        List<Map<String,Object>> ls= new ArrayList<>();
        for(Map<String,Object> map:mapDatas){
            Map one = new HashMap();
            primaryKey.forEach((k,v)->{
                if(map.containsKey(k)){
                    one.put(k, map.get(k));
                }
            });
            if(one.size()>0){
                ls.add(one);
            }
        }
        Map<String, List<Object>> result = getResult(infoOfEntity, ls);
        Map aliasResult =new HashMap();
        result.forEach((k,v)->{
            aliasResult.put(columns.get(k).getColumn(), v);
        });
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(infoOfEntity.getConfig().getDaoBaseClassName());

        if(aliasResult.size()<=0)
            throw new BaseException(504, "删除条件不能为空");
        baseDao.deleteAll(entityName, aliasResult);
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
        Map<String,List<Object>> aliasResult = new HashMap<>();
        Map<String,ColumnProperty> aliasColumns = new HashMap<>();
        result.forEach((k,v)->{
            aliasResult.put(columns.get(k).getColumn(), v);
            aliasColumns.put(k, columns.get(k));
        });
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
                    if(mapOne.get(key)!=null) {
                        builder.append(" WHEN ").append(makePkString(aliasColumns, mapOne, infoOfEntity)).append(" THEN ").append("'").append(mapOne.get(key)).append("'");
                    }
                }
            }
            if(!StringUtils.isEmpty(builder.toString())){
                if(builder.length()>0) {
                    builder.insert(0, " CASE ").append(" END ");
                    condition.put(key, builder.toString());
                }else condition.put(key, null);
            }
        });
        Map ol=new HashMap();
        condition.forEach((k,v)->{
            ol.put(columns.get(k).getColumn(), v);
        });
        baseDao.updateAll(entityName, aliasResult,ol);

    }

    private List<Map<String,Object>> getAllRecords(String entityName,Map pks,List<Map<String,Object>> mapDatas){
        InfoOfEntity infoOfEntity = EntityMap.getAndJugeNotEmpty(entityName);
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
        BaseDao baseDao= (BaseDao) currentWebApplicationContext.getBean(infoOfEntity.getConfig().getDaoBaseClassName());
        ConditionEntity conditionEntity = EntityMap.readEntityLabel(entityName, MakeConditionUtil.parseLast(cons), EntityMap.makeDefaulOrderBy(entityName, null));
        conditionEntity.setMainTable(entityName);
        List<Map<String, Object>> allRecodes =baseDao.findAllNoPage(conditionEntity);
        return allRecodes;
    }
    /**
     * @param pks 主键的列名作为key值
     * @param values 每个更新记录的值
     * */
    private String makePkString(Map<String,ColumnProperty> pks,Map<String,Object> values,InfoOfEntity infoOfEntity){
        StringBuilder builder=new StringBuilder();
        StringBuilder quote = new StringBuilder();
        if(DataSourceType.ORACLE.equals(infoOfEntity.getConfig().getSourceType())){
            quote.append("\"");
        }
        pks.forEach((k,v)->{
            builder.append(" ").append(quote).append(v.getColumn()).append(quote).append(" = '").append(values.get(k)).append("' and ");
        });
        String result = builder.substring(0, builder.length() - 4);
        return result;
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        currentWebApplicationContext= (WebApplicationContext) applicationContext;
    }
}
