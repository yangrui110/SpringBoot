package com.demo.web.core.xmlUtil;

import com.demo.config.datasource.dynamic.InfoOfDruidDataSourceConfig;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/6/14 12:05
 * 解析mapper.xml文件
 */
public class MapperUtil {

    /**
     * @param path classpath:/*.xml
     * */
    public static Resource[] getResources(String path) throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        return resolver.getResources(path);
    }

    /**
     * 获取命名空间对应的java类
     * */
    public static Class readMapperNamespace(String xml) throws UnsupportedEncodingException, DocumentException, ClassNotFoundException {
        SAXReader reader=new SAXReader();
        Document document=reader.read(new ByteArrayInputStream(xml.getBytes("utf-8")));
        Element element=document.getRootElement();
        String namespace = element.attributeValue("namespace");
        if(namespace!=null)
         return ClassLoader.getSystemClassLoader().loadClass(namespace);
        return null;
    }
}
