package com.lagou.edu.utils;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnUtils {
    private ThreadLocal<Connection> threadLocal = new ThreadLocal<>(); //存储当前线程的连接


    /**
     * 从当前线程获取连接
     * @return
     * @throws SQLException
     */
    public Connection getCurrentConnection() throws SQLException {
        //判断当前线程中是否已经绑定连接，如果没有绑定，需要从连接池中获取一个连接绑定到线当前线程
        Connection connection = threadLocal.get();
        if(connection == null) {
            connection = DruidUtils.getInstance().getConnection();
            threadLocal.set(connection);
        }
        return connection;
    }
}
