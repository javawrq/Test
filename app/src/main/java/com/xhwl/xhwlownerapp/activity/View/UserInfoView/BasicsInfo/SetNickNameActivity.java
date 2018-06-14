package com.xhwl.xhwlownerapp.activity.View.UserInfoView.BasicsInfo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.ClearEditText;
import com.xhwl.xhwlownerapp.UIUtils.MyAPP;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.UIUtils.ShowToast;
import com.xhwl.xhwlownerapp.activity.View.LoginView.LoginActivity;
import com.xhwl.xhwlownerapp.net.Constant;
import com.xhwl.xhwlownerapp.net.HttpConnectionInter;
import com.xhwl.xhwlownerapp.net.HttpConnectionTools;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class SetNickNameActivity extends BaseActivity implements View.OnClickListener {

    private AutoLinearLayout mTopBack;
    private TextView mTopTitle;
    private ClearEditText mModifyNikename;
    /**
     * 确定
     */
    private Button mModifyNikenameSubmit;
    //private User user;
    private String id,token,nikename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_nick_name);
        initView();
    }

    private void initView() {
        //user = (User) getIntent().getSerializableExtra("user");
        nikename = getIntent().getStringExtra("mUserInfoNikename");
        id = SPUtils.get(SetNickNameActivity.this,"id","");
        token = SPUtils.get(SetNickNameActivity.this,"token","");
        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopTitle.setText("设置昵称");
        mModifyNikename = (ClearEditText) findViewById(R.id.modify_nikename);
        mModifyNikenameSubmit = (Button) findViewById(R.id.modify_nikename_submit);
        mModifyNikenameSubmit.setOnClickListener(this);

        mModifyNikename.setText(nikename);
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
            case R.id.modify_nikename_submit:
                if(!mModifyNikename.getText().toString().trim().isEmpty()) modifyNikeName(id,token,mModifyNikename.getText().toString().trim());
                else showToast("请输入昵称");
                break;
        }
    }

    //修改昵称
    private void modifyNikeName(String id,String token,String nickName){
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.UPDATEUSERINFO,
                HttpConnectionTools.HttpData("id", id, "token", token, "nickName", nickName),
                new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        //网络请求成功
                        try {
                            JSONObject obj = new JSONObject(content);
                            int errorCode = obj.getInt("errorCode");
                            if (errorCode == 200) {
                                handler1.sendEmptyMessage(0x1);
                            } else if (errorCode == 116) {
                                handler1.sendEmptyMessage(0x2);
                            } else if (errorCode == 400) {
                                handler1.sendEmptyMessage(0x3);
                            } else if (errorCode == 401) {
                                handler1.sendEmptyMessage(0x4);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler1.sendEmptyMessage(201);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        handler1.sendEmptyMessage(0x122);
                    }
                });

    }

    Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x1) {
                ShowToast.show("修改成功");
                SPUtils.put(SetNickNameActivity.this,"nickName",mModifyNikename.getText().toString().trim());
                mModifyNikename.setText(nikename);
                finish();
            } else if (msg.what == 0x2) {
                ShowToast.show("账户不存在");
            } else if (msg.what == 0x3) {
                ShowToast.show("您已退出登陆");
                startToAIctivity(LoginActivity.class);
                MyAPP.Logout(SetNickNameActivity.this);
                finish();
            } else if (msg.what == 0x4) {
                ShowToast.show("token已经过期，请重新登录");
                startToAIctivity(LoginActivity.class);
                MyAPP.Logout(SetNickNameActivity.this);
                finish();
            } else if (msg.what == 0x122) {
                ShowToast.show("请检查网络");
            }else {
                ShowToast.show("修改失败");
            }
        }
    };

    //单击返回键返回，不退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
        }
        return true;
    }
}
