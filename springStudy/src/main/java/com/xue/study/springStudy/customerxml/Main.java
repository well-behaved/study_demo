package com.xue.study.springStudy.customerxml;

import com.xue.study.springStudy.customerxml.dto.User;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/1/18 17:10
 * @Description: 自定义标签解析
 */
public class Main {
    public static void main(String[] args) {
        ApplicationContext beans = new ClassPathXmlApplicationContext("springcustom.xml");
        User user = (User) beans.getBean("testbean");
        System.out.println("username:" + user.getUserName() + "  " + "email:" + user.getEmail());
    }
}
