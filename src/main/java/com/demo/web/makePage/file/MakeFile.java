package com.demo.web.makePage.file;

import com.alibaba.fastjson.JSONObject;
import com.demo.config.datasource.type.DataSourceType;
import com.demo.web.core.crud.centity.MainTableInfo;
import com.demo.web.core.xmlEntity.ColumnProperty;
import com.demo.web.core.xmlEntity.EntityMap;
import com.demo.web.core.xmlEntity.InfoOfEntity;
import com.demo.web.util.file.FileUtil;
import com.demo.web.util.file.zip.Compress;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
        ClassPathResource rowResource = new ClassPathResource("htmlTemplates/inner/list-row");
        String row = FileUtil.getFileString(rowResource.getFile());
        System.out.println("替换前："+pattern);

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
            StringBuffer builder=new StringBuffer();
            String describetion = pattern.replaceAll("innerDesc-yangrui", next.getString("describetion"));
            describetion=describetion.replaceAll("innerColumn-yangrui", next.getString("alias"));
            builder.append(describetion);
            if(iterator.hasNext()){
                JSONObject next2 = (JSONObject) iterator.next();
                String describetion2 = pattern.replaceAll("innerDesc-yangrui", next2.getString("describetion"));
                describetion2=describetion2.replaceAll("innerColumn-yangrui", next.getString("alias"));
                builder.append(describetion2);
            }
            System.out.println(builder.toString());
            result.append(row.replaceAll("rows-yangrui",builder.toString() ));
        }
        return result.toString();
    }
    /**
     * @function 制作列表显示的列
     * */
    public static String makeTableList(JSONObject map) throws IOException {
        ClassPathResource tableResource = new ClassPathResource("htmlTemplates/inner/list-table");
        String pattern = FileUtil.getFileString(tableResource.getFile());
        StringBuilder result=new StringBuilder();
        Iterator<Object> iterator = map.values().iterator();
        while (iterator.hasNext()){
            JSONObject next = (JSONObject) iterator.next();
            if(next.getBoolean("show"))
                result.append(pattern.replaceAll("listTableField", next.getString("alias")));
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
        Iterator iterator = columns.values().iterator();
        while (iterator.hasNext()){
            JSONObject os = (JSONObject) iterator.next();
            if(os.getBoolean("edit")){
                String s=pattern.replaceAll("label-yangrui", os.getString("describetion"));
                s=s.replaceAll("column-yangrui", os.getString("column"));
                builder.append(s);
            }else{
                String s=patternReadOnly.replaceAll("label-yangrui", os.getString("describetion"));
                s=s.replaceAll("column-yangrui", os.getString("column"));
                builder.append(s);
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
        Iterator iterator = columns.values().iterator();
        while (iterator.hasNext()){
            JSONObject os = (JSONObject) iterator.next();
            if(os.getBoolean("add")){
                String s=pattern.replaceAll("label-yangrui", os.getString("describetion"));
                s=s.replaceAll("column-yangrui", os.getString("column"));
                builder.append(s);
            }
        }
        return builder.toString();
    }

    /**
     * 制作增加界面的列
     * */
    public static String makeViewList(Map columns) throws IOException {
        ClassPathResource tableResource = new ClassPathResource("htmlTemplates/inner/view-row");
        String pattern = FileUtil.getFileString(tableResource.getFile());
        StringBuilder builder=new StringBuilder();
        Iterator iterator = columns.values().iterator();
        while (iterator.hasNext()){
            JSONObject os = (JSONObject) iterator.next();
            if(os.getBoolean("view")){
                String s=pattern.replaceAll("label-yangrui", os.getString("describetion"));
                s=s.replaceAll("column-yangrui", os.getString("column"));
                builder.append(s);
            }
        }
        return builder.toString();
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
        for(File file:files){
            if("add".equals(file.getName())){
                String addList = makeAddList(JSONObject.parseObject(params));
                compressHtml(condition, outputStream, file,addList);
            }else if("edit".equals(file.getName())){
                Map<String, ColumnProperty> primaryKey = EntityMap.getPrimaryKey((String) condition.get("post_entity"));
                String editList = makeEditList(JSONObject.parseObject(params),primaryKey);
                compressHtml(condition, outputStream, file, editList);
            }else if("view".equals(file.getName())){
                String viewList = makeViewList(JSONObject.parseObject(params));
                compressHtml(condition,outputStream ,file ,viewList );
            }else if("list".equals(file.getName())){
                compressList(condition,outputStream,file);
            }
        }
    }

    public static void compressList(Map condition, ZipOutputStream outputStream,File file) throws IOException {
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
                result=result.replaceAll("entityName-yangrui", entityName);
                Compress.compressString(result, outputStream, builder.toString());
            }else if(f.getName().endsWith(".js")){
                String result = fileString.replaceAll("entityName-yangrui", mainTable.getTableAlias());//增加界面使用主表替换
                result = result.replaceAll("aliasName-yangrui", entityName);//增加界面使用主表替换
                result=result.replaceAll("fields-yangrui", tableList);
                if(DataSourceType.SQL.equals(mainTable.getInfoOfEntity().getConfig().getSourceType()))
                    result=result.replaceAll("orderby-yangrui", makeOrderBy(entityName));
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
    public static void compressHtml(Map condition, ZipOutputStream outputStream,File file,String rows) throws IOException {
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
                result=result.replaceAll("entityName-yangrui", entityName);
                Compress.compressString(result, outputStream, builder.toString());
            }else if(f.getName().endsWith(".js")){
                String result = fileString.replaceAll("entityName-yangrui", mainTable.getTableAlias());//增加界面使用主表替换
                Compress.compressString(result, outputStream, builder.toString());
            }else if(f.getName().endsWith(".css")){
                Compress.compressString(fileString, outputStream, builder.toString());
            }
        }
    }
}
