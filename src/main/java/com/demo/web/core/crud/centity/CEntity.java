package com.demo.web.core.crud.centity;

import lombok.Data;

/**
 * @autor 杨瑞
 * @date 2019/5/18 18:02
 */

public class CEntity {
    private String left;
    private String operator;
    private String right;

    public String getLeft() {
        return left;
    }

    public void setLeft(String left) {
        this.left = left;
    }

    public String getOperator() {
        return operator==null?"=":operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getRight() {
        return right;
    }

    public void setRight(String right) {
        this.right = right;
    }
}
