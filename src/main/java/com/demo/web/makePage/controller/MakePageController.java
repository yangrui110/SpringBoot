package com.demo.web.makePage.controller;

import com.alibaba.fastjson.JSONObject;
import com.demo.config.advice.BaseException;
import com.demo.config.advice.ResultEntity;
import com.demo.config.advice.ResultEnum;
import com.demo.config.util.MapUtil;
import com.demo.web.core.crud.centity.ConditionEntity;
import com.demo.web.core.crud.centity.FindEntity;
import com.demo.web.core.crud.centity.MainTableInfo;
import com.demo.web.core.crud.service.BaseServiceImpl;
import com.demo.web.core.util.MakeConditionUtil;
import com.demo.web.core.xmlEntity.ColumnProperty;
import com.demo.web.core.xmlEntity.EntityMap;
import com.demo.web.core.xmlEntity.InfoOfEntity;
import com.demo.web.makePage.file.MakeFile;
import com.demo.web.util.http.HeaderType;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipOutputStream;

/**
 * @autor 杨瑞
 * @date 2019/7/3 20:15
 */
@Controller
@RequestMapping("makePage")
public class MakePageController {

    @Autowired
    private BaseServiceImpl baseService;

    /**
     *
     * */
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
    @ResponseBody
    @GetMapping("getFromPostEntity")
    public ResultEntity getFromPostEntity(String entityName){
        Map<String, Object> condition = MakeConditionUtil.makeCondition("post_entity", entityName);
        FindEntity findEntity=new FindEntity();
        findEntity.setEntityName("postParam");
        findEntity.setCondition(condition);
        List<Map<String, Object>> list = baseService.findAllNoPage(findEntity, new ConditionEntity());
        Map<String,Object> result=new HashMap<>();
        if(list.size()>0){
            result=list.get(0);
        }
        return new ResultEntity(ResultEnum.OK,result);
    }

    /**
     * 保存数据到数据表中
     * */
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
        Map data = MapUtil.toMap("post_param", jsonObject.getJSONObject("columns").toString());
        data.put("post_entity", jsonObject.getString("entityName"));
        entity.setData(data);
        Map os=new HashMap();
        Map os1=new HashMap();
        os1.put("left", "post_entity");
        os1.put("right", jsonObject.getString("entityName"));
        List ls=new ArrayList();
        ls.add(os1);
        os.put("conditionList", ls);
        entity.setCondition(os);
        int totalNum = baseService.totalNum(entity, new ConditionEntity());
        if(totalNum==0)
            baseService.insert(entity);
        else baseService.update(entity);
        //在系统的文件夹下生成压缩文件
        String listHtml = MakeFile.makeListHtml(jsonObject.getJSONObject("columns"));
        System.out.println("替换后："+listHtml);

        String tableList = MakeFile.makeTableList(jsonObject.getJSONObject("columns"));
        System.out.println("表格显示："+tableList);

        MainTableInfo mainTable = EntityMap.getMainTable(jsonObject.getString("entityName"));
        System.out.println("主表："+mainTable);

        String addList = MakeFile.makeAddList(jsonObject.getJSONObject("columns"));
        System.out.println("增加的列："+addList);

        String viewList = MakeFile.makeViewList(jsonObject.getJSONObject("columns"));
        System.out.println("查看的列："+viewList);

        return new ResultEntity(ResultEnum.OK, MapUtil.toMap("result", true));

    }

    @GetMapping("makes")
    public void makes(@RequestParam("entityName") String entityName ,HttpServletResponse response) throws IOException {

        FindEntity entity = new FindEntity();
        entity.setEntityName("postParam");
        entity.setCondition(MakeConditionUtil.makeCondition("post_entity",entityName));
        List<Map<String, Object>> allNoPage =  baseService.findAllNoPage(entity, new ConditionEntity());
        if (allNoPage.size() <= 0) {
            return;
        }
        JSONObject params=JSONObject.parseObject((String) ((Map)allNoPage.get(0)).get("post_param"));
        String viewList = MakeFile.makeViewList(params);
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
