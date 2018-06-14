package com.xhwl.xhwlownerapp.activity.View.Guide;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.activity.View.HomeView.HomeActivity;

public class BootActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot);
        boolean mFirst = isFirstEnter(BootActivity.this,BootActivity.this.getClass().getName());
        if(mFirst)
            mHandler.sendEmptyMessageDelayed(SWITCH_GUIDACTIVITY,2000);
        else
            mHandler.sendEmptyMessageDelayed(SWITCH_MAINACTIVITY,2000);
    }
    //****************************************************************
    // 判断应用是否初次加载，读取SharedPreferences中的guide_activity字段
    //****************************************************************
    private static final String SHAREDPREFERENCES_NAME = "my_pref";
    private static final String KEY_GUIDE_ACTIVITY = "guide_activity";
    private boolean isFirstEnter(Context context, String className){
        if(context==null || className==null||"".equalsIgnoreCase(className))return false;
        String mResultStr = context.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE)
                .getString(KEY_GUIDE_ACTIVITY, "");//取得所有类名 如 com.my.MainActivity
        if(mResultStr.equalsIgnoreCase("false"))
            return false;
        else
            return true;
    }

    //*************************************************
    // Handler:跳转至不同页面
    //*************************************************
    private final static int SWITCH_MAINACTIVITY = 1000;
    private final static int SWITCH_GUIDACTIVITY = 1001;
    public Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch(msg.what){
                case SWITCH_MAINACTIVITY:
                    Intent mIntent = new Intent();
                    mIntent.setClass(BootActivity.this, HomeActivity.class);
                    BootActivity.this.startActivity(mIntent);
                    BootActivity.this.finish();
                    break;
                case SWITCH_GUIDACTIVITY:
                    mIntent = new Intent();
                    mIntent.setClass(BootActivity.this, GuideActivity.class);
                    BootActivity.this.startActivity(mIntent);
                    BootActivity.this.finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //单击返回键返回，不退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
        }
        return true;
    }
}
