package com.demo.web.makePage.file;

import com.alibaba.fastjson.JSONObject;
import com.demo.config.datasource.type.DataSourceType;
import com.demo.web.core.crud.centity.COrderBy;
import com.demo.web.core.crud.centity.MainTableInfo;
import com.demo.web.core.xmlEntity.ColumnProperty;
import com.demo.web.core.xmlEntity.EntityMap;
import com.demo.web.core.xmlEntity.InfoOfEntity;
import com.demo.web.util.file.FileUtil;
import com.demo.web.util.file.zip.Compress;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipOutputStream;

/**
 * @autor 杨瑞
 * @date 2019/7/4 22:25
 */
public class MakeFile {

    /**
     * @function 制作查询条件
     * map传入的参数条件
     * pattern 需要被替换的字符串
     * */
    public static String makeListHtml(JSONObject map) throws IOException {
        ClassPathResource resource = new ClassPathResource("htmlTemplates/inner/list-input");
        String pattern = FileUtil.getFileString(resource.getFile());
        System.out.println("替换前："+pattern);

        ClassPathResource tableSelectResource = new ClassPathResource("htmlTemplates/inner/select-html");
        String patternSelect = FileUtil.getFileString(tableSelectResource.getFile());

        Map<String,String> mapData=new HashMap<>();
        mapData.put("pattern", pattern);
        mapData.put("patternSelect", patternSelect);

        StringBuffer result=new StringBuffer();
        //第一次遍历，获取所有需要查询的列
        Map views = new HashMap();
        map.forEach((k,v)->{
            if(((JSONObject)v).getBoolean("search")){
                views.put(k,v );
            }
        });
        Iterator iterator = views.values().iterator();
        while (iterator.hasNext()){
            JSONObject next = (JSONObject) iterator.next();
            String pa=pattern;
            if(!StringUtils.isEmpty(next.get("workParentValue"))){
                pa=patternSelect;
            }
            result.append(makeListPattern(next, makeListPattern(next, pa)));
        }
        return result.toString();
    }
    /**
     * @function 制作列表显示的列
     * */
    public static String makeTableList(JSONObject map) throws IOException {
        ClassPathResource tableResource = new ClassPathResource("htmlTemplates/inner/list-table");
        String pattern = FileUtil.getFileString(tableResource.getFile());
        ClassPathResource columnResource = new ClassPathResource("htmlTemplates/inner/pic-compoments");
        String patternColumn = FileUtil.getFileString(columnResource.getFile());
        StringBuilder result=new StringBuilder();
        Iterator<Object> iterator = map.values().iterator();
        while (iterator.hasNext()){
            JSONObject next = (JSONObject) iterator.next();
            if(next.getBoolean("show")) {
                String patternOne = pattern.replaceAll("listTableField", next.getString("alias"));
                String patternTwo = patternOne.replaceAll("fieldDesc", next.getString("describetion"));
                if("picture".equals(next.getString("compomentType"))){
                    patternColumn=patternColumn.replaceAll("layColumnName-yangrui",next.getString("alias"));
                    patternTwo=patternTwo.replaceAll("layTemplate-yangrui",patternColumn);
                }else patternTwo=patternTwo.replaceAll("layTemplate-yangrui","");
                result.append(patternTwo);
            }
        }
        if(result.length()>0)
            return result.toString();
        else return "";
    }

    /**
     * 制作编辑界面的列
     * @param columns 传入的列参数
     * */
    public static String makeEditList(Map columns,Map<String,ColumnProperty> primaryKey) throws IOException {
        ClassPathResource tableResource = new ClassPathResource("htmlTemplates/inner/edit-input");
        String pattern = FileUtil.getFileString(tableResource.getFile());
        ClassPathResource tableResourceReadOnly = new ClassPathResource("htmlTemplates/inner/edit-input-readOnly");
        String patternReadOnly = FileUtil.getFileString(tableResourceReadOnly.getFile());
        StringBuilder builder=new StringBuilder();

        ClassPathResource tableSelectResource = new ClassPathResource("htmlTemplates/inner/select-addAndEditor-html");
        String patternSelect = FileUtil.getFileString(tableSelectResource.getFile());

        Map<String,String> map=new HashMap<>();
        map.put("pattern", pattern);
        map.put("patternSelect", patternSelect);

        Iterator iterator = columns.values().iterator();
        while (iterator.hasNext()){
            JSONObject os = (JSONObject) iterator.next();
            if(os.getBoolean("edit")){
                String pa=pattern;
                if(!StringUtils.isEmpty(os.get("workParentValue"))){
                    pa=patternSelect;
                }
                builder.append(makeListPattern(os, pa));
            }else{
                /*String s=patternReadOnly.replaceAll("label-yangrui", os.getString("describetion"));
                s=s.replaceAll("column-yangrui", os.getString("column"));
                builder.append(s);*/
            }
        }
        return builder.toString();
    }

    /**
     * 制作增加界面的列
     * */
    public static String makeAddList(Map columns) throws IOException {
        ClassPathResource tableResource = new ClassPathResource("htmlTemplates/inner/add-input");
        String pattern = FileUtil.getFileString(tableResource.getFile());
        StringBuilder builder=new StringBuilder();

        ClassPathResource tableSelectResource = new ClassPathResource("htmlTemplates/inner/select-addAndEditor-html");
        String patternSelect = FileUtil.getFileString(tableSelectResource.getFile());

        Map<String,String> map=new HashMap<>();
        map.put("pattern", pattern);
        map.put("patternSelect", patternSelect);

        Iterator iterator = columns.values().iterator();
        while (iterator.hasNext()){
            JSONObject os = (JSONObject) iterator.next();
            if(os.getBoolean("add")){
                String pa=pattern;
                if(!StringUtils.isEmpty(os.get("workParentValue"))){
                    pa=patternSelect;
                }
                builder.append(makeListPattern(os,pa));
            }
        }
        return builder.toString();
    }

    /**
     * 制作增加界面的列
     * */
    public static String makeViewList(Map columns) throws IOException {
        ClassPathResource tableResource = new ClassPathResource("htmlTemplates/inner/edit-input-readOnly");
        String pattern = FileUtil.getFileString(tableResource.getFile());
        StringBuilder builder=new StringBuilder();

        ClassPathResource tableSelectResource = new ClassPathResource("htmlTemplates/inner/select-view-html");
        String patternSelect = FileUtil.getFileString(tableSelectResource.getFile());

        Map<String,String> map=new HashMap<>();
        map.put("pattern", pattern);
        map.put("patternSelect", patternSelect);
        Iterator iterator = columns.values().iterator();
        while (iterator.hasNext()){
            JSONObject os = (JSONObject) iterator.next();
            if(os.getBoolean("view")){
                String pa=pattern;
                if(!StringUtils.isEmpty(os.get("workParentValue"))){
                    pa=patternSelect;
                }
                builder.append(makeListPattern(os, pa));
            }
        }
        return builder.toString();
    }

    private static String makeListPattern(JSONObject os,String pattern){
        String s=pattern.replaceAll("label-yangrui", os.getString("describetion"));
        s=s.replaceAll("column-yangrui", os.getString("column"));
        s=s.replaceAll("columnAlias-yangrui", os.getString("alias"));
        return s;
    }
    /**
     * 制作排序字段,以主键作为排序字段
     * */
    public static String makeOrderBy(String entityName){
        Map<String, ColumnProperty> primaryKey = EntityMap.getPrimaryKey(entityName);
        StringBuilder builder=new StringBuilder();
        primaryKey.forEach((k,v)->{
            builder.append("{names:'").append(k).append("'}");
        });
        return builder.toString();
    }
    /**
     * @param condition 所有的参数
     * */
    public static void compress(Map condition,ZipOutputStream outputStream) throws IOException {
        ClassPathResource resource=new ClassPathResource("htmlTemplates/template");
        File[] files = resource.getFile().listFiles();
        String params = (String) condition.get("post_param");
        Map<String,String> compoments = registerTimerCompoments(JSONObject.parseObject(params));
        String orderBy = getOrderByString(JSONObject.parseObject(params));
        for(File file:files){
            if("add".equals(file.getName())){
                String addList = makeAddList(JSONObject.parseObject(params));
                compressHtml(condition, outputStream, file,addList,compoments);
            }else if("edit".equals(file.getName())){
                Map<String, ColumnProperty> primaryKey = EntityMap.getPrimaryKey((String) condition.get("post_entity"));
                String editList = makeEditList(JSONObject.parseObject(params),primaryKey);
                compressHtml(condition, outputStream, file, editList,compoments);
            }else if("view".equals(file.getName())){
                String viewList = makeViewList(JSONObject.parseObject(params));
                compressHtml(condition,outputStream ,file ,viewList,compoments);
            }else if("list".equals(file.getName())){
                compressList(condition,outputStream,file,compoments,orderBy);
            }
        }
    }

    public static void compressList(Map condition, ZipOutputStream outputStream,File file,Map<String,String> compoments,String orderBy) throws IOException {
        String entityName= (String) condition.get("post_entity");
        String params= (String) condition.get("post_param");
        String listHtml = makeListHtml(JSONObject.parseObject(params));
        String tableList = makeTableList(JSONObject.parseObject(params));
        MainTableInfo mainTable = EntityMap.getMainTable(entityName);
        File[] files = file.listFiles();
        for(File f:files){
            String fileString = FileUtil.getFileString(f);
            StringBuilder builder=new StringBuilder();
            builder.append(entityName).append("/").append(entityName).append("-").append(file.getName()).append("/").append(entityName).append("-").append(f.getName());
            if(f.getName().endsWith(".html")){
                String result=fileString.replaceAll("rows-yangrui", listHtml);
                result=result.replaceAll("entityName-yangrui", mainTable.getTableAlias());
                result = result.replaceAll("aliasName-yangrui", entityName);
                Compress.compressString(result, outputStream, builder.toString());
            }else if(f.getName().endsWith(".js")){
                String result = fileString.replaceAll("entityName-yangrui", mainTable.getTableAlias());//增加界面使用主表替换
                result = result.replaceAll("aliasName-yangrui", entityName);//增加界面使用主表替换
                result=result.replaceAll("fields-yangrui", tableList);
                result=result.replaceAll("timerCompoments-yangrui",compoments.get("timer"));
                result=result.replaceAll("selectCompoments-yangrui", compoments.get("select"));
                result=result.replaceAll("orderBy-yangrui",orderBy);
                if(StringUtils.isEmpty(orderBy)) {
                    if (DataSourceType.SQL.equals(mainTable.getInfoOfEntity().getConfig().getSourceType()))
                        result = result.replaceAll("orderby-yangrui", makeOrderBy(entityName));
                }
                Compress.compressString(result, outputStream, builder.toString());
            }else if(f.getName().endsWith(".css")){
                Compress.compressString(fileString, outputStream, builder.toString());
            }
        }
    }
    /**
     * 压缩并替换模板文件
     * @param  condition 需要替换的条件
     * @param outputStream 压缩文件的输出流
     * @param file 文件夹
     * @param rows 预先需要替换的行
     * */
    public static void compressHtml(Map condition, ZipOutputStream outputStream,File file,String rows,Map<String,String> compoments) throws IOException {
        String entityName= (String) condition.get("post_entity");
        MainTableInfo mainTable = EntityMap.getMainTable((String) condition.get("post_entity"));
        Map<String, ColumnProperty> primaryKey = EntityMap.getPrimaryKey(mainTable.getTableAlias());
        File[] files = file.listFiles();
        for(File f:files){
            String fileString = FileUtil.getFileString(f);
            StringBuilder builder=new StringBuilder();
            builder.append(entityName).append("/").append(entityName).append("-").append(file.getName()).append("/").append(entityName).append("-").append(f.getName());
            if(f.getName().endsWith(".html")){
                String result=fileString.replaceAll("rows-yangrui", rows);
                result=result.replaceAll("entityName-yangrui", mainTable.getTableAlias());
                result=result.replaceAll("aliasName-yangrui", entityName);
                Compress.compressString(result, outputStream, builder.toString());
            }else if(f.getName().endsWith(".js")){
                String result = fileString.replaceAll("entityName-yangrui", mainTable.getTableAlias());//增加界面使用主表替换
                result = result.replaceAll("aliasName-yangrui", entityName);
                result=result.replaceAll("timerCompoments-yangrui",compoments.get("timer"));
                result=result.replaceAll("selectCompoments-yangrui", compoments.get("select"));
                result=result.replaceAll("selectCheckedCompoments-yangrui", compoments.get("selectChecked"));
                Compress.compressString(result, outputStream, builder.toString());
            }else if(f.getName().endsWith(".css")){
                Compress.compressString(fileString, outputStream, builder.toString());
            }
        }
    }

    /**
     * 获取各种组件的字符串
     * */
    private static Map<String, String> registerTimerCompoments(JSONObject params) throws IOException {
        Map<String,String> result=new HashMap<>();
        StringBuilder timeBuilder=new StringBuilder();
        StringBuilder workBuilder = new StringBuilder();
        StringBuilder workCheckedBuilder = new StringBuilder();
        ClassPathResource timerResource = new ClassPathResource("htmlTemplates/inner/timer-compoments");
        ClassPathResource workResource = new ClassPathResource("htmlTemplates/inner/select-js");
        ClassPathResource workCheckedResource = new ClassPathResource("htmlTemplates/inner/select-checked-js");
        String timePattern = FileUtil.getFileString(timerResource.getFile());
        String workPattern = FileUtil.getFileString(workResource.getFile());
        String workCheckedPattern = FileUtil.getFileString(workCheckedResource.getFile());
        Iterator<Object> iterator = params.values().iterator();
        while (iterator.hasNext()){
            JSONObject next = (JSONObject) iterator.next();
            if("timer".equals(next.getString("compomentType"))) {
                String patternOne = timePattern.replaceAll("layDateId-yangrui", next.getString("alias"));
                timeBuilder.append(patternOne);
            }else if(!StringUtils.isEmpty(next.get("workParentValue"))){
                //存入字典
                String parentValue = workPattern.replaceAll("dicValue-yangrui", (String) next.get("workParentValue"));
                parentValue=parentValue.replaceAll("column-yangrui", next.getString("alias"));
                workBuilder.append(parentValue);

                String parentCheckedValue = workCheckedPattern.replaceAll("dicValue-yangrui", (String) next.get("workParentValue"));
                parentCheckedValue=parentCheckedValue.replaceAll("column-yangrui", next.getString("alias"));
                workCheckedBuilder.append(parentCheckedValue);
            }
        }
        result.put("timer", timeBuilder.toString());
        result.put("select", workBuilder.toString());
        result.put("selectChecked", workCheckedBuilder.toString());
        return result;
    }

    /**
     * 获取排序字段的字符串  替换的字符串：orderBy-yangrui
     * */
    private static String getOrderByString(JSONObject params){
        Iterator<Object> iterator = params.values().iterator();
        List<COrderBy> as=new ArrayList<>();
        while (iterator.hasNext()){
            JSONObject next = (JSONObject) iterator.next();
            if(next.get("orderBy")!=null&&!"".equals(next.getString("orderBy"))) {
                COrderBy cOrderBy=new COrderBy();
                cOrderBy.setNames(next.getString("alias"));
                cOrderBy.setDirect(next.getString("orderBy"));
                as.add(cOrderBy);
            }
        }
        if(as.size()>0) {
            return ",orderBy:" + JSONObject.toJSONString(as);
        }
        return "";
    }
}
