package com.xue.study.springStudy.aop.cglib;

/**
 *
 * cglib代理，也叫做子类代理。在内存中构建一个子类对象从而实现对目标对象功能的扩展。
 * @Auther: xuexiong@souche.com
 * @Date: 2018/12/13 16:03
 * @Description:
 */
public class CglibMain {

    public static void main(String[] args) {
        BookFacadeImpl bookFacade = new BookFacadeImpl();
        BookFacadeCglib cglib = new BookFacadeCglib();
        BookFacadeImpl bookCglib = (BookFacadeImpl) cglib.getInstance(bookFacade);
        bookCglib.addBook();
    }
}
