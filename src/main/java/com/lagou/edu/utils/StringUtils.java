package com.lagou.edu.utils;

public class StringUtils {

    /**
     * 首字母大写，以获取setter和getter方法
     * @param str
     * @return
     */
    public static String initCap(String str){
        if(null == str || "".equals(str)){
            return  str;
        }
        if(str.length() == 1){
            return str.toUpperCase();
        } else{
            return str.substring(0,1).toUpperCase()+str.substring(1);
        }
    }
}
