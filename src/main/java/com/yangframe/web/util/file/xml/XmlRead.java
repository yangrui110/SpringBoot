package com.yangframe.web.util.file.xml;

import org.dom4j.DocumentException;
import org.dom4j.Element;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/5/15 16:15
 * 读取xml的elemnt对象
 */
public class XmlRead {

    /**
     * 解析某个实体的定义，转为List<Map<String,String>>
     * */
    public static List<Map<String,String>> getElementAttribute(Element element){
        List<Element> elements = element.elements();
        elements.forEach((value)->{
            System.out.println(value.getName()+"---"+value.attributeValue("name"));
        });
        return null;
    }

    public static void main(String[] args) throws IOException, DocumentException {
    }
}
