package com.yangframe.web.core.crud.centity;

/**
 * @autor 杨瑞
 * @date 2019/5/18 18:34
 * @describetion 定义排序的基础字段
 */
public class COrderBy {
    String names;
    String direct;

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    /**
     * 
     * */
    public String getDirect() {
        return direct==null?"asc":direct;
    }

    public void setDirect(String direct) {
        this.direct = direct;
    }
}
