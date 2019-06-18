package com.demo.web.core.xmlEntity;

import com.demo.config.advice.BaseException;
import com.demo.config.datasource.dynamic.InfoOfDruidDataSourceConfig;
import com.demo.config.datasource.type.DataSourceType;
import com.demo.web.core.crud.centity.COrderBy;
import com.demo.web.core.crud.centity.ConditionEntity;
import com.demo.web.core.crud.centity.FindEntity;
import com.demo.web.core.xmlUtil.XmlUtil;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/5/11 16:30
 * <p>读取的实体定义文件都存放在这个类的static变量中</p>
 */
public class EntityMap {

    private static Logger logger= LoggerFactory.getLogger(EntityMap.class);
    //每一个Map中存放的都是其key和document对象,只可访问该对象
    public static final Map<String,Element> entitys=new HashMap();

    //维护表别名和表之间的关系，key=tableAlias,value=tableName
    public static final Map<String,InfoOfEntity> tables=new HashMap<>();
    /**
     * 获取表的对应关系
     * */
    public static String getTableName(String key){
        InfoOfEntity entity = tables.get(key);
        if(entity==null) {
            logger.info("该表"+key+"未定义");
            throw new BaseException(304, "该表"+key+"未定义");
        }
        return entity.getEntityName();
    }
    /**
     * 判断传入的实体别名是否为空，如果为空，则抛出异常信息
     * */
    public static InfoOfEntity getAndJugeNotEmpty(String key){
        InfoOfEntity entity1 = tables.get(key);
        if(entity1==null)
            throw new BaseException(304,"未定义的实体别名");
        return entity1;
    }
    /**
     * 判断是否是个视图表
     * @return 如果是视图，则返回为true
     * */
    public static boolean judeIsViewEntity(String entityName){
        InfoOfEntity entity = tables.get(entityName);
        return entity.isView();
    }
    /**
     * <p>初始化所有的实体定义xml文件到entitys变量中</p>
     * */
    public static void readXmlIntoMap(InfoOfDruidDataSourceConfig config) throws IOException, DocumentException {

        PathMatchingResourcePatternResolver resolver=new PathMatchingResourcePatternResolver();
        Resource[] resources=resolver.getResources("classpath:entity/**/*.xml");
        for (Resource r:resources) {
            if(r.getFilename().endsWith(".xml"))
                readOneToMap(r,config);
        }

    }

    /**
     * 读取一个资源文件到实体中去
     * */
    public static void readOneToMap(Resource resource, InfoOfDruidDataSourceConfig config) throws IOException, DocumentException {
        //解析完毕
        String xml= XmlUtil.getXml(resource);
        Map<String,Element> map=readEntity(xml,config);
        map.forEach((key,value)->{
            InfoOfEntity entity = tables.get(key);
            entity.setConfig(config);
            entitys.put(key, value);
            tables.put(key, entity);
        });
    }

    /**
     * 把oracle的表名转为双引号包裹
     * */
    private static String parseDoubleQuote(String column){

        return "\""+column+"\"";
    }
    /**
     * 把oracle的列名转为双引号
     * */
    private static void parseColumnDoubleQuote(ColumnProperty column){
        if(!StringUtils.isEmpty(column.getColumn()))
            column.setColumn(parseDoubleQuote(column.getColumn()));
        if(!StringUtils.isEmpty(column.getAlias()))
            column.setAlias(parseDoubleQuote(column.getAlias()));
        if (!StringUtils.isEmpty(column.getTableName()))
            column.setTableName(parseDoubleQuote(column.getTableName()));
    }
    /**
     * <p>将xml文本解析为Map对象</p>
     * */
    public static Map<String,Element> readEntity(String xml,InfoOfDruidDataSourceConfig config) throws UnsupportedEncodingException, DocumentException {
        SAXReader reader=new SAXReader();
        Map map=new HashMap();
        Document document=reader.read(new ByteArrayInputStream(xml.getBytes("utf-8")));
        Element element=document.getRootElement();
        List<Element> list=element.elements();
        for (Element element1:list) {
            Attribute ats=element1.attribute("tableName");
            Attribute alias=element1.attribute("tableAlias");
            //需要抛出异常信息
            InfoOfEntity entity=new InfoOfEntity();
            if("view-entity".equals(element1.getName())) {
                String al=alias.getValue();
                entity.setView(true);
                entity.setEntityName(al);
                entity.setEntityAlias(al);
                entity.setConfig(config);
                tables.put(al,entity);
                map.put(al, element1);
                continue;
            }
            if(ats==null)
                throw new BaseException(304,"存在未定义tableName的entity标签");
            String key=ats.getValue();
            if(alias!=null) {
                key=alias.getValue();
                if(!StringUtils.isEmpty(key)) {
                    if(tables.containsKey(key)) {
                        logger.error("该表{}的别名重复，请检查后输入{}", ats.getValue(), key);
                        throw new BaseException(304,"该表{}的别名重复，请检查后输入{}");
                    }
                    InfoOfEntity entity1=new InfoOfEntity();
                    entity1.setEntityName(ats.getValue());
                    entity1.setEntityAlias(key);
                    entity1.setConfig(config);
                    tables.put(key,entity1);
                }
            }
            map.put(key, element1);
        }
        return map;
    }

    /**
     * 获取entityName值
     * */
    public static String getEntityName(String key){
        if(StringUtils.isEmpty(key)){
            throw new BaseException(304, "表名不能为空");
        }
        Object os=tables.get(key);
        if(os==null)
            return key;
        return (String)os;
    }

    /**
     * 读取实体xml到String字符串中,构造成sql语句中的查询列属性
     * */
    public static ConditionEntity readEntityToCondition(String entityName,Map<String,Object> cons,List<COrderBy> orderBy){
        Element element=getElement(entityName);
        if(element==null)
            throw new BaseException(304, "表名"+entityName+"不存在");
        if(cons==null)
            cons=new HashMap<>();
        if(orderBy==null)
            orderBy=new ArrayList<>();
        if("entity".equals(element.getName())){
            return readEntityLabel(entityName, cons, orderBy);
        }
        else if("view-entity".equals(element.getName())){
            return readColumnViewEntity(element, cons,orderBy );
        } else return null;
    }

    public static ConditionEntity readEntityLabel(String entityName, Map<String,Object> cons, List<COrderBy> orderBy){
        ConditionEntity entity=new ConditionEntity();
        Element element=getElement(entityName);
        Map<String, ColumnProperty> propertyMap = readColumnEntity(element);
        parseWhereCondition(propertyMap, element.attributeValue("tableAlias"));
        entity.setColumns(makeSelectColumn(propertyMap));
        entity.setMainTable(getTableName(entityName));
        entity.setMainAlias("a");
        entity.setCondition(makeWhereCondition(cons, propertyMap));
        //entity.setCondition(cons==null?new HashMap<>():cons);
        StringBuilder builder=new StringBuilder();
        orderBy.forEach((k)->{
            builder.append(k.getNames()).append(" ").append(k.getDirect()).append(",");
        });
        String os="";
        if(builder.length()>0){
            os=builder.substring(0, builder.length()-1);
        }
        entity.setOrderBy(os);
        entity.setJoins(null);
        return entity;
    }

    /**
     * 读取view-entity标签
     * */
    public static ConditionEntity  readColumnViewEntity(Element element,Map<String,Object> cons, List<COrderBy> orderBy){
        ConditionEntity entity=new ConditionEntity();  //需要返回的条件
        //第一遍遍历，获取主表以及key-map值,alias对应tableAlias
        Map<String,String> keyMap=getMemberMap(element);//存放表和表别名之间的对应关系
        entity.setMainTable(getTableName(keyMap.get("great-main-table")));
        entity.setMainAlias(keyMap.get("great-main-table-alias"));
        //第二次遍历，获取需要查询的列
        Map<String,ColumnProperty> columns=getViewColumns(element);
        parseWhereCondition(columns, keyMap.get("great-main-table"));
        entity.setColumns(makeSelectColumn(columns));
        //第三遍遍历，获取left join关系
        entity.setJoins(makeJoinString(element));
        //处理查询条件
        entity.setCondition(makeWhereCondition(cons, columns));
        //处理排序字段
        entity.setOrderBy(makeOrderBy(orderBy, columns));
        return entity;
    }

    /**
     * 制作需要查询的列
     * */
    private static String makeSelectColumn(Map<String,ColumnProperty> columns){
        StringBuilder builder=new StringBuilder();
        columns.forEach((k,v)->{
            builder.append(v.getTableMemberAlias()).append(".").append(v.getColumn()).append(" as ").append(v.getAlias())
                    .append(",");
        });
        return builder.substring(0, builder.length()-1);
    }

    /**
     * 制作join关联语句
     * */
    private static String makeJoinString(Element element){
        StringBuilder join=new StringBuilder();
        Map<String,JoinRelation> relations=getJoinRelation(element);
        //获取view-link的出现顺序
        List<String> sortViewLink = getSortViewLink(element);
        sortViewLink.forEach((m)->{
            JoinRelation relation = relations.get(m);
            join.append(relation.getJoin()).append(" ").append(relation.getReferTable()).append(" ").append(relation.getReferMemberAlias())
                    .append(" on ");
            List<String> cols=relation.getColumn();
            for (int i = 0; i < cols.size(); i++) {
                join.append(relation.getTableMemberAlias()).append(".").append(cols.get(i))
                        .append(" = ")
                        .append(relation.getReferMemberAlias()).append(".").append(relation.getReferColumn().get(i))
                        .append(" ");
            }
        });
        return join.toString();
    }

    /**
     * 制作查询条件
     * */
    private static String makeWhereCondition(Map<String,Object> cons,Map<String,ColumnProperty> columns){
        StringBuilder builder=new StringBuilder();
            Object list=cons.get("conditionList");
            Object combine = cons.get("combine")==null?"and":cons.get("combine");
            if(list!=null&&list instanceof List&&((List) list).size()>0){
                ((List<Map>) list).forEach((k)->{
                    Object conditionList = k.get("conditionList");
                    Object left = k.get("left");
                    Object right = k.get("right");
                    Object operator = k.get("operator");
                    if(conditionList!=null)
                        builder.append(" (").append(makeWhereCondition(k, columns)).append(") ");
                    if (conditionList==null){
                        if(!StringUtils.isEmpty(left)&&!StringUtils.isEmpty(right)){
                            ColumnProperty property = columns.get(left);
                            builder.append(" ").append(property.getTableMemberAlias()).append(".").append(property.getColumn()).append(" ")
                                    .append(operator==null?"=":operator).append(" ")
                                    .append("'").append(right).append("'").append(" ");
                            builder.append(combine).append(" ");
                        }
                    }
                });
                return builder.substring(0, builder.length()-combine.toString().length()-1);
            }

        /*
        columns.forEach((s,v)->{
            cons.forEach((k,l)->{
                if("left".equals(k)||"right".equals(k)||"operator".equals(k)) {
                    if (l instanceof Map) {
                        builder.append("(").append(makeWhereCondition((Map<String, Object>) l, columns)).append(")");
                    } else if (l instanceof String) {
                        builder.append(l);
                    }
                }
                /*Object o =k.getLeft();
                if(o!=null&&v.getAlias().equals(o)){
                    k.setLeft(v.getTableMemberAlias()+"."+v.getColumn());
                }
            });
        });
*/
        return builder.toString();
    }
    /**
     * 处理排序字段
     * */
    private static String makeOrderBy(List<COrderBy> orderBy,Map<String,ColumnProperty> columns){
        StringBuilder bu=new StringBuilder();
        String result="";
        columns.forEach((k,v)->{
            orderBy.forEach((m)->{
                if(m.getNames().equals(v.getAlias())){
                    bu.append(" ").append(v.getTableMemberAlias()).append(".").append(v.getColumn()).append(" ").append(m.getDirect()).append(",");
                }
            });
        });
        if(bu.length()>0)
            result=bu.substring(0, bu.length()-1);
        return result;
    }
    /**
     * @param element 传入的table元素
     * */
    private static List<String> getSortViewLink(Element element){
        List<Element> elements = element.elements();
        List<String> result=new ArrayList<>();
        elements.forEach((k)->{
           if("view-link".equals(k.getName())){
               String relTableAlias = k.attributeValue("relTableAlias");
               result.add(relTableAlias);
           }
        });
        return result;
    }
    /**
     * 获取表之间的关联关系
     * */
    private static Map<String,JoinRelation> getJoinRelation(Element element){
        Map<String,JoinRelation> relationMap=new HashMap<>();
        List<Element> elements = element.elements();
        Map<String,String> keyMap=getMemberMap(element);
        elements.forEach((k)->{
            JoinRelation relation=new JoinRelation();
            if("view-link".equals(k.getName())){
                String curTable=k.attributeValue("tableAlias");
                String relTable=k.attributeValue("relTableAlias");
                String relOption=k.attributeValue("relOptional");
                if("true".equals(relOption))
                    relOption=" inner join ";
                else relOption=" left join ";
                relation.setJoin(relOption);
                relation.setTableMemberAlias(curTable);
                relation.setTableAlias(keyMap.get(curTable));
                relation.setTableName(getTableName(keyMap.get(curTable)));
                relation.setReferMemberAlias(relTable);
                relation.setReferTableAlias(keyMap.get(relTable));
                relation.setReferTable(getTableName(keyMap.get(relTable)));
                List<Element> elements1 = k.elements();
                elements1.forEach((v)->{
                    String fieldName=v.attributeValue("field-name");
                    String relFieldName=v.attributeValue("rel-field-name");
                    relation.getColumn().add(fieldName);
                    relation.getReferColumn().add(relFieldName);
                });
                relationMap.put(relTable, relation);
            }
        });
        return relationMap;
    }
    /**
     * 读取entity标签
     * */
    public static Map<String,ColumnProperty> readColumnEntity(Element element){
        String tableName = element.attributeValue("tableName");
        List<Element> elements = element.elements();
        Map<String,ColumnProperty> columns=new HashMap();
        elements.forEach((el)->{
            if(el.getName().equals("column")) {
                ColumnProperty property=new ColumnProperty();
                String name = el.attributeValue("name");
                String alias = el.attributeValue("alias");
                alias = alias == null ? name : alias;
                if (name == null)
                    throw new BaseException(304, "该标签" + el.getName() + "的name属性不能为空");
                property.setColumn(name);
                property.setAlias(alias);
                property.setTableName(tableName);
                property.setTableAlias("a");
                property.setTableMemberAlias("a");
                columns.put(alias, property);
            }
        });
        return columns;
    }
    /**
     * 制作oracle的条件
     * */
    public static void makeOracleData(FindEntity entity){
        if(entity.getData()!=null){
            Map map=new HashMap();
            entity.getData().forEach((k,v)->{
                map.put(parseDoubleQuote((String) k), v);
            });
            entity.setData(map);
        }
        /*if(entity.getCondition().size()>0){
            for (CEntity cEntity : entity.getCondition()) {
                cEntity.setLeft(parseDoubleQuote(cEntity.getLeft()));
            }
        }*/
    }
    /**
     * 验证输入的Map键值是否存在实体定义中
     * */
    public static void yanzhengMap(Map<String,Object> map,String entityName){
        Element element=getElement(entityName);
        if("view-entity".equals(element.getName())){
            ensureColumn(map, getViewColumns(entityName));
        }else if("entity".equals(element.getName())){
            ensureColumn(map,getColumnMap(entityName));
        }else {
            throw new BaseException(304, "实体不能为空");
        }

    }
    private static void ensureColumn(Map<String,Object> map,Map keys){
        map.forEach((key,value)->{
            if(!keys.containsKey(key)){
                System.out.println("该key:"+key+"没有被定义");
                throw new BaseException(304, "该key:"+key+"没有被定义");
            }
        });
    }

    /**
     * @param entityName 对应的实体名
     * */
    private static Map<String,ColumnProperty> getColumnMap(String entityName){
        Element element=getElement(entityName);
        return getColumnMap(element);
    }
    /**
     * 获取实体的列值
     * @param element 对应的实体
     * */
    private static Map<String,ColumnProperty> getColumnMap(Element element){

        List<Element> elements = element.elements();
        String table=element.attributeValue("tableAlias");
        String tableName=element.attributeValue("tableName");
        Map<String,ColumnProperty> keys=new HashMap();
        elements.forEach((el)->{
            if("column".equals(el.getName())) {
                String name = el.attributeValue("name");
                String alias = el.attributeValue("alias");
                alias = alias == null ? name : alias;
                if (name == null)
                    throw new BaseException(304, "该标签" + el.getName() + "的name属性不能为空");
                ColumnProperty property=new ColumnProperty();
                property.setColumn(name);
                property.setAlias(alias);
                property.setTableMemberAlias("a");
                property.setTableAlias(table);
                property.setTableName(tableName);
                keys.put(alias, property);
            }

        });
        return keys;
    }

    /**
     * @param entityName 实体名
     * */
    public static Map<String,ColumnProperty> getViewColumns(String entityName){
        Element element=getElement(entityName);
        return getViewColumns(element);
    }

    /**
     * 获取视图的所有列
     * @return map 返回的Map集合中，包含对应的列以及列的相关属性
     * */
    private static Map<String,ColumnProperty> getViewColumns(Element element){
        Map<String,String> keyMap=getMemberMap(element);
        List<Element> elements = element.elements();
        Map<String,ColumnProperty> keys=new HashMap();
        elements.forEach((el)->{
            if("alias".equals(el.getName())){
                String alias=el.attributeValue("alias");
                String name=el.attributeValue("column");
                String aliasTable=el.attributeValue("referTable");
                alias=alias==null?name:alias;
                ColumnProperty property=new ColumnProperty();
                property.setAlias(alias);
                property.setColumn(name);
                property.setTableMemberAlias(aliasTable);
                property.setTableAlias(keyMap.get(aliasTable));
                property.setTableName(getTableName(keyMap.get(aliasTable)));
                if(alias!=null)
                    keys.put(alias, property);
            }else if("alias-all".equals(el.getName())){
                Map<String, String> memberMap = getMemberMap(element);
                String aliasTable=el.attributeValue("referTable");
                Map<String,ColumnProperty> result=getColumnMap(memberMap.get(aliasTable));
                excludeColumn(result,el);
                result.forEach((k,v)->{
                    ColumnProperty property=new ColumnProperty();
                    property.setTableName(getTableName(keyMap.get(aliasTable)));
                    property.setTableAlias(keyMap.get(aliasTable));
                    property.setTableMemberAlias(aliasTable);
                    property.setAlias(k);
                    property.setColumn(k);
                    keys.put(k, property);
                });
            }
        });
        return keys;
    }
    /**
     * 排除掉exclude标签的列
     * @param map 所有的列值
     * @param element alias-all标签对应的element元素
     * */
    private static void excludeColumn(Map map,Element element){
        List<Element> elements = element.elements();
        elements.forEach((k)->{
            if("exclude".equals(k.getName())){
                String column=k.attributeValue("column");
                if(!StringUtils.isEmpty(column)){
                    if(map.containsKey(column)){
                        map.remove(column);
                    }
                }
            }
        });
    }
    /**
     * 获取member-entity实体定义
     * */
    private static Map<String,String> getMemberMap(Element element){
        Map<String,String> map=new HashMap();
        List<Element> elements = element.elements();
        elements.forEach((k)->{
            String name=k.getName();
            if("member-entity".equals(name)){
                String tableAlias = k.attributeValue("tableAlias");
                String alias=k.attributeValue("alias");
                if(tableAlias!=null&&alias!=null) {
                    if (map.get("great-main-table") == null) {
                        map.put("great-main-table",tableAlias);
                        map.put("great-main-table-alias",alias);
                    }
                    map.put(alias, tableAlias);
                }
            }
        });
        return map;
    }
    /**
     * 获取主键
     * */
    public static String getPK(String entityName){
        Element element=getElement(entityName);
        List<Element> elements = element.elements();
        Map<String,Object> pk=new HashMap<>();
        StringBuilder builder=new StringBuilder();
        elements.forEach((k)->{
            if("primary-key".equals(k.getName())){
               String name=k.attributeValue("name");
               if(StringUtils.isEmpty(name))
                   throw new BaseException(304, "主键属性为null");
               else builder.append(name);
            }
        });
        /*elements.forEach((k)->{
            if("column".equals(k.getName())){
                if(builder.toString().)
            }
        });*/
        return StringUtils.isEmpty(builder.toString())?null:builder.toString();
    }

    private static Element getElement(String entityName){
        Element element=entitys.get(entityName);
        return  element;
    }

    /**
     * 处理更新、删除、插入的数据列操作
     * */
    public static void dealUpCondition(FindEntity findEntity){
        Map<String, ColumnProperty> columnMap = getColumnMap(findEntity.getEntityName());
        parseWhereCondition(columnMap,findEntity.getEntityName());
        findEntity.setCons(makeWhereCondition(findEntity.getCondition(), columnMap));
    }

    //将所需要的where条件转为指定的数据源类型
    private static void parseWhereCondition(Map<String,ColumnProperty> columnMap,String entityName){
        Element element = getElement(entityName);
        InfoOfEntity andJugeNotEmpty = getAndJugeNotEmpty(element.attributeValue("tableAlias"));
        columnMap.forEach((k,v)->{
            if(DataSourceType.ORACLE.equals(andJugeNotEmpty.getConfig().getSourceType()))
                parseColumnDoubleQuote(v);
        });
    }
}
