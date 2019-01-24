package com.xue.study.springStudy.customerxml.handler;

import com.xue.study.springStudy.customerxml.parser.UserBeanDefinitionParser;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * Handler文件，扩展自NamespaceHandlerSupport，目的是将组件注册到Spring容器中
 */
public class MyNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
        /*
         * 遇到user标签，则交给UserBeanDefinitionParser来解析
         */
        registerBeanDefinitionParser("user", new UserBeanDefinitionParser());
    }

}