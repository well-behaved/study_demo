package com.xue.study.springStudy.autowire;

import com.xue.study.springStudy.service.IHello;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.util.Optional;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/1/24 11:52
 * @Description:
 */
public class Main {
    public static void main(String[] args) {
        //从classpath路径上装载XML的配置信息
        Resource resource = new ClassPathResource("spring-自动装配.xml");
        //实例化IOC容器,此时容器并未实例化beans-config.xml所定义的各个受管bean.
        XmlBeanFactory factory = new XmlBeanFactory(resource);
        MyClass myClass = (MyClass) factory.getBean("myClass");
       Optional.ofNullable(myClass).map(MyClass::getXueTeacherImpl).ifPresent(
               Teacher::sysName
       );
    }
}
