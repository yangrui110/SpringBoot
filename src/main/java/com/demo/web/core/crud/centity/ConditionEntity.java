package com.demo.web.core.crud.centity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @autor 杨瑞
 * @date 2019/5/21 13:21
 */
@Data
public class ConditionEntity {

    private String columns;
    private String joins;
    private String mainTable;
    private String mainAlias;
    private List<CEntity> cons = new ArrayList<>();
    private String orderBy;

    private Integer start;
    private Integer end;
}
