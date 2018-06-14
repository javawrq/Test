package com.xhwl.xhwlownerapp.activity.View.RegisterView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.Entity.UserEntity.ThreeParty;
import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.ClearEditText;
import com.xhwl.xhwlownerapp.UIUtils.PhoneJudeg;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.activity.View.HomeView.HomeActivity;
import com.xhwl.xhwlownerapp.net.Constant;
import com.xhwl.xhwlownerapp.net.HttpConnectionInter;
import com.xhwl.xhwlownerapp.net.HttpConnectionTools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 首次第三方登录
 * 绑定手机号
 */
public class WechatRegBindPhoneActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mBindSetPasswordBack;
    /**
     * 请输入手机号
     */
    private ClearEditText mBindWechatUserPhone;
    /**
     * 发送验证码
     */
    private TextView mBindWechatSendYzm;
    /**
     * 请输入验证码
     */
    private ClearEditText mBindWechatYzm;
    /**
     * 下一步
     */
    private Button mBindWechatSubmit;

    private int i = 60;//倒计时

    //第三方信息实体
    private ThreeParty threeParty;
    //第三方类别 1微信 2QQ 3微博
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wechat_reg_bind_phone);
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
        threeParty = (ThreeParty) getIntent().getSerializableExtra("threeParty");
        type = getIntent().getIntExtra("type", 0);
        mBindSetPasswordBack = (ImageView) findViewById(R.id.bind_set_password_back);
        mBindSetPasswordBack.setOnClickListener(this);
        mBindWechatUserPhone = (ClearEditText) findViewById(R.id.bind_wechat_user_phone);
        mBindWechatSendYzm = (TextView) findViewById(R.id.bind_wechat_send_yzm);
        mBindWechatSendYzm.setOnClickListener(this);
        mBindWechatYzm = (ClearEditText) findViewById(R.id.bind_wechat_yzm);
        mBindWechatSubmit = (Button) findViewById(R.id.bind_wechat_submit);
        mBindWechatSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.bind_set_password_back:
                //关闭
                finish();
                break;
            case R.id.bind_wechat_send_yzm:
                //点击发送验证码
                if (type == 3) {
                    //微博
                    sendYzm(mBindWechatUserPhone.getText().toString().trim(), threeParty.getUid());
                } else if (type == 1 || type == 2) {
                    //QQ 微信
                    sendYzm(mBindWechatUserPhone.getText().toString().trim(), threeParty.getOpenid());
                    Log.e("threeParty.getOpenid()", threeParty.getOpenid() + "==" + mBindWechatUserPhone.getText().toString().trim());
                }

                break;
            case R.id.bind_wechat_submit:
                /**
                 * 提交，验证验证码进入下一步，
                 * 判断该手机号是否已经被绑定，已绑定，提示已经绑定，
                 * 未绑定判断该账号是否设置密码，
                 * 是则直接登录，否则设置密码
                 */
                if (type == 3) {
                    //微博
                    submit(mBindWechatUserPhone.getText().toString().trim(),
                            mBindWechatYzm.getText().toString().trim(), threeParty.getUid(),
                            threeParty.getIconurl(), threeParty.getName(), type);
                } else if (type == 1 || type == 2) {
                    //QQ 微信
                    submit(mBindWechatUserPhone.getText().toString().trim(),
                            mBindWechatYzm.getText().toString().trim(), threeParty.getOpenid(),
                            threeParty.getIconurl(), threeParty.getName(), type);
                }
                break;
        }
    }

    //发送验证码
    private void sendYzm(String telephone, String openid) {
        final PhoneJudeg[] phoneJudeg = {new PhoneJudeg(WechatRegBindPhoneActivity.this)};
        //判断手机号是否为正确格式
        if (!phoneJudeg[0].judgePhoneNums(mBindWechatUserPhone.getText().toString())) {
            return;
        }
        //网络请求返回的结果集
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.THREEPARTYGETVERIFYCODE, HttpConnectionTools.HttpData(
                "telephone", telephone, "openId", openid), new HttpConnectionInter() {
            @Override
            public void onFinish(String content) {
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    int errorCode = jsonObject.getInt("errorCode");
                    if (errorCode == -4) {
                        //获取验证码失败
                        handler.sendEmptyMessage(0x123);
                    } else if (errorCode == 200) {
                        //获取验证码成功
                        handler.sendEmptyMessage(0x124);
                        //倒计时
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (; i > 0; i--) {
                                    handler.sendEmptyMessage(-9);
                                    if (i <= 0) {
                                        break;
                                    }
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                handler.sendEmptyMessage(-8);
                            }
                        }).start();
                    } else if (errorCode == 112) {
                        //该号码已经被注册
                        handler.sendEmptyMessage(0x125);
                    } else if (errorCode == 111) {
                        //数据有误
                        handler.sendEmptyMessage(0x126);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                handler.sendEmptyMessage(0x404);
            }
        });
    }

    //确定提交
    private void submit(String telephone, String verifyCode, String openId, String imageUrl, String nickName, int type) {
        if (TextUtils.isEmpty(mBindWechatUserPhone.getText().toString())) {
            showToast("手机号不能为空");
            return;
        } else {
            SPUtils.put(WechatRegBindPhoneActivity.this, "bingPhone", mBindWechatUserPhone.getText().toString());
        }
        if (TextUtils.isEmpty(mBindWechatYzm.getText().toString())) {
            showToast("请输入验证码");
            return;
        }
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.THREEPARTYTESTCODE,
                HttpConnectionTools.HttpData(
                        "telephone", telephone, "verifyCode", verifyCode,
                        "openId", openId, "imageUrl", imageUrl,
                        "nickName", nickName, "isNewVersion", "1", "type", type), new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if (errorCode == 200) {
                                String result = jsonObject.getString("result");//获得结果集
                                SPUtils.put(WechatRegBindPhoneActivity.this, "result", result);
                                jsonObject = new JSONObject(result);//解析结果集
                                String sysAccount = jsonObject.getString("sysAccount");//获得用户基本信息
                                String roomList = jsonObject.getString("roomList");//获得用户基本信息
                                SPUtils.put(WechatRegBindPhoneActivity.this, "sysAccount", jsonObject.getString("sysAccount"));
                                SPUtils.put(WechatRegBindPhoneActivity.this, "roomList", jsonObject.getString("roomList"));
                                //业主登录成功
                                jsonObject = new JSONObject(sysAccount);//解析用户基本信息
                                SPUtils.put(WechatRegBindPhoneActivity.this, "userName", jsonObject.getString("sysUserName"));//业主用户名
                                SPUtils.put(WechatRegBindPhoneActivity.this, "userTelephone", jsonObject.getString("name"));//业主手机号
                                SPUtils.put(WechatRegBindPhoneActivity.this, "userSex", jsonObject.getString("sex"));//业主性别
                                SPUtils.put(WechatRegBindPhoneActivity.this, "userSysAccount", jsonObject.getString("id"));//业主accountID
                                SPUtils.put(WechatRegBindPhoneActivity.this, "userToken", jsonObject.getString("token"));//用户token
                                SPUtils.put(WechatRegBindPhoneActivity.this, "userWechatNickName", jsonObject.getString("weChatNickName"));//微信昵称
                                SPUtils.put(WechatRegBindPhoneActivity.this, "userQQNickName", jsonObject.getString("qqnickName"));//QQ昵称
                                SPUtils.put(WechatRegBindPhoneActivity.this, "userNickName", jsonObject.getString("nickName"));//用户昵称
                                SPUtils.put(WechatRegBindPhoneActivity.this, "userImageUrl", jsonObject.getString("imageUrl"));//头像地址
                                SPUtils.put(WechatRegBindPhoneActivity.this, "userWeiboNickName", jsonObject.getString("weiboNickName"));//微博昵称
                                SPUtils.put(WechatRegBindPhoneActivity.this, "userType", jsonObject.getString("userType"));
                                handler.sendEmptyMessage(0x200);
                            } else if (errorCode == 110) {
                                //验证码过期
                                handler.sendEmptyMessage(0x222);
                            } else if (errorCode == 111) {
                                //验证码无效
                                handler.sendEmptyMessage(0x223);
                            } else if (errorCode == 100) {
                                //设置密码
                                handler.sendEmptyMessage(0x224);
                            } else if (errorCode == 201) {
                                //数据有误
                                handler.sendEmptyMessage(0x225);
                            }else if (errorCode == 112) {
                                //该第三方已将被其他账号绑定
                                handler.sendEmptyMessage(112);
                            }else if (errorCode == -1) {
                                //该账号已经绑定第三方
                                handler.sendEmptyMessage(-1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        handler.sendEmptyMessage(0x404);
                    }
                });

    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                //获取验证码失败
                showToast("获取验证码失败");
            } else if (msg.what == 0x124) {
                //获取验证码成功
                showToast("验证码已发送");

            } else if (msg.what == 0x125) {
                //该手机号已被绑定
                showToast("该手机号已被绑定");

            } else if (msg.what == 0x126) {
                //数据有误
                showToast("数据有误");
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

            if (msg.what == 0x200) {
                //登录成功
                showToast("登录成功");
                startActivity(new Intent(WechatRegBindPhoneActivity.this, HomeActivity.class));
                finish();
            } else if (msg.what == 0x222) {
                //验证码过期
                showToast("验证码过期");
            } else if (msg.what == 0x223) {
                //验证码无效
                showToast("验证码无效");
            } else if (msg.what == 0x224) {
                //验证码无效
                showToast("请设置密码");
                Intent intent = new Intent();
                intent.setClass(WechatRegBindPhoneActivity.this, BindWechatSetPwdActivity.class);
                Log.e("telephone", mBindWechatUserPhone.getText().toString().trim());
                Log.e("openId", threeParty.getOpenid());
                Log.e("imageUrl", threeParty.getIconurl());
                Log.e("nickName", threeParty.getName());
                Log.e("type",type+"");
                intent.putExtra("telephone", mBindWechatUserPhone.getText().toString().trim());
                intent.putExtra("openId", threeParty.getOpenid());
                intent.putExtra("imageUrl", threeParty.getIconurl());
                intent.putExtra("nickName", threeParty.getName());
                intent.putExtra("type", type);
                startActivity(intent);

            } else if (msg.what == 0x225) {
                //系统异常，操作失败
                showToast("系统异常");
            }

            if (msg.what == 0x404) {
                showToast("请检查网络");
            }

            if (msg.what == -9) {
                mBindWechatSendYzm.setText("重新发送(" + i + ")");
                mBindWechatSendYzm.setTextColor(Color.GRAY);
                mBindWechatSendYzm.setClickable(false);
            } else if (msg.what == -8) {
                mBindWechatSendYzm.setText("获取验证码");
                mBindWechatSendYzm.setTextColor(getResources().getColor(R.color.colorText));
                mBindWechatSendYzm.setClickable(true);
                i = 60;
            }


        }
    };
}
