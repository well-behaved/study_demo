package com.xue.demo.service;

import com.xue.demo.bean.Customer;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2018/10/24 15:14
 * @Description:
 */
public interface HellowService {
    // 根据用户Id查询Customer(不查询Address)
     Customer find(long id);
}
