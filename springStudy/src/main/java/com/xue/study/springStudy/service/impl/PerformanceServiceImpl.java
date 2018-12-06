package com.xue.study.springStudy.service.impl;

import com.xue.study.springStudy.service.IPerformanceService;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2018/10/9 20:57
 * @Description:
 */
public class PerformanceServiceImpl implements IPerformanceService {
    @Override
    public void perform() {
        System.out.println("-----方法开始执行-----");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("-----方法结束执行-----");
    }
}
