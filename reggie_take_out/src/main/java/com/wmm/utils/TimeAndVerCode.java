package com.wmm.utils;

import java.util.Date;
import java.util.HashMap;

/**
 * 验证码时效和存储工具类
 */
public class TimeAndVerCode {
    //存入当前时间，在验证时拿取，若验证的时间减去当前时间大于3600000ms则失效;
    public static HashMap<String,Date> currentTimeMap = new HashMap<>();
    //通过邮箱key对应一个验证码valus，在验证时从静态nap获取进行验证
    public static HashMap <String,String> verCodeMap = new HashMap<>();

    public static boolean removeCodeAndTime(String email){
        currentTimeMap.remove(email);
        verCodeMap.remove(email);
        return true;
    }
}
