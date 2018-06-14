package com.xhwl.xhwlownerapp.activity.View.UserInfoView.DeviceInfo;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.Entity.DeviceEntivity.Device;
import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.activity.View.UserInfoView.DeviceInfo.Adapter.DeviceAdapter;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的设备
 */
public class MyDeviceActivity extends BaseActivity implements View.OnClickListener {

    private AutoLinearLayout mTopBack;
    private TextView mTopTitle;
    private ListView mDeviceList;
    private List<Device> list = new ArrayList<>();
    private Device device;
    private DeviceAdapter adapter;
    private String userType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_device);
        initView();
        initDate();
    }

    private void initDate() {
        device = new Device("窗帘");
        list.add(device);
        device = new Device("电子秤");
        list.add(device);
        device = new Device("空气净化器");
        list.add(device);
        device = new Device("摄像头");
        list.add(device);
        device = new Device("报警器");
        list.add(device);
        adapter = new DeviceAdapter(list,this);
        mDeviceList.setAdapter(adapter);
    }

    private void initView() {
        userType = SPUtils.get(this,"userType","");
        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopTitle.setText("我的设备");
        mDeviceList = (ListView) findViewById(R.id.device_list);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            default:
                break;
            case R.id.top_back:
                //返回
                finish();
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
