package com.xue.demo.springstarter.myannotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author: xuexiong@souche.com
 * @date: 2022/1/12 14:47
 * @description:
 */
@Target(ElementType.METHOD)
@Inherited
@Retention(RUNTIME)
public @interface HttpRequestAnnotations {
    /**
     * 请求地址
     *
     * @return
     */
    String url();
}
