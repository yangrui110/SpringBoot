package com.yangframe.config.runOver;

import com.yangframe.config.datasource.dynamic.DynamicDataSource;
import com.yangframe.config.util.ApplicationContextUtil;
import com.yangframe.config.whiteList.WhiteList;
import com.yangframe.web.core.aspect.AspectCrud;
import com.yangframe.web.core.aspect.AspectExternal;
import com.yangframe.web.core.crud.service.BaseServiceImpl;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationRunOver implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        initDatabase();
        initBaseService();
        initAspect();
        //初始化白名单设置
        WhiteList.initWhiteLists();;
    }

    /**
     * 初始化数据库
     * */
    void initDatabase(){
        new DynamicDataSource(ApplicationContextUtil.applicationContext);
    }
    /**
     * 初始化通用的增删改差
     * */
    void initBaseService(){
        BaseServiceImpl.setApplicationContext(ApplicationContextUtil.applicationContext);
    }

    void initAspect(){
        AspectCrud.setApplicationContext(ApplicationContextUtil.applicationContext);
        AspectExternal.setApplicationContext(ApplicationContextUtil.applicationContext);
    }
}
