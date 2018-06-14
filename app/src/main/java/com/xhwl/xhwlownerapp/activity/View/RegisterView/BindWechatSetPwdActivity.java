package com.xhwl.xhwlownerapp.activity.View.RegisterView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
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
 * 第三方注册设置密码
 */
public class BindWechatSetPwdActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mWechatSetPasswordBack;
    /**
     * 密码:
     */
    private ClearEditText mWechatSetPasswordEdit;
    private ImageView mWechatSetPassNoSee;
    /**
     * 确定
     */
    private Button mWechatSetPasswordBtn;
    /**
     * 是否查看密码
     */
    private boolean isSee = false;

    private String telephone, openId, nickName, imageUrl;
    private int type;
    /**
     * 请确认密码:
     */
    private ClearEditText mWechatSetPasswordEdit1;
    private ImageView mWechatSetPassNoSee1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_wechat_set_pwd);
        initView();
        initDate();
    }

    private void initDate() {
        telephone = getIntent().getStringExtra("telephone");
        openId = getIntent().getStringExtra("openId");
        nickName = getIntent().getStringExtra("nickName");
        imageUrl = getIntent().getStringExtra("imageUrl");
        type = getIntent().getIntExtra("type", 0);

        Log.e("telephone", telephone);
        Log.e("openId", openId);
        Log.e("imageUrl", nickName);
        Log.e("nickName", imageUrl);
        Log.e("type", type + "");
    }

    private void initView() {
        mWechatSetPasswordBack = (ImageView) findViewById(R.id.wechat_set_password_back);
        mWechatSetPasswordBack.setOnClickListener(this);
        mWechatSetPasswordEdit = (ClearEditText) findViewById(R.id.wechat_set_password_edit);
        mWechatSetPassNoSee = (ImageView) findViewById(R.id.wechat_set_pass_no_see);
        mWechatSetPassNoSee.setOnClickListener(this);
        mWechatSetPasswordBtn = (Button) findViewById(R.id.wechat_set_password_btn);
        mWechatSetPasswordBtn.setOnClickListener(this);
        mWechatSetPasswordEdit1 = (ClearEditText) findViewById(R.id.wechat_set_password_edit_1);
        mWechatSetPassNoSee1 = (ImageView) findViewById(R.id.wechat_set_pass_no_see_1);
        mWechatSetPassNoSee1.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.wechat_set_password_back:
                //关闭
                finish();
                break;
            case R.id.wechat_set_pass_no_see:
                //查看密码
                if (!isSee) {
                    //设置可查看
                    mWechatSetPassNoSee.setBackgroundResource(R.drawable.pass_see);
                    mWechatSetPasswordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //设置不能查看
                    mWechatSetPassNoSee.setBackgroundResource(R.drawable.pass_no_see);
                    mWechatSetPasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                isSee = !isSee;
                break;
            case R.id.wechat_set_password_btn:
                //确定密码
                if (TextUtils.isEmpty(mWechatSetPasswordEdit.getText().toString().trim())) {
                    showToast("密码不能为空");
                }else if (TextUtils.isEmpty(mWechatSetPasswordEdit1.getText().toString().trim())) {
                    showToast("确认密码不能为空");
                }else if (!mWechatSetPasswordEdit1.getText().toString().trim().equals(mWechatSetPasswordEdit.getText().toString().trim())) {
                    showToast("两次密码不一致，请重新输入");
                }else {
                    Log.e("Bind", telephone + " " + openId + " " + imageUrl);
                    bindWechatSetPwd(telephone, mWechatSetPasswordEdit1.getText().toString().trim(), openId,
                            imageUrl, nickName, type);
                }
                break;
            case R.id.wechat_set_pass_no_see_1:
                if (!isSee) {
                    //设置可查看
                    mWechatSetPassNoSee1.setBackgroundResource(R.drawable.pass_see);
                    mWechatSetPasswordEdit1.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //设置不能查看
                    mWechatSetPassNoSee1.setBackgroundResource(R.drawable.pass_no_see);
                    mWechatSetPasswordEdit1.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                break;
        }
    }

    //微信登录设置密码
    private void bindWechatSetPwd(String telephone, String password, String openId, String imageUrl, String nickName, int type) {
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.THREEPARTYREGISTER,
                HttpConnectionTools.HttpData("telephone", telephone, "password", password, "openId", openId,
                        "imageUrl", imageUrl, "nickName", nickName, "type", type), new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if (errorCode == 200) {
                                //注册成功
                                handler.sendEmptyMessage(200);
                            } else if (errorCode == 111) {
                                //数据有误
                                handler.sendEmptyMessage(111);
                            } else if (errorCode == 201) {
                                //系统异常
                                handler.sendEmptyMessage(201);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //网络异常
                            handler.sendEmptyMessage(500);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        handler.sendEmptyMessage(500);
                    }
                });
    }

    //注册成功直接使用密码登录
    private void bindWechatLogin(String telephone, String password) {
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.LOGIN,
                HttpConnectionTools.HttpData("telephone", telephone,
                        "password", password), new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(content);
                            int code = jsonObject.getInt("errorCode");
                            if (code == 200) {
                                String result = jsonObject.getString("result");//获得结果集
                                SPUtils.put(BindWechatSetPwdActivity.this, "result", result);
                                jsonObject = new JSONObject(result);//解析结果集
                                String sysAccount = jsonObject.getString("sysAccount");//获得用户基本信息
                                String roomList = jsonObject.getString("roomList");//获得用户基本信息
                                SPUtils.put(BindWechatSetPwdActivity.this, "sysAccount", jsonObject.getString("sysAccount"));
                                SPUtils.put(BindWechatSetPwdActivity.this, "roomList", jsonObject.getString("roomList"));
                                //业主登录成功
                                jsonObject = new JSONObject(sysAccount);//解析用户基本信息
                                SPUtils.put(BindWechatSetPwdActivity.this, "userName", jsonObject.getString("sysUserName"));//业主用户名
                                SPUtils.put(BindWechatSetPwdActivity.this, "userTelephone", jsonObject.getString("name"));//业主手机号
                                SPUtils.put(BindWechatSetPwdActivity.this, "userSex", jsonObject.getString("sex"));//业主性别
                                SPUtils.put(BindWechatSetPwdActivity.this, "userSysAccount", jsonObject.getString("id"));//业主accountID
                                SPUtils.put(BindWechatSetPwdActivity.this, "userToken", jsonObject.getString("token"));//用户token
                                SPUtils.put(BindWechatSetPwdActivity.this, "userWechatNickName", jsonObject.getString("weChatNickName"));//微信昵称
                                SPUtils.put(BindWechatSetPwdActivity.this, "userQQNickName", jsonObject.getString("qqnickName"));//QQ昵称
                                SPUtils.put(BindWechatSetPwdActivity.this, "userNickName", jsonObject.getString("nickName"));//用户昵称
                                SPUtils.put(BindWechatSetPwdActivity.this, "userImageUrl", jsonObject.getString("imageUrl"));//头像地址
                                SPUtils.put(BindWechatSetPwdActivity.this, "userWeiboNickName", jsonObject.getString("weiboNickName"));//微博昵称
                                SPUtils.put(BindWechatSetPwdActivity.this, "userType", jsonObject.getString("userType"));
                                handler1.sendEmptyMessage(0x200);
                            } else if (code == 113) {
                                //用户密码密码错误
                                handler1.sendEmptyMessage(113);
                            } else if (code == 114) {
                                //用户名不存在
                                handler1.sendEmptyMessage(114);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler1.sendEmptyMessage(400);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        handler1.sendEmptyMessage(400);
                    }
                });
    }

    Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x200) {
                showToast("登录成功");
                Intent in = new Intent(BindWechatSetPwdActivity.this, HomeActivity.class);
                startActivity(in);
            } else if (msg.what == 114) {
                showToast("用户不存在,请先注册");
            } else if (msg.what == 113) {
                showToast("密码错误");
            } else if (msg.what == 404) {
                showToast("用户没有访问权限,请联系物业管理人员");
            } else if (msg.what == 400) {
                showToast("请检查网络");
            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 200) {
                showToast("注册成功");
                bindWechatLogin(telephone, mWechatSetPasswordEdit.getText().toString().trim());
            } else if (msg.what == 201) {
                showToast("注册失败");
            } else if (msg.what == 111) {
                showToast("数据有误");
            } else if (msg.what == 500) {
                showToast("请检查网络");
            }else if (msg.what == 112) {
                if(type == 1){
                    showToast("该微信号已经被绑定");
                }else if(type == 2){
                    showToast("该QQ号已经被绑定");
                }else if(type == 3){
                    showToast("该微博号已经被绑定");
                }
            }else if (msg.what == -1) {
                if(type == 1){
                    showToast("该账号已经绑定微信，请先解绑");
                }else if(type == 2){
                    showToast("该账号已经绑定QQ，请先解绑");
                }else if(type == 3){
                    showToast("该账号已经绑定微博，请先解绑");
                }
            }
        }
    };
}
