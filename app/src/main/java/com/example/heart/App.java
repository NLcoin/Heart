package com.example.heart;

import android.support.multidex.MultiDexApplication;

import com.example.heart.model.UserModel;
import com.example.heart.utils.UserHolderUtil;
import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by bjhl on 16/3/15.
 */
public class App extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(getApplicationContext());// IM利用了fresco，需要初始化一下
        UserModel user = new UserModel();
        user.id = "10002";
        user.number = "10002";
        user.type = 0;
        user.name = "test";
        user.avatar = "";
        user.end_type = 4;
        UserHolderUtil.getUserHolder(getApplicationContext()).saveUser(user, "", "");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}