package com.xue.springbootdemo.demo.dao;

import com.xue.springbootdemo.demo.bean.Customer;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created on 2020-10-29
 */
@Mapper
public interface CustomerMapper {
    // 根据用户Id查询Customer(不查询Address)
    Customer find(long id);
    // 根据用户Id查询Customer(同时查询Address)
    Customer findWithAddress(long id);
    // 根据orderId查询Customer
    Customer findByOrderId(long orderId);
    // 持久化Customer对象
    int save(Customer customer);
}