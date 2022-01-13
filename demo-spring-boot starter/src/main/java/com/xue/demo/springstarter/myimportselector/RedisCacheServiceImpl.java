package com.xue.demo.springstarter.myimportselector;

/**
 * @author xuexiong@souche.com
 * @return
 * @exception
 * @date 2022/1/13 16:20
 */
public class RedisCacheServiceImpl implements ICacheService {
    @Override
    public String getCacheType() {
        return "=====RedisCacheServiceImpl--RedisCache====";
    }
}

