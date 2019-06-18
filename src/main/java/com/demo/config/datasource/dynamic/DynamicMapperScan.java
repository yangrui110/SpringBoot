package com.demo.config.datasource.dynamic;

import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @autor 杨瑞
 * @date 2019/6/11 21:15
 */
public class DynamicMapperScan {

    public static ClassPathMapperScanner getScanner(MapperScanConfig mapperScanConfig, BeanDefinitionRegistry registry, ResourceLoader resourceLoader){
        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);

        // this check is needed in Spring 3.1
        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }

        scanner.setSqlSessionTemplateBeanName(mapperScanConfig.getSqlSessionTemplateRef());
        List<String> basePackages = new ArrayList<String>();
        for (String pkg : mapperScanConfig.getBasePackages()) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        return scanner;
    }

}
