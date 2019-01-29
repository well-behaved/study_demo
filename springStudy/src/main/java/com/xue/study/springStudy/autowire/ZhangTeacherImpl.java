package com.xue.study.springStudy.autowire;

import com.xue.study.springStudy.autowire.Teacher;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/1/24 18:46
 * @Description:
 */
@Data
@Component("teacher")
public class ZhangTeacherImpl implements Teacher {
    private String name = "张先生";
    @Override
    public void sysName() {
        System.out.println(name);
    }
}
