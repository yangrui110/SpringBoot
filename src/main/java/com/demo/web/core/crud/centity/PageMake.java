package com.demo.web.core.crud.centity;

import com.demo.config.datasource.type.DataSourceType;

/**
 * @autor 杨瑞
 * @date 2019/6/8 12:00
 * @describetion 传入的最小页数是1
 */
public class PageMake {

    /**
     * 制作mysql的分页参数
     * */
    public static void makeMysqlPage(ConditionEntity entity){
        if(entity.getStart()!=null&&entity.getEnd()!=null) {
            entity.setStart((entity.getStart() - 1)*entity.getEnd());
            entity.setEnd(entity.getEnd());
        }
    }

    /**
     * 制作sql的分页参数
     * */
    public static void makeSqlPage(ConditionEntity entity){
        Integer start=entity.getStart();
        Integer end=entity.getEnd();
        if(entity.getStart()!=null&&entity.getEnd()!=null){
            entity.setStart((start-1)*end);
            entity.setEnd(start*end);
        }
    }

    public static void makePage(ConditionEntity entity,String sourceType){
        if(DataSourceType.MYSQL.equals(sourceType)){ PageMake.makeMysqlPage(entity); }
        else if(DataSourceType.ORACLE.equals(sourceType)){ PageMake.makeSqlPage(entity); }
        else if(DataSourceType.SQL.equals(sourceType)){PageMake.makeSqlPage(entity); }
    }
}
