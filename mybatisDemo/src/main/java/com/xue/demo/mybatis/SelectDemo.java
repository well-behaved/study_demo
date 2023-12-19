package com.xue.demo.mybatis;

import com.xue.demo.mybatis.dao.CustomerMapper;
import com.xue.demo.mybatis.domain.Customer;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SqlSession 简单查询演示类
 *
 * @author louluan
 */
public class SelectDemo {

    public static void main(String[] args) throws Exception {
        /*
         * 1.加载mybatis的配置文件，初始化mybatis，创建出SqlSessionFactory，是创建SqlSession的工厂
         * 这里只是为了演示的需要，SqlSessionFactory临时创建出来，在实际的使用中，SqlSessionFactory只需要创建一次，当作单例来使用
         */
        InputStream inputStream = Resources.getResourceAsStream("mybatisConfig.xml");
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory factory = builder.build(inputStream);

        /*
        2. 从SqlSession工厂 SqlSessionFactory中创建一个SqlSession，进行数据库操作
         */
        SqlSession sqlSession = factory.openSession();

        /*
        使用sqlSession进行查询等相关操作
         */
        //获取代理类
        CustomerMapper customerMapper = sqlSession.getMapper(CustomerMapper.class);
        //插入
        Customer oneCustomer = new Customer();
        oneCustomer.setName("张三");
        oneCustomer.setPhone("18333333333");
        customerMapper.save(oneCustomer);
        //查询
        Customer customer = customerMapper.find(oneCustomer.getId());
        System.out.println("customer:" + customer.toString());

        /*
         关闭SqlSession 事务等处理
         */
        sqlSession.commit();
        sqlSession.close();
        // 出现异常的时候，回滚事务
//        sqlSession.rollback();
    }

}