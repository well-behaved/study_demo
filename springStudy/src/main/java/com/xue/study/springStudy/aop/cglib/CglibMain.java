package com.xue.study.springStudy.aop.cglib;

import net.sf.cglib.proxy.Enhancer;

/**
 *
 * cglib代理，也叫做子类代理。在内存中构建一个子类对象从而实现对目标对象功能的扩展。
 * @Auther: xuexiong@souche.com
 * @Date: 2018/12/13 16:03
 * @Description:
 */
public class CglibMain {

    public static void main(String[] args) {
        BookFacadeCglib cglib = new BookFacadeCglib();
        //创建加强器，用来创建动态代理类
        Enhancer enhancer = new Enhancer();
        //为加强器指定要代理的业务类（即：为下面生成的代理类指定父类）
        enhancer.setSuperclass(BookFacadeImpl.class);
        //设置回调：对于代理类上所有方法的调用，都会调用CallBack，而Callback则需要实现intercept()方法进行拦
        enhancer.setCallback(cglib);

        BookFacadeImpl bookFacade = (BookFacadeImpl) enhancer.create();

        bookFacade.addBook();



    }
}
