package com.xue.demo.springstarter;

import com.xue.demo.springstarter.myannotations.EnableHttpUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: xuexiong@souche.com
 * @date: 2022/1/12 15:35
 * @description:
 */
@SpringBootApplication
@EnableHttpUtil
public class DemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
