package com.xue.study.springStudy.ioc;

import com.xue.study.springStudy.service.IHello;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/4/8 11:10
 * @Description:
 */
public class SingleTest {
    public static void main(String[] args) {
        Resource resource = new ClassPathResource("springDemo1.xml");
        //实例化IOC容器,此时容器并未实例化beans-config.xml所定义的各个受管bean.
        XmlBeanFactory factory = new XmlBeanFactory(resource);
        IHello hello = (IHello) factory.getBean("helloWorldBean");
        hello.listTest();
        hello.listTest();
        IHello hello2 = (IHello) factory.getBean("helloWorldBean");
        hello2.listTest();
        IHello hello3 = (IHello) factory.getBean("helloWorldBean");
        hello3.listTest();
        IHello hello4 = (IHello) factory.getBean("helloWorldBean");
    }
}
