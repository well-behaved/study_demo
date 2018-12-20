package com.xue.study.springStudy.aop.common.Aop1;

import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * 通知类
 */
public class SayHelloBeforeAdvice implements MethodBeforeAdvice {//实现相应增强类的接口
    @Override
    public void before(Method arg0, Object[] arg1, Object arg2) throws Throwable {
        //arg0 是 目标类的方法     arg1是目标类的入参数   arg2是目标类实例  发生异常则抛给Throwable
        System.out.println("hello");
    }
}