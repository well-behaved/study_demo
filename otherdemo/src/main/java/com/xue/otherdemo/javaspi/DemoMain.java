package com.xue.otherdemo.javaspi;

import com.xue.otherdemo.javaspi.service.HelloService;

import java.util.Optional;
import java.util.ServiceLoader;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2018/12/27 18:18
 * @Description: spi例子
 */
public class DemoMain {
    public static void main(String[] args) {
        ServiceLoader<HelloService> loaders =
                ServiceLoader.load(HelloService.class);

//        System.out.println(Optional.of(loaders).map(ServiceLoader::));
        System.out.println("begin----------");
        for (HelloService in : loaders) {
            in.sayHellow();
        }
        System.out.println("end----------");
    }
}
