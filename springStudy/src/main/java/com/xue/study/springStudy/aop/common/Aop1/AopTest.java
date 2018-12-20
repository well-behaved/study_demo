package com.xue.study.springStudy.aop.common.Aop1;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AopTest {
    @Test //测试在spring 通过ProxyFactoryBean 配置代理
    public void test2(){
        ApplicationContext a = new ClassPathXmlApplicationContext("springaop.xml");
        wait w = (wait)a.getBean("testAOP");
        w.say();
    }
}