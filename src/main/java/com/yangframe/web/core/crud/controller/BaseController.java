package com.yangframe.web.core.crud.controller;

import com.yangframe.config.advice.BaseException;
import com.yangframe.config.advice.ResultEntity;
import com.yangframe.config.advice.ResultEnum;
import com.yangframe.config.util.MapUtil;
import com.yangframe.web.core.crud.centity.*;
import com.yangframe.web.core.crud.service.BaseServiceImpl;
import com.yangframe.web.core.crud.service.BaseServiceExternal;
import com.yangframe.web.core.xmlEntity.ColumnProperty;
import com.yangframe.web.core.xmlEntity.EntityMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/6/6 13:49
 * @describetion 通用的增删改查的控制器接口
 */
@Api(description = "通用的增删改查")
@Controller
@RequestMapping("common")
public class BaseController {

    @Autowired
    private BaseServiceImpl baseService;

    @Autowired
    private BaseServiceExternal baseServiceInner;

    @ApiOperation("带分页的通用查找方法")
    @ResponseBody
    @PostMapping("findAll")
    public ResultEntity findAll(@RequestBody FindEntity entity){
        return new ResultEntity(ResultEnum.OK, baseService.findAll(entity, new ConditionEntity()),baseService.totalNum(entity, new ConditionEntity()));
    }

    @ApiOperation("不带分页的通用查找方法")
    @ResponseBody
    @PostMapping("findAllNoPage")
    public ResultEntity findAllNoPage(@RequestBody FindEntity entity){
        return new ResultEntity(ResultEnum.OK, baseService.findAllNoPage(entity, new ConditionEntity()),baseService.totalNum(entity, new ConditionEntity()));
    }

    @ApiOperation("删除单个数据")
    @ResponseBody
    @PostMapping("delete")
    public ResultEntity delete(@RequestBody DelEntity delEntity){
        baseService.delete(delEntity.getEntityName(),delEntity.getData());
        return new ResultEntity(ResultEnum.OK, new ModelMap("result", true));
    }

    @ApiOperation("更新单个数据")
    @ResponseBody
    @PostMapping("update")
    public ResultEntity update(@RequestBody FindEntity entity){
        baseService.update(entity);
        return new ResultEntity(ResultEnum.OK, new ModelMap("result", true));
    }
    @ApiOperation("插入一条数据")
    @ResponseBody
    @PostMapping("insert")
    public ResultEntity insert(@RequestBody FindEntity entity){
        baseService.insert(entity);
        return new ResultEntity(ResultEnum.OK, new ModelMap("result", true));
    }

    /**
     * 获取视图表和实体表之间的对应关系
     * **/
    @ApiOperation("获取主键视图和实体的主键对应关系")
    @ResponseBody
    @GetMapping("getPK")
    public ResultEntity getPK(@RequestParam("viewEntityName") String viewEntityName
            ,@RequestParam("entityName")String entityName){
        Map<String, ColumnProperty> primaryKey = EntityMap.getPrimaryKey(entityName);
        Map result=new HashMap();
        //获取到当前视图的所有alias标签
        primaryKey.forEach((k,v)->{
            result.put(k, k);
        });
        Element element = EntityMap.getElement(viewEntityName);
        for(Element el: element.elements()){
            if("alias".equals(el.getName())){
                String column = el.attributeValue("column");
                String alias = el.attributeValue("alias");
                if(primaryKey.containsKey(column)){
                    result.put(column, alias);
                }
            }
        }
        //反转key和value
        Map mapOne=new HashMap();
        result.forEach((k,v)->{
            mapOne.put(v, k);
        });
        /*Map<String, ColumnProperty> primaryKey = EntityMap.getPrimaryKey(viewEntityName);
        Map<String,String> result=new HashMap<>();
        primaryKey.forEach((k,v)->{
            result.put(k, v.getColumn());
        });*/
        return new ResultEntity(ResultEnum.OK,mapOne);
    }

    @ApiOperation("更新或者是插入")
    @ResponseBody
    @PostMapping("store")
    public ResultEntity store(@RequestBody FindEntity entity){
        baseService.store(entity);
        return new ResultEntity(ResultEnum.OK, MapUtil.toMap("result",true));
    }

    /**
     * 删除选择的数据
     * */
    @ApiOperation("批量删除数据")
    @ResponseBody
    @PostMapping("delSelect")
    public ResultEntity delSelect(@RequestBody DelSelectEntity delSelectEntity){
        List<Map<String,Object>> datas = delSelectEntity.getDatas();
        if(datas.size()==0){
            throw new BaseException(507,"删除条件不能为空");
        }
        baseService.delSelect(delSelectEntity.getEntityName(), datas);
        return new ResultEntity(ResultEnum.OK, new ModelMap("result", true));
    }

    /**
     * 批量更新
     * */
    @ApiOperation("批量更新")
    @ResponseBody
    @PostMapping("updateAll")
    public ResultEntity updateAll(@RequestBody UpdateAllEntity entity){
        baseService.updateAll(entity.getEntityName(), entity.getData());
        return new ResultEntity(ResultEnum.OK,new ModelMap("result", "true"));
    }
    /**
     * 批量插入
     * */
    @ApiOperation("批量插入")
    @ResponseBody
    @PostMapping("insertAll")
    public ResultEntity insertAll(@RequestBody UpdateAllEntity entity){
        baseService.updateAll(entity.getEntityName(), entity.getData());
        return new ResultEntity(ResultEnum.OK,new ModelMap("result", "true"));
    }

    /**
     * 批量更新或者删除
     * */
    @ApiOperation("批量更新或者插入")
    @ResponseBody
    @PostMapping("storeAll")
    public ResultEntity storeAll(@RequestBody UpdateAllEntity entity){
        baseServiceInner.storeAll(entity.getEntityName(), entity.getData());
        return new ResultEntity(ResultEnum.OK, new ModelMap("result", true));
    }
    /**
     * 携带关联表查询方法
     * */
    @ApiOperation("携带关联表查询")
    @ResponseBody
    @PostMapping("referFindAll")
    public ResultEntity referFindAll(@RequestBody FindEntity findEntity){
        List<Map<String, Object>> findAll = baseServiceInner.referFindAll(findEntity);
        return new ResultEntity(ResultEnum.OK, findAll,baseService.totalNum(findEntity,new ConditionEntity()));
    }

    /**
     * 携带关联表查询方法
     * */
    @ApiOperation("更新多对多的关联表")
    @ResponseBody
    @PostMapping("updateManyToManyTable")
    public ResultEntity updateManyToManyTable(@RequestBody OneToManyEntity oneToManyEntity){
        baseServiceInner.updateManyToManyTable(oneToManyEntity.getEntityName(), oneToManyEntity.getData(), oneToManyEntity.getMainColumns());
        return new ResultEntity(ResultEnum.OK, new ModelMap("result", true));
    }

}
