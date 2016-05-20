package com.example.heart.ui.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baijiahulian.common.utils.DisplayUtils;
import com.baijiahulian.hermes.engine.models.SendMsgModel;
import com.baijiahulian.tianxiao.base.util.TXUIMethods;
import com.example.heart.R;
import com.example.heart.model.ResReceiveMessageModel;
import com.example.heart.ui.activity.LiveRecordActivity;
import com.example.heart.wsserver.IMServer;
import com.example.heart.wsserver.Message;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import tyrantgit.widget.HeartLayout;
import tyrantgit.widget.HeartView;

/**
 * Created by Bruce Too On 2/25/16. At 22:50
 */
public class LiveRecordDialogFragment extends DialogFragment implements View.OnClickListener,
    IMServer.OnReceiveMessageListener, IMServer.OnSendMessageListener {

    private final static String TAG = LiveRecordDialogFragment.class.getSimpleName();
    private LiveRecordActivity mActivity;
    private View rootView;
    private View topHeader;
    private View leftHeader;
    private boolean isOpen;

    private RecyclerView mListUsers;

    private Handler handler = new Handler();

    private Random mRandom = new Random();
    private HeartLayout mHeartLayout;
    private RecyclerView mChatListView;
    private LinearLayoutManager mLinearLayoutManager;
    private MessageAdapter mMessageAdapter;
    private UsersAdapter mUsersAdapter;
    private ImageView mChatIv;
    private ImageView mShareIv;
    private EditText mInputEditText;
    LinearLayout mOperatorLinearlayout;
    LinearLayout mInputLinearlayout;
    Button mSendButton;

    private List<ChatInfo> mMessageDataList = new ArrayList<>();
    private List<UserInfo> mUserDataList = new ArrayList<>();

    private Timer mTimer = new Timer();

    public static enum ITEM_TYPE {
        ITEM_TYPE_WELCOME, ITEM_TYPE_TEXT
    }

    private IMServer mImServer;

    private String mClassId = "100";

    public void setAct(LiveRecordActivity act) {
        this.mActivity = act;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_layer, container, false);
        rootView = view.findViewById(R.id.root_view);
        topHeader = view.findViewById(R.id.tv_header_top);
        leftHeader = view.findViewById(R.id.tv_header_left);
        mListUsers = (RecyclerView) view.findViewById(R.id.listview_users);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mListUsers.setLayoutManager(layoutManager);
        mListUsers.setHasFixedSize(true);
        mUsersAdapter = new UsersAdapter();
        mListUsers.setAdapter(mUsersAdapter);

        mHeartLayout = (HeartLayout) view.findViewById(R.id.heart_layout);
        mChatListView = (RecyclerView) view.findViewById(R.id.listview_public_chat);
        mLinearLayoutManager = new LinearLayoutManager(mActivity);
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mChatListView.setLayoutManager(mLinearLayoutManager);
        mChatListView.setHasFixedSize(true);
        mMessageAdapter = new MessageAdapter();
        mChatListView.setAdapter(mMessageAdapter);
        mShareIv = (ImageView) view.findViewById(R.id.img_shareroom);
        mChatIv = (ImageView) view.findViewById(R.id.img_chat);

        mInputEditText = (EditText) view.findViewById(R.id.edittext_chat);
        mOperatorLinearlayout = (LinearLayout) view.findViewById(R.id.layout_oper_container);
        mInputLinearlayout = (LinearLayout) view.findViewById(R.id.layout_bottom);
        mSendButton = (Button) view.findViewById(R.id.btn_send);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 监听软键盘的隐藏和显示 但是activity的配置必须是 android:windowSoftInputMode="adjustResize" 但是此处的需求不能resize activity中的布局
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
                System.err.println("******" + heightDiff);
                if (heightDiff > 100) { // 软键盘存在
                    if (mOperatorLinearlayout.getVisibility() == View.VISIBLE) {
                        mOperatorLinearlayout.setVisibility(View.GONE);
                        mInputLinearlayout.setVisibility(View.VISIBLE);
                        animateToHide();
                    }
                } else {
                    if (mOperatorLinearlayout.getVisibility() != View.VISIBLE) {
                        mOperatorLinearlayout.setVisibility(View.VISIBLE);
                        mInputLinearlayout.setVisibility(View.INVISIBLE);
                        animateToShow();
                    }
                }
            }
        });

        view.findViewById(R.id.click_view).setOnClickListener(this);
        mShareIv.setOnClickListener(this);
        mChatIv.setOnClickListener(this);
        mSendButton.setOnClickListener(this);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mImServer) {
            mImServer.stop();
        }
    }

    private void initData() {
        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mHeartLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mHeartLayout.addHeart(randomColor());
                    }
                });
            }
        }, 500, 1000);
        // mMessageDataList.addAll(contactList);
        for (int i = 0; i < 20; i++) {
            mMessageDataList.add(new ChatInfo("丽丽:", 1, "我点亮了我点亮了我点亮了我点亮了我点亮了我点亮了我点亮了我点亮了我点亮了",
                ITEM_TYPE.ITEM_TYPE_TEXT));
            mMessageDataList.add(new ChatInfo("机电:", 1, "我点亮了", ITEM_TYPE.ITEM_TYPE_TEXT));
            mMessageDataList.add(new ChatInfo("地方:", 1, "我点亮了", ITEM_TYPE.ITEM_TYPE_TEXT));
            mMessageDataList.add(new ChatInfo("多少:", 1, "我点亮了", ITEM_TYPE.ITEM_TYPE_TEXT));
        }
        mMessageAdapter.notifyDataSetChanged();
        mChatListView.scrollToPosition(mMessageDataList.size() - 1);

        for (int i = 0; i < 20; i++) {
            mUserDataList.add(new UserInfo());
        }
        mUsersAdapter.notifyDataSetChanged();

        // 启动IM
        mImServer = new IMServer(getActivity(), mClassId);
        mImServer.setReceiveMessageListener(this);
        mImServer.start();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.click_view) {
            if (mInputLinearlayout.getVisibility() == View.VISIBLE) {
                HideInput();
            } else {
                // 增加心形
                mHeartLayout.addHeart(randomColor());
            }
        } else if (view.getId() == mShareIv.getId()) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.room_share, null);
            PopupWindow menuWindow =
                new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT); // 后两个参数是width和height
            // menuWindow.showAsDropDown(layout); //设置弹出效果
            // menuWindow.showAsDropDown(null, 0, layout.getHeight());
            // 设置如下四条信息，当点击其他区域使其隐藏，要在show之前配置
            menuWindow.setFocusable(true);
            menuWindow.setOutsideTouchable(true);
            menuWindow.update();
            menuWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.wheel_bg));
            // menuWindow.showAtLocation(mShareIv, Gravity.BOTTOM|Gravity.RIGHT, menuWindow.getWidth(), 0); //
            // 设置layout在PopupWindow中显示的位置
            // int x = AppConfig.screenWidth / 2 - menuWindow.getWidth() / 2;
            menuWindow.showAsDropDown(mShareIv, menuWindow.getWidth() / 2, 0 - menuWindow.getHeight());
            // menuWindow.showAsDropDown(mShareIv, 0, layout.getHeight());
        } else if (view.getId() == mChatIv.getId()) {
            ShowInput();
        } else if (view.getId() == R.id.btn_send) {
            HideInput();
            mMessageDataList.add(new ChatInfo("机电:", 1, mInputEditText.getText().toString(), ITEM_TYPE.ITEM_TYPE_TEXT));
            mMessageAdapter.notifyItemInserted(mMessageDataList.size());
            mChatListView.scrollToPosition(mMessageDataList.size() - 1);
            // 服务器发送消息
            SendMessage(mInputEditText.getText().toString());
            mInputEditText.setText("");
        }
    }

    private void SendMessage(String str) {
        Message message = new Message();
        message.setBody(str);
        mImServer.sendMessage(message, "-1", this);
    }

    private void ShowInput() {
        mOperatorLinearlayout.setVisibility(View.GONE);
        // TXUIMethods.showSoftInput(mInputEditText);
        mInputLinearlayout.setVisibility(View.VISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                TXUIMethods.showSoftInput(mInputEditText);
            }
        }, 100);

        // 头部的View执行退出动画
        animateToHide();
    }

    private void HideInput() {
        mOperatorLinearlayout.setVisibility(View.VISIBLE);
        // TXUIMethods.hideSoftInput(mInputEditText);
        mInputLinearlayout.setVisibility(View.INVISIBLE);
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mInputEditText.getWindowToken(), 0);

        animateToShow();
    }

    private void animateToShow() {
        ObjectAnimator leftAnim = ObjectAnimator.ofFloat(leftHeader, "translationX", -leftHeader.getWidth(), 0);
        ObjectAnimator topAnim = ObjectAnimator.ofFloat(topHeader, "translationY", -topHeader.getHeight(), 0);
        animatorSetShow.playTogether(leftAnim, topAnim);
        animatorSetShow.setDuration(300);
        animatorSetShow.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isOpen = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isOpen = true;
            }
        });
        if (!isOpen) {
            animatorSetShow.start();
        }
    }

    AnimatorSet animatorSetHide = new AnimatorSet();
    AnimatorSet animatorSetShow = new AnimatorSet();

    private void animateToHide() {
        // Log.e("animateTo", "hide:" + animatorSetShow.isRunning());
        ObjectAnimator leftAnim = ObjectAnimator.ofFloat(leftHeader, "translationX", 0, -leftHeader.getWidth());
        ObjectAnimator topAnim = ObjectAnimator.ofFloat(topHeader, "translationY", 0, -topHeader.getHeight());
        animatorSetHide.playTogether(leftAnim, topAnim);
        animatorSetHide.setDuration(300);
        animatorSetHide.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isOpen = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isOpen = true;
            }
        });
        if (!isOpen) {
            animatorSetHide.start();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.MainDialog) {
            @Override
            public void onBackPressed() {
                // 监听软键盘不存在时 dialog的返回键监听 处理自定义逻辑
                Log.e("MainDialogFragment", "onBackPressed");
                super.onBackPressed();
                mActivity.finish();
            }
        };
        return dialog;
    }

    private int randomColor() {
        return Color.rgb(mRandom.nextInt(255), mRandom.nextInt(255), mRandom.nextInt(255));
    }

    public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == ITEM_TYPE.ITEM_TYPE_WELCOME.ordinal()) {
                View itemView =
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_room_publicchat, parent, false);
                return new WelcomeViewHolder(itemView);
            } else {
                View itemView =
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_room_publicchat_text, parent, false);
                return new TextViewHolder(itemView);
            }
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder vh, int position) {
            ChatInfo info = mMessageDataList.get(position);
            if (vh instanceof WelcomeViewHolder) {
                WelcomeViewHolder welcomeViewHolder = (WelcomeViewHolder) vh;
                welcomeViewHolder.iv.setImageDrawable(getResources().getDrawable(R.drawable.rank_10));
                welcomeViewHolder.tvName.setText(info.name);
                welcomeViewHolder.tvContent.setText(info.text);
                welcomeViewHolder.heartView.setColor(randomColor());
            } else if (vh instanceof TextViewHolder) {
                TextViewHolder textViewHolder = (TextViewHolder) vh;
                Drawable d = getResources().getDrawable(R.drawable.rank_102);
                d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
                // 创建ImageSpan
                ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
                HeartView vv = new HeartView(mActivity);
                vv.setColor(randomColor());
                Drawable drawable = vv.getDrawable();
                drawable.setBounds(0, 0, DisplayUtils.dip2px(mActivity, 18), DisplayUtils.dip2px(mActivity, 18));
                ImageSpan imageSpan1 = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
                SpannableString spannableString = new SpannableString("rank" + info.name + info.text + "heart");
                // 用ImageSpan对象替换rank
                spannableString.setSpan(span, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(
                    new ForegroundColorSpan(getResources().getColor(R.color.cell_public_chat_usernick)), 4,
                    info.name.length() + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), info.name.length() + 4,
                    info.text.length() + info.name.length() + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(imageSpan1, info.text.length() + info.name.length() + 4,
                    spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                textViewHolder.tvContent.setText(spannableString);
            }
        }

        @Override
        public int getItemViewType(int position) {
            ChatInfo info = mMessageDataList.get(position);
            if (info.type == ITEM_TYPE.ITEM_TYPE_WELCOME) {
                return ITEM_TYPE.ITEM_TYPE_WELCOME.ordinal();
            } else if (info.type == ITEM_TYPE.ITEM_TYPE_TEXT) {
                return ITEM_TYPE.ITEM_TYPE_TEXT.ordinal();
            }
            return ITEM_TYPE.ITEM_TYPE_TEXT.ordinal();
        }

        @Override
        public int getItemCount() {
            return mMessageDataList == null ? 0 : mMessageDataList.size();
        }

        public class WelcomeViewHolder extends RecyclerView.ViewHolder {
            protected ImageView iv;
            protected TextView tvName;
            protected TextView tvContent;
            protected HeartView heartView;

            public WelcomeViewHolder(View itemView) {
                super(itemView);
                iv = (ImageView) itemView.findViewById(R.id.txt_chat_iv);
                tvName = (TextView) itemView.findViewById(R.id.txt_chat_name);
                tvContent = (TextView) itemView.findViewById(R.id.txt_chat_content);
                heartView = (HeartView) itemView.findViewById(R.id.txt_chat_heart);
                itemView.setTag(this);
            }
        }

        public class TextViewHolder extends RecyclerView.ViewHolder {
            protected TextView tvContent;

            public TextViewHolder(View itemView) {
                super(itemView);
                tvContent = (TextView) itemView.findViewById(R.id.txt_chat_content);
                itemView.setTag(this);
            }
        }
    }

    public class ChatInfo {
        public ChatInfo(String name, int rank, String text, ITEM_TYPE type) {
            this.name = name;
            this.rank = rank;
            this.text = text;
            this.type = type;
        }

        public ITEM_TYPE type;
        public int rank;
        public String name;
        public String text;
    }

    public class UserInfo {

    }

    public class UsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_user_item, parent, false);
            return new UserHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder vh, int position) {
            ChatInfo info = mMessageDataList.get(position);
            UserHolder userHolder = (UserHolder) vh;
            userHolder.ivPic.setImageDrawable(getResources().getDrawable(R.drawable.default_head));
            userHolder.ivRank.setImageDrawable(getResources().getDrawable(R.drawable.global_xing_1));
        }

        @Override
        public int getItemCount() {
            return mMessageDataList == null ? 0 : mMessageDataList.size();
        }

        public class UserHolder extends RecyclerView.ViewHolder {
            protected SimpleDraweeView ivPic;
            protected SimpleDraweeView ivRank;

            public UserHolder(View itemView) {
                super(itemView);
                ivPic = (SimpleDraweeView) itemView.findViewById(R.id.user_portrait);
                ivRank = (SimpleDraweeView) itemView.findViewById(R.id.img_user_type);
                itemView.setTag(this);
            }
        }
    }

    @Override
    public void onReceiveMessageSucc(ResReceiveMessageModel model) {
        mMessageDataList.add(new ChatInfo("机电:", 1, model.content, ITEM_TYPE.ITEM_TYPE_TEXT));
        mMessageAdapter.notifyItemInserted(mMessageDataList.size());
        mChatListView.scrollToPosition(mMessageDataList.size() - 1);
    }

    @Override
    public void onSendMessageSucc(long msgKey, SendMsgModel model) {
        Log.d(TAG, "success");
    }

    @Override
    public void onSendMessageFail(long msgKey, int error_Code, String error_msg) {
        Log.d(TAG, "error_Code " + error_Code + " error_msg " + error_msg);
    }
}
