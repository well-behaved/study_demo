package com.xue.study.springStudy.aop.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class BookFacadeCglib implements MethodInterceptor {
    // 实现回调方法
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        System.out.println("预处理——————");
        proxy.invokeSuper(obj, args); //调用业务类（父类中）的方法
        System.out.println("调用后操作——————");
        return null;
    }
}