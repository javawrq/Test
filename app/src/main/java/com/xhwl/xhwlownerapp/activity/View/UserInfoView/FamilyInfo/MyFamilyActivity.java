package com.xhwl.xhwlownerapp.activity.View.UserInfoView.FamilyInfo;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.Entity.FamilyEntivity.Family;
import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.activity.View.UserInfoView.FamilyInfo.Adapter.FamilyAdapter;
import com.zhy.autolayout.AutoLinearLayout;

import java.util.ArrayList;
import java.util.List;

public class MyFamilyActivity extends BaseActivity implements View.OnClickListener {

    private AutoLinearLayout mTopBack;
    private TextView mTopTitle;
    private TextView mTopBtn;
    private ListView mFamilyList;
    private List<Family> list = new ArrayList<>();
    private Family family;
    private FamilyAdapter adapter;
    private String userType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_family);
        initView();
        initDate();
    }

    private void initDate() {
        family = new Family("我的家庭1");
        list.add(family);
        family = new Family("我的家庭2");
        list.add(family);
        family = new Family("我的家庭3");
        list.add(family);
        family = new Family("我的家庭4");
        list.add(family);
        adapter = new FamilyAdapter(list,this);
        mFamilyList.setAdapter(adapter);
    }

    private void initView() {
        userType = SPUtils.get(this,"userType","");
        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopTitle.setText("我的家庭");
        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mTopBtn.setOnClickListener(this);
        mTopBtn.setText("添加");
        mTopBtn.setVisibility(View.VISIBLE);
        mFamilyList = (ListView) findViewById(R.id.family_list);
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
            case R.id.top_btn:
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
