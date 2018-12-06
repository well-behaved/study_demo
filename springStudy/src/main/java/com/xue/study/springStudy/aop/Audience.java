package com.xue.study.springStudy.aop;

import com.xue.study.springStudy.service.IExtendPerformanceService;
import com.xue.study.springStudy.service.impl.ExtendPerformanceServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2018/10/9 20:48
 * @Description:
 */
@Aspect
@Component
@Slf4j
public class Audience {
    public Audience() {
        System.out.println("------------被扫描到了-------------");
    }

    /**
     * execution(<修饰符模式>?<返回类型模式><方法名模式>(<参数模式>)<异常模式>?)
     * execution()
     * 表达式的主体
     * 第一个“*”符号
     * 表示返回值的类型任意
     * com.loongshawn.method.ces
     * AOP所切的服务的包名，即，需要进行横切的业务类
     * 包名后面的“..”
     * 表示当前包及子包
     * 第二个“*”
     * 表示类名，*即所有类
     * .*(..)
     * 表示任何方法名，括号表示参数，两个点表示任何参数类型
     */
    @Pointcut("execution(* com.xue.study.springStudy.service.impl.PerformanceServiceImpl.*(..))")
    public void pointcutName() {
    }

    /*
     * 配置前置通知,使用在方法point()上注册的切入点
     * 同时接受JoinPoint切入点对象,可以没有该参数
     */
    @Before("pointcutName()")
    public void performance(JoinPoint joinPoint) {
        System.out.println("执行before.....");
    }

    //配置后置通知,使用在方法point()上注册的切入点
    @After("pointcutName()")
    public void after(JoinPoint joinPoint) {
        System.out.println("执行after.....");
    }

    //配置环绕通知,使用在方法point()上注册的切入点
    @Around(value = "pointcutName()")
    public Object around(ProceedingJoinPoint joinPoint) {
        Object object = null;
        System.out.println("执行around before.....");
        try {
            object = joinPoint.proceed(joinPoint.getArgs());
            //执行方法
            System.out.println("执行around after.....");
        } catch (Throwable e) {
            System.out.println("执行around exception after.....");
        }
        return object;
    }

    //配置后置返回通知,使用在方法point()上注册的切入点
    @AfterReturning("pointcutName()")
    public void afterReturn(JoinPoint joinPoint) {
        System.out.println("afterReturn " + joinPoint);
    }

    //配置抛出异常后通知,使用在方法point()上注册的切入点
    @AfterThrowing(pointcut = "pointcutName()", throwing = "ex")
    public void afterThrow(JoinPoint joinPoint, Exception ex) {
        System.out.println("afterThrow " + joinPoint + "\t" + ex.getMessage());
    }

    // “...aop.Person”后面的 “+” 号，表示只要是Person及其子类都可以添加新的方法
    @DeclareParents(value = "com.xue.study.springStudy.service.impl.PerformanceServiceImpl+", defaultImpl =
            ExtendPerformanceServiceImpl.class)
    public IExtendPerformanceService extendPerformanceService;

}
