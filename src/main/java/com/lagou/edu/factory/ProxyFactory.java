package com.lagou.edu.factory;


import com.lagou.edu.annon.Autowired;
import com.lagou.edu.annon.Service;
import com.lagou.edu.utils.TransactionManager;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Service(value = "proxyFactory")
public class ProxyFactory {

    @Autowired(value = "transactionManager")
    private TransactionManager transactionManager;


    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    public Object getProxy(Object target) {
        //判断该类是否实现了接口，如果实现接口，使用JDK动态代理，否则使用CGLIB代理
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
                    //异常记得向上抛出
                    throw e;
                }

                return invoke;
            }
        });
    }

}
