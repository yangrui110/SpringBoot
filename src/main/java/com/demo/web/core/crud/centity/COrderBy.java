package com.demo.web.core.crud.centity;

/**
 * @autor 杨瑞
 * @date 2019/5/18 18:34
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
