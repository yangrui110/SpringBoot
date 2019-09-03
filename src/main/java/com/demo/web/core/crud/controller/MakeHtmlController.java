package com.demo.web.core.crud.controller;

import com.demo.config.advice.ResultEntity;
import com.demo.config.advice.ResultEnum;
import com.demo.web.core.crud.centity.ConditionEntity;
import com.demo.web.core.crud.centity.FindEntity;
import com.demo.web.core.crud.service.BaseServiceImpl;
import com.demo.web.core.util.FindEntityUtil;
import com.demo.web.core.util.MakeConditionUtil;
import com.demo.web.core.xmlEntity.ColumnProperty;
import com.demo.web.core.xmlEntity.EntityMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/6/24 19:29
 * @describetion 自动生成前台界面的控制器类
 */
@Controller
public class MakeHtmlController {

    @Autowired
    private BaseServiceImpl baseService;
    /**
     * 获取某个表的所有查询出的列名
     * */
    @ResponseBody
    @GetMapping("getAllColumns")
    public ResultEntity getAllColumns(String entityName){

        Map<String, ColumnProperty> allColumns = EntityMap.getAllColumns(entityName);
        return new ResultEntity(ResultEnum.OK, allColumns);
    }



    /**
     * 生成前端代码的压缩格式
     * */
    @GetMapping("makeFromCode")
    public void makeFromCode(){

    }
}
