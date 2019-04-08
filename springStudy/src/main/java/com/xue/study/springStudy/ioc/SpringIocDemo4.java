package com.xue.study.springStudy.ioc;

import com.xue.study.springStudy.service.IHello;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2018/12/14 11:04
 * @Description:
 */
public class SpringIocDemo4 {
    public static void main(String[] args) {
    /*    //-----------BeanFactory IoC容器---------------------//
        //从classpath路径上装载XML的配置信息
        Resource resource = new ClassPathResource("springDemo1.xml");
        //实例化IOC容器,此时容器并未实例化beans-config.xml所定义的各个受管bean.
        XmlBeanFactory factory = new XmlBeanFactory(resource);
        IPerformanceService performanceService = (IPerformanceService) factory.getBean("performanceService");
        performanceService.perform();

        factory.destroySingletons();*/

        ApplicationContext ac = new ClassPathXmlApplicationContext("springDemo2.xml");
        IHello helloWorldBean = (IHello) ac.getBean("helloWorldBean");
        System.out.println(helloWorldBean.sayHellow());
        /*
          在spring容器启动的bean载入的时候就开始了初始化
         * 在 invokeBeanFactoryPostProcessors(beanFactory);  开始进行执行它的方法
         * 大概就是如果类实现了 BeanFactoryPostProcessor 这个类，就要调用它的postProcessBeanFactory方法，将容器中所有的类中的${}替换，包括父类，属性等所有东西
         * 在 invokeBeanFactoryPostProcessors(beanFactory);  开始进行初注册到容器中
         * 大概就是如果实现了BeanPostProcessor ，就放置到List<BeanPostProcessor> beanPostProcessors中。
         *
         */




    }
}
