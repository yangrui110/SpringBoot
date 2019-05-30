package com.demo.config.datasource.source;

import com.demo.config.datasource.OracleDataSourceConfig;
import com.demo.config.datasource.SqlDataSourceConfig;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @autor 杨瑞
 * @date 2019/5/17 8:53
 */
@Configuration
@MapperScan(basePackages={"com.demo.web.core.crud.sql.dao"},sqlSessionTemplateRef = "sqlTemplate")
public class SqlSource {

    @Autowired
    SqlDataSourceConfig config;

    /**
     * 创建数据源
     * */
    @Bean(value="sqlDataSource")
    public DataSource sqlDataSource(){
        DataSource build = DataSourceBuilder.create().url(config.getUrl())
                .password(config.getPassword())
                .username(config.getUsername())
                .build();
        System.out.println(config.toString());
        return build;
    }

    /**
     * 创建连接工厂类
     * */
    @Bean(value = "createSqlSessionFactory")
    public SqlSessionFactory createSqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean=new SqlSessionFactoryBean();
        factoryBean.setDataSource(sqlDataSource());
        Resource[] resources = new PathMatchingResourcePatternResolver().getResources("classpath:mapper/sql/*.xml");
        System.out.println("length="+resources.length);
        factoryBean.setMapperLocations(resources);
        factoryBean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:config/mybatisConfig.xml"));
        return factoryBean.getObject();
    }

    /**
     * 创建数据库事务管理器
     * */
    @Bean(value = "sqlDataSourceTransitionManager")
    public DataSourceTransactionManager sqlDataSourceTransitionManager(){
        return new DataSourceTransactionManager(sqlDataSource());
    }

    /**
     * 创建template
     * */
    @Bean
    public SqlSessionTemplate sqlTemplate() throws Exception {
        return new SqlSessionTemplate(createSqlSessionFactory());
    }
}
