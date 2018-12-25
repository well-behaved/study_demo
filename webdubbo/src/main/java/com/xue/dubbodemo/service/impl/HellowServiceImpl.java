package com.xue.dubbodemo.service.impl;

import com.xue.dubbodemo.service.HellowService;
import org.springframework.stereotype.Service;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2018/10/24 15:15
 * @Description:
 */
@Service("hellowService")
public class HellowServiceImpl implements HellowService {
    @Override
    public void sayHellow(String name) {
        System.out.println("你好"+name);
    }
}
