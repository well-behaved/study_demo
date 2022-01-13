package com.xue.demo.springstarter.myimportselector;

import com.xue.demo.springstarter.DemoApplication;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: xuexiong@souche.com
 * @date: 2022/1/13 16:29
 * @description:
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class ICacheServiceTest extends TestCase {
    @Autowired
    private ICacheService iCacheService;
    @Test
    public void testGetCacheType() {
        System.out.println(iCacheService.getCacheType());
    }
}