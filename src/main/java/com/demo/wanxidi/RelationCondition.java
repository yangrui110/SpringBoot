package com.demo.wanxidi;

import java.util.HashMap;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/7/8 23:33
 */

public class RelationCondition {
    private Map condition;
    private int start;
    private int end;

    public Map getCondition() {
        return condition==null?new HashMap():condition;
    }

    public void setCondition(Map condition) {
        this.condition = condition;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }
}
