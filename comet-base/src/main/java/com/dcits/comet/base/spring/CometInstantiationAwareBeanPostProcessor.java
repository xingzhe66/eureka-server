package com.dcits.comet.base.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;

import java.beans.PropertyDescriptor;

/**
 * 一般情况下，当我们需要实现InstantiationAwareBeanPostProcessor接口时，是通过继承Spring框架中InstantiationAwareBeanPostProcessor接口实现类
 * InstantiationAwareBeanPostProcessorAdapter这个适配器类来简化我们实现接口的工作
 * Created by yanxiao on 2016/8/1.
 */
public class CometInstantiationAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter {

    public CometInstantiationAwareBeanPostProcessor() {
//        System.out.println("【InstantiationAwareBeanPostProcessor接口】调用InstantiationAwareBeanPostProcessor构造方法");
    }

    /**
     * 实例化Bean之前调用
     */
    @Override
    public Object postProcessBeforeInstantiation(Class beanClass, String beanName) throws BeansException {
//        System.out.println("【InstantiationAwareBeanPostProcessor接口】调用InstantiationAwareBeanPostProcessor接口的postProcessBeforeInstantiation方法");
        return beanClass;
    }

    /**
     * 实例化Bean之后调用
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//        System.out.println("【InstantiationAwareBeanPostProcessor接口】调用InstantiationAwareBeanPostProcessor接口的postProcessAfterInitialization方法");
        return bean;
    }

    /**
     * 设置某个属性时调用
     */
    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName)
            throws BeansException {
//        System.out.println("【InstantiationAwareBeanPostProcessor接口】调用InstantiationAwareBeanPostProcessor接口的postProcessPropertyValues方法");
        return pvs;
    }
}