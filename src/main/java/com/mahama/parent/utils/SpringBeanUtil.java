package com.mahama.parent.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.*;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SpringBeanUtil implements ApplicationContextAware, ApplicationEventPublisherAware {
    private static ApplicationContext applicationContext;
    private static ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringBeanUtil.applicationContext = applicationContext;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        SpringBeanUtil.applicationEventPublisher = applicationEventPublisher;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static ApplicationEventPublisher getApplicationEventPublisher() {
        return applicationEventPublisher;
    }

    //通过name获取 Bean.
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    //通过Clazz返回指定的Beans
    public static <T> Map<String, T> getBeansByType(Class<T> clazz) {
        return getApplicationContext().getBeansOfType(clazz);
    }

    public static boolean isDev() {
        return getActiveProfile().equals("dev");
    }

    /// 获取当前环境
    public static String getActiveProfile() {
        try {
            return applicationContext.getEnvironment().getActiveProfiles()[0];
        } catch (Exception err) {
            return "prod";
        }
    }

    public static String getProperty(String key) {
        return applicationContext.getEnvironment().getProperty(key,"");
    }
}
