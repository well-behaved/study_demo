package com.xue.demo.mapstruct;

import com.xue.demo.mapstruct.bean.UserInfo1;
import com.xue.demo.mapstruct.bean.UserInfo2;
import com.xue.demo.mapstruct.bean.mapper.DemoMapper;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2018/9/26 21:23
 * @Description:
 */
public class Test {
    public static void main(String[] args) {
        UserInfo2 userInfo2 = new UserInfo2();
        userInfo2.setName1("asdf");
        DemoMapper demoMapper = DemoMapper.INSTANCE;
        UserInfo1 userInfo1 = demoMapper.to(userInfo2);
        System.out.println(userInfo1.getName1());
    }
}
