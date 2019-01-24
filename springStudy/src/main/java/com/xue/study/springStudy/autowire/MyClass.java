package com.xue.study.springStudy.autowire;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/1/24 11:50
 * @Description:
 */
@Data
@Component
public class MyClass {
    /**
     * 班主任
     */
//    @Autowired
    private Teacher teacher;
}
