package com.xhwl.xhwlownerapp.activity.View.Shop;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;

//小七商城
public class XQShopActivity extends BaseActivity {

    private WebView mWebView1;
    private WebSettings mWebSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xqshop);
        initView();
    }

    private void initView() {
        mWebView1 = (WebView) findViewById(R.id.webView1);

        //启用支持javascript
        mWebSettings = mWebView1.getSettings();

        //支持js
        mWebSettings.setJavaScriptEnabled(true);

        //将图片调整到适合webview的大小
        mWebSettings.setUseWideViewPort(true);

        //支持通过JS打开新窗口
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(true);

        //开启DOM storage API功能（HTML5 提供的一种标准的接口，主要将键值对存储在本地，在页面加载完毕后可以通过 JavaScript 来操作这些数据。）
        mWebSettings.setDomStorageEnabled(true);

        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        mWebView1.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        mWebView1.loadUrl("http://56028283.m.weimob.com/vshop/56028283/index");
    }

    //改写物理按键——返回的逻辑
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode==KeyEvent.KEYCODE_BACK)
        {
            if(mWebView1.canGoBack())
            {
                mWebView1.goBack();//返回上一页面
                return true;
            }
            else
            {
                finish();
            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        //webView销毁时，清除缓存
        super.onDestroy();
        mWebView1.stopLoading();
        mWebView1.removeAllViews();
        mWebView1.destroy();
        mWebView1 = null;
    }
}
