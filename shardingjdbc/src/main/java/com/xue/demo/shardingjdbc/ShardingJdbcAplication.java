package com.xue.demo.shardingjdbc;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @program: ShardingJdbcDemo
 * @description:
 * @author: chengh
 * @create: 2019-07-01 00:41
 */
@SpringBootApplication(
        scanBasePackages = {"com.xue.demo.shardingjdbc"},
        exclude = {DruidDataSourceAutoConfigure.class})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ShardingJdbcAplication {
 
    public static void main(String[] args) {
        SpringApplication.run(ShardingJdbcAplication.class, args);

    }
 
}