package com.example.heart.utils;


import com.orhanobut.logger.Logger;

/**
 * Created by lijiazhi on 16/4/6.
 *
 * 打印log日志
 */
public class LPLogger {
    static {
        Logger.init()
                .setMethodCount(3);

    }
    private static final String TAG = "LivePlayer";

    public static void d(String message) {
        Logger.d(TAG, message);
    }

    public static void e(String message) {
        Logger.e(TAG, message);
    }

    public static void e(Exception e) {
        Logger.e(TAG, e);
    }

    public static void e(String message, Exception e) {
        Logger.e(TAG, message, e);
    }

    public static void w(String message) {
        Logger.w(TAG, message);
    }

    public static void i(String message) {
        Logger.i(TAG, message);
    }

    public static void v(String message) {
        Logger.v(TAG, message);
    }

    public static void wtf(String message) {
        Logger.wtf(TAG, message);
    }

    //Unusable
//    public static void json(String json) {
//        Logger.json(json);
//    }
}
