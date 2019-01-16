package com.xue.study.springStudy.transactional.dao.impl;

import com.xue.study.springStudy.transactional.dao.ClassDao;
import com.xue.study.springStudy.transactional.dto.ClassDo;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/1/16 17:52
 * @Description:
 */
public class ClassDaoImpl extends JdbcDaoSupport implements ClassDao {
    @Override
    public int addOne(ClassDo classDo) {
        String sql = "insert into class(CLASS_ID,CLASS_NAME) values(?,?)";
        return this.getJdbcTemplate().update(sql,classDo.getClassId(),classDo.getClassName());
    }
}
