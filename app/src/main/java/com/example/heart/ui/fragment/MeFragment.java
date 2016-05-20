package com.example.heart.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baijiahulian.tianxiao.ui.TXBaseFragment;
import com.example.heart.R;

public class MeFragment extends TXBaseFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_me, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mTvTitle = (TextView) getView().findViewById(R.id.tx_main_main_tv_title);
//        mTvIncomeToday = (TextView) getView().findViewById(R.id.tx_main_main_tv_today);
//        mTvIncomeWeek = (TextView) getView().findViewById(R.id.tx_main_main_tv_week);
//        mTvIncomeMonth = (TextView) getView().findViewById(R.id.tx_main_main_tv_month);
//        mTvIncomeTotal = (TextView) getView().findViewById(R.id.tx_main_main_tv_total);
//        getView().findViewById(R.id.tx_person_main_iv_home).setOnClickListener(this);
//        // 初始化功能列表
//        initViews();
//        mDataService.getOrgIncome(this, new ITXDataServiceCallback<TXOrgIncomeModel>() {
//            @Override
//            public void onSuccess(TXServiceResultModel result, TXOrgIncomeModel obj, Object param) {
//                updateIncome(obj);
//            }
//
//            @Override
//            public void onError(TXErrorModel result, Object param) {
//                TXTips.show(getActivity(), result.message);
//                updateIncome(null);
//            }
//        }, null);
//        String name = "";
//        if (TXAuthManager.getInstance().getUserAccount() != null) {
//            name = TXAuthManager.getInstance().getUserAccount().shortName;
//            if (TextUtils.isEmpty(name)) {
//                name = TXAuthManager.getInstance().getUserAccount().name;
//            }
//        }
//        mTvTitle.setText(name);
    }
}
