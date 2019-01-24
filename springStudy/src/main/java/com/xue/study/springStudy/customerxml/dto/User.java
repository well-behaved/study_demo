package com.xue.study.springStudy.customerxml.dto;

import lombok.Data;

/**
 * POJO，接收配置文件
 */
@Data
public class User {
    /**
     * 姓名
     */
    private String userName;
    /**
     * 邮箱
     */
    private String email;
}