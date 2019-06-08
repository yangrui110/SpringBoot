package com.demo.web.core.crud.centity;

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
        entity.setStart(entity.getStart()-1);
        entity.setEnd(entity.getEnd());
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
}
