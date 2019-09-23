package com.yangframe.web.core.crud.controller;

import com.yangframe.config.advice.ResultEntity;
import com.yangframe.config.advice.ResultEnum;
import com.yangframe.web.core.crud.centity.ConditionEntity;
import com.yangframe.web.core.crud.centity.FindEntity;
import com.yangframe.web.core.crud.centity.Operator;
import com.yangframe.web.core.crud.service.BaseServiceImpl;
import com.yangframe.web.core.util.MakeConditionUtil;
import com.yangframe.web.core.xmlEntity.ColumnProperty;
import com.yangframe.web.core.xmlEntity.EntityMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/9/23 12:23
 */
@Api("获取到关联表的相关信息")
@Controller
@RequestMapping("refer")
public class ReferController {

    @Autowired
    private BaseServiceImpl baseService;

    @ApiOperation("查询到关联表")
    @ResponseBody
    @PostMapping("referFindAll")
    public ResultEntity referFindAll(@RequestBody FindEntity findEntity){
        Map<String, ColumnProperty> source = EntityMap.getPrimaryKey(findEntity.getEntityName());
        Map<String, ColumnProperty> referAllColumns = EntityMap.getAllColumns(findEntity.getEntityName());
        List<Map<String, Object>> allNoPage = baseService.findAll(findEntity, new ConditionEntity());

        Map<String,Object> alias = new HashMap<>();
        source.forEach((k,v)->{
            referAllColumns.forEach((m,n)->{
                if(n.getColumn().equals(v.getColumn())&&n.getTableName().equals(v.getTableName())){
                    alias.put(n.getAlias(), null);
                }
            });
        });
        //遍历完毕后，获取两个表之间的主键关联
        alias.forEach((k,v)->{
            List<Object> ls =new ArrayList<>();
            for(Map<String,Object> one :allNoPage){
                //开始处理
                if(one.containsKey(k))
                    ls.add(one.get(k));
            }
            if(ls.size()>0)
                alias.put(k, ls);
        });
        Map<String, Object> objectMap = MakeConditionUtil.makeCondition(alias, Operator.IN);
        List<Map<String, Object>> serviceAllNoPage = baseService.findAllNoPage(FindEntity.newInstance().makeEntityName(findEntity.getReferTableName()).makeData(objectMap), new ConditionEntity());

        //合并两者
        for(Map<String,Object> one:allNoPage){
            StringBuilder builder = new StringBuilder();
            source.forEach((m,n)->{
                builder.append(one.get(m)).append("_");
            });
            String key = builder.substring(0, builder.length()-1);
            List<Object> ls =new ArrayList<>();
            for(Map<String,Object> service : serviceAllNoPage){

                StringBuilder builders = new StringBuilder();
                alias.forEach((m,n)->{
                    builders.append(service.get(m)).append("_");
                });
                String referKey = builders.substring(0, builders.length()-1);
                if(key.equals(referKey)){
                    ls.add(service);
                }
            }
            one.put("referAll", ls);
        }

        return new ResultEntity(ResultEnum.OK,allNoPage);
    }

}
