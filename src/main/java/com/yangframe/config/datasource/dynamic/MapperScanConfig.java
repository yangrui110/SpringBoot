package com.yangframe.config.datasource.dynamic;

import lombok.Data;

/**
 * @autor 杨瑞
 * @date 2019/6/11 21:17
 */
@Data
public class MapperScanConfig {
    private String[] basePackages;
    private String sqlSessionTemplateRef;
}
