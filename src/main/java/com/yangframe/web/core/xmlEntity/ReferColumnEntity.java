package com.yangframe.web.core.xmlEntity;

import lombok.Data;

/**
 * @autor 杨瑞
 * @date 2019/9/26 15:19
 */
@Data
public class ReferColumnEntity {

    private String column;
    private String referColumn;
    private String operator;
}
