package com.lagou.edu.utils;

import com.lagou.edu.annon.Autowired;
import com.lagou.edu.annon.Repository;

import java.sql.Connection;
import java.sql.SQLException;

@Repository(value = "transactionManager")
public class TransactionManager {

    @Autowired(value = "connectionUtils")
    private ConnUtils connectionUtils;

    /**
     * 开启事务
     * @throws SQLException
     */
    public void beginTransaction() throws SQLException {
        Connection currentConnection = connectionUtils.getCurrentConnection();
        currentConnection.setAutoCommit(false);
    }

    /**
     * 提交事务
     * @throws SQLException
     */
    public void commit() throws SQLException {
        Connection currentConnection = connectionUtils.getCurrentConnection();
        currentConnection.commit();
    }

    /**
     * 事务回滚
     * @throws SQLException
     */
    public void rollback() throws SQLException{
        Connection currentConnection = connectionUtils.getCurrentConnection();
        currentConnection.rollback();
    }

}
