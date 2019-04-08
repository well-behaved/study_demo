package com.xue.study.springStudy.ioc;

import com.xue.study.springStudy.service.IHello;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@SuppressWarnings("deprecation")
public class SpringIocDemo1 {
    public static void main(String[] args) {
        //无用代码
        ApplicationContext applicationContext;
        FileSystemXmlApplicationContext fileSystemXmlApplicationContext;


        //-----------BeanFactory IoC容器---------------------//
        //从classpath路径上装载XML的配置信息
        Resource resource = new ClassPathResource("springDemo1.xml");
        //实例化IOC容器,此时容器并未实例化beans-config.xml所定义的各个受管bean.
        XmlBeanFactory factory = new XmlBeanFactory(resource);
        IHello hello = (IHello) factory.getBean("helloWorldBean");
        System.out.println(hello.sayHellow());






		/*
		在XmlBeanFactory中，初始化了一个
        XmlBeanDefinitionReader对象，有了这个Reader对象，那些以XML方式定义的BeanDefinition就有了处理的地方。
        我们可以看到，对这些XML形式的信息的处理实际上是由这个XmlBeanDefinitionReader来完成的。
        构造XmlBeanFactory这个loC容器时，需要指定BeanDefinition的信息来源，
        而这个信息来源需要封装成Spring中的Resource类来给出。Resource是Spring用来封装1/0操作的类。比如，我们的BeanDefinition信息是以XML
        文件形式存在的，那么可以使用像”ClassPath-Resourceres=newClassPathResource(“beans.xml”);
        ”这样具体的ClassPathResource来构造需要的Resource,然后将Resource作为构造参数传递给XmlBeanFactory构造函数
        。这样，IoC容器就可以方便地定位到需要的BeanDefinition信息来对Bean完成容器的初始化和依赖注入过程。
		 */
    }

}
