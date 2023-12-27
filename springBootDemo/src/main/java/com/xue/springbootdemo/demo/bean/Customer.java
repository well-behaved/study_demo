package com.xue.springbootdemo.demo.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Created on 2020-10-29
 */
@Getter
@Setter
@ToString
public class Customer {
    private long id;

    private String name;

    private String phone;
}