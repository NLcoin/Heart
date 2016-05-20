package com.example.heart.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bjhl on 16/4/8.
 */
public class UserModel extends BaseModel {

    @SerializedName("id")
    public String id;

    @SerializedName("number")
    public String number;

    @SerializedName("type")
    public int type;// 0 观众 1 主播 2 管理员

    @SerializedName("name")
    public String name;// 用户昵称

    @SerializedName("avatar")
    public String avatar;// 用户头像

    @SerializedName("end_type")
    public int end_type;// 1 pc网页 2 pc客户端 3 m站 4 app
}
