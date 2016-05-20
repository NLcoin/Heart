package com.example.heart.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by bjhl on 16/4/8.
 */
public class ResReceiveMessageModel extends BaseModel {

    @SerializedName("class_id")
    public String classId;//主播房间id

    @SerializedName("id")
    public String id;//// 消息唯一标识

    @SerializedName("time")
    public long time;// 到秒的时间戳

    @SerializedName("content")
    public String content;// 用户头像

    @SerializedName("from")
    public UserModel from;
}
