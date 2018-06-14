package com.xhwl.xhwlownerapp.activity.View.UserInfoView;

import android.content.Intent;
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

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.ClearEditText;
import com.xhwl.xhwlownerapp.UIUtils.PhoneJudeg;
import com.xhwl.xhwlownerapp.net.Constant;
import com.xhwl.xhwlownerapp.net.HttpConnectionInter;
import com.xhwl.xhwlownerapp.net.HttpConnectionTools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 忘记密码
 */
public class ForgetPwdActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mForgetPwdClose;
    /**
     * 手机号
     */
    private ClearEditText mForgetPwdPhone;
    /**
     * 发送验证码
     */
    private TextView mForgetPwdSend;
    /**
     * 验证码
     */
    private ClearEditText mForgetPwdVerificationcode;
    /**
     * 下一步
     */
    private Button mForgetPwdNext;

    //倒计时
    private int i = 60;
    private PhoneJudeg phoneJudeg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);
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
        phoneJudeg = new PhoneJudeg(ForgetPwdActivity.this);
        mForgetPwdClose = (ImageView) findViewById(R.id.forget_pwd_close);
        mForgetPwdClose.setOnClickListener(this);
        mForgetPwdPhone = (ClearEditText) findViewById(R.id.forget_pwd_phone);
        mForgetPwdSend = (TextView) findViewById(R.id.forget_pwd_send);
        mForgetPwdSend.setOnClickListener(this);
        mForgetPwdVerificationcode = (ClearEditText) findViewById(R.id.forget_pwd_verificationcode);
        mForgetPwdNext = (Button) findViewById(R.id.forget_pwd_next);
        mForgetPwdNext.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.forget_pwd_close:
                //关闭
                finish();
                break;
            case R.id.forget_pwd_send:
                //发送验证码
                sendVerificationCode();
                countDown();
                break;
            case R.id.forget_pwd_next:
                //下一步 验证验证码
                testVerificatCode();
                break;
        }
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

    //忘记密码 -- 发送验证码
    private void sendVerificationCode(){
        if(TextUtils.isEmpty(mForgetPwdPhone.getText().toString().trim())){
            showToast("请输入手机号");
        }else{
            //判断手机号是否为正确格式
            if(!phoneJudeg.judgePhoneNums(mForgetPwdPhone.getText().toString().trim())){
                return;
            }
            //网络请求返回的结果集
            HttpConnectionTools.HttpServler(Constant.HOST2+ Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.GETVERIFYCODEBYTYPE,
                    HttpConnectionTools.HttpData("type",2,"telephone",
                            mForgetPwdPhone.getText().toString().trim()),new HttpConnectionInter() {
                @Override
                public void onFinish(String content) {
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        int errorCode =  jsonObject.getInt("errorCode");
                        if(errorCode == -4){
                            //获取失败
                            handler.sendEmptyMessage(-4);
                        }else if(errorCode == -3){
                            //请勿频繁操作
                            handler.sendEmptyMessage(-3);
                        }else if(errorCode == -2){
                            //该手机未注册
                            handler.sendEmptyMessage(-2);
                        }else if(errorCode == -1){
                            //该手机已注册
                            handler.sendEmptyMessage(-1);
                        }else if(errorCode == 200){
                            //获取成功
                            handler.sendEmptyMessage(200);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(500);
                    }
                }
                @Override
                public void onError(Exception e) {
                    handler.sendEmptyMessage(500);
                    Log.e("TAG","Error");
                }
            });
        }
    }

    //忘记密码 -- 验证验证码
    private void testVerificatCode(){
        if(TextUtils.isEmpty(mForgetPwdPhone.getText().toString().trim())){
            showToast("请输入手机号");
        } //判断手机号是否为正确格式
        if(!phoneJudeg.judgePhoneNums(mForgetPwdPhone.getText().toString().trim())){
            return;
        } else if(TextUtils.isEmpty(mForgetPwdVerificationcode.getText().toString().trim())){
            showToast("请输入验证码");
        } else{
            //提交验证码，后台验证
            //网络请求返回的结果集
            HttpConnectionTools.HttpServler(Constant.HOST2+ Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.TESTVERIFYCODE,HttpConnectionTools.HttpData(
                    "telephone",mForgetPwdPhone.getText().toString().trim(),
                    "verificatCode",mForgetPwdVerificationcode.getText().toString().trim()), new HttpConnectionInter() {
                @Override
                public void onFinish(String content) {
                    JSONObject jsonObj = null;
                    try {
                        jsonObj = new JSONObject(content);
                        int errorCode =  jsonObj.getInt("errorCode");
                        if(errorCode == 110){
                            //验证码过期
                            handler.sendEmptyMessage(110);
                        }else if(errorCode == -2){
                            //验证码错误
                            handler.sendEmptyMessage(111);
                        }else if(errorCode == 200){
                            //验证成功
                            handler.sendEmptyMessage(0x200);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(500);
                    }
                }
                @Override
                public void onError(Exception e) {
                    handler.sendEmptyMessage(500);
                    Log.e("TAG","Error");
                }
            });
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            //发送验证码
            if(msg.what == 200){
                showToast("短信发送成功");
            }else if(msg.what == -4){
                showToast("短信发送失败");
            }else if(msg.what == -3){
                showToast("请勿频繁操作");
            }else if(msg.what == -2){
                showToast("该手机未注册");
            }else if(msg.what == -1){
                showToast("该手机已注册");
            }

            //验证验证码
            if(msg.what == 0x200){
                Intent intent = new Intent(ForgetPwdActivity.this,ResetPwdActivity.class);
                intent.putExtra("phone",mForgetPwdPhone.getText().toString().trim());
                intent.putExtra("code",mForgetPwdVerificationcode.getText().toString().trim());
                startActivity(intent);
                finish();
            }else if(msg.what == 111){
                showToast("验证码有误");
            }else if(msg.what == 110){
                showToast("验证码已过期");
            }

            if(msg.what == 500){
                showToast("请检查网络");
            }

            /**
             * 获取验证码
             */
            if (msg.what == -9) {
                mForgetPwdSend.setText(i+"秒后可重新获取");
                mForgetPwdSend.setEnabled(false);
            } else if (msg.what == -8) {
                mForgetPwdSend.setEnabled(true);
                mForgetPwdSend.setText("获取验证码");
                mForgetPwdSend.setTextColor(getResources().getColor(R.color.setPassword));
                mForgetPwdSend.setClickable(true);
                i = 60;
            }
        }
    };
}
