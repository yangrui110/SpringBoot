package com.yangframe.config.util;

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.function.Supplier;

/**
 * @autor 杨瑞
 * @date 2019/6/11 19:21
 */
public class BeanOperatorUtil {

    /**
     * 增加一个Bean到IOC容器中去
     * */
    public static void addBean(ConfigurableApplicationContext context, String beanName, Class classz, Supplier instanceSupplier) throws IllegalAccessException {
        if(context.containsBean(beanName)){

        }
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(classz,instanceSupplier);
        BeanDefinitionRegistry registry=getRegistry(context);
        AbstractBeanDefinition definition = builder.getRawBeanDefinition();
        registry.registerBeanDefinition(beanName, definition);
    }
    public static BeanDefinitionRegistry getRegistry(ConfigurableApplicationContext context){
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) context.getBeanFactory();
        return registry;
    }
}
