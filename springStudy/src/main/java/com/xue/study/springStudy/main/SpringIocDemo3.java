package com.xue.study.springStudy.main;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2018/9/27 17:15
 * @Description:
 */
public class SpringIocDemo3 {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("springDemo1.xml");
    }
}
