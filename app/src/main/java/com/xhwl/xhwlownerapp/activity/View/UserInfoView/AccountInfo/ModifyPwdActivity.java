package com.xhwl.xhwlownerapp.activity.View.UserInfoView.AccountInfo;

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
import android.widget.TextView;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.ClearEditText;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.activity.View.UserInfoView.ForgetPwdActivity;
import com.xhwl.xhwlownerapp.net.Constant;
import com.xhwl.xhwlownerapp.net.HttpConnectionInter;
import com.xhwl.xhwlownerapp.net.HttpConnectionTools;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 修改密码
 */
public class ModifyPwdActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "ModifyPwdActivity";
    private ImageView mAccountModifypwdClose;
    /**
     * 旧密码:
     */
    private ClearEditText mAccountModifypwdOldpwd;
    private ImageView mSetPassNoSee1;
    /**
     * 新密码:
     */
    private ClearEditText mAccountModifypwdNewpwd;
    private ImageView mSetPassNoSee2;
    /**
     * 确认新密码:
     */
    private ClearEditText mAccountModifypwdNewrepwd;
    private ImageView mSetPassNoSee3;
    /**
     * 忘记密码？
     */
    private TextView mAccountModifypwdForgotpwd;
    /**
     * 确定
     */
    private Button mAccountModifypwdSubmit;

    /**
     * 是否查看密码
     */
    private boolean isSee = false;
    private String newPwd,newrePwd,oldPwd,phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
        initView();
    }

    private void initView() {
        phone = SPUtils.get(this,"userTelephone","");
        mAccountModifypwdClose = (ImageView) findViewById(R.id.account_modifypwd_close);
        mAccountModifypwdClose.setOnClickListener(this);
        mAccountModifypwdOldpwd = (ClearEditText) findViewById(R.id.account_modifypwd_oldpwd);
        mSetPassNoSee1 = (ImageView) findViewById(R.id.set_pass_no_see_1);
        mSetPassNoSee1.setOnClickListener(this);
        mAccountModifypwdNewpwd = (ClearEditText) findViewById(R.id.account_modifypwd_newpwd);
        mSetPassNoSee2 = (ImageView) findViewById(R.id.set_pass_no_see2);
        mSetPassNoSee2.setOnClickListener(this);
        mAccountModifypwdNewrepwd = (ClearEditText) findViewById(R.id.account_modifypwd_newrepwd);
        mSetPassNoSee3 = (ImageView) findViewById(R.id.set_pass_no_see3);
        mSetPassNoSee3.setOnClickListener(this);
        mAccountModifypwdForgotpwd = (TextView) findViewById(R.id.account_modifypwd_forgotpwd);
        mAccountModifypwdForgotpwd.setOnClickListener(this);
        mAccountModifypwdSubmit = (Button) findViewById(R.id.account_modifypwd_submit);
        mAccountModifypwdSubmit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.account_modifypwd_close:
                //关闭
                finish();
                break;
            case R.id.set_pass_no_see_1:
                //设置查看密码
                if(!isSee){
                    //设置可查看
                    mSetPassNoSee1.setBackgroundResource(R.drawable.pass_see);
                    mAccountModifypwdOldpwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    //设置不能查看
                    mSetPassNoSee1.setBackgroundResource(R.drawable.pass_no_see);
                    mAccountModifypwdOldpwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                isSee = !isSee;
                break;
            case R.id.set_pass_no_see2:
                //设置查看密码
                if(!isSee){
                    //设置可查看
                    mSetPassNoSee2.setBackgroundResource(R.drawable.pass_see);
                    mAccountModifypwdNewpwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    //设置不能查看
                    mSetPassNoSee2.setBackgroundResource(R.drawable.pass_no_see);
                    mAccountModifypwdNewpwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                isSee = !isSee;
                break;
            case R.id.set_pass_no_see3:
                //设置查看密码
                if(!isSee){
                    //设置可查看
                    mSetPassNoSee3.setBackgroundResource(R.drawable.pass_see);
                    mAccountModifypwdNewrepwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    //设置不能查看
                    mSetPassNoSee3.setBackgroundResource(R.drawable.pass_no_see);
                    mAccountModifypwdNewrepwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                isSee = !isSee;
                break;
            case R.id.account_modifypwd_forgotpwd:
                //忘记密码
                startToAIctivity(ForgetPwdActivity.class);
                break;
            case R.id.account_modifypwd_submit:
                //确认修改密码
                if(TextUtils.isEmpty(mAccountModifypwdOldpwd.getText().toString().trim())){
                    showToast("请输入旧密码");
                    return;
                }else if(TextUtils.isEmpty(mAccountModifypwdNewpwd.getText().toString().trim())){
                    showToast("请输入新密码");
                    return;
                }else if(TextUtils.isEmpty(mAccountModifypwdNewrepwd.getText().toString().trim())){
                    showToast("请确认新密码");
                    return;
                }else if(!mAccountModifypwdNewrepwd.getText().toString().trim().equals(mAccountModifypwdNewpwd.getText().toString().trim())){
                    showToast("两次输入的密码不一致，请重新输入");
                    return;
                } else {
                    newPwd = mAccountModifypwdNewpwd.getText().toString().trim();
                    newrePwd  = mAccountModifypwdNewrepwd.getText().toString().trim();
                    oldPwd = mAccountModifypwdOldpwd.getText().toString().trim();
                    //修改密码
                    modifyPwd(phone,newrePwd,oldPwd);
                }

                break;
        }
    }

    //通过旧密码修改密码
    private void modifyPwd(String telephone,String newPsw,String oldPsw){
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.MODIFYPWD,
                HttpConnectionTools.HttpData("telephone", telephone, "newPsw", newPsw, "oldPsw", oldPsw),
                new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if(errorCode == 200){
                                handler.sendEmptyMessage(200);
                            }else if (errorCode == 201){
                                handler.sendEmptyMessage(201);
                            }else if (errorCode == 403){
                                handler.sendEmptyMessage(403);
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

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 200){
                showToast("修改成功");
                finish();
            }else if(msg.what == 403){
                showToast("旧密码错误");
            }else if(msg.what == 201){
                showToast("修改失败");
                Log.e(TAG,"系统异常，操作失败");
            }else if(msg.what == 500){
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
