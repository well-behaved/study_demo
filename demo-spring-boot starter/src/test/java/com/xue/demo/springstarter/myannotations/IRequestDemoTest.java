package com.xue.demo.springstarter.myannotations;

import com.xue.demo.springstarter.DemoApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: xuexiong@souche.com
 * @date: 2022/1/12 15:45
 * @description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class IRequestDemoTest {
    @Autowired
    private IRequestDemo iRequestDemo;

    @Test
    public void testTest1() {
        iRequestDemo.test1();
    }

    @Test
    public void testTest2() {
        iRequestDemo.test2();
    }
}