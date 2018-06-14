package com.xhwl.xhwlownerapp.activity.View.UserInfoView.BasicsInfo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.Service.MyService;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.CircleImageView;
import com.xhwl.xhwlownerapp.UIUtils.MyAPP;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.UIUtils.StringUtils;
import com.xhwl.xhwlownerapp.UIUtils.dialog.SelfDialog;
import com.xhwl.xhwlownerapp.activity.View.LoginView.LoginActivity;
import com.xhwl.xhwlownerapp.net.Constant;
import com.xhwl.xhwlownerapp.net.HttpConnectionInter;
import com.xhwl.xhwlownerapp.net.HttpConnectionTools;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class MyInfoActivity extends BaseActivity implements View.OnClickListener {

    private AutoLinearLayout mTopBack;
    private TextView mTopTitle;
    /**
     * 高龙群
     */
    private TextView mUserInfoUsername;
    private CircleImageView mUserInfoHeadimg;
    private AutoLinearLayout mUserInfoHeadimgLiner;
    /**
     * 高龙群
     */
    private TextView mUserInfoNikename;
    private AutoLinearLayout mUserInfoNikenameLiner;
    /**
     * 男
     */
    private TextView mUserInfoSex;
    private AutoLinearLayout mUserInfoSexLiner;
    private AutoLinearLayout mUserInfoQrcodeLiner;

    private Boolean userInfoSex = false;
    private TextView mTopBtn;
    private ImageView mTopRecord;
    /**
     * 退出登录
     */
    private Button mUserInfoLogout;

    private String userName, userImgUrl, userNickName, userSex, token,phone;
    private TextView mUserInfoPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        initView();
    }

    private void initView() {
        //user = (User) getIntent().getSerializableExtra("user");
        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopTitle.setText("基础信息");
        mUserInfoUsername = (TextView) findViewById(R.id.user_info_username);
        mUserInfoHeadimg = (CircleImageView) findViewById(R.id.user_info_headimg);
        mUserInfoHeadimgLiner = (AutoLinearLayout) findViewById(R.id.user_info_headimg_liner);
        mUserInfoHeadimgLiner.setOnClickListener(this);
        mUserInfoNikename = (TextView) findViewById(R.id.user_info_nikename);
        mUserInfoNikenameLiner = (AutoLinearLayout) findViewById(R.id.user_info_nikename_liner);
        mUserInfoNikenameLiner.setOnClickListener(this);
        mUserInfoSex = (TextView) findViewById(R.id.user_info_sex);
        mUserInfoSexLiner = (AutoLinearLayout) findViewById(R.id.user_info_sex_liner);
        mUserInfoSexLiner.setOnClickListener(this);
        mUserInfoQrcodeLiner = (AutoLinearLayout) findViewById(R.id.user_info_qrcode_liner);
        mUserInfoQrcodeLiner.setOnClickListener(this);
        mUserInfoLogout = (Button) findViewById(R.id.user_info_logout);
        mUserInfoLogout.setOnClickListener(this);
        mUserInfoPhone = (TextView) findViewById(R.id.user_info_phone);
        initDate();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initDate();
    }

    private void initDate() {
        userImgUrl = SPUtils.get(MyInfoActivity.this, "imageUrl", "");
        userName = SPUtils.get(MyInfoActivity.this, "sysUserName", "");
        userNickName = SPUtils.get(MyInfoActivity.this, "nickName", "");
        userSex = SPUtils.get(MyInfoActivity.this, "sex", "");
        token = SPUtils.get(MyInfoActivity.this, "token", "");
        phone = SPUtils.get(MyInfoActivity.this,"name","");
        //设置用户姓名
        //mUserInfoUsername.setText(userName);
        //设置头像
        Picasso.with(this)
                .load(userImgUrl)
                .error(R.drawable.headimg).into(mUserInfoHeadimg);

        //设置昵称
        Log.e("userNickName",userNickName.isEmpty()+"");
        if (!StringUtils.isEmpty(userNickName)) {
            mUserInfoNikename.setText(userNickName);
        } else {
            mUserInfoNikename.setText("未设置");
        }
        Log.e("userSex",userSex+"");
        //设置性别
        if (!StringUtils.isEmpty(userSex)) {
            userInfoSex = userSex == null ? null : Boolean.parseBoolean(userSex);
            if (userInfoSex == true) {
                mUserInfoSex.setText("男");
            } else if (userInfoSex == false) {
                mUserInfoSex.setText("女");
            } else {
                mUserInfoSex.setText("未设置");
            }
        } else {
            mUserInfoSex.setText("未设置");
        }

        //设置手机号
        if (phone != "null") {
            mUserInfoPhone.setText(phone);
        }

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            default:
                break;
            case R.id.top_back:
                //返回
                finish();
                break;
            case R.id.user_info_headimg_liner:
                //修改头像
                intent.setClass(MyInfoActivity.this, SetHeadImgActivity.class);
                startActivity(intent);
                break;
            case R.id.user_info_nikename_liner:
                //修改昵称
                intent.setClass(MyInfoActivity.this, SetNickNameActivity.class);
                intent.putExtra("mUserInfoNikename",mUserInfoNikename.getText().toString());
                startActivity(intent);
                break;
            case R.id.user_info_sex_liner:
                //修改性别
                intent.setClass(MyInfoActivity.this, SetSexActivity.class);
                intent.putExtra("mUserInfoSex",mUserInfoSex.getText().toString());
                startActivity(intent);
                break;
            case R.id.user_info_qrcode_liner:
                //查看二维码
                break;
            case R.id.user_info_logout:
                //退出登录
                final SelfDialog dialog;
                dialog = new SelfDialog(MyInfoActivity.this);
                dialog.setMessage("确定退出小七应用吗？");
                dialog.setTitle("退出后将无法接收到小七通知");
                dialog.setYesOnclickListener("确定", new SelfDialog.onYesOnclickListener() {
                    @Override
                    public void onYesClick() {
                        ///已经绑定了QQ，点击则执行解绑操作
                        MyAPP.Logout(MyInfoActivity.this);
                        LogOut(token);
                    }
                });
                dialog.setNoOnclickListener("取消", new SelfDialog.onNoOnclickListener() {
                    @Override
                    public void onNoClick() {
                        dialog.dismiss();
                    }
                });
                dialog.show();

                break;
        }
    }

    //退出登录按钮
    private void LogOut(String token) {
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.LOGINOUT,
                HttpConnectionTools.HttpData("token", token), new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if (errorCode == 200) {
                                handler.sendEmptyMessage(200);
                            } else if (errorCode == 403) {
                                handler.sendEmptyMessage(403);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(403);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        handler.sendEmptyMessage(403);
                    }
                });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 200) {
                showToast("登出成功");
                startToAIctivity(LoginActivity.class);
                MyAPP.Logout(MyInfoActivity.this);
                Intent stopIntent = new Intent(MyInfoActivity.this, MyService.class);
                stopService(stopIntent);
                finish();
            } else if (msg.what == 403) {
                showToast("网络异常，登出失败");
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
