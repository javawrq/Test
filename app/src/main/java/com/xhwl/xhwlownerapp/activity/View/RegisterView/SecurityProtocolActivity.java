package com.xhwl.xhwlownerapp.activity.View.RegisterView;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;

public class SecurityProtocolActivity extends BaseActivity {

    private WebView mUserProtocol;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_protocol);
        initView();
        initDate();
    }

    private void initView() {
        mUserProtocol = (WebView) findViewById(R.id.user_protocol);
        //启用支持javascript
        WebSettings settings = mUserProtocol.getSettings();
        settings.setJavaScriptEnabled(true);
    }

    private void initDate() {
        //WebView加载web资源
        mUserProtocol.loadUrl("http://202.105.104.105:8006/ssh/appDocument/yzPrivacy.jsp");
        //mUserProtocol.loadUrl("http://202.105.96.131:3002/xa/#/");
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        mUserProtocol.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
    }

    //单击返回键返回，不退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
        }
        return true;
    }
}
