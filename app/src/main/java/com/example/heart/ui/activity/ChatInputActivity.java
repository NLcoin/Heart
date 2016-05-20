package com.example.heart.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.baijiahulian.tianxiao.ui.TXBaseActivity;
import com.example.heart.R;

public class ChatInputActivity extends TXBaseActivity implements View.OnClickListener {

    private EditText mContent;
    private Button mSend;
    public static void launch(Activity context, int requestCode) {
        Intent intent = new Intent(context, ChatInputActivity.class);
        context.startActivityForResult(intent, requestCode);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        initData();
    }

    protected boolean bindContentView() {
        setContentView(R.layout.test);
        return true;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    private void initViews() {
//         mContent = (EditText)findViewById(R.id.edittext_chat);
//        mSend = (Button)findViewById(R.id.btn_send);
//
//        mSend.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public void onClick(View view) {
//        if (view.getId() == R.id.btn_send) {
//            Intent it = new Intent();
//            it.putExtra("result", mContent.getText().toString());
//            setResult(Activity.RESULT_OK, it);
//            finish();
//        }
    }
}
