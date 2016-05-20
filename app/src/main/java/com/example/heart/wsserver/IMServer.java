package com.example.heart.wsserver;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.baijiahulian.hermes.IMEnvironment;
import com.baijiahulian.hermes.engine.models.SendMsgModel;
import com.example.heart.model.ResReceiveMessageModel;
import com.example.heart.utils.GsonUtil;
import com.example.heart.utils.LPLogger;
import com.example.heart.utils.UserHolderUtil;
import com.genshuixue.liveplayer.WebSocketClient;
import com.genshuixue.liveplayer.WebSocketListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bjhl on 16/4/7.
 */
public class IMServer {
    private final static String TAG = IMServer.class.getSimpleName();
    private static final String[] mHost = { "ws://172.21.138.1:392", "ws://beta-im-proxy.genshuixue.com:8080/", // beta
        "ws://im-proxy.genshuixue.com/" // www
    };
    private Context mContext;
    private String mClassId;
    private boolean isLogin = false;

    private static String getHost() {
        return mHost[IMEnvironment.getInstance().getDebugMode().value()];
    }

    public static final Gson gson = new GsonBuilder().create();
    private final int CONNECT_STATE_NO = 0;// 没连接
    private final int CONNECT_STATE_HAVE = 1;// 已经连接
    private final int CONNECT_STATE_ON = 2;// 正在连接

    private int connectState = CONNECT_STATE_NO;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private Timer timer = null;
    private TimerTask task = null;
    // 心跳时间间隔－－－30秒
    private int heartTimeout = 30 * 1000;
    // 重连次数
    private String loginSessionId = "android-socket-im";
    private WebSocketClient webSocketClient = null;
    private boolean isEngineRunning = false;
    private OnReceiveMessageListener mReceiveMessageListener;

    public IMServer(Context context, String class_id) {
        mContext = context;
        mClassId = class_id;
    }

    public void setReceiveMessageListener(OnReceiveMessageListener listener) {
        this.mReceiveMessageListener = listener;
    }

    /**
     * 发消息
     */
    public void sendMessage(Message msg, String to, final OnSendMessageListener listener) {
        if (!isEngineRunning || null == webSocketClient) {
            return;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("message_type", "message_send");
        jsonObject.addProperty("content", msg.getBody());
        jsonObject.addProperty("to", to);
        JsonElement jsonElement =
            GsonUtil.jsonParser.parse(GsonUtil.toString(UserHolderUtil.getUserHolder(mContext).getUser()));
        jsonObject.add("from", jsonElement);

        webSocketClient.send(loginSessionId, jsonObject.toString());
    }

    /**
     * 接收到消息
     */
    public void dealReceiveMessage(String body) {
        if (null != mReceiveMessageListener) {
            try {
                final ResReceiveMessageModel model = gson.fromJson(body, ResReceiveMessageModel.class);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mReceiveMessageListener.onReceiveMessageSucc(model);// 成功后进入异步任务处理，因此不许要切换线程
                    }
                });
            } catch (Exception e) {
                LPLogger.e("ReceiveMessage json解析错误");
            }
        }
    }

    /**
     * 启动
     */
    public void start() {
        LPLogger.d("start");
        webSocketClient = new WebSocketClient();
        webSocketClient.setWebSocketListener(wsListener);
        connect();
    }

    /**
     * 停止
     */
    public void stop() {
        LPLogger.d("stop");
        stopTimer();
        if (connectState == CONNECT_STATE_HAVE) {
            webSocketClient.close(loginSessionId);
            webSocketClient.setWebSocketListener(null);
            webSocketClient = null;
            connectState = CONNECT_STATE_NO;
        }
        isEngineRunning = false;
    }

    /**
     * 连接服务器
     */
    private void connect() {
        // loginSessionId = UUID.randomUUID().toString();
        webSocketClient.connect(loginSessionId, getHost());
        connectState = CONNECT_STATE_ON;
        LPLogger.d("connect");
    }

    /**
     * 重新连接服务器
     */
    private void reconnect() {
        if (connectState == CONNECT_STATE_ON || connectState == CONNECT_STATE_HAVE)
            return;
        webSocketClient.close(loginSessionId);
        isEngineRunning = false;
        LPLogger.d("reconnect……");
        connect();
    }

    /**
     * 客户端接收到login成功消息后，启动定时器，发送心跳
     */
    private void restartTimer() {
        stopTimer();
        startTimer();
    }

    /**
     * 停止定时器
     */
    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
        task = null;
    }

    /**
     * 定时发送
     */
    private void startTimer() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                if (connectState == CONNECT_STATE_HAVE) {
                    doHeart();
                }
            }
        };
        timer.schedule(task, heartTimeout, heartTimeout);// 延迟一个心跳间隔
    }

    /**
     * 服务器回调
     */
    private WebSocketListener wsListener = new WebSocketListener() {
        @Override
        public void onOpen(Object guid) {
            LPLogger.d("onOpen");
            connectState = CONNECT_STATE_HAVE;
            isEngineRunning = true;
            doLogin();
            restartTimer();
        }

        @Override
        public void onMessage(Object guid, String msg) {
            LPLogger.d("onMessage, " + msg);
            if (!isEngineRunning)
                return;// 异步情况下，又可能本身已经被主动停止－－须判断
            final String final_msg = msg;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject jsonMsg = new JSONObject(final_msg);
                        String message_type = jsonMsg.getString("message_type");
                        if ("login_res".equals(message_type)) {
                            // 登录成功
                            if (0 == jsonMsg.getInt("code")) {
                                isLogin = true;
                            } else {
                                isLogin = false;
                            }
                            return;
                        }
                        if ("message_send".equals(message_type)) {
                            // 发消息--回调
                            // String sign = jsonMsg.getString("sign");
                            // dealReceiveMessage(sign, response);
                        } else if ("message_receive".equals(message_type)) {
                            // 接收到新消息
                            dealReceiveMessage(jsonMsg.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        @Override
        public void onClose(Object guid) {
            LPLogger.d("onClose");
            connectState = CONNECT_STATE_NO;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    reconnect();
                }
            });
        }

        @Override
        public void onError(Object guid, String err) {
            LPLogger.d("onError, " + err);
            // 传输错误时，重连服务器－－－－－是否有个错误列表，进行不同的处理
            connectState = CONNECT_STATE_NO;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    reconnect();
                }
            });
        }
    };

    /**
     * 发送登陆信息
     */
    private void doLogin() {
        if (!isEngineRunning || null == webSocketClient) {
            return;
        }
        JsonObject loginJson = new JsonObject();
        loginJson.addProperty("message_type", "login_req");
        loginJson.addProperty("class_id", mClassId);
        JsonElement jsonElement =
            GsonUtil.jsonParser.parse(GsonUtil.toString(UserHolderUtil.getUserHolder(mContext).getUser()));
        loginJson.add("user", jsonElement);

        webSocketClient.send(loginSessionId, loginJson.toString());
    }

    /**
     * 发送心跳
     */
    private void doHeart() {
        if (!isEngineRunning || null == webSocketClient) {
            return;
        }
        JsonObject heartBeatJson = new JsonObject();
        heartBeatJson.addProperty("message_type", "heart_beat");

        LPLogger.d("webSocketClient send heart session_id=" + loginSessionId + ", json=" + heartBeatJson.toString());
        webSocketClient.send(loginSessionId, heartBeatJson.toString());
    }

    public interface CommonInterface {
    }

    /**
     * 消息发送完成回调接口
     */
    public interface OnSendMessageListener extends CommonInterface {
        void onSendMessageSucc(long msgKey, SendMsgModel model);

        void onSendMessageFail(long msgKey, int error_Code, String error_msg);
    }

    /**
     * 接收到消息
     */
    public interface OnReceiveMessageListener extends CommonInterface {
        void onReceiveMessageSucc(ResReceiveMessageModel model);
    }
}
