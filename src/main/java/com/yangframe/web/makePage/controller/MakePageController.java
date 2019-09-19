package com.yangframe.web.makePage.controller;

import com.alibaba.fastjson.JSONObject;
import com.yangframe.config.advice.BaseException;
import com.yangframe.config.advice.ResultEntity;
import com.yangframe.config.advice.ResultEnum;
import com.yangframe.config.util.MapUtil;
import com.yangframe.web.core.crud.centity.ConditionEntity;
import com.yangframe.web.core.crud.centity.FindEntity;
import com.yangframe.web.core.crud.centity.MainTableInfo;
import com.yangframe.web.core.crud.service.BaseServiceImpl;
import com.yangframe.web.core.util.MakeConditionUtil;
import com.yangframe.web.core.xmlEntity.ColumnProperty;
import com.yangframe.web.core.xmlEntity.EntityMap;
import com.yangframe.web.core.xmlEntity.InfoOfEntity;
import com.yangframe.web.makePage.file.MakeFile;
import com.yangframe.web.util.http.HeaderType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.zip.ZipOutputStream;

/**
 * @autor 杨瑞
 * @date 2019/7/3 20:15
 */
@Api(value = "makePage",description = "生成layui界面")
@Controller
@RequestMapping("makePage")
public class MakePageController {

    @Autowired
    private BaseServiceImpl baseService;

    /**
     *
     * */
    @ApiOperation("获取定义的所有实体和视图")
    @ResponseBody
    @GetMapping("getAllTables")
    public ResultEntity getAllTables(){
        Map<String, InfoOfEntity> tables = EntityMap.tables;
        List<String> results=new ArrayList<>();
        tables.forEach((k,v)->{
            results.add(k);
        });
        return new ResultEntity(ResultEnum.OK,results);
    }
    @ApiOperation("获取视图或者实体的所有列")
    @ResponseBody
    @GetMapping("getAllColumns")
    public ResultEntity getAllColumns(@RequestParam("entityName")String entityName){
        if(StringUtils.isEmpty(entityName))
            throw new BaseException(304,"传入的实体名不能为空");
        Map<String, ColumnProperty> allColumns = EntityMap.getAllColumns(entityName);
        JSONObject result=new JSONObject();
        result.put("columns", allColumns);
        result.put("tableName", entityName);
        return new ResultEntity(ResultEnum.OK,allColumns);
    }
    /**
     * @param entityName 表名，对应的postparam表的主键
     * */
    @ApiOperation("从条件表中获取已保存的条件")
    @ResponseBody
    @GetMapping("getFromPostEntity")
    public ResultEntity getFromPostEntity(String entityName){
        Element element = EntityMap.getElement(entityName);
        Map<String, Object> condition = MakeConditionUtil.makeCondition("postEntity", entityName);
        FindEntity findEntity=new FindEntity();
        findEntity.setEntityName("postParam");
        findEntity.setData(condition);
        List<Map<String, Object>> list = baseService.findAllNoPage(findEntity, new ConditionEntity());
        Map<String,Object> result= new HashMap<>();
        Element element1 = element.elements().stream().filter((k) -> {
            if ("describetion".equals(k.getName())) return true;
            return false;
        }).findFirst().orElse(null);
        if(list.size()>0) {
            result= list.get(0);
        }else{
            result.put("postParam",MapUtil.toMap("tableTitle",element1==null?"":element1.getText()));
        }
        return new ResultEntity(ResultEnum.OK,result);
    }

    /**
     * 保存数据到数据表中
     * */
    @ApiOperation("保存数据到数据表中")
    @ResponseBody
    @PostMapping("savePageData")
    public ResultEntity savePageData(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        byte[] bys=new byte[1024];
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        int len=-1;
        while((len=inputStream.read(bys))!=-1){
            outputStream.write(bys, 0,len );
        }
        String params=new String(outputStream.toByteArray());
        System.out.println(params);
        outputStream.close();
        JSONObject jsonObject = JSONObject.parseObject(params);
        //插入postParam数据表中
        FindEntity entity=new FindEntity();
        entity.setEntityName("postParam");
        Map toMap = MapUtil.toMap("postParam", params);
        toMap.put("postEntity", jsonObject.getString("entityName"));
        entity.setData(toMap);
        baseService.insertOrUpdate(entity);

        return new ResultEntity(ResultEnum.OK, MapUtil.toMap("result", true));

    }

    @ApiOperation("生成管理界面")
    @GetMapping("makes")
    public void makes(@RequestParam("entityName") String entityName ,HttpServletResponse response) throws IOException {

        FindEntity entity = new FindEntity();
        entity.setEntityName("postParam");
        entity.setData(MakeConditionUtil.makeCondition("postEntity",entityName));
        List<Map<String, Object>> allNoPage =  baseService.findAllNoPage(entity, new ConditionEntity());
        if (allNoPage.size() <= 0) {
            return;
        }
        Map map = (allNoPage.get(0));
        JSONObject params=JSONObject.parseObject((String) map.get("postParam"));
        JSONObject columns = params.getJSONObject("columns");
        String viewList = MakeFile.makeViewList(columns);
        System.out.println("查看的列：" + viewList);

        //
        HeaderType.setResponseFile(entityName+".zip", response);
        byte[] buff = new byte[1024];
        OutputStream osx = null;
        try {
            osx = response.getOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(osx);
            MakeFile.compress(allNoPage.get(0), zipOutputStream);
            zipOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }
}
