package com.xue.demo.jdk2;

import java.util.ServiceLoader;

/**
 * @Author: xuexiong@souche.com
 * @Date: 2019-06-27 16:02
 * @Description:
 */
public interface IUserService {
    String sayHellow();
    static IUserService newInstance(){
        return ServiceLoader.load(IUserService.class)
                .findFirst()
                .orElseThrow(() -> new RuntimeException(
                        "No PrimeChecker service provider found."));
    }
}
