package com.xue.study.springStudy.main;

import com.xue.study.springStudy.service.IExtendPerformanceService;
import com.xue.study.springStudy.service.IPerformanceService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2018/10/9 20:41
 * @Description:
 */
public class SpringAopDemo1 {
    public static void main(String[] args) {
    /*    //-----------BeanFactory IoC容器---------------------//
        //从classpath路径上装载XML的配置信息
        Resource resource = new ClassPathResource("springDemo1.xml");
        //实例化IOC容器,此时容器并未实例化beans-config.xml所定义的各个受管bean.
        XmlBeanFactory factory = new XmlBeanFactory(resource);
        IPerformanceService performanceService = (IPerformanceService) factory.getBean("performanceService");
        performanceService.perform();

        factory.destroySingletons();*/

        ApplicationContext ac = new ClassPathXmlApplicationContext("springDemo1.xml");
        IPerformanceService performanceService2 = (IPerformanceService) ac.getBean("performanceService");
        performanceService2.perform();


        IExtendPerformanceService demo = (IExtendPerformanceService) performanceService2;
        demo.say();


    }
}
