package com.demo.web.core.crud.controller;

import com.demo.config.advice.ResultEntity;
import com.demo.config.advice.ResultEnum;
import com.demo.web.core.crud.centity.ConditionEntity;
import com.demo.web.core.crud.centity.FindEntity;
import com.demo.web.core.crud.service.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @autor 杨瑞
 * @date 2019/6/6 13:49
 * @describetion 通用的增删改查的控制器接口
 */
@Controller
@RequestMapping("common")
public class BaseController {

    @Autowired
    private BaseServiceImpl baseService;

    @ResponseBody
    @PostMapping("findAll")
    public ResultEntity findAll(@RequestBody FindEntity entity){
        return new ResultEntity(ResultEnum.OK, baseService.findAll(entity, new ConditionEntity()));
    }
}
