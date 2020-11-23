package com.lagou.edu.factory;

import com.lagou.edu.annon.Service;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * bean生产工厂
 */
public class BeanFactory {


    private static BeanFactory beanFactory = new BeanFactory();

    public BeanFactory(){

    }

    public static BeanFactory newInstance() {
        return beanFactory;
    }

    private static HashMap<String, Object> beans = new HashMap<>();

    /*static {
        System.out.println("生产bean");
        //读取xml
        InputStream inputStream = BeanFactory.class.getClassLoader().getResourceAsStream("beans.xml");
        try {
            Document document = new SAXReader().read(inputStream);
            Element rootElement = document.getRootElement();
            List<Element> beanElements = rootElement.selectNodes("//bean");

            //实例化所有的对象，装到容器中
            for (Element beanElement : beanElements) {
                String id = beanElement.attributeValue("id");
                String className = beanElement.attributeValue("class");
                Class<?> clazz = Class.forName(className);
                Object bean = clazz.newInstance();

                beans.put(id, bean);
            }

            //处理依赖关系
            List<Element> propertys = rootElement.selectNodes("//property");
            if(propertys != null && propertys.size() > 0) {
                for (Element element : propertys) {
                    String name = element.attributeValue("name");
                    String ref = element.attributeValue("ref");
                    //获取父元素
                    String parentId = element.getParent().attributeValue("id");
                    Object parentObject = beans.get(parentId);
                    Object refObject = beans.get(ref);

                    Method[] methods = parentObject.getClass().getMethods();

                    for (int i = 0; i < methods.length; i++) {
                        Method method = methods[i];
                        if(("set" + name).equalsIgnoreCase(method.getName())) {
                            method.invoke(parentObject, refObject);
                        }
                    }

                    //维护依赖关系后重新将bean放⼊map中
                    beans.put(parentId, parentObject);
                }

            }


            System.out.println("bean容器中的对象：" + beans);

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }*/


    //提供外部根据对象ID获取对应的实例对象的方法
    public static Object getInstance(String beanName) {
        System.out.println(beanName);
        System.out.println(beans);
        return beans.get(beanName);
    }

    public static Object getInstanceByClassType(Object classType) {
        final Set<String> keys = beans.keySet();
        for (String key : keys) {
            final Object o = beans.get(key);
            if(o.getClass().equals(classType)) {
                return o;
            }
        }
        return null;
    }


    public static void addInstance(String beanName, Object bean) {
        beans.put(beanName, bean);
        System.out.println("此时容器中的bean实例：" + beans);
    }

}
