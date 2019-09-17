package com.yangframe.config.datasource.dynamic;

import com.alibaba.druid.pool.DruidDataSource;
import com.yangframe.config.datasource.type.DataSourceType;
import com.yangframe.config.util.Util;
import com.yangframe.web.core.xmlUtil.XmlUtil;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/6/13 22:00
 */
public class ReadDruidXml {

    private static InfoOfDruidDataSourceConfig parseElementToDataSource(Element e) throws ClassNotFoundException {
        List<Attribute> attributes = e.attributes();
        DruidDataSource druidDataSource=DefaultDruidDataSourceBuilder.makeDefaultDruiDataSource();
        druidDataSource.setUsername(e.attributeValue("name"));
        druidDataSource.setPassword(e.attributeValue("password"));
        druidDataSource.setUrl(e.attributeValue("url"));
        druidDataSource.setValidationQuery(e.attributeValue("validation-query")==null?"select 1":e.attributeValue("validation-query"));
        druidDataSource.setMaxActive(e.attributeValue("max-active")==null?druidDataSource.getMaxActive():Integer.parseInt(e.attributeValue("max-active")));
        druidDataSource.setMinIdle(e.attributeValue("min-idle")==null?druidDataSource.getMinIdle():Integer.parseInt(e.attributeValue("min-idle")));
        InfoOfDruidDataSourceConfig config=new InfoOfDruidDataSourceConfig();
        config.setDruidDataSource(druidDataSource);
        String value = e.attributeValue("source-bean-name");
        String sourceType=e.attributeValue("sourceType")==null?"mysql":e.attributeValue("sourceType");
        String key= sourceType+Util.getRandUUID();
        if(!StringUtils.isEmpty(value)){
            key=sourceType+value;
        }
        config.setSourceBeanName(key);
        config.setSourceDaoPath(e.attributeValue("source-dao-path").split(","));
        config.setSourceDaoXmlPath(e.attributeValue("source-dao-xml-path").split(","));
        config.setSourceBeanXmlPath(e.attributeValue("source-bean-xml-path"));
        String s = e.attributeValue("dao-base-class-name");
        config.setDaoBaseClassName(Class.forName(s));
        config.setSourceType(e.attributeValue("sourceType")==null?DataSourceType.MYSQL:e.attributeValue("sourceType"));
        return  config;
    }

    private static Map<String,InfoOfDruidDataSourceConfig> readEntity(String xml) throws UnsupportedEncodingException, DocumentException, ClassNotFoundException {
        SAXReader reader=new SAXReader();
        Map map=new HashMap();
        Document document=reader.read(new ByteArrayInputStream(xml.getBytes("utf-8")));
        Element element=document.getRootElement();
        List<Element> list=element.elements();
        for (Element e :list) {
            InfoOfDruidDataSourceConfig config = parseElementToDataSource(e);
            map.put(config.getSourceBeanName(), config);
        }
        return map;
    }

    /**
     * 读取druid的入口类
     * */
    public static Map readDatasource() throws IOException, DocumentException, ClassNotFoundException {
        //读取datasource.xml对应的数据源配置
        PathMatchingResourcePatternResolver resource=new PathMatchingResourcePatternResolver();
        String xml= XmlUtil.getXml(resource.getResource("classpath:config/datasource.xml"));
        return readEntity(xml);
    }
}
