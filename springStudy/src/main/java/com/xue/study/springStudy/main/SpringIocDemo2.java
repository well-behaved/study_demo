package com.xue.study.springStudy.main;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2018/8/20 09:48
 * @Description:
 */
public class SpringIocDemo2 {
    public static void main(String[] args) {
        ClassPathResource res = new ClassPathResource("beans.xml");

        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();

        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);

        reader.loadBeanDefinitions(res);

        // 1、创建IoC配置文件的抽象资源（包含BenaDefinition定义信息）
        //
        //2、创建BeanFactory
        //
        //3、创建BeanDefinition读取器
        //
        //4、读取配置信息，完成载入和注册后IoC容器就创建出来了
    }
}
