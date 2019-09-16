package com.demo.web.core.crud.centity;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/9/15 17:03
 */
@Data
public class UpdateAllEntity {
    String entityName;
    List<Map<String,Object>> mapDatas;
}
