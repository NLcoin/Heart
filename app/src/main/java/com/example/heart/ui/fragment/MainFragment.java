package com.example.heart.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.baijiahulian.tianxiao.ui.TXBaseFragment;
import com.example.heart.R;

import me.xiaopan.psts.PagerSlidingTabStrip;

public class MainFragment extends TXBaseFragment {

    private PagerSlidingTabStrip mPagerSlidingTabStrip;
    private ViewPager mViewPager;
    private ImageView mSearch;
    private ImageView mChat;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPagerSlidingTabStrip = (PagerSlidingTabStrip) getView().findViewById(R.id.slidingTabStrip);
        mViewPager = (ViewPager) getView().findViewById(R.id.viewPager);
        mSearch = (ImageView) getView().findViewById(R.id.img_search);
        mChat = (ImageView) getView().findViewById(R.id.img_chat);
        PagerAdapter adapter = new MyPagerAdapter(getFragmentManager());
        mViewPager.setAdapter(adapter);
        mPagerSlidingTabStrip.setViewPager(mViewPager);
        mViewPager.setCurrentItem(1);
        mPagerSlidingTabStrip.setOnClickTabListener(new PagerSlidingTabStrip.OnClickTabListener() {
            @Override
            public void onClickTab(View tab, int index) {

            }
        });
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return FollowFragment.newInstance(0);
            } else if (position == 1) {
                return HotFragment.newInstance(1);
            } else {
                return RecommendFragment.newInstance(2);
            }
        }
    }
}
