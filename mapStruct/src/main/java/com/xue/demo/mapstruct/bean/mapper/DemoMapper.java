package com.xue.demo.mapstruct.bean.mapper;

import com.xue.demo.mapstruct.bean.UserInfo1;
import com.xue.demo.mapstruct.bean.UserInfo2;
import org.mapstruct.MapMapping;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2018/9/26 21:17
 * @Description:
 */
@Mapper
public interface DemoMapper {
    DemoMapper INSTANCE = Mappers.getMapper(DemoMapper.class);
    UserInfo1 to(UserInfo2 userInfo2);

}
