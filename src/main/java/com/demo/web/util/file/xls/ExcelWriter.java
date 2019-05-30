package com.demo.web.util.file.xls;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @autor 杨瑞
 * @date 2019/5/14 10:09
 * 数据写入Excel表格中
 */
public class ExcelWriter {

    /**
     * 将数据写入到一个sheet中去
     * */
    public static HSSFWorkbook writeIntoOneSheet(String sheetName, List<Map<String,Object>> map,Map<String,String> title){
        HSSFWorkbook workbook=new HSSFWorkbook();
        makeNewSheet(sheetName, map, title, workbook);
        return workbook;
    }

    /**
     * 写入到多个sheet中去
     * */
    public static void makeNewSheet(String sheetName, List<Map<String,Object>> map,Map<String,String> title,HSSFWorkbook workbook){
        HSSFSheet sheet=workbook.createSheet(sheetName);
        HSSFRow row = sheet.createRow(0);

        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式

        //维护一个键值对，保存key和列数之间的对应关系
        Map<String,Integer> integerMap=new HashMap<>();

        Set<String> titles = title.keySet();
        Iterator<String> iterator1 = titles.iterator();
        //将键值作为第一行数据
        int q=0;
        while (iterator1.hasNext()){
            String next = iterator1.next();
            HSSFCell cell = row.createCell(q);
            cell.setCellValue(title.get(next));
            cell.setCellStyle(style);
            integerMap.put(next, q);
            q++;
        }
        //处理其他行的数据
        for(int i=1;i<=map.size();i++){
            HSSFRow row1 = sheet.createRow(i);
            Map detail=map.get(i-1);
            Set<String> set = detail.keySet();
            Iterator<String> iterator = set.iterator();
            while (iterator.hasNext()){
                String next = iterator.next();
                Integer result=integerMap.get(next);
                if(result==null) {
                    //System.out.println("没有获取到：" + next);
                    continue;
                }
                HSSFCell cell = row1.createCell(integerMap.get(next));
                cell.setCellValue(parseToString(detail.get(next)));
                cell.setCellStyle(style);
            }
        }
    }
    private static String parseToString(Object o){
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if(o instanceof Date){
            return dateFormat.format((Date)o);
        }else if(o instanceof Timestamp){
            Timestamp s= (Timestamp) o;
            return dateFormat.format(new Date(s.getTime()));
        }else if(o instanceof Integer){
            return Integer.toString((Integer) o);
        }else if(o instanceof Float){
            return Float.toString((Float) o);
        }else if(o instanceof Double){
            return Double.toString((Double) o);
        }else if(o instanceof String)
            return (String) o;
        else if(o instanceof Long){
            return Long.toString((Long) o);
        }else if(o instanceof BigDecimal){
            return ((BigDecimal)o).toString();
        }
        return "";
    }

}
