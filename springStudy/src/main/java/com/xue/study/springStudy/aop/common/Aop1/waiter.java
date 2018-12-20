package com.xue.study.springStudy.aop.common.Aop1;

//目标对象实现接口并重写方法
public class waiter implements wait {
    @Override
    public void say() {
        System.out.println("先生");
    }

}