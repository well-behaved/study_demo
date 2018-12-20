package com.xue.study.springStudy.aop.JdkProxy;

public class SayImpl implements Say {
    @Override
    public void sayHello(String words) {
        System.out.println("hello:" + words);
    }
}