package com.xue.study.springStudy.customerxml.parser;


import com.xue.study.springStudy.customerxml.dto.User;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * 继承了AbstractSingleBeanDefinitionParser ，但根本上来说，是实现了BeanDefinitionParser接口
 */
public class UserBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {
    @Override
    protected Class getBeanClass(Element element) {
        return User.class;
    }
    @Override
    protected void doParse(Element element, BeanDefinitionBuilder bean) {
        String userName = element.getAttribute("userName");
        String email = element.getAttribute("email");
        /*
        放入BeanDefinitionBuilder中，完成所有的bean解析后注册到beanfactory中
         */
        if (StringUtils.hasText(userName)) {
            bean.addPropertyValue("userName", userName);
        }
        if (StringUtils.hasText(email)){
            bean.addPropertyValue("email", email);
        } 
        
    }
}