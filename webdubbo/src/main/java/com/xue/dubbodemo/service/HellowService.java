package com.xue.dubbodemo.service;

import com.xue.dubbodemo.listeren.CallbackListener;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2018/10/24 15:14
 * @Description:
 */
public interface HellowService {
    public String sayHellow(String name);

//    public String sayHellowMain(MainParam mainParam);

    /**
     * 回调测试
     * @param listeren
     * @return
     */
    public void callback(CallbackListener listeren);
}
