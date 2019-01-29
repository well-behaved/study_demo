package com.xue.study.springStudy.autowire;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/1/24 18:44
 * @Description:
 */
@Data
@Component("teacher")
public class XueTeacherImpl implements Teacher {
    private String name = "薛先生";

    @Override
    public void sysName() {
        System.out.println(name);
    }
}
