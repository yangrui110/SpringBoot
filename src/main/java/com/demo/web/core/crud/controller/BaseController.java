package com.demo.web.core.crud.controller;

import com.demo.config.advice.ResultEntity;
import com.demo.config.advice.ResultEnum;
import com.demo.config.util.MapUtil;
import com.demo.web.core.crud.centity.ConditionEntity;
import com.demo.web.core.crud.centity.DelSelectEntity;
import com.demo.web.core.crud.centity.FindEntity;
import com.demo.web.core.crud.service.BaseServiceImpl;
import com.demo.web.core.xmlEntity.ColumnProperty;
import com.demo.web.core.xmlEntity.EntityMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @PostMapping("findAllNoPage")
    public ResultEntity findAllNoPage(@RequestBody FindEntity entity){
        return new ResultEntity(ResultEnum.OK, baseService.findAllNoPage(entity, new ConditionEntity()),baseService.totalNum(entity, new ConditionEntity()));
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
    @GetMapping("getPK")
    public ResultEntity getPK(@RequestParam("viewEntityName") String viewEntityName){
        /*Map<String, Object> primaryKey = EntityMap.getPrimaryKey(entityName);
        Map<String, ColumnProperty> allColumns = EntityMap.getAllColumns(viewEntityName);
        Map result=new HashMap();
        allColumns.forEach((k,v)->{
            primaryKey.forEach((s,l)->{
                if(v.getColumn().equals(s))
                    result.put(k, v.getColumn());
            });
        });*/
        Map<String, ColumnProperty> primaryKey = EntityMap.getPrimaryKey(viewEntityName);
        Map<String,String> result=new HashMap<>();
        primaryKey.forEach((k,v)->{
            result.put(k, v.getColumn());
        });
        return new ResultEntity(ResultEnum.OK,result);
    }

    /**
     * 删除选择的数据
     * */

    @ResponseBody
    @PostMapping("delSelect")
    public ResultEntity delSelect(@RequestBody DelSelectEntity delSelectEntity){
        List<Map> datas = delSelectEntity.getDatas();
        datas.forEach((k)->{
            FindEntity findEntity=new FindEntity();
            findEntity.setEntityName(delSelectEntity.getEntityName());
            List condition=new ArrayList();
            k.forEach((key,value)->{
                Map map=new HashMap();
                map.put("left", key);
                map.put("right", value);
                condition.add(map);
            });
            Map maps=new HashMap();
            maps.put("conditionList", condition);
            findEntity.setCondition(maps);
            baseService.delete(findEntity);
        });
        return new ResultEntity(ResultEnum.OK, new ModelMap("result", true));
    }
    @ResponseBody
    @PostMapping("object")
    public ResultEntity object(@RequestBody Object os){
        return new ResultEntity(ResultEnum.OK, MapUtil.toMap("result", true));
    }

}
