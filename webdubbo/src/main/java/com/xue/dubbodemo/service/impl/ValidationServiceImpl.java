package com.xue.dubbodemo.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xue.dubbodemo.bean.ValidationParameter;
import com.xue.dubbodemo.service.ValidationService;
import com.xue.dubbodemo.util.JsonUtils;
import org.springframework.stereotype.Service;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2018/12/27 09:47
 * @Description:
 */
@Service("validationService")
public class ValidationServiceImpl implements ValidationService {
    @Override
    public void save(ValidationParameter parameter) {
        System.out.println("ValidationServiceImpl.save-----"+JsonUtils.objetcToJsonCommon(parameter));
    }

    @Override
    public void update(ValidationParameter parameter) {
        System.out.println("ValidationServiceImpl.update-----"+JsonUtils.objetcToJsonCommon(parameter));
    }

    @Override
    public void delete(int id) {
        System.out.println("ValidationServiceImpl.delete-----"+JsonUtils.objetcToJsonCommon(id));
    }
}
