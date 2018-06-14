package com.xhwl.xhwlownerapp.activity.View.UserInfoView.BasicsInfo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
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

public class SetSexActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private AutoLinearLayout mTopBack;
    private TextView mTopTitle;
    /**
     * 男
     */
    private RadioButton mModifySexMen;
    /**
     * 女
     */
    private RadioButton mModifySexWomen;
    /**
     * 确定
     */
    private Button mModifySexSubmit;
    private RadioGroup mModifySexRadio;
    private String modifySex;
    private boolean userInfoSex;
    //private User user;
    private String id, token;
    private RadioButton radbtn;
    private int sextype = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_sex);
        initView();
    }

    private void initView() {
        //user = (User) getIntent().getSerializableExtra("user");
        id = SPUtils.get(SetSexActivity.this, "id", "");
        token = SPUtils.get(SetSexActivity.this, "token", "");
        modifySex = getIntent().getStringExtra("mUserInfoSex");
        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopTitle.setText("设置性别");
        mModifySexMen = (RadioButton) findViewById(R.id.modify_sex_men);
        mModifySexWomen = (RadioButton) findViewById(R.id.modify_sex_women);
        mModifySexSubmit = (Button) findViewById(R.id.modify_sex_submit);
        mModifySexSubmit.setOnClickListener(this);
        mModifySexRadio = (RadioGroup) findViewById(R.id.modify_sex_radio);
        mModifySexRadio.setOnCheckedChangeListener(this);

        if("男".equals(modifySex)){
            mModifySexMen.setChecked(true);
        }else if ("女".equals(modifySex)){
            mModifySexWomen.setChecked(true);
        }

//        mModifySexRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
//            @Override
//            public void onCheckedChanged(RadioGroup rg, int checkedId) {
//                // TODO Auto-generated method stub
//                if(checkedId == mModifySexMen.getId()){
//                    mModifySexMen.setChecked(true);
//                    //mModifySexRadio.check(mModifySexMen.getId());
//                }else if(checkedId == mModifySexWomen.getId()){
//                    mModifySexWomen.setChecked(true);
//                }else{
//                    mModifySexMen.setChecked(true);
//                }
//            }
//        });
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
            case R.id.modify_sex_submit:
                //提交性别修改
                if (modifySex == null) {
                    showToast("请选择性别");
                } else {
                    modifySex(id, token, userInfoSex);
                }
                break;
            case R.id.modify_sex_men:
                break;
            case R.id.modify_sex_women:
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

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        radbtn = (RadioButton) findViewById(checkedId);
        modifySex = (String) radbtn.getText();
        if (modifySex.equals("男")) {
            userInfoSex = true;
        } else if (modifySex.equals("女")) {
            userInfoSex = false;
        }
        //Toast.makeText(getApplicationContext(), "你选了" + radbtn.getText(), Toast.LENGTH_LONG).show();
    }

    //修改性别
    private void modifySex(String id, String token, boolean userInfoSex) {
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.UPDATEUSERINFO,
                HttpConnectionTools.HttpData("id", id, "token", token, "sex", userInfoSex),
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
                            } else {
                                handler1.sendEmptyMessage(201);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler1.sendEmptyMessage(0x122);
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
                SPUtils.put(SetSexActivity.this, "sex", userInfoSex + "");
                finish();
            } else if (msg.what == 0x2) {
                ShowToast.show("账户不存在");
            } else if (msg.what == 0x3) {
                ShowToast.show("您已退出登陆");
                startToAIctivity(LoginActivity.class);
                MyAPP.Logout(SetSexActivity.this);
                finish();
            } else if (msg.what == 0x4) {
                ShowToast.show("token已经过期，请重新登录");
                startToAIctivity(LoginActivity.class);
                MyAPP.Logout(SetSexActivity.this);
                finish();
            } else if (msg.what == 0x122) {
                ShowToast.show("请检查网络");
            } else {
                ShowToast.show("修改失败");
            }
        }
    };

}
