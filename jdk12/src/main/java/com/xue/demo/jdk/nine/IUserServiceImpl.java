package com.xue.demo.jdk.nine;

import com.xue.demo.jdk2.IUserService;

/**
 * @Author: xuexiong@souche.com
 * @Date: 2019-06-27 16:03
 * @Description:
 */
public class IUserServiceImpl implements IUserService {
    @Override
    public String sayHellow() {
        return "你好兄弟";
    }
}
