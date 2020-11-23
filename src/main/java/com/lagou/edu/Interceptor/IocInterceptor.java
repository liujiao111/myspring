package com.lagou.edu.Interceptor;

import com.lagou.edu.annon.Autowired;
import com.lagou.edu.annon.Controller;
import com.lagou.edu.annon.Repository;
import com.lagou.edu.annon.Service;
import com.lagou.edu.factory.BeanFactory;

import javax.servlet.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.*;

public class IocInterceptor implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("==============init");
        List<Class> classes = null;
        try {
            classes = loadClassByLoader(this.getClass().getClassLoader());
            for (Class aClass : classes) {
                //获取类上自定义的注解
                Service serviceAnnotation = (Service) aClass.getDeclaredAnnotation(Service.class);
                Repository repositoryAnnotation = (Repository) aClass.getDeclaredAnnotation(Repository.class);
                Controller controllerAnnotation = (Controller) aClass.getDeclaredAnnotation(Controller.class);

                //如果类上有这两个注解，则将其类实例化，并加入IOC容器中
                String beanName = "";
                if(serviceAnnotation == null && repositoryAnnotation == null && controllerAnnotation ==null) {
                    continue;
                }
                if(serviceAnnotation != null) {
                    beanName = serviceAnnotation.value();
                } else if(repositoryAnnotation != null) {
                    beanName = repositoryAnnotation.value();
                } else if(controllerAnnotation != null) {
                    beanName = controllerAnnotation.value();
                }
                Object bean = aClass.newInstance();
                BeanFactory.addInstance(beanName, bean);

                //判断类中属性是否有@Autowired注解，如果有，从IOC容器中找出对应类型的bean并赋值
                final Field[] fields = aClass.getDeclaredFields();
                for (int i = 0; i < fields.length; i++) {
                    Field field = fields[i];
                    field.setAccessible(true);
                    Autowired autowiredAnnotation = field.getDeclaredAnnotation(Autowired.class);
                    if(autowiredAnnotation != null) {
                        System.out.println("当前类：" + aClass + ", 属性：" + field.getName() + "有autowired注解....");
                        String autowiredBeanName = autowiredAnnotation.value();


                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("==============doFilter");

    }

    @Override
    public void destroy() {
        System.out.println("==============destroy");
    }

    //通过loader加载所有类
    private List<Class> loadClassByLoader(ClassLoader load) throws Exception{
        Enumeration<URL> urls = load.getResources("");
        //放所有类型
        List<Class> classes = new ArrayList<Class>();
        while (urls.hasMoreElements()) {
            URL url = urls.nextElement();
            //文件类型（其实是文件夹）
            if (url.getProtocol().equals("file")) {
                loadClassByPath(null, url.getPath(), classes, load);
            }
        }
        return classes;
    }
    //通过文件路径加载所有类 root 主要用来替换path中前缀（除包路径以外的路径）
    private void loadClassByPath(String root, String path, List<Class> list, ClassLoader load) {
        File f = new File(path);
        if(root==null) root = f.getPath();
        //判断是否是class文件
        if (f.isFile() && f.getName().matches("^.*\\.class$")) {
            try {
                String classPath = f.getPath();
                //截取出className 将路径分割符替换为.（windows是\ linux、mac是/）
                String className = classPath.substring(root.length()+1,classPath.length()-6).replace('/','.').replace('\\','.');
                list.add(load.loadClass(className));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            File[] fs = f.listFiles();
            if (fs == null) return;
            for (File file : fs) {
                loadClassByPath(root,file.getPath(), list, load);
            }
        }
    }
}
