package com.xue.demo.jdk.nine;

import com.xue.demo.jdk2.IUserService;
import com.xue.demo.jdk2.User;

import java.lang.module.ModuleDescriptor;
import java.util.List;
import java.util.Set;

/**
 * @Author: xuexiong@souche.com
 * @Date: 2019-06-27 14:05
 * @Description:
 */
public class MainJdk9 {

    public static void main(String[] args) throws Exception {
        System.out.println("开始----------");
        /*
        前言：jdk1.9修改了JDK的目录结构
         */
        /*
         一，模块化系统
         https://www.cnblogs.com/IcanFixIt/p/6947763.html

            导出语句（exports statement）；
            开放语句（opens statement）；
            需要语句（requires statement）；
            使用语句（uses statement）；
            提供语句（provides statement）。

         */
        IUserService iUserService = IUserService.newInstance();
        System.out.println(iUserService.sayHellow());
        /*
        二，jshell工具
        说明：总计而言就是类似js的consel中可以直接编写代码一样，方便做一些简单的操作
        操作：终端输入jshell就可以进入了
         */
        /*
         * 三，集合工厂方法
         */
        List<String> objects = List.of("xuexiong","demo");
        Set.of();
        System.out.println("结束----------");
        /*
          四， 改进的 Stream API
         */
        String

    }
}
