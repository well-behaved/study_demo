package com.xue.study.springStudy.aop.common.Aop1;

import org.springframework.aop.AfterReturningAdvice;
import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2018/12/17 20:22
 * @Description:
 */
public class SayHelloAfterReturnAdvice implements AfterReturningAdvice {
    @Override
    public void afterReturning(Object returnValue, Method method, Object[] args, Object target) throws Throwable {
        System.out.println("SayHelloAfterAdvice.afterReturning");
    }
}
