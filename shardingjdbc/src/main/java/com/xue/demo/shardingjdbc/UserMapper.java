package com.xue.demo.shardingjdbc;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    /**
     * 保存
     */
    void save(User user);
    /**
     * 删除数据
     */
    void truncate();
 
    /**
     * 查询
     * @param ids
     * @return
     */
    List<User> getByIds(@Param("ids") List<Long> ids);
}