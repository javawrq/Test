package com.xhwl.xhwlownerapp.activity.View.UserInfoView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.ClearEditText;
import com.xhwl.xhwlownerapp.net.Constant;
import com.xhwl.xhwlownerapp.net.HttpConnectionInter;
import com.xhwl.xhwlownerapp.net.HttpConnectionTools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 忘记密码、重置密码
 */
public class ResetPwdActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mResetPassClose;
    /**
     * 密码:
     */
    private ClearEditText mSetPasswordEdit;
    private ImageView mResetPassNoSee;
    /**
     * 确认密码:
     */
    private ClearEditText mSetRepasswordEdit;
    private ImageView mResetRepassNoSee;
    /**
     * 确认
     */
    private Button mResetPassConfirm;

    private String phone;
    private String verificationCode;

    private boolean isSee = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pwd);
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
        phone = getIntent().getStringExtra("phone");
        verificationCode = getIntent().getStringExtra("code");
        mResetPassClose = (ImageView) findViewById(R.id.reset_pass_close);
        mResetPassClose.setOnClickListener(this);
        mSetPasswordEdit = (ClearEditText) findViewById(R.id.set_password_edit);
        mResetPassNoSee = (ImageView) findViewById(R.id.reset_pass_no_see);
        mResetPassNoSee.setOnClickListener(this);
        mSetRepasswordEdit = (ClearEditText) findViewById(R.id.set_repassword_edit);
        mResetRepassNoSee = (ImageView) findViewById(R.id.reset_repass_no_see);
        mResetRepassNoSee.setOnClickListener(this);
        mResetPassConfirm = (Button) findViewById(R.id.reset_pass_confirm);
        mResetPassConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.reset_pass_close:
                //关闭
                finish();
                break;
            case R.id.reset_pass_no_see:
                //设置密码可否查看
                if(!isSee){
                    //设置可查看
                    mResetPassNoSee.setBackgroundResource(R.drawable.pass_see);
                    mSetPasswordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    //设置不能查看

                    mResetPassNoSee.setBackgroundResource(R.drawable.pass_no_see);
                    mSetPasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                isSee = !isSee;
                break;
            case R.id.reset_repass_no_see:
                //设置确认密码可否查看
                if(!isSee){
                    //设置可查看
                    mResetRepassNoSee.setBackgroundResource(R.drawable.pass_see);
                    mSetRepasswordEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    //设置不能查看
                    mResetRepassNoSee.setBackgroundResource(R.drawable.pass_no_see);
                    mSetRepasswordEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                isSee = !isSee;
                break;
            case R.id.reset_pass_confirm:
                //确认提交
                forgetOldPsw();
                break;
        }
    }

    //确认重置密码
    private void forgetOldPsw(){
        if(TextUtils.isEmpty(mSetPasswordEdit.getText().toString().trim())){
            showToast("密码不能为空");
        }else if(TextUtils.isEmpty(mSetRepasswordEdit.getText().toString().trim())){
            showToast("请再次输入密码");
        }else if(!mSetPasswordEdit.getText().toString().trim().equals(mSetRepasswordEdit.getText().toString().trim())){
            showToast("两次输入密码不一致，请重新输入");
        }else {
            HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME+Constant.INTERFACEVERSION +Constant.MODIFYPWD_FORGETPWD,
                    HttpConnectionTools.HttpData("telephone", phone, "verificatCode",
                            verificationCode, "newPsw", mSetRepasswordEdit.getText().toString().trim()),
                    new HttpConnectionInter() {
                        @Override
                        public void onFinish(String content) {
                            try {
                                JSONObject jsonObject = new JSONObject(content);
                                int errorCode = jsonObject.getInt("errorCode");
                                if(errorCode == 111){
                                    handler.sendEmptyMessage(111);
                                }else if(errorCode == 110){
                                    handler.sendEmptyMessage(110);
                                }else if(errorCode == 210){
                                    handler.sendEmptyMessage(210);
                                }else if(errorCode == 200){
                                    handler.sendEmptyMessage(200);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    }
            );
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 111){
                showToast("验证码无效");
            }else if(msg.what == 110){
                showToast("验证码已过期");
            }else if(msg.what == 210){
                showToast("系统异常，操作失败");
            }else if(msg.what == 200){
                showToast("重置成功");
                //startToAIctivity(LoginActivity.class);
                finish();
            }
        }
    };
}
