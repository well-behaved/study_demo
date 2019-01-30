package com.xue.study.springStudy.transactional.service;

import com.xue.study.springStudy.transactional.dto.ClassDo;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/1/16 17:44
 * @Description:
 */
public interface ClassService {
    /**
     * xml配置添加一些
     * @param classDo
     * @return
     */
    int iaddOnes(ClassDo classDo);
    /**
     * 添加一个
     * @return
     */
    int iaddOne(ClassDo classDo);
    /**
     * 注解式添加一个
     * @return
     */
    int addOneAnnotation(ClassDo classDo);
    /**
     * 编程式
     */
    public int programmingAddOne(ClassDo classDo);
}
