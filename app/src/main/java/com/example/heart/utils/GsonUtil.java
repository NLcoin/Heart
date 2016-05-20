package com.example.heart.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by bjhl on 16/4/8.
 */
public class GsonUtil {
    private static final String TAG = GsonUtil.class.getSimpleName();
    // 默认所有日期格式都是一样的
    // 支持转换成Date类型
    public static final Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
            return new Date(json.getAsJsonPrimitive().getAsLong());
        }
    }).registerTypeHierarchyAdapter(Calendar.class, new CalendarAdapter()).create();
    public static final JsonParser jsonParser = new JsonParser();

    public static class CalendarAdapter implements JsonSerializer<Calendar>, JsonDeserializer<Calendar> {

        public Calendar deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
            Calendar calendar = Calendar.getInstance();
            // Calendar 的 time 有问题, 使用 Date
            calendar.setTime(new Date(json.getAsJsonPrimitive().getAsLong()));
            return calendar;
        }

        public JsonElement serialize(Calendar calendar, Type type, JsonSerializationContext context) {
            return new JsonPrimitive(Long.valueOf(calendar.getTimeInMillis()));
        }

    }

    public static <T> T parseString(String result, Class<T> classOfT) {
        if (result != null && classOfT != null) {
            try {
                return gson.fromJson(result, classOfT);
            } catch (JsonSyntaxException var3) {
                Log.e(TAG, "catch exception when format json str:" + result);
                throw var3;
            }
        } else {
            return null;
        }
    }

    public static String toString(Object obj) {
        return obj == null ? "" : gson.toJson(obj);
    }

    public static <T> T parseJsonObject(JsonObject result, Class<T> classOfT) {
        return gson.fromJson(result, classOfT);
    }

    // add ljz
    public static JsonObject toJsonObject(Object obj) {
        return jsonParser.parse(toString(obj)).getAsJsonObject();
    }
}
