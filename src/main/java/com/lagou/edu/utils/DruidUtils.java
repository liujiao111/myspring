package com.lagou.edu.utils;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * @author 应癫
 */
public class DruidUtils {

    private DruidUtils(){
    }

    private static DruidDataSource druidDataSource = new DruidDataSource();


    static {
        druidDataSource.setDriverClassName("com.mysql.jdbc.Driver");
        druidDataSource.setUrl("jdbc:mysql://120.27.12.173:3306/bank?characterEncoding=utf8");
        druidDataSource.setUsername("lxq");
        druidDataSource.setPassword("Lxq410221@5214");

    }

    public static DruidDataSource getInstance() {
        return druidDataSource;
    }

}
