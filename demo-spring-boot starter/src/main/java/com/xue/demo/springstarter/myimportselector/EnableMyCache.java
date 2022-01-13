package com.xue.demo.springstarter.myimportselector;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author xuexiong@souche.com
 * @return
 * @exception
 * @date 2022/1/13 16:20
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(CacheSelector.class)
public @interface EnableMyCache {
    /**
     * 缓存类型
     */
    CacheTypeEnums type() default CacheTypeEnums.MEMORY;
}

