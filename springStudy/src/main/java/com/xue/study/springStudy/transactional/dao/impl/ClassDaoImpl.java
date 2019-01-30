package com.xue.study.springStudy.transactional.dao.impl;

import com.xue.study.springStudy.transactional.dao.ClassDao;
import com.xue.study.springStudy.transactional.dto.ClassDo;
import com.xue.study.springStudy.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/1/16 17:52
 * @Description:
 */
public class ClassDaoImpl extends JdbcDaoSupport implements ClassDao {
    @Override
    public int addOne(ClassDo classDo) {
        System.out.println("----ClassDaoImpl.addOne------"+JsonUtils.objetcToJsonCommon(classDo));
        String sql = "insert into class(CLASS_ID,CLASS_NAME) values(?,?)";
        return this.getJdbcTemplate().update(sql,classDo.getClassId(),classDo.getClassName());
    }
}
