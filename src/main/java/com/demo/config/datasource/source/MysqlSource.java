package com.demo.config.datasource.source;

import com.demo.config.datasource.MysqlDataSourceConfig;
import com.demo.config.datasource.condition.MysqlDataSourceCondition;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * @autor 杨瑞
 * @date 2019/5/17 8:53
 */
@Conditional(MysqlDataSourceCondition.class)
@Configuration
@MapperScan(basePackages={"com.demo.web.dao.mysql","com.demo.web.core.crud.dao.mysql"},sqlSessionTemplateRef = "mysqlTemplate")
public class MysqlSource {

    @Autowired
    MysqlDataSourceConfig config;

    /**
     * 创建数据源
     * */
    @Bean(value="mysqlDataSource")
    @Primary
    public DataSource mysqlDataSource(){
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
    @Bean
    @Primary
    public SqlSessionFactory createMysqlSessionFactory() throws Exception {
        SqlSessionFactoryBean factoryBean=new SqlSessionFactoryBean();
        factoryBean.setDataSource(mysqlDataSource());
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/mysql/*.xml"));
        factoryBean.setConfigLocation(new PathMatchingResourcePatternResolver().getResource("classpath:config/mybatisConfig.xml"));
        return factoryBean.getObject();
    }

    /**
     * 创建数据库事务管理器
     * */
    @Bean(value = "mysqlDataSourceTransitionManager")
    @Primary
    public DataSourceTransactionManager mysqlDataSourceTransitionManager(){
        return new DataSourceTransactionManager(mysqlDataSource());
    }

    /**
     * 创建template
     * */
    @Bean(value = "mysqlTemplate")
    @Primary
    public SqlSessionTemplate mysqlTemplate() throws Exception {
        return new SqlSessionTemplate(createMysqlSessionFactory());
    }
}
