package com.xhwl.xhwlownerapp.activity.View.GrantCard;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.zaaach.toprightmenu.TopRightMenu;
import com.zhy.autolayout.AutoLinearLayout;

import java.io.UnsupportedEncodingException;


/**
 * 门卡管理
 */
public class DoorCardManagerActivity extends BaseActivity implements View.OnClickListener {
    AutoLinearLayout mWebviewTopBack;
    TextView mWebviewTopTitle;
    ImageView mWebviewTopRecord;
    WebView mDoorCardManager;
    private TopRightMenu mTopRightMenu;
    private boolean showIcon = true;
    private boolean dimBg = true;
    private boolean needAnim = true;
    private Intent intent = new Intent();
    private String userName;
    private String projectCode;
    private String granterPhone;
    private String proName;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_door_card_manager);
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initView() {
        userName  = SPUtils.get(DoorCardManagerActivity.this,"userName","");
        projectCode  = SPUtils.get(DoorCardManagerActivity.this,"proCode","");
        granterPhone  = SPUtils.get(DoorCardManagerActivity.this,"userTelephone","");
        proName  = SPUtils.get(DoorCardManagerActivity.this,"proName","");
        String loadUrl = setUrl(userName, projectCode, granterPhone);
        Log.e("url", loadUrl);

        mWebviewTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
        mWebviewTopBack.setOnClickListener(this);
        mWebviewTopTitle = (TextView) findViewById(R.id.top_title);
        mWebviewTopRecord = (ImageView) findViewById(R.id.top_record);
        mWebviewTopRecord.setOnClickListener(this);
        mDoorCardManager = (WebView) findViewById(R.id.door_card_manager);
        mWebviewTopRecord.setBackgroundResource(R.drawable.more);
        mWebviewTopRecord.setVisibility(View.VISIBLE);
        mDoorCardManager.setWebContentsDebuggingEnabled(true);

        WebSettings webSettings = mDoorCardManager.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAppCacheEnabled(true);//是否使用缓存
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        mDoorCardManager.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mDoorCardManager.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //获取HTML title
                Log.d("ANDROID_LAB", "TITLE=" + title);
                mWebviewTopTitle.setText(title);
            }

            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, false);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                request.grant(request.getResources());
            }
        });
        Log.e("title", mDoorCardManager.getTitle());
        //与此同时需要在webview当中注册，后面的“Android”与html中的对应：
        //this.mDoorCardManager.addJavascriptInterface(new AndroidAndJS.AndroidAndJSInterface(),"jsObj");
        mDoorCardManager.loadUrl(loadUrl);
    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.top_back) {
            finish();

        } else if (i == R.id.top_record) {//更多
            intent.setClass(DoorCardManagerActivity.this, GrantCardActivity.class);
            startActivity(intent);
        }
    }

    public String setUrl(String userName, String projectCode, String granterPhone) {
        userName = toUtf8(userName);
        String url = "https://yq.xhmind.com:8093/#/management/?projectCode=" + projectCode + "&userName=" + userName + "&granterPhone=" + granterPhone;
        return url;
    }

    public static String toUtf8(String str) {
        String result = null;
        try {
            result = new String(str.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    //改写物理按键——返回的逻辑
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mDoorCardManager.canGoBack()) {
                mDoorCardManager.goBack();//返回上一页面
                return true;
            } else {
                finish();
            }
        }
        return true;
    }
}
