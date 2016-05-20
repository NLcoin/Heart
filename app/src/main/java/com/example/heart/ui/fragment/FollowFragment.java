package com.example.heart.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.baijiahulian.tianxiao.ui.TXBaseFragment;
import com.example.heart.R;

public class FollowFragment extends TXBaseFragment {
    public static final String INTENT_IN_STATUS_ID = "intent-in-status-id";

    public static FollowFragment newInstance(int status) {
        FollowFragment f = new FollowFragment();
        Bundle b = new Bundle();
        b.putInt(INTENT_IN_STATUS_ID, status);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follow, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CheckBox cb = (CheckBox)getView().findViewById(R.id.cb);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                System.err.println(isChecked);
            }
        });
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.err.println("click");
            }
        });
    }
}
