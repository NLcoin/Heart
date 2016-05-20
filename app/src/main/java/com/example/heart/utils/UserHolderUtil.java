package com.example.heart.utils;

import android.content.Context;

import com.example.heart.model.UserModel;
import com.google.gson.Gson;

/**
 * Created by bjhl on 16/4/8.
 * 当前用户
 */
public class UserHolderUtil {

    public static Gson mGson = new Gson();

    private static UserHolderUtil utilUser;
    private static SharedPreferencesUtil share;
    private static UserModel infoUser;
    private static String auth_token;
    private static Context mContext;

    public static UserHolderUtil getUserHolder(Context context) {
        mContext = context;
        if (utilUser == null) {
            utilUser = new UserHolderUtil();
        }
        if (share == null) {
            share = SharedPreferencesUtil.cerateShare(context);
        }
        return utilUser;
    }

    /**
     * @desc 存储auth授权和用户信息
     */
    public void saveUser(UserModel user, String auth_token, String im_token) {
        infoUser = user;
        String info = mGson.toJson(user);
        share.save("USER_INFO", info);
        share.save("IM_TOKEN", im_token);
        saveAuth(auth_token);
    }

    public void cancelUser() {
        infoUser = null;
        auth_token = null;
        share.clean("AUTH_TOKEN");
        share.clean("USER_INFO");
        share.clean("PERSON_INFO");
        share.clean("IM_TOKEN");
        share.clean("SAVED_QUESTION_SUBJECT");
        share.clean("NEWS_CHANNEL");
//        EventBus.getDefault().post(new MyEventBusType(MyEventBusType.LOG_OUT));
    }

    /**
     * @desc 获取IM_TOKEN
     */
    public String getImToken() {
        return share.read("IM_TOKEN");
    }

    /**
     * @desc 获取用户信息
     */
    public UserModel getUser() {
        if (infoUser == null) {
            checkLogin();
        }
        return infoUser;
    }

    /**
     * @desc 存储auth授权
     */
    private void saveAuth(String auth_token) {
        share.save("AUTH_TOKEN", auth_token);
        UserHolderUtil.auth_token = auth_token;
    }

    /**
     * @return
     * @desc 获取非匿名auth授权
     */
    private String getAuth() {
        return share.read("AUTH_TOKEN");
    }

    /**
     * @param auth 匿名auth
     * @desc 存储匿名auth授权
     */
    public void saveAnonymousAuth(String auth) {
        share.save("ANONYMOUS_AUTH", auth);
        UserHolderUtil.auth_token = auth;
    }

    /**
     * @desc 获取匿名auth
     */
    private String getAnonymousAuth() {
        return share.read("ANONYMOUS_AUTH");
    }

    /**
     * @desc 获取auth, 会自动判断是否为登陆状态返回匿名或者非匿名auth
     */
    public String getAutoAuth() {
        if (auth_token == null) {
            if (checkLogin()) {
                auth_token = getAuth();
            } else {
                auth_token = getAnonymousAuth();
            }
        }
        return auth_token;
    }

    /**
     * @return true为登陆，false为未登陆
     * @desc 检查用户是否登陆, 并且自动初始化infoUser
     */
    public boolean checkLogin() {
        if (infoUser != null) {
            return Boolean.TRUE;
        } else {
            String info = share.read("USER_INFO");
            if (info != null) {
                infoUser = mGson.fromJson(info, UserModel.class);
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        }
    }
//
//    /**
//     * @param model 个人资料String
//     * @desc 保存个人信息
//     */
//    public void savePersonnInfo(String model) {
//        share.save("PERSON_INFO", model);
//        if (checkLogin()) {
//            Gson gson = new Gson();
//            ResultModel result = gson.fromJson(model, ResultModel.class);
//            infoUser = result.getResult().getUser();
//            String info = MyGson.gson.toJson(infoUser);
//            share.save("USER_INFO", info);
//        }
//    }
//
//    /**
//     * @return 返回个人资料String
//     * @desc 读取个人资料String
//     */
//    public String readPersonInfo() {
//        return share.read("PERSON_INFO");
//    }
}
