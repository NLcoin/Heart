package com.example.heart.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.baijiahulian.tianxiao.ui.TXBaseActivity;
import com.example.heart.R;
import com.example.heart.ui.fragment.MainFragment;
import com.example.heart.ui.fragment.MeFragment;

public class MainActivity extends TXBaseActivity implements View.OnClickListener {

    private static final int TAB_Main = 0;
    private static final int TAB_Me = 1;
    private static final int TAB_COUNT = 2;
    private Fragment[] mFragments;

    private ImageView tabMain;
    private ImageView tabMe;
    private ImageView tabRoom;
    private FrameLayout container;
    private int mLastSelectedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initData();
    }

    protected boolean bindContentView() {
        setContentView(R.layout.activity_main);
        return true;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mLastSelectedIndex = -1;
        onButtonClicked(savedInstanceState.getInt("tab_index", TAB_Main));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("tab_index", mLastSelectedIndex);
    }

    private void initViews() {
        mFragments = new Fragment[TAB_COUNT];
        mFragments[TAB_Main] = new MainFragment();
        mFragments[TAB_Me] = new MeFragment();
        tabMain = (ImageView) findViewById(R.id.activity_main_main);
        tabMe = (ImageView) findViewById(R.id.activity_main_me);
        tabRoom = (ImageView) findViewById(R.id.activity_main_room);
        container = (FrameLayout) findViewById(R.id.main_fragment_container);
        tabMain.setOnClickListener(this);
        tabMe.setOnClickListener(this);
        tabRoom.setOnClickListener(this);
    }

    private void initData() {
        onButtonClicked(TAB_Main);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.activity_main_main) {
            onButtonClicked(TAB_Main);
        } else if (view.getId() == R.id.activity_main_me) {
            onButtonClicked(TAB_Me);
        } else if (view.getId() == R.id.activity_main_room) {
            LiveRecordActivity.launch(this);
        }
    }

    private void onButtonClicked(int index) {
        if (mLastSelectedIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            if (mLastSelectedIndex >= 0) {
                trx.hide(mFragments[mLastSelectedIndex]);
            }
            if (!mFragments[index].isAdded()) {
                trx.add(R.id.main_fragment_container, mFragments[index]);
            }
            trx.show(mFragments[index]).commitAllowingStateLoss();
            mLastSelectedIndex = index;
        }
    }
    private long exitTime = 0;
    @Override
    public void onBackPressed() {
        // 判断是否在两秒之内连续点击返回键，是则退出，否则不退出
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 将系统当前的时间赋值给exitTime
            exitTime = System.currentTimeMillis();
        } else {
            super.onBackPressed();
        }
//        super.onBackPressed();
    }
}
