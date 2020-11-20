package com.lagou.edu.utils;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {

    private ConnUtils connectionUtils;

    public void setConnectionUtils(ConnUtils connectionUtils) {
        this.connectionUtils = connectionUtils;
    }

    /**
     * 开启事务
     * @throws SQLException
     */
    public void beginTransaction() throws SQLException {
        final Connection currentConnection = connectionUtils.getCurrentConnection();
        currentConnection.setAutoCommit(false);
    }

    /**
     * 提交事务
     * @throws SQLException
     */
    public void commit() throws SQLException {
        final Connection currentConnection = connectionUtils.getCurrentConnection();
        currentConnection.commit();
    }

    /**
     * 事务回滚
     * @throws SQLException
     */
    public void rollback() throws SQLException{
        final Connection currentConnection = connectionUtils.getCurrentConnection();
        currentConnection.rollback();
    }

}
