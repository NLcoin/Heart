package com.example.heart.ui.activity;

import java.util.Map;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.OnClick;

import com.baijiahulian.tianxiao.ui.TXBaseActivity;
import com.baijiahulian.tianxiao.views.TXTips;
import com.example.heart.R;
import com.example.heart.constants.Constants;
import com.example.heart.constants.OpenIdCatalog;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.SinaSsoHandler;

public class LoginActivity extends TXBaseActivity implements IUiListener {

    @Bind(R.id.btn_sina)
    ImageView mBtnSina;
    @Bind(R.id.btn_wechat)
    ImageView mBtnWechat;
    @Bind(R.id.btn_qq)
    ImageView mBtnQq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected boolean bindContentView() {
        setContentView(R.layout.activity_login);
        return true;
    }

    @OnClick({ R.id.btn_sina, R.id.btn_wechat, R.id.btn_qq })
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_sina:
                break;
            case R.id.btn_wechat:
                break;
            case R.id.btn_qq:
                break;
        }
    }
    /**
     * QQ登陆
     */
    private void qqLogin() {
        Tencent mTencent = Tencent.createInstance(Constants.QQ_APPID,
                this);
        mTencent.login(this, "all", this);
    }

    BroadcastReceiver receiver;
    /**
     * 微信登陆
     */
    private void wxLogin() {
        IWXAPI api = WXAPIFactory.createWXAPI(this, Constants.WEICHAT_APPID, false);
        api.registerApp(Constants.WEICHAT_APPID);

        if (!api.isWXAppInstalled()) {
            TXTips.show(LoginActivity.this,"手机中没有安装微信客户端");
            return;
        }
        // 唤起微信登录授权
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_login";
        api.sendReq(req);
        // 注册一个广播，监听微信的获取openid返回（类：WXEntryActivity中）
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OpenIdCatalog.WECHAT);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    String openid_info = intent.getStringExtra(LoginBindActivityChooseActivity.BUNDLE_KEY_OPENIDINFO);
                    openIdLogin(OpenIdCatalog.WECHAT, openid_info);
                    // 注销这个监听广播
                    if (receiver != null) {
                        unregisterReceiver(receiver);
                    }
                }
            }
        };

        registerReceiver(receiver, intentFilter);
    }

    /**
     * 新浪登录
     */
    private void sinaLogin() {
        final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");
        SinaSsoHandler sinaSsoHandler = new SinaSsoHandler();
        mController.getConfig().setSsoHandler(sinaSsoHandler);
        mController.doOauthVerify(this, SHARE_MEDIA.SINA,
                new SocializeListeners.UMAuthListener() {

                    @Override
                    public void onStart(SHARE_MEDIA arg0) {
                    }

                    @Override
                    public void onError(SocializeException arg0,
                                        SHARE_MEDIA arg1) {
                        TXTips.show(LoginActivity.this, "新浪授权失败");
                    }

                    @Override
                    public void onComplete(Bundle value, SHARE_MEDIA arg1) {
                        if (value != null && !TextUtils.isEmpty(value.getString("uid"))) {
                            // 获取平台信息
                            mController.getPlatformInfo(LoginActivity.this, SHARE_MEDIA.SINA, new SocializeListeners.UMDataListener() {
                                @Override
                                public void onStart() {

                                }

                                @Override
                                public void onComplete(int i, Map<String, Object> map) {
                                    if (i == 200 && map != null) {
                                        StringBuilder sb = new StringBuilder("{");
                                        Set<String> keys = map.keySet();
                                        int index = 0;
                                        for (String key : keys) {
                                            index++;
                                            String jsonKey = key;
                                            if (jsonKey.equals("uid")) {
                                                jsonKey = "openid";
                                            }
                                            sb.append(String.format("\"%s\":\"%s\"", jsonKey, map.get(key).toString()));
                                            if (index != map.size()) {
                                                sb.append(",");
                                            }
                                        }
                                        sb.append("}");
                                        openIdLogin(OpenIdCatalog.WEIBO, sb.toString());
                                    } else {
                                        TXTips.show(LoginActivity.this, "发生错误：" + i);
                                    }
                                }
                            });
                        } else {
                            TXTips.show(LoginActivity.this, "授权失败");
                        }
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA arg0) {
                        TXTips.show(LoginActivity.this, "已取消新浪登陆");
                    }
                });
    }

    // 获取到QQ授权登陆的信息
    @Override
    public void onComplete(Object o) {
        openIdLogin(OpenIdCatalog.QQ, o.toString());
    }

    @Override
    public void onError(UiError uiError) {

    }

    @Override
    public void onCancel() {

    }

    /***
     *
     * @param catalog 第三方登录的类别
     * @param openIdInfo 第三方的信息
     */
    private void openIdLogin(final String catalog, final String openIdInfo) {
//        final ProgressDialog waitDialog = DialogHelp.getWaitDialog(this, "登陆中...");
//        OSChinaApi.open_login(catalog, openIdInfo, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                LoginUserBean loginUserBean = XmlUtils.toBean(LoginUserBean.class, responseBody);
//                if (loginUserBean.getResult().OK()) {
//                    handleLoginBean(loginUserBean);
//                } else {
//                    // 前往绑定或者注册操作
//                    Intent intent = new Intent(LoginActivity.this, LoginBindActivityChooseActivity.class);
//                    intent.putExtra(LoginBindActivityChooseActivity.BUNDLE_KEY_CATALOG, catalog);
//                    intent.putExtra(LoginBindActivityChooseActivity.BUNDLE_KEY_OPENIDINFO, openIdInfo);
//                    startActivityForResult(intent, REQUEST_CODE_OPENID);
//                }
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//                AppContext.showToast("网络出错" + statusCode);
//            }
//
//            @Override
//            public void onStart() {
//                super.onStart();
//                waitDialog.show();
//            }
//
//            @Override
//            public void onFinish() {
//                super.onFinish();
//                waitDialog.dismiss();
//            }
//        });
    }
}
