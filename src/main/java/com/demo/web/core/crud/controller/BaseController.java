package com.demo.web.core.crud.controller;

import com.demo.config.advice.ResultEntity;
import com.demo.config.advice.ResultEnum;
import com.demo.config.util.MapUtil;
import com.demo.web.core.crud.centity.ConditionEntity;
import com.demo.web.core.crud.centity.FindEntity;
import com.demo.web.core.crud.service.BaseServiceImpl;
import oracle.jdbc.proxy.annotation.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
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
        return new ResultEntity(ResultEnum.OK, baseService.findAll(entity, new ConditionEntity()),baseService.totalNum(entity, new ConditionEntity()));
    }

    @ResponseBody
    @PostMapping("delete")
    public ResultEntity delete(@RequestBody FindEntity entity){
        baseService.delete(entity);
        return new ResultEntity(ResultEnum.OK, new ModelMap("result", true));
    }

    @ResponseBody
    @PostMapping("update")
    public ResultEntity update(@RequestBody FindEntity entity){
        baseService.update(entity);
        return new ResultEntity(ResultEnum.OK, new ModelMap("result", true));
    }
    @ResponseBody
    @PostMapping("insert")
    public ResultEntity insert(@RequestBody FindEntity entity){
        baseService.insert(entity);
        return new ResultEntity(ResultEnum.OK, new ModelMap("result", true));
    }

    @ResponseBody
    @PostMapping("object")
    public ResultEntity object(@RequestBody Object os){
        return new ResultEntity(ResultEnum.OK, MapUtil.toMap("result", true));
    }

}
