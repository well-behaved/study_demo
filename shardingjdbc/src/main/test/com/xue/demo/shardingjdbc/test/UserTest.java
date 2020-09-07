package com.xue.demo.shardingjdbc.test;

import com.alibaba.fastjson.JSON;
import com.xue.demo.shardingjdbc.ShardingJdbcAplication;
import com.xue.demo.shardingjdbc.User;
import com.xue.demo.shardingjdbc.UserMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK
        , classes = ShardingJdbcAplication.class)
@WebAppConfiguration
public class UserTest {

    @Autowired
    UserMapper userMapper;


    @Test
    public void truncate() {
        userMapper.truncate();
        System.out.println("删除表数据先");
    }


    @Test
    public void save() {
//        userMapper.truncate();
        // 奇数
        for (int i = 0; i < 100; i=i+2) {
            User user = new User();
            user.setId(new Integer(i).longValue());
            user.setName("chengh" + i);
            user.setCreateTime(new Date());
            user.setSex(i % 2);
            user.setPhone("12345678910");
            user.setEmail("123@qq.com");
            user.setPassword("123456");
            userMapper.save(user);
        }
        //偶数
        for (int i = 1; i < 100; i=i+2) {
            User user = new User();
            user.setId(new Integer(i).longValue());
            user.setName("chengh" + i);
            user.setCreateTime(new Date());
            user.setSex(i % 2);
            user.setPhone("12345678910");
            user.setEmail("123@qq.com");
            user.setPassword("123456");
            userMapper.save(user);
        }
    }

    @Test
    public void getByIds() {
        List<Long> listParam = new ArrayList<>();
        listParam.add(12L);
        listParam.add(13L);
        System.out.println(JSON.toJSONString(
                userMapper.getByIds(listParam)));
    }
    @Test
    public void getByIdsAndName() {
        List<Long> ids = new ArrayList<>();
        ids.add(12L);
        ids.add(13L);
        List<String> names = new ArrayList<>();
        names.add("12312");
        System.out.println(JSON.toJSONString(
                userMapper.getByIdsAndName(ids,names)));
    }

}