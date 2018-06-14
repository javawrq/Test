package com.xhwl.xhwlownerapp.activity.View.HomeView.FamilyActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.zhy.autolayout.AutoLinearLayout;

public class AllRoomActivity extends BaseActivity implements View.OnClickListener {

    private AutoLinearLayout mTopBack;
    private TextView mTopTitle;
    private GridView mAllRoomGridview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_room);
        initView();
    }

    //单击返回键返回，不退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
        }
        return true;
    }

    private void initView() {
        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopTitle.setText("全部房间");
        mAllRoomGridview = (GridView) findViewById(R.id.all_room_gridview);
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
        }
    }
}
