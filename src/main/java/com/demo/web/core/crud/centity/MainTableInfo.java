package com.demo.web.core.crud.centity;

import com.demo.web.core.xmlEntity.InfoOfEntity;
import lombok.Data;

/**
 * @autor 杨瑞
 * @date 2019/7/6 17:02
 */
@Data
public class MainTableInfo {
    private String alias;
    private String tableAlias;
    private String viewName;
    private InfoOfEntity infoOfEntity;
}
