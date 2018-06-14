package com.xhwl.xhwlownerapp.activity.View.LoginView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.UIUtils.VerificationCodeView;
import com.xhwl.xhwlownerapp.activity.View.HomeView.HomeActivity;
import com.xhwl.xhwlownerapp.activity.View.RegisterView.SetPasswordActivity;
import com.xhwl.xhwlownerapp.net.Constant;
import com.xhwl.xhwlownerapp.net.HttpConnectionInter;
import com.xhwl.xhwlownerapp.net.HttpConnectionTools;
import com.zyao89.view.zloading.ZLoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import static com.zyao89.view.zloading.Z_TYPE.SINGLE_CIRCLE;

/**
 * 验证验证码
 * 1、验证码登录
 * 2、第三方绑定手机验证验证码
 */
public class VerificationCodeActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mVerificationcodeBack;
    /**
     * +8613977889945
     */
    private TextView mVerificationcodePhone;

    private VerificationCodeView mVerificationcodeview;
    /**
     * 50秒后可重新获取
     */
    private TextView mVerificationcodeTime;
    /**
     * 使用语音验证码
     */
    private TextView mVerificationcodeVoice;

    /**
     * 倒计时时间
     */
    private int i = 60 ;

    /**
     * 用户手机号
     */
    private String telephone,code;
    private ZLoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_code);
        initView();
    }

    private void initView() {
        telephone = getIntent().getStringExtra("telephone");
        Log.e("telephone",telephone+"");
        mVerificationcodeBack = (ImageView) findViewById(R.id.verificationcode_back);
        mVerificationcodeBack.setOnClickListener(this);
        mVerificationcodePhone = (TextView) findViewById(R.id.verificationcode_phone);
        mVerificationcodePhone.setText("+86"+telephone);

        mVerificationcodeview = (VerificationCodeView) findViewById(R.id.verificationcodeview);
        mVerificationcodeTime = (TextView) findViewById(R.id.verificationcode_time);
        mVerificationcodeTime.setOnClickListener(this);
        mVerificationcodeTime.setClickable(false);
        mVerificationcodeVoice = (TextView) findViewById(R.id.verificationcode_voice);
        mVerificationcodeVoice.setOnClickListener(this);
        //验证码输入框的监听
        mVerificationcodeview.setOnCodeFinishListener(new VerificationCodeView.OnCodeFinishListener() {
            @Override
            public void onComplete(String Code) {
                Log.e("Code",Code+"");
                code = Code;
                verifyCode(Code,"1");
                loadingDialog = new ZLoadingDialog(VerificationCodeActivity.this);
                loadingDialog.setLoadingBuilder(SINGLE_CIRCLE)//设置类型
                        .setLoadingColor(Color.BLUE)//颜色
                        .setHintText("Loading...")
                        .setHintTextSize(16) // 设置字体大小 dp
                        .setHintTextColor(Color.GRAY)  // 设置字体颜色
                        .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                        .setDialogBackgroundColor(Color.parseColor("#CCffffff")) // 设置背景色，默认白色
                        .show();
            }
        });
        //倒计时
        countDown();
    }

    /**
     * 倒计时
     */
    private void countDown() {
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.verificationcode_back:
                //返回
                finish();
                break;
            case R.id.verificationcode_voice:
                //使用语音通话
                showToast("暂未开通");
                break;
            case R.id.verificationcode_time:
                //重新获取验证码
                resetYzm(0);
                break;
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            /**
             * 获取验证码
             */
            if (msg.what == -9) {
                mVerificationcodeTime.setText(i+"秒后可重新获取");
            } else if (msg.what == -8) {
                mVerificationcodeTime.setText("获取验证码");
                mVerificationcodeTime.setTextColor(getResources().getColor(R.color.setPassword));
                mVerificationcodeTime.setClickable(true);
                i = 60;
            }

            /**
             * 验证验证码
             */
            if(msg.what == 200){
                //验证成功,直接登录
                showToast("登录成功");
                startToAIctivity(HomeActivity.class);
                finish();
                loadingDialog.dismiss();
            }else if(msg.what == -2){
                //验证码有误,请重新输入
                showToast("验证码有误,请重新输入");
                loadingDialog.dismiss();
            }else if(msg.what == 110){
                //验证码过期，请重新获取
                showToast("验证码过期,请重新获取");
                loadingDialog.dismiss();
            }else if(msg.what == 100){
                //该手机号未设置密码，请设置密码
                showToast("该手机号未设置密码，请设置密码");
                Intent in = new Intent(VerificationCodeActivity.this,SetPasswordActivity.class);
                in.putExtra("telephone",telephone);
                in.putExtra("code",code);
                startActivity(in);
                loadingDialog.dismiss();
            }else if(msg.what == 111){
                //数据有误
                showToast("数据有误");
                loadingDialog.dismiss();
            }else if(msg.what == 404){
                //数据有误
                showToast("用户没有访问权限,请联系物业管理人员");
                loadingDialog.dismiss();
            }
            if(msg.what == 500){
                showToast("请检查网络");
                loadingDialog.dismiss();
            }
        }
    };

    /**
     * 验证码登陆
     * @param Code
     */
    private void verifyCode(String Code,String isNewVersion){
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.VERIFYCODELOGIN,
                HttpConnectionTools.HttpData("telephone",telephone,"verifyCode",Code,"isNewVersion",isNewVersion),
                new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        Log.e("content",content);
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if(errorCode == 200){
                                String result = jsonObject.getString("result");//获得结果集
                                SPUtils.put(VerificationCodeActivity.this,"result",result);
                                jsonObject = new JSONObject(result);//解析结果集
                                String sysAccount = jsonObject.getString("sysAccount");//获得用户基本信息
                                String roomList = jsonObject.getString("roomList");//获得用户基本信息
                                SPUtils.put(VerificationCodeActivity.this,"sysAccount",jsonObject.getString("sysAccount"));
                                SPUtils.put(VerificationCodeActivity.this,"roomList",jsonObject.getString("roomList"));
                                //业主登录成功
                                jsonObject = new JSONObject(sysAccount);//解析用户基本信息
                                SPUtils.put(VerificationCodeActivity.this,"userName",jsonObject.getString("sysUserName"));//业主用户名
                                SPUtils.put(VerificationCodeActivity.this,"userTelephone",jsonObject.getString("name"));//业主手机号
                                SPUtils.put(VerificationCodeActivity.this,"userSex",jsonObject.getString("sex"));//业主性别
                                SPUtils.put(VerificationCodeActivity.this,"userSysAccount",jsonObject.getString("id"));//业主accountID
                                SPUtils.put(VerificationCodeActivity.this,"userToken",jsonObject.getString("token"));//用户token
                                SPUtils.put(VerificationCodeActivity.this,"userWechatNickName",jsonObject.getString("weChatNickName"));//微信昵称
                                SPUtils.put(VerificationCodeActivity.this,"userQQNickName",jsonObject.getString("qqnickName"));//QQ昵称
                                SPUtils.put(VerificationCodeActivity.this,"userNickName",jsonObject.getString("nickName"));//用户昵称
                                SPUtils.put(VerificationCodeActivity.this,"userImageUrl",jsonObject.getString("imageUrl"));//头像地址
                                SPUtils.put(VerificationCodeActivity.this,"userWeiboNickName",jsonObject.getString("weiboNickName"));//微博昵称
                                SPUtils.put(VerificationCodeActivity.this,"userType",jsonObject.getString("userType"));
                                handler.sendEmptyMessage(200);
                            }else if(errorCode == -2){
                                //验证码不正确，请重新输入
                                handler.sendEmptyMessage(-2);
                            }else if(errorCode == 110){
                                //验证码过期，重新获取
                                handler.sendEmptyMessage(110);
                            }else if(errorCode == 100){
                                //该手机号未设置密码，请设置密码
                                handler.sendEmptyMessage(100);
                            }else if(errorCode == 111){
                                //数据有误
                                handler.sendEmptyMessage(111);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        handler.sendEmptyMessage(500);
                    }
                });
    }

    /**
     * 重新发送验证码
     */
    private void resetYzm(int type){
        //获取验证码
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.GETVERIFYCODEBYTYPE,
                HttpConnectionTools.HttpData("type",type,"telephone",telephone
                        ), new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if(errorCode == 200){
                                //发送成功
                                handler1.sendEmptyMessage(0x200);
                            }else if(errorCode == -1){
                                //该手机已经注册
                                handler1.sendEmptyMessage(-1);
                            }else if(errorCode == -2){
                                //该手机未注册
                                handler1.sendEmptyMessage(-2);
                            }else if(errorCode == -3){
                                //请勿频繁操作
                                handler1.sendEmptyMessage(-3);
                            }else if(errorCode == -4){
                                //短信发送失败
                                handler1.sendEmptyMessage(-4);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        handler.sendEmptyMessage(500);
                    }
                });
    }

    Handler handler1 = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0x200){
                //发送成功  跳转页面验证验证码
                showToast("发送成功");
                countDown();
                mVerificationcodeTime.setClickable(false);
            }else if(msg.what == -1){
                //该手机已经注册
                showToast("该手机已经注册");
            }else if(msg.what == -2){
                //该手机未注册
                showToast("该手机未注册");
            }else if(msg.what == -3){
                //请勿频繁操作
                showToast("请勿频繁操作");
            }else if(msg.what == -4){
                //短信发送失败
                showToast("短信发送失败");
            }
            if(msg.what == 500){
                showToast("请检查网络");
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
