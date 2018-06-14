package com.xhwl.xhwlownerapp.activity.View.HomeView.DeviceActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.zhy.autolayout.AutoLinearLayout;

//添加网关
public class AddGatewayActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mTopBackImg;
    private AutoLinearLayout mTopBack;
    private TextView mTopTitle;
    private ImageView mDevicePilotLampExplain;
    /**
     * 无法正常操作网关重置
     */
    private TextView mDeviceResetGateway;
    /**
     * 下一步
     */
    private Button mDeviceAddGatewayNext;

    private Intent mIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_gateway);
        initView();
    }

    private void initView() {
        mTopBackImg = (ImageView) findViewById(R.id.top_back_img);
        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopTitle.setText("网关添加");
        mDevicePilotLampExplain = (ImageView) findViewById(R.id.device_pilot_lamp_explain);
        mDevicePilotLampExplain.setOnClickListener(this);
        mDeviceResetGateway = (TextView) findViewById(R.id.device_reset_gateway);
        mDeviceResetGateway.setOnClickListener(this);
        //添加下划线
        mDeviceResetGateway.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mDeviceAddGatewayNext = (Button) findViewById(R.id.device_add_gateway_next);
        mDeviceAddGatewayNext.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.top_back:
                finish();
                break;
            case R.id.device_pilot_lamp_explain:
                //指示灯说明
                mIntent = new Intent(this,PilotLampExplainActivity.class);
                startActivity(mIntent);
                break;
            case R.id.device_reset_gateway:
                //重置网关
                mIntent = new Intent(this,ResetGatewayActivity.class);
                startActivity(mIntent);
                break;
            case R.id.device_add_gateway_next:
                //下一步
                mIntent = new Intent(this,GatewayLoginActivity.class);
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
