package com.demo.web.core.crud.centity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/6/6 13:53
 * @describetion 查找的实体条件
 */
public class FindEntity {

    //实体的名字
    private String entityName;
    //查询条件

    //{conditionList:[{conditionList:[{left:'id',operator:'=',right:'2'},combine:'and'},{left:'name',operator:'<',right:'lisi'}],combine:'or'}

    //{conditionList:[{left:'id',operator:'=',right:'2'},{left:'name',operator:'<',right:'lisi'}],combine:'or'}
    private Map<String,Object> condition = new HashMap<>();
    private Integer start;
    private Integer end;
    private List<COrderBy> orderBy;
    //传入的数据
    private Map data = new HashMap();

    private String cons; //增删改时的条件

    public String getCons() {
        return cons;
    }

    public void setCons(String cons) {
        this.cons = cons;
    }

    public static FindEntity newInstance(){
        return new FindEntity();
    }
    public FindEntity makeEntityName(String entityName){
        this.entityName=entityName;
        return this;
    }
    public FindEntity makeCondition(Map<String,Object> map){
        List list=new ArrayList();
        list.add(map);
        condition.put("condition", list);
        return this;
    }
    public FindEntity makeStart(Integer start){
        this.start=start;
        return this;
    }
    public FindEntity makeEnd(Integer end){
        this.end=end;
        return this;
    }

    public FindEntity makeCondition(Map map,String operator,Map map1){
        List list=new ArrayList();
        list.add(map);
        list.add(map1);
        this.condition.put("condition", list);
        this.condition.put("operator", operator);
        return this;
    }
    public FindEntity makeData(Map map){
        this.data=map;
        return this;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public List<COrderBy> getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(List<COrderBy> orderBy) {
        this.orderBy = orderBy;
    }

    public Map getData() {
        return data;
    }

    public void setData(Map data) {
        this.data = data;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public Map<String, Object> getCondition() {
        return condition;
    }

    public void setCondition(Map<String, Object> condition) {
        this.condition = condition;
    }
}
