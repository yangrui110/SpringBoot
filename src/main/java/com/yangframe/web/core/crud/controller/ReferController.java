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
        //获取到两个表之间的关联
        Map<String, Object> alias = getRefreColumn(source, referAllColumns);
        //给关联列设置查到的值
        setDataForAlias(alias,allNoPage);
        //根据上面的条件，从关联表查询数据
        Map<String, Object> objectMap = MakeConditionUtil.makeCondition(alias, Operator.IN);
        List<Map<String, Object>> serviceAllNoPage = baseService.findAllNoPage(FindEntity.newInstance().makeEntityName(findEntity.getReferTableName()).makeData(objectMap), new ConditionEntity());
        //将查询到的关联表结果，赋值到主表的查询结果中去
        parseReferDataToMain(allNoPage,source,serviceAllNoPage,alias);

        return new ResultEntity(ResultEnum.OK,allNoPage);
    }

    private Map<String,Object> getRefreColumn(Map<String,ColumnProperty> source,Map<String,ColumnProperty> referAllColumns){
        Map<String,Object> alias = new HashMap<>();
        source.forEach((k,v)->{
            referAllColumns.forEach((m,n)->{
                if(n.getColumn().equals(v.getColumn())&&n.getTableName().equals(v.getTableName())){
                    alias.put(n.getAlias(), null);
                }
            });
        });
        return alias;
    }

    void setDataForAlias(Map<String,Object> alias,List<Map<String,Object>> allNoPage){
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

    }

    void parseReferDataToMain(List<Map<String,Object>> allNoPage,Map<String,ColumnProperty> mainPrimaryKey,List<Map<String,Object>> referData,Map<String,Object> referAlias){
        for(Map<String,Object> one:allNoPage){
            StringBuilder builder = new StringBuilder();
            mainPrimaryKey.forEach((m,n)->{
                builder.append(one.get(m)).append("_");
            });
            String key = builder.substring(0, builder.length()-1);
            List<Object> ls =new ArrayList<>();
            for(Map<String,Object> service : referData){

                StringBuilder builders = new StringBuilder();
                referAlias.forEach((m,n)->{
                    builders.append(service.get(m)).append("_");
                });
                String referKey = builders.substring(0, builders.length()-1);
                if(key.equals(referKey)){
                    ls.add(service);
                }
            }
            one.put("referAll", ls);
        }

    }
}
