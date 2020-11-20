package com.lagou.edu.factory;


import com.lagou.edu.utils.TransactionManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyFactory {

    private TransactionManager transactionManager;


    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public Object getProxy(Object target) {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), target.getClass().getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Object invoke = null;
                try{
                    //开启事务
                    transactionManager.beginTransaction();

                    //执行原有业务逻辑
                    invoke = method.invoke(target, args);

                    //提交事务
                    transactionManager.commit();
                } catch (Exception e) {
                    System.out.println("异常：" + e);
                    //事务回滚
                    transactionManager.rollback();
                    //异常记得向上跑出
                    throw e;
                }

                return invoke;
            }
        });
    }

}
