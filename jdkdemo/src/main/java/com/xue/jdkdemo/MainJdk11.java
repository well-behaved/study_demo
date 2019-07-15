package com.xue.jdkdemo;

/**
 * @Author: xuexiong@souche.com
 * @Date: 2019-07-10 15:28
 * @Description:
 */
public class MainJdk11 {

    public static void main(String[] args) {
        /*
        嵌套类中支持private  ??
         */
        MainJdk11 demo = new MainJdk11();

        /*
        字符串加强
         */
        // 判断字符串是否为空白
        " ".isBlank(); // true
        // 去除首尾空格
        " Javastack ".strip(); // "Javastack"
        // 去除尾部空格
        " Javastack ".stripTrailing();
        // 去除首部空格
        " Javastack ".stripLeading(); // "Javastack "
        // 复制字符串
        "Java".repeat(3); // "JavaJavaJava"
        // 行数统计
        "A\nB\nC".lines().count(); // 3

        /*
        用于 Lambda 参数的局部变量语法
         */

        /*
        增加一个新的GC    ZGC
         */

    }

    class PrivateClassDemo {
        private String demo;
    }
}
