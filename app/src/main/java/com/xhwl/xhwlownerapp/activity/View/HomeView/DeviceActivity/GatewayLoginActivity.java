package com.xhwl.xhwlownerapp.activity.View.HomeView.DeviceActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.ClearEditText;
import com.xhwl.xhwlownerapp.UIUtils.StringUtils;
import com.xhwl.xhwlownerapp.UIUtils.ToastUtil;
import com.xhwl.xhwlownerapp.zxing.bean.ZxingConfig;
import com.xhwl.xhwlownerapp.zxing.common.Constant;
import com.zhy.autolayout.AutoLinearLayout;

public class GatewayLoginActivity extends BaseActivity implements View.OnClickListener {


    private AutoLinearLayout mTopBack;
    /**
     * 请输入设备型号
     */
    private ClearEditText mGatewayUsername;
    /**
     * 请输入密码
     */
    private ClearEditText mGatewayPassword;
    /**
     * 确定
     */
    private Button mGatewayLogin;
    /**
     * 您还可以通过扫码登录
     */
    private TextView mGatewayScanQrcodeLogin;
    private TextView mTopTitle;
    private final int REQUEST_CODE_SCAN = 1;
    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_login);
        initView();
    }

    private void initView() {
        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mGatewayUsername = (ClearEditText) findViewById(R.id.gateway_username);
        mGatewayPassword = (ClearEditText) findViewById(R.id.gateway_password);
        mGatewayLogin = (Button) findViewById(R.id.gateway_login);
        mGatewayLogin.setOnClickListener(this);
        mGatewayScanQrcodeLogin = (TextView) findViewById(R.id.gateway_scan_qrcode_login);
        mGatewayScanQrcodeLogin.setOnClickListener(this);
        //添加下划线
        mGatewayScanQrcodeLogin.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopTitle.setText("登录方式选择");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.top_back:
                finish();
                break;
            case R.id.gateway_login:
                if (StringUtils.isEmpty(mGatewayUsername.getText().toString().trim())){
                    ToastUtil.showSingleToast("用户名有误");
                }
                break;
            case R.id.gateway_scan_qrcode_login:
                mIntent = new Intent(this, GatewayQRCodeLoginActivity.class);
                /*ZxingConfig是配置类
                *可以设置是否显示底部布局，闪光灯，相册，
                * 是否播放提示音  震动
                * 设置扫描框颜色等
                * 也可以不传这个参数
                * */
                ZxingConfig config = new ZxingConfig();
                config.setPlayBeep(true);//是否播放扫描声音 默认为true
                config.setShake(true);//是否震动  默认为true
                config.setDecodeBarCode(false);//是否扫描条形码 默认为true
                config.setFullScreenScan(false);//是否全屏扫描  默认为true  设为false则只会在扫描框中扫描
                mIntent.putExtra(Constant.INTENT_ZXING_CONFIG, config);
                startActivityForResult(mIntent, REQUEST_CODE_SCAN);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {

                String content = data.getStringExtra(Constant.CODED_CONTENT);
                Log.e("TAG","扫描结果为：" + content);
            }
        }
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
