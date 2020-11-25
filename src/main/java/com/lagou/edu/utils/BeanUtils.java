package com.lagou.edu.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BeanUtils {

    public static void setValue(Object obj, String value) {
        String[] attrs = value.split("\\|");
        for (int i = 0; i < attrs.length; i++) {
            String[] attr = attrs[i].split("\\:");
            //判断是否是处理级联引用
            if (attr[0].contains(".")) {
                String[] str = attr[0].split("\\.");
                try {
                    //1.获取级联属性是否为null
                    Method getMethod = obj.getClass().getDeclaredMethod("get" + StringUtils.initCap(str[0]));
                    Object tmp = getMethod.invoke(obj);

                    if (tmp == null) {
                        //2.为null时，需要初始化后，再设置属性值
                        //2.1 首先实例化
                        Field field = obj.getClass().getDeclaredField(str[0]);
                        tmp = field.getType().getDeclaredConstructor().newInstance();
                        //2.2 设置级联引用的属性
                        setValue(tmp, attrs[i].substring(attrs[i].indexOf(".") + 1));
                        //2.3 将实例化完成的级联属性设置到对象中
                        Method method = obj.getClass().getDeclaredMethod("set" + StringUtils.initCap(str[0]), field.getType());
                        method.invoke(obj, tmp);
                    } else {
                        //3.不为空时，直接设置级联引用的属性
                        setValue(tmp, attrs[i].substring(attrs[i].indexOf(".") + 1));
                    }
                } catch (Exception e) {

                }
            } else {
                //非级联引用
                try {
                    //getField返回所有public的属性；       getDeclaredField 返回类所有声明的属性
                    Field field = obj.getClass().getDeclaredField(attr[0]);
                    //getMethod 返回所有public的方法，包含父类    getDeclaredMethod返回所有声明的方法，不包含父类
                    Method method = obj.getClass().getDeclaredMethod("set" + StringUtils.initCap(attr[0]), field.getType());
                    //获取属性实际值
                    Object val = convertType(field.getType().getName(), attr[1]);
                    method.invoke(obj, val);
                } catch (Exception e) {

                }
            }

        }

    }

        private static Object convertType (String type, String value){
            if (Integer.class.getName().equals(type) || "int".equals(type)) {
                return Integer.valueOf(value);
            } else if (Double.class.getName().equals(type) || "double".equals(type)) {
                return Double.valueOf(value);
            } else if (Long.class.getName().equals(type) || "long".equals(type)) {
                return Long.valueOf(value);
            } else if (Date.class.getName().equals(type)) {
                SimpleDateFormat sdf = null;
                if (value.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                } else if (value.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                } else {
                    return new Date();
                }
                try {
                    return sdf.parse(value);
                } catch (Exception e) {
                    return new Date();
                }
            } else {
                return value;
            }
        }

}
