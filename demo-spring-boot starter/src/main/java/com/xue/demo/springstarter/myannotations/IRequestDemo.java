package com.xue.demo.springstarter.myannotations;

@HttpUtilAnnotations
public interface IRequestDemo {
    //调用test1时，会对http://abc.com发送请求
    @HttpRequestAnnotations(url = "http://abc.com")
    void test1();

    //调用test2时，会对http://test2.com发送请求
    @HttpRequestAnnotations(url = "http://test2.com")
    void test2();
}
