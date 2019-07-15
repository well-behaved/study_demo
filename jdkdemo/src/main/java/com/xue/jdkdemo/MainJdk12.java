package com.xue.jdkdemo;

/**
 * @Author: xuexiong@souche.com
 * @Date: 2019-07-15 11:29
 * @Description:
 */
public class MainJdk12 {
    public static void main(String[] args) {
        /*
            swich新特性
         */
        DemoEnum demoEnum = DemoEnum.TWO;
        switch (demoEnum){
            case ONE,THREE -> System.out.println("你好");
            case TWO -> System.out.println("你好");
        }
        int demo = switch (demoEnum){
            case ONE,THREE -> {System.out.println("dmeo"); break  1;}
            case TWO -> {System.out.println("dmeo"); break 2;}
        };
        int demo2 = switch (demoEnum){
            case ONE,THREE -> 1;
            case TWO -> 2;
        };
        System.out.println(demo);
        System.out.println(demo2);
        /*
         Shenandoah GC
         */

    }
    enum DemoEnum{
        ONE,
        TWO,
        THREE;
    }
}
