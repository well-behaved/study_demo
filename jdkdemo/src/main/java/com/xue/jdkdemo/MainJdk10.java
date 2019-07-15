package com.xue.jdkdemo;

/**
 * @Author: xuexiong@souche.com
 * @Date: 2019-07-10 14:44
 * @Description:
 */
public class MainJdk10 {
    public static void main(String[] args) {
        System.out.println("----开始运行");
        /*
        局部变量类型推断
        JDK10 可以使用var作为局部变量类型推断标识符，此符号仅适用于局部变量，增强for循环的索引，以及传统for循环的本地变量；
        它不能使用于方法形式参数，构造函数形式参数，方法返回类型，字段，catch形式参数或任何其他类型的变量声明。
         */
        var str = "ABC";
        System.out.println(str);
        System.out.println("----结束运行");
        /*
        jdk没6个月发布一版？
         */

    }
}
