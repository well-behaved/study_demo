package com.xue.springbootdemo.demo.service.impl;


import com.xue.springbootdemo.demo.bean.Customer;
import com.xue.springbootdemo.demo.dao.CustomerMapper;
import com.xue.springbootdemo.demo.service.HellowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2018/10/24 15:15
 * @Description:
 */
@Service
public class HellowServiceImpl implements HellowService {
    @Autowired
    private CustomerMapper customerMapper;


    @Override
    public Customer find(long id) {
        return customerMapper.find(id);
    }
}
