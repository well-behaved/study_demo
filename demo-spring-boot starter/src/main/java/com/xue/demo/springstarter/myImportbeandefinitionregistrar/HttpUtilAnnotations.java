package com.xue.demo.springstarter.myImportbeandefinitionregistrar;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author: xuexiong@souche.com
 * @date: 2022/1/12 14:45
 * @description:
 * 类级注解，标注该注解的类才会被扫描到
 */
@Target(ElementType.TYPE)
@Inherited
@Retention(RUNTIME)
public @interface HttpUtilAnnotations {
}
