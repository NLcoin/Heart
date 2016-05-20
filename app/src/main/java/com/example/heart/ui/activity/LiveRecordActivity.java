package com.example.heart.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.baijiahulian.tianxiao.ui.TXBaseActivity;
import com.example.heart.R;
import com.example.heart.ui.fragment.LiveRecordDialogFragment;
import com.example.heart.ui.fragment.VideoFragment;

public class LiveRecordActivity extends TXBaseActivity{

    public static void launch(Context context) {
        Intent intent = new Intent(context, LiveRecordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initData();
    }

    protected boolean bindContentView() {
        setContentView(R.layout.activity_live_record);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initViews() {
        VideoFragment fragment = new VideoFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.layout_video_play, fragment)
                .commit();

        LiveRecordDialogFragment fragment1 = new LiveRecordDialogFragment();
        fragment1.setAct(this);
        fragment1.show(getSupportFragmentManager(), "MainDialogFragment");
    }

    private void initData() {
    }
}
