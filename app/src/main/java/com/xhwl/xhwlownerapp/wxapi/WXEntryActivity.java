package com.xhwl.xhwlownerapp.wxapi;

import android.os.Bundle;

import com.umeng.socialize.weixin.view.WXCallbackActivity;
import com.xhwl.xhwlownerapp.R;

public class WXEntryActivity extends WXCallbackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);
    }
}