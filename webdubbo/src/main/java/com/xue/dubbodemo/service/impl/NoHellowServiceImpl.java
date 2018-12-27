package com.xue.dubbodemo.service.impl;

import com.xue.dubbodemo.listeren.CallbackListener;
import com.xue.dubbodemo.service.HellowService;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2018/12/26 16:34
 * @Description:
 */
public class NoHellowServiceImpl implements HellowService {
    @Override
    public String sayHellow(String name) {
        return "不太欢迎你："+name;

    }

    @Override
    public void callback(CallbackListener listeren) {
        System.out.println("NoHellowServiceImpl.customer----");
        listeren.callBack(System.currentTimeMillis());
    }
}
