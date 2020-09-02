package com.xue.demo.shardingjdbc;

@Mapper
public interface UserMapper {
    /**
     * 保存
     */
    void save(User user);
 
    /**
     * 查询
     * @param ids
     * @return
     */
    List<User> getByIds(@Param("ids") List<Long> ids);
}