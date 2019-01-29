package com.xue.study.springStudy.base.jdbc;


import java.sql.*;

/**
 * @Auther: xuexiong@souche.com
 * @Date: 2019/1/29 09:54
 * @Description:
 */
public class Main {
    /**
     * JDBC中定义了一些接口：
     * 1、驱动管理：
     * DriverManager
     * 2、连接接口
     * Connection
     * DatabasemetaData
     * 3、语句对象接口
     * Statement
     * PreparedStatement
     * CallableStatement
     * 4、结果集接口
     * ResultSet
     * ResultSetMetaData
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        /**
         * JDBC访问数据库的工作过程：
         * 加载驱动，建立连接
         * 创建语句对象
         * 执行SQL语句
         * 处理结果集
         * 关闭连接
         */
        //注册驱动
        String driver = "com.mysql.jdbc.Driver";
        ;
        Class.forName(driver);
        //连接数据库
        String url = "jdbc:mysql://115.29.10.121:3306/xuexiongtest?useUnicode=true&amp&characterEncoding=UTF-8";
        String user = "root";
        String pwd = "dpjA8Z6XPXbvos";
        Connection conn = DriverManager.getConnection(url, user, pwd);
        // 将自动提交设置为 false，
        //若设置为 true 则数据库将会把每一次数据更新认定为一个事务并自动提交
        try {
            //将自动提交设置为false
            conn.setAutoCommit(false);
            String sql = "UPDATE xuexiongtest.class t set t.CLASS_NAME=? where t.CLASS_ID=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1,"xueclass");
            ps.setString(2,"1");
            ps.executeUpdate();
            conn.commit();
        } catch (Exception e) {
            //一旦其中一个操作出错都将回滚，使两个操作都不成功
            conn.rollback();
            e.printStackTrace();
        }
        //关闭连接
        conn.close();
    }

    /**
     * 打印
     * @param rs
     * @throws SQLException
     */
    private static void sysout(ResultSet rs) throws SQLException {
        //处理结果 ...
        //rs结果集中包含一个游标,游标默认在结果集
        //的第一行之前
        //rs.next():移动结果集游标到下一行
        //检查是否有数据, 如果有返回true, 否则false
        while (rs.next()) {
            //getXXX(列名): 返回结果集当前行中
            // 指定列名的数据.
            String name = rs.getString("CLASS_NAME");
            String id = rs.getString("CLASS_ID");
            //输出查询结果
            System.out.println(id + "," + name);
        }
    }
}
