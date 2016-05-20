package com.example.heart.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 *
 * @ClassName SharedPreferencesUtil
 * @PackageName com.genshuixue.student.util;
 * @创建人 dongrui
 * @修改日期 2014-7-9 上午3:14:14
 * @描述 shareprefereces存储工具类
 */
public class SharedPreferencesUtil {
    private static SharedPreferences sp;
    private static SharedPreferencesUtil utilShare;
    private static SharedPreferences.Editor editor;
    private final static String SP_NAME = "STUDENT";
    private final static int MODE = Context.MODE_PRIVATE;

    public SharedPreferencesUtil(Context context) {
        if (sp == null) {
            sp = context.getSharedPreferences(SP_NAME, MODE);
        }
        editor = sp.edit();
    }

    /**
     * @param
     * @描述 获取单例SharedPreferencesUtil工具类
     * @创建人 dongrui
     * @修改日期 2014-7-9 上午3:17:25
     */
    public static SharedPreferencesUtil cerateShare(Context context) {
        if (utilShare == null) {
            utilShare = new SharedPreferencesUtil(context.getApplicationContext());
        }
        return utilShare;
    }

    /**
     *
     * @param
     * @描述 存储key value
     * @创建人 dongrui
     * @修改日期 2014-7-9 上午3:18:03
     */
    public boolean save(String key, String value) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        editor.putString(key, value);
        return editor.commit();
    }

    /**
     *
     * @param
     * @描述 存储key value
     * @创建人 dongrui
     * @修改日期 2014-7-9 上午3:18:03
     */
    public boolean saveBoolean(String key, boolean value) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        editor.putBoolean(key, value);
        return editor.commit();
    }

    /**
     *
     * @param key
     * @param value
     * @desc 存储long值
     * @return
     */
    public boolean saveLong(String key, long value) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        editor.putLong(key, value);
        return editor.commit();
    }

    /**
     *
     * @param key
     * @return
     * @desc 取出long值
     */
    public long readLong(String key) {
        long i;
        i = sp.getLong(key, 0);
        if (i == 0) {
            return 0;
        } else {
            return i;
        }
    }

    /**
     *
     * @param key
     * @return
     * @desc 取出boolean值
     */
    public boolean readBoolean(String key) {
        boolean b;
        b = sp.getBoolean(key, true);
        return b;
    }

    /**
     *
     * @param
     * @描述 存储key value
     * @创建人 dongrui
     * @修改日期 2014-7-9 上午3:18:03
     */
    public boolean saveInt(String key, int value) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        editor.putInt(key, value);
        return editor.commit();
    }

    /**
     *
     * @param
     * @描述 根据key获取string类型value
     * @创建人 dongrui
     * @修改日期 2014-7-9 上午3:18:18
     */
    public int readInt(String key) {
        int i;
        i = sp.getInt(key, 0);
        if (i == 0) {
            return 0;
        } else {
            return i;
        }
    }

    /**
     *
     * @param
     * @描述 根据key获取string类型value
     * @创建人 dongrui
     * @修改日期 2014-7-9 上午3:18:18
     */
    public String read(String key) {
        String str = null;
        str = sp.getString(key, null);
        if (str == null) {
            return null;
        } else if (str.equals("")) {
            return null;
        } else {
            return str;
        }
    }

    /**
     *
     * @param
     * @描述 根据key清除某个value
     * @创建人 dongrui
     * @修改日期 2014-7-9 上午3:20:14
     */
    public void clean(String key) {
        editor.remove(key);
        editor.commit();
    }
}
