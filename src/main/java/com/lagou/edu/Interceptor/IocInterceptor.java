package com.lagou.edu.Interceptor;

import com.lagou.edu.annon.*;
import com.lagou.edu.factory.BeanFactory;
import com.lagou.edu.factory.CgLibProxyFactory;
import com.lagou.edu.factory.ProxyFactory;
import org.reflections.Reflections;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author liujiao
 * WEB项目过滤器：
 * 主要职责：
 * 在初始化时，扫描包下带有 @service @repository @controller注解的类，将其实例化并加入IOC容器中
 */
public class IocInterceptor implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        //扫描项目中所有带Service、Repository、Controller注解的类
        Reflections reflections = new Reflections();
        Set<Class<?>> serviceAnnotations = reflections.getTypesAnnotatedWith(Service.class);
        Set<Class<?>> repositoryAnnotations = reflections.getTypesAnnotatedWith(Repository.class);
        Set<Class<?>> controllerAnnotations = reflections.getTypesAnnotatedWith(Controller.class);

        try {
            //将带有该注解的类实例化并处理其依赖问题， 将其加入bean工厂中
            //实例化bean
            newInstance(repositoryAnnotations, 2);
            newInstance(serviceAnnotations, 1);
            newInstance(controllerAnnotations, 3);

            HashMap<String, Object> beans = BeanFactory.getBeans();

            //处理bean之间的依赖关系
            handlerDependency(beans);

            //处理事务(需要处理完对象之间的依赖关系再进行事务处理)
            handlerTransaction(beans);

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理事务
     * @param beans
     */
    private void handlerTransaction(HashMap<String, Object> beans) {
        final Set<String> keySet = beans.keySet(); //key为 bean name，value为类实例
        for (String beanName : keySet) {
            Object bean = beans.get(beanName);
            Class beanClass = bean.getClass();

            //如果该类有@Transactional注解， 表明该类的所有方法都受事务控制
            Transactional transactionalAnnotation = (Transactional) beanClass.getAnnotation(Transactional.class);
            if(transactionalAnnotation != null) {

                //获取该类的代理对象，便于控制事务
                Object proxy;
                //判断该类是否实现了接口，如果实现接口，使用JDK动态代理，否则使用CGLIB代理
                if(bean.getClass().getInterfaces().length > 0) {
                    ProxyFactory proxyFactory = (ProxyFactory) BeanFactory.getInstance("proxyFactory");
                    proxy = proxyFactory.getProxy(bean);
                } else {
                    CgLibProxyFactory proxyFactory = (CgLibProxyFactory) BeanFactory.getInstance("cgLibProxyFactory");
                    proxy = proxyFactory.getProxy(bean);
                }

                //将代理对象重新放入IOC容器中
                BeanFactory.addInstance(beanName, proxy);
            } else {
                BeanFactory.addInstance(beanName, bean);
            }
        }
    }

    /**
     * 处理对象依赖关系
     */
    private void handlerDependency(HashMap<String, Object> beans) throws IllegalAccessException {
        final Set<String> keySet = beans.keySet(); //key为 bean name，value为类实例
        for (String beanName : keySet) {
            Object bean = beans.get(beanName);
            Class beanClass = bean.getClass();
            //判断类中属性是否有@Autowired注解，如果有，从IOC容器中找出对应类型的bean并赋值
            Field[] fields = beanClass.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                Autowired autowiredAnnotation = field.getDeclaredAnnotation(Autowired.class);
                if (autowiredAnnotation != null) {
                    String autowiredBeanName = autowiredAnnotation.value();
                    Object autowiredBean = BeanFactory.getInstance(autowiredBeanName);
                    field.set(bean, autowiredBean); //给当前对象赋值
                }
            }
            //将处理好依赖关系的对象重新放入IOC容器中
            BeanFactory.addInstance(beanName, bean);
        }
    }


    /**
     * 实例化类对象
     * @param annotationClasses
     */
    private void newInstance(Set<Class<?>> annotationClasses, int type) throws IllegalAccessException, InstantiationException {
        for (Class<?> annotationClass : annotationClasses) {
            Object o = annotationClass.newInstance();
            String beanName = getBeanName(annotationClass, type);
            BeanFactory.addInstance(beanName, o);
        }
    }

    /**
     * 根据注解类获取类在IOC容器中的唯一标识名称
     * @param annotationClass
     * @param type
     * @return
     */
    private String getBeanName(Class<?> annotationClass, int type) {
        //bean名称，唯一标识
        String beanName = "";
        if(type == 1){
            Service annotation = annotationClass.getAnnotation(Service.class);
            beanName = annotation.value();
        } else if(type == 2) {
            Repository annotation = annotationClass.getAnnotation(Repository.class);
            beanName = annotation.value();
        } else if(type == 3) {
            Controller annotation = annotationClass.getAnnotation(Controller.class);
            beanName = annotation.value();
        }

        //如果没有bean名称，默认以实现的接口名首字母小写的接口名，如果没有接口，则以自己类名首字母小写为bean name
        if(StringUtils.isEmpty(beanName)) {
            String name;
            if(annotationClass.getInterfaces().length > 0) {
                name = annotationClass.getInterfaces()[0].getSimpleName();
            } else {
                name = annotationClass.getSimpleName();
            }
            beanName = name.substring(0,1).toLowerCase() + name.substring(1);
        }
        return beanName;
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        System.out.println("==============destroy");
    }
}
