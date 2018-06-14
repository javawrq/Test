package com.xhwl.xhwlownerapp.activity.View.HomeView;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xhwl.xhwlownerapp.R;

public class VersionActivity extends Activity {

    private WebView mVersion;
    private String link;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);
        initView();
        initDate();
    }

    private void initView() {
        link = getIntent().getStringExtra("link");
        mVersion = (WebView) findViewById(R.id.version);
        //启用支持javascript
        WebSettings settings = mVersion.getSettings();
        settings.setJavaScriptEnabled(true);
    }

    private void initDate() {
        //WebView加载web资源
        mVersion.loadUrl(link);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        mVersion.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //以"http","https"开头的url在本页用webview进行加载，其他链接进行跳转
                if(url.startsWith("http:") || url.startsWith("https:") ) {
                    //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                    view.loadUrl(url);
                    return false;
                }else{
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
            }
        });
    }
}
