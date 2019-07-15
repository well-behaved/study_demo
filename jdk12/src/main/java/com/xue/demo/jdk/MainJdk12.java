package com.xue.demo.jdk;

/**
 * @Author: xuexiong@souche.com
 * @Date: 2019-06-26 19:19
 * @Description:
 */
public class MainJdk12 {
    public static void main(String[] args) {
        /*
        Shenandoah GC是一个面向low-pause-time的垃圾收集器，
        它最初由Red Hat实现，支持aarch64及amd64 architecture；
        ZGC也是面向low-pause-time的垃圾收集器，不过ZGC是基于colored pointers来实现，
        而Shenandoah GC是基于brooks pointers来实现；如果要使用Shenandoah
        GC需要编译时--with-jvm-features选项带有shenandoahgc，
        然后启动时使用-XX:+UnlockExperimentalVMOptions -XX:+UseShenandoahGC
         */

    }
}
