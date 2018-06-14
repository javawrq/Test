package com.xhwl.xhwlownerapp.activity.View.RegisterView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.ClearEditText;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.activity.View.HomeView.HomeActivity;
import com.xhwl.xhwlownerapp.net.Constant;
import com.xhwl.xhwlownerapp.net.HttpConnectionInter;
import com.xhwl.xhwlownerapp.net.HttpConnectionTools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 首次验证码登录
 * 设置密码
 */
public class SetPasswordActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "SetPasswordActivity";

    private ImageView mSetPasswordBack;
    /**
     * 密码:
     */
    private ClearEditText mSetPasswordEdit;
    private ImageView mSetPassNoSee;
    /**
     * 确定
     */
    private Button mSetPasswordBtn;

    /**
     * 是否查看密码
     */
    private boolean isSee = false;

    /**
     * 用户手机号码
     */
    private String telephone, code;
    /**
     * 请确认密码:
     */
    private ClearEditText mSetPasswordEdit1;
    private ImageView mSetPassNoSee1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        initView();
    }

    private void initView() {
        telephone = getIntent().getStringExtra("telephone");
        code = getIntent().getStringExtra("code");
        mSetPasswordBack = (ImageView) findViewById(R.id.set_password_back);
        mSetPasswordBack.setOnClickListener(this);
        mSetPasswordEdit = (ClearEditText) findViewById(R.id.set_password_edit);
        mSetPassNoSee = (ImageView) findViewById(R.id.set_pass_no_see);
        mSetPassNoSee.setOnClickListener(this);
        mSetPasswordBtn = (Button) findViewById(R.id.set_password_btn);
        mSetPasswordBtn.setOnClickListener(this);
        mSetPasswordEdit1 = (ClearEditText) findViewById(R.id.set_password_edit_1);
        mSetPassNoSee1 = (ImageView) findViewById(R.id.set_pass_no_see_1);
        mSetPassNoSee1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.set_password_back:
                //返回
                finish();
                break;
            case R.id.set_pass_no_see:
                //设置查看密码
                if (!isSee) {
                    //设置可查看
                    mSetPassNoSee.setBackgroundResource(R.drawable.pass_see);
                    mSetPasswordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //设置不能查看
                    mSetPassNoSee.setBackgroundResource(R.drawable.pass_no_see);
                    mSetPasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                isSee = !isSee;
                break;
            case R.id.set_password_btn:
                if (TextUtils.isEmpty(mSetPasswordEdit.getText().toString().trim())) {
                    showToast("密码不能为空");
                }else if(TextUtils.isEmpty(mSetPasswordEdit1.getText().toString().trim())){
                    showToast("确认密码不能为空");
                } else if(!mSetPasswordEdit1.getText().toString().trim().equals(mSetPasswordEdit.getText().toString().trim())){
                    showToast("两次输入密码不一致");
                }else {
                    //确定密码并注册
                    userReg(telephone, mSetPasswordEdit.getText().toString().trim(), code);
                }

                break;
            case R.id.set_pass_no_see_1:
                if (!isSee) {
                    //设置可查看
                    mSetPassNoSee1.setBackgroundResource(R.drawable.pass_see);
                    mSetPasswordEdit1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //设置不能查看
                    mSetPassNoSee1.setBackgroundResource(R.drawable.pass_no_see);
                    mSetPasswordEdit1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                isSee = !isSee;
                break;
        }
    }

    private void userReg(String telephone, String password, String verificatCode) {
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.REGISTER,
                HttpConnectionTools.HttpData("telephone", telephone, "password", password, "verificatCode", verificatCode),
                new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if (errorCode == 200) {
                                //注册成功
                                handler.sendEmptyMessage(0x200);
                            } else if (errorCode == 110) {
                                //验证码过期
                                handler.sendEmptyMessage(0x110);
                            } else if (errorCode == -2) {
                                //验证码有误
                                handler.sendEmptyMessage(0x2);
                            } else if (errorCode == -5) {
                                //您已注册过，请直接登录
                                handler.sendEmptyMessage(0x5);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(500);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        handler.sendEmptyMessage(500);
                    }
                });
    }

    /**
     * 密码登录
     */
    private void pwdLogin() {
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.LOGIN,
                HttpConnectionTools.HttpData("telephone", telephone,
                        "password", mSetPasswordEdit.getText().toString().trim()), new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(content);
                            int code = jsonObject.getInt("errorCode");
                            if (code == 200) {
                                String result = jsonObject.getString("result");//获得结果集
                                SPUtils.put(SetPasswordActivity.this, "result", result);
                                jsonObject = new JSONObject(result);//解析结果集
                                String sysAccount = jsonObject.getString("sysAccount");//获得用户基本信息
                                String roomList = jsonObject.getString("roomList");//获得用户基本信息
                                SPUtils.put(SetPasswordActivity.this, "sysAccount", jsonObject.getString("sysAccount"));
                                SPUtils.put(SetPasswordActivity.this, "roomList", jsonObject.getString("roomList"));
                                //业主登录成功
                                jsonObject = new JSONObject(sysAccount);//解析用户基本信息
                                SPUtils.put(SetPasswordActivity.this, "userName", jsonObject.getString("sysUserName"));//业主用户名
                                SPUtils.put(SetPasswordActivity.this, "userTelephone", jsonObject.getString("name"));//业主手机号
                                SPUtils.put(SetPasswordActivity.this, "userSex", jsonObject.getString("sex"));//业主性别
                                SPUtils.put(SetPasswordActivity.this, "userSysAccount", jsonObject.getString("id"));//业主accountID
                                SPUtils.put(SetPasswordActivity.this, "userToken", jsonObject.getString("token"));//用户token
                                SPUtils.put(SetPasswordActivity.this, "userWechatNickName", jsonObject.getString("weChatNickName"));//微信昵称
                                SPUtils.put(SetPasswordActivity.this, "userQQNickName", jsonObject.getString("qqnickName"));//QQ昵称
                                SPUtils.put(SetPasswordActivity.this, "userNickName", jsonObject.getString("nickName"));//用户昵称
                                SPUtils.put(SetPasswordActivity.this, "userImageUrl", jsonObject.getString("imageUrl"));//头像地址
                                SPUtils.put(SetPasswordActivity.this, "userWeiboNickName", jsonObject.getString("weiboNickName"));//微博昵称
                                SPUtils.put(SetPasswordActivity.this, "userType", jsonObject.getString("userType"));
                                handler.sendEmptyMessage(200);
                            } else if (code == 113) {
                                //用户密码密码错误
                                handler.sendEmptyMessage(113);
                            } else if (code == 114) {
                                //用户名不存在
                                handler.sendEmptyMessage(114);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        handler.sendEmptyMessage(400);
                    }
                });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 200) {
                showToast("登录成功");
                startToAIctivity(HomeActivity.class);
            } else if (msg.what == 114) {
                showToast("用户不存在,请先注册");
            } else if (msg.what == 113) {
                showToast("密码错误");
            } else if (msg.what == 404) {
                showToast("用户没有访问权限,请联系物业管理人员");
            } else if (msg.what == 400) {
                showToast("请检查网络");
            }

            if (msg.what == 0x200) {
                Log.e(TAG, "注册成功");
                //注册成功直接登录
                pwdLogin();
            } else if (msg.what == 0x110) {
                showToast("验证码已过期");
            } else if (msg.what == 0x2) {
                showToast("验证码有误");
            } else if (msg.what == 0x5) {
                showToast("您已注册过，请直接登录");
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
