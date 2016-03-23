package com.chenyp.collaboration.util;

import android.net.Uri;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidateUtil {

    /**
     * 判断字符串是否为空
     *
     * @param str
     * @return
     */
    public static boolean isValid(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * 判断集合是否为空
     *
     * @param collection
     * @return
     */
    public static boolean isValid(Collection collection) {
        if (collection == null || collection.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * 检验数组是否为空
     *
     * @param objects
     * @return
     */
    public static boolean isValid(Object[] objects) {
        if (objects == null || objects.length == 0) {
            return false;
        }
        return true;
    }

    /**
     * 检验字符串是否是整数
     *
     * @param num
     * @return
     */
    public static boolean isNumeric(String num) {
        if (num == null) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(num);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    public static boolean isAuthorizeValid(String authorize) {
        if (!authorize.matches("([1-9]\\d*[,]?)*")) {
            return false;
        }
        return true;
    }

    public static boolean isLocationUrl(String url) {
        String[] strings = url.split("image1");
        if (strings.length > 0) {
            return false;
        }
        return true;
    }


    public static void main(String[] args) {
        System.out.println(isLocationUrl("http:/192.168.191.1:8080/Broker?type=100&exhibitDetail=2&image=3"));
    }

}
