package com.yangframe.web.core.xmlEntity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @autor 杨瑞
 * @date 2019/5/27 21:20
 */
@Data
public class JoinRelation {

    private String join;

    private String tableName;
    private String tableAlias;
    private String tableMemberAlias;

    private String referTable;
    private String referTableAlias;
    private String referMemberAlias;

    private List<ReferColumnEntity> referColumnEntities =new ArrayList<>();
}
