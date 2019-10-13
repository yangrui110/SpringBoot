package com.yangframe.config.datasource.dynamic;

import com.yangframe.config.util.BeanOperatorUtil;
import com.yangframe.web.core.xmlEntity.EntityMap;
import org.apache.ibatis.session.SqlSessionFactory;
import org.dom4j.DocumentException;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @autor 杨瑞
 * @date 2019/6/11 18:44
 */
public class DynamicDataSource implements ResourceLoaderAware {

    private ApplicationContext applicationContext;
    private ResourceLoader resourceLoader;

    public static Map<String,InfoOfDruidDataSourceConfig> configMap = new HashMap<>();

    public DynamicDataSource(ApplicationContext applicationContext) {
        setApplicationContext(applicationContext);
    }

    //主方法，在setApplicationContext方法中被调用
    public void makeFactroy() {
        configMap.forEach((k,v)->{
            makeDataSource(v);
            DataSource builder = (DataSource) applicationContext.getBean(k);
            makeTransactionalManager(builder,k);
            makeSqlFactory(builder, k,v.getSourceDaoXmlPath());
            Object factory = applicationContext.getBean(k+"factory");
            makeSqlTemplate((SqlSessionFactory) factory,k);
            makeMapperScan(v);
            makeEntityXml(v);
        });
    }

    /**
     * 读取xml的实体定义文件
     * */
    private void makeEntityXml(InfoOfDruidDataSourceConfig config){
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            Resource[] resources = resolver.getResources(config.getSourceBeanXmlPath());
            for (Resource resource:resources){
                EntityMap.readOneToMap(resource,config);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext=applicationContext;
        try {
            configMap=ReadDruidXml.readDatasource();
            makeFactroy();
        }catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader=resourceLoader;
    }

    private void makeDataSource(InfoOfDruidDataSourceConfig config){
        try {
            BeanOperatorUtil.addBean((ConfigurableApplicationContext) applicationContext,config.getSourceBeanName(),DataSource.class,()->{
                return config.getDruidDataSource();
            });
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void makeTransactionalManager(DataSource config,String key) {
        //事务管理器
        try {
            BeanOperatorUtil.addBean((ConfigurableApplicationContext) applicationContext, key+"manager", DataSourceTransactionManager.class, () -> {
                DataSourceTransactionManager manager = new DataSourceTransactionManager(config);
                return manager;
            });
            System.out.println("事务管理器"+key+"manager注入完毕");
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
    }
    //
    private void makeSqlFactory(DataSource dataSource,String key,String[] path){
        try {
            BeanOperatorUtil.addBean((ConfigurableApplicationContext) applicationContext, key+"factory", SqlSessionFactory.class,()->{
                SqlSessionFactoryBean factoryBean=new SqlSessionFactoryBean();
                factoryBean.setDataSource(dataSource);
                try {
                    Resource[] resources=new Resource[]{};
                    for (String pathOne:path) {
                        Resource[] resourcesOne = new PathMatchingResourcePatternResolver().getResources(pathOne);
                        int orign=resources.length;
                        resources=new Resource[resources.length+resourcesOne.length];
                        System.arraycopy(resourcesOne, 0, resources, orign, resourcesOne.length);
                    }
                    factoryBean.setMapperLocations(resources);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                factoryBean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:config/mybatisConfig.xml"));
                SqlSessionFactory sqlSessionFactory = null;
                try {
                    sqlSessionFactory = factoryBean.getObject();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return sqlSessionFactory;
            });
            System.out.println("sqlSessionFatory注入完毕");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void makeSqlTemplate(SqlSessionFactory factory,String key){
        try {
            BeanOperatorUtil.addBean((ConfigurableApplicationContext) applicationContext, key+"template", SqlSessionTemplate.class,()->{
                SqlSessionTemplate template = new SqlSessionTemplate(factory);
                return template;
            });
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void makeMapperScan(InfoOfDruidDataSourceConfig info){
        MapperScanConfig config=new MapperScanConfig();
        String[] as=info.getSourceDaoPath();
        config.setBasePackages(as);
        config.setSqlSessionTemplateRef(info.getSourceBeanName()+"template");
        try {
            BeanOperatorUtil.addBean((ConfigurableApplicationContext) applicationContext, info.getSourceBeanName()+"mapper", ClassPathMapperScanner.class,()->{
                ClassPathMapperScanner scanner = DynamicMapperScan.getScanner(config, BeanOperatorUtil.getRegistry((ConfigurableApplicationContext) applicationContext), resourceLoader);
                return scanner;
            });
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        ClassPathMapperScanner mapperOk1 = (ClassPathMapperScanner) applicationContext.getBean(info.getSourceBeanName()+"mapper");
        List<String> basePackages = new ArrayList<String>();
        for (String pkg : as) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        mapperOk1.registerFilters();
        mapperOk1.doScan(StringUtils.toStringArray(basePackages));
    }
}
