package com.lagou.edu.factory;

import com.lagou.edu.annon.Autowired;
import com.lagou.edu.annon.Service;
import com.lagou.edu.utils.TransactionManager;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

@Service(value = "cgLibProxyFactory")
public class CgLibProxyFactory implements MethodInterceptor {
    private Object target;

    @Autowired(value = "transactionManager")
    private TransactionManager transactionManager;

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        Object result;
        try {
            //开启事务
            transactionManager.beginTransaction();

            //执行方法
            result = methodProxy.invoke(target, objects);

            //提交事务
            transactionManager.commit();
        } catch (Exception e) {
            transactionManager.rollback();
            //异常记得向上抛出
            throw e;
        }
        return result;
    }

    public Object getProxy(Object target) {
        this.target = target;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.target.getClass());
        // 回调方法
        enhancer.setCallback(this);
        // 创建代理对象
        return enhancer.create();
    }
}
