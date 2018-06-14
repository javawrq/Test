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
import com.zhy.autolayout.AutoLinearLayout;

import java.io.UnsupportedEncodingException;

/**
 * 访客邀请
 */
public class VisitorInvitationActivity extends BaseActivity implements View.OnClickListener {

    private AutoLinearLayout mWebviewTopBack;
    /**
     * 远程开门
     */
    private TextView mWebviewTopTitle;
    private WebView mVisitorInvitation;

    private ImageView mWebviewTopRecord;
    private AndroidAndJS androidAndJS;
    private String userName;
    private String projectCode;
    private String granterPhone;
    private String proName;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_invitation);
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void initView() {
        userName  = SPUtils.get(VisitorInvitationActivity.this,"userName","");
        projectCode  = SPUtils.get(VisitorInvitationActivity.this,"proCode","");
        granterPhone  = SPUtils.get(VisitorInvitationActivity.this,"userTelephone","");
        proName  = SPUtils.get(VisitorInvitationActivity.this,"proName","");

        androidAndJS = new AndroidAndJS(VisitorInvitationActivity.this,proName,granterPhone);
        String loadUrl = setUrl(userName, projectCode, granterPhone);
        Log.e("url", loadUrl);
        mWebviewTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
        mWebviewTopBack.setOnClickListener(this);
        mWebviewTopTitle = (TextView) findViewById(R.id.top_title);

        mVisitorInvitation = (WebView) findViewById(R.id.visitor_invitation);
        mVisitorInvitation.setWebContentsDebuggingEnabled(true);
        WebSettings webSettings = mVisitorInvitation.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setAppCacheEnabled(true);//是否使用缓存
        //设置可以访问文件
        webSettings.setAllowFileAccess(true);
        mVisitorInvitation.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        mVisitorInvitation.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                //获取HTML title
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
        //与此同时需要在webview当中注册，后面的“Android”与html中的对应：
        this.mVisitorInvitation.addJavascriptInterface(new AndroidAndJS.AndroidAndJSInterface(),"jsObj");
        mVisitorInvitation.loadUrl(loadUrl);

        mWebviewTopRecord = (ImageView) findViewById(R.id.top_record);
        mWebviewTopRecord.setBackgroundResource(R.drawable.record);
        mWebviewTopRecord.setVisibility(View.VISIBLE);
        mWebviewTopRecord.setOnClickListener(this);
    }

    public String setUrl(String userName, String projectCode, String granterPhone) {
        userName = toUtf8(userName);
        String url = "https://yq.xhmind.com:8093/#/callerInvite/?projectCode=" + projectCode + "&userName=" + userName + "&granterPhone=" + granterPhone;
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

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.top_back) {//返回
            finish();

        } else if (i == R.id.top_record) {//记录
            Intent intent = new Intent(this, RecordActivity.class);
            startActivity(intent);

        }
    }

    //改写物理按键——返回的逻辑
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mVisitorInvitation.canGoBack()) {
                mVisitorInvitation.goBack();//返回上一页面
                return true;
            } else {
                finish();
            }
        }
        return true;
    }

}
