package com.yangframe.web.core.aspect;

import com.yangframe.config.datasource.type.DataSourceType;
import com.yangframe.web.core.crud.centity.ConditionEntity;
import com.yangframe.web.core.crud.centity.FindEntity;
import com.yangframe.web.core.crud.centity.MainTableInfo;
import com.yangframe.web.core.util.OracleParser;
import com.yangframe.web.core.xmlEntity.EntityMap;
import com.yangframe.web.core.xmlEntity.InfoOfEntity;
import com.yangframe.web.util.file.FileUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.dom4j.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @see com.yangframe.web.core.crud.dao.BaseDao
 * 拦截BaseDao类，当执行其中的方法时，就会被调用
 * */
@Component
@Aspect
public class OracleAspect {

    @Around(value = "execution( public * com.yangframe.web.core.crud.dao.BaseDao.findByPK(..))")
    public Object aspectPoint(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        aspectCommon(args);
        return parseResult(point.proceed(args));
    }


    @Around(value = "execution( public * com.yangframe.web.core.crud.dao.BaseDao.findAll*(..))")
    public Object aspectFindAll(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        aspectConditon((ConditionEntity) args[0]);
        return parseResult(point.proceed(args));
    }

    @Around(value = "execution( public * com.yangframe.web.core.crud.dao.BaseDao.update(..))")
    public Object aspectUpdate(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        aspectUpdate(args);
        return parseResult(point.proceed(args));
    }
    @Around(value = "execution( public * com.yangframe.web.core.crud.dao.BaseDao.insert(..))")
    public Object aspectInsert(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        aspectCommon(args);
        return parseResult(point.proceed(args));
    }
    @Around(value = "execution( public * com.yangframe.web.core.crud.dao.BaseDao.totalNum(..))")
    public Object aspectTotalNum(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        aspectConditon((ConditionEntity) args[0]);
        return parseResult(point.proceed(args));
    }
    @Around(value = "execution( public * com.yangframe.web.core.crud.dao.BaseDao.delete(..))")
    public Object aspectDelete(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        aspectCommon(args);
        return parseResult(point.proceed(args));
    }

    @Around(value = "execution( public * com.yangframe.web.core.crud.dao.BaseDao.insertAll(..))")
    public Object aspectInsertAll(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        aspectInsertAllDetail(args);
        return parseResult(point.proceed(args));
    }
    @Around(value = "execution( public * com.yangframe.web.core.crud.dao.BaseDao.updateAll(..))")
    public Object aspectUpdateAll(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        aspectUpdateAllDetail(args);
        return parseResult(point.proceed(args));
    }
    @Around(value = "execution( public * com.yangframe.web.core.crud.dao.BaseDao.deleteAll(..))")
    public Object aspectdeleteAll(ProceedingJoinPoint point) throws Throwable {
        Object[] args = point.getArgs();
        aspectDeleteAllDetail(args);
        return parseResult(point.proceed(args));
    }
    private void aspectDeleteAllDetail(Object... args){
        //1、实体名 2、PK数据
        InfoOfEntity infoOfEntity = EntityMap.getAndJugeNotEmpty((String) args[0]);
        args[0] = infoOfEntity.getEntityName();
        if(DataSourceType.ORACLE.equals(infoOfEntity.getConfig().getSourceType())){
            args[0]= OracleParser.parseEntityName((String) args[0]);
            args[1] =OracleParser.parseMap((Map<String, Object>) args[1]);
        }
    }
    private void aspectUpdateAllDetail(Object... args){
        String entityName = (String) args[0];
        Map<String,List<Object>> pks= (Map<String, List<Object>>) args[1];
        Map<String,String> data= (Map<String, String>) args[2];
        InfoOfEntity infoOfEntity = EntityMap.getAndJugeNotEmpty(entityName);
        args[0] = infoOfEntity.getEntityName();
        if(DataSourceType.ORACLE.equals(infoOfEntity.getConfig().getSourceType())){
            args[0] = OracleParser.parseEntityName(infoOfEntity.getEntityName());
            args[1] = OracleParser.parseMap(pks);
            args[2] = OracleParser.parseMap(data);
        }
    }
    private void aspectInsertAllDetail(Object... args){
        String entityName = (String) args[0];
        List<String> keys= (List<String>) args[1];
        InfoOfEntity infoOfEntity = EntityMap.getAndJugeNotEmpty(entityName);
        args[0] = infoOfEntity.getEntityName();
        if(DataSourceType.ORACLE.equals(infoOfEntity.getConfig().getSourceType())){
            args[0] = OracleParser.parseEntityName(infoOfEntity.getEntityName());
            for(int i=0;i<keys.size();i++){
                keys.set(i, "\""+keys.get(i)+"\"");
            }
        }
    }
    private void aspectFindEntity(FindEntity entity){
        InfoOfEntity infoOfEntity = EntityMap.getAndJugeNotEmpty(entity.getEntityName());
        entity.setEntityName(infoOfEntity.getEntityName());
        if(DataSourceType.ORACLE.equals(infoOfEntity.getConfig().getSourceType())){
            entity.setCondition(OracleParser.parseMap(entity.getCondition()));
            entity.setData(OracleParser.parseMap(entity.getData()));
        }
    }
    private void aspectConditon(ConditionEntity entity){
        Element element = EntityMap.getElement(entity.getMainTable());
        InfoOfEntity infoOfEntity = null;
        if("view-entity".equals(element.getName())) {
            MainTableInfo mainTable = EntityMap.getMainTable(entity.getMainTable());
            infoOfEntity = mainTable.getInfoOfEntity();
        }else {
            infoOfEntity = EntityMap.getAndJugeNotEmpty(entity.getMainTable());
        }
        entity.setMainTable(infoOfEntity.getEntityName());
        if(DataSourceType.ORACLE.equals(infoOfEntity.getConfig().getSourceType())){
            OracleParser.parseConditionEntity(entity);
        }
    }

    private void aspectUpdate(Object[] args){
        //1、实体名 2、PK数据
        InfoOfEntity infoOfEntity = EntityMap.getAndJugeNotEmpty((String) args[0]);
        args[0] = infoOfEntity.getEntityName();
        if(DataSourceType.ORACLE.equals(infoOfEntity.getConfig().getSourceType())){
            args[0]= OracleParser.parseEntityName((String) args[0]);
            args[1] =OracleParser.parseMap((Map<String, Object>) args[1]);
            args[2] =OracleParser.parseMap((Map<String, Object>) args[2]);
        }
    }
    private void aspectCommon(Object[] args){
        //1、实体名 2、PK数据
        InfoOfEntity infoOfEntity = EntityMap.getAndJugeNotEmpty((String) args[0]);
        args[0] = infoOfEntity.getEntityName();
        if(DataSourceType.ORACLE.equals(infoOfEntity.getConfig().getSourceType())){
            args[0]= OracleParser.parseEntityName((String) args[0]);
            args[1] =OracleParser.parseMap((Map<String, Object>) args[1]);
        }
    }

    private void aspectName(Object[] args){
        //1、实体名 2、PK数据
        InfoOfEntity infoOfEntity = EntityMap.getAndJugeNotEmpty((String) args[0]);
        args[0] = infoOfEntity.getEntityName();
        if(DataSourceType.ORACLE.equals(infoOfEntity.getConfig().getSourceType())){
            args[0]= OracleParser.parseEntityName((String) args[0]);
        }
    }

    private Object parseResult(Object data){
        if(data instanceof Map){
            parseOne(data);
        }else if(data instanceof List){
            for(Object item : (List)data){
                if(item instanceof Map)
                    parseOne(item);
            }
        }
        return data;
    }

    private void parseOne(Object data){
        ((Map) data).forEach((k,v)->{
            if(v instanceof Blob){
                parseBlob((Map) data, k, v);
            }else if(v instanceof Clob){
                parseClob((Map) data, k, v);
            }
        });
    }
    private void parseBlob(Map data,Object k,Object v){
        try {
            InputStream binaryStream = ((Blob) v).getBinaryStream();
            byte[] bytes = FileUtil.readFileToByte(binaryStream);
            String bs = new String(bytes);
            ((Map) data).put(k, bs);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
    private void parseClob(Map data,Object k,Object v){
        try {
            Reader reader = ((Clob) v).getCharacterStream();
            String bs = FileUtil.readCharactor(reader);
            ((Map) data).put(k, bs);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
