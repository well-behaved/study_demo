package com.xue.study.springStudy.transactional;

import com.xue.study.springStudy.transactional.dto.ClassDo;
import com.xue.study.springStudy.transactional.service.ClassService;
import com.xue.study.springStudy.utils.JsonUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Random;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/1/16 20:21
 * @Description:
 */
public class MainAnnotation {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("springtxannotation.xml");
        ClassService classService =  (ClassService) applicationContext.getBean("classService");
        ClassDo classDo= new ClassDo();
        classDo.setClassId(String.valueOf(new Random().nextInt()%100000));
        classDo.setClassName("薛先生"+classDo.getClassId());
        System.out.println(JsonUtils.objetcToJsonCommon(classDo));
        classService.addOneAnnotation(classDo);
    }
}
