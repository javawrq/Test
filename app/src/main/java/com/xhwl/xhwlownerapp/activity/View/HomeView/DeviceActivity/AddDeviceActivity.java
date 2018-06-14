package com.xhwl.xhwlownerapp.activity.View.HomeView.DeviceActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.zhy.autolayout.AutoLinearLayout;

//添加设备
public class AddDeviceActivity extends BaseActivity implements View.OnClickListener {


    private AutoLinearLayout mTopBack;
    private TextView mTopTitle;
    private AutoLinearLayout mDeviceAddGateway;
    private Intent mIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_device);
        initView();
    }

    private void initView() {
        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopTitle.setText("添加设备");

        mDeviceAddGateway = (AutoLinearLayout) findViewById(R.id.device_add_gateway);
        mDeviceAddGateway.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.top_back:
                //返回
                finish();
                break;

            case R.id.device_add_gateway:
                mIntent = new Intent(this,AddGatewayActivity.class);
                startActivity(mIntent);
                break;
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
