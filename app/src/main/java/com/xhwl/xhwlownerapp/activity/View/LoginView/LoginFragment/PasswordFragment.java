package com.xhwl.xhwlownerapp.activity.View.LoginView.LoginFragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseFrament;
import com.xhwl.xhwlownerapp.UIUtils.ClearEditText;
import com.xhwl.xhwlownerapp.UIUtils.PhoneJudeg;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.activity.View.HomeView.HomeActivity;
import com.xhwl.xhwlownerapp.activity.View.UserInfoView.ForgetPwdActivity;
import com.xhwl.xhwlownerapp.net.Constant;
import com.xhwl.xhwlownerapp.net.HttpConnectionInter;
import com.xhwl.xhwlownerapp.net.HttpConnectionTools;
import com.zyao89.view.zloading.ZLoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import static com.zyao89.view.zloading.Z_TYPE.SINGLE_CIRCLE;

/**
 * 密码登录
 */
public class PasswordFragment extends BaseFrament implements View.OnClickListener {

    private String TOURIST;
    private View view;
    /**
     * 请输入手机号码
     */
    private ClearEditText mLoginPasswordUserphone;
    /**
     * 请输入密码
     */
    private ClearEditText mLoginPasswordUserpwd;
    private ImageView mLoginPassNoSee;
    /**
     * 找回密码
     */
    private TextView mLoginForgetPwd;
    /**
     * 登陆
     */
    private Button mLoginPwdBtn;

    /**
     * 是否查看密码
     */
    private boolean isSee = false;

    /**
     * 判断手机号格式
     */
    private PhoneJudeg phoneJudeg;
    private boolean flag = true;//处理在短时间内多次点击同一组件
    private synchronized void setFlag() {
        flag = false;
    }
    private ZLoadingDialog loadingDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the rb_nobtn_selector for this fragment
        view = inflater.inflate(R.layout.fragment_password, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        phoneJudeg = new PhoneJudeg(getActivity());
        mLoginPasswordUserphone = (ClearEditText) view.findViewById(R.id.login_password_userphone);
        mLoginPasswordUserpwd = (ClearEditText) view.findViewById(R.id.login_password_userpwd);
        mLoginPassNoSee = (ImageView) view.findViewById(R.id.login_pass_no_see);
        mLoginPassNoSee.setOnClickListener(this);
        mLoginForgetPwd = (TextView) view.findViewById(R.id.login_forget_pwd);
        mLoginForgetPwd.setOnClickListener(this);
        mLoginPwdBtn = (Button) view.findViewById(R.id.login_pwd_btn);
        mLoginPwdBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.login_pass_no_see:
                //查看密码
                if(!isSee){
                    //设置可查看
                    mLoginPassNoSee.setBackgroundResource(R.drawable.pass_see);
                    mLoginPasswordUserpwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    //设置不能查看
                    mLoginPassNoSee.setBackgroundResource(R.drawable.pass_no_see);
                    mLoginPasswordUserpwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                isSee = !isSee;
                break;
            case R.id.login_forget_pwd:
                //忘记密码
                startActivity(new Intent(getActivity(), ForgetPwdActivity.class));
                break;
            case R.id.login_pwd_btn:
                if (flag) {
                    //密码登录
                    if(TextUtils.isEmpty(mLoginPasswordUserphone.getText().toString().trim())){
                        showToast("请输入手机号");
                        return;
                    }else if(TextUtils.isEmpty(mLoginPasswordUserpwd.getText().toString().trim())){
                        showToast("请输入密码");
                        return;
                    }else if(!phoneJudeg.judgePhoneNums(mLoginPasswordUserphone.getText().toString())){
                        return;
                    }else {
                        pwdLogin();
                        loadingDialog = new ZLoadingDialog(getActivity());
                        loadingDialog.setLoadingBuilder(SINGLE_CIRCLE)//设置类型
                                .setLoadingColor(Color.BLUE)//颜色
                                .setHintText("Loading...")
                                .setHintTextSize(16) // 设置字体大小 dp
                                .setHintTextColor(Color.GRAY)  // 设置字体颜色
                                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                                .setCanceledOnTouchOutside(false)
                                .show();
                    }
                    setFlag();
                    // do some things
                    new TimeThread().start();
                }
                break;
        }
    }

    /**
     * 计时线程（防止在一定时间段内重复点击按钮）
     */
    private class TimeThread extends Thread {
        public void run() {
            try {
                Thread.sleep(2000);
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 密码登录
     */
    private void pwdLogin(){
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.LOGIN,
                HttpConnectionTools.HttpData("telephone", mLoginPasswordUserphone.getText().toString().trim(),
                        "password", mLoginPasswordUserpwd.getText().toString().trim()), new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if(errorCode == 200){
                                String result = jsonObject.getString("result");//获得结果集
                                SPUtils.put(getActivity(),"result",result);
                                jsonObject = new JSONObject(result);//解析结果集
                                String sysAccount = jsonObject.getString("sysAccount");//获得用户基本信息
                                String roomList = jsonObject.getString("roomList");//获得用户基本信息
                                SPUtils.put(getActivity(),"sysAccount",jsonObject.getString("sysAccount"));
                                SPUtils.put(getActivity(),"roomList",jsonObject.getString("roomList"));
                                //业主登录成功
                                jsonObject = new JSONObject(sysAccount);//解析用户基本信息
                                SPUtils.put(getActivity(),"userName",jsonObject.getString("sysUserName"));//业主用户名
                                SPUtils.put(getActivity(),"userTelephone",jsonObject.getString("name"));//业主手机号
                                SPUtils.put(getActivity(),"userSex",jsonObject.getString("sex"));//业主性别
                                SPUtils.put(getActivity(),"userSysAccount",jsonObject.getString("id"));//业主accountID
                                SPUtils.put(getActivity(),"userToken",jsonObject.getString("token"));//用户token
                                SPUtils.put(getActivity(),"userWechatNickName",jsonObject.getString("weChatNickName"));//微信昵称
                                SPUtils.put(getActivity(),"userQQNickName",jsonObject.getString("qqnickName"));//QQ昵称
                                SPUtils.put(getActivity(),"userNickName",jsonObject.getString("nickName"));//用户昵称
                                SPUtils.put(getActivity(),"userImageUrl",jsonObject.getString("imageUrl"));//头像地址
                                SPUtils.put(getActivity(),"userWeiboNickName",jsonObject.getString("weiboNickName"));//微博昵称
                                SPUtils.put(getActivity(),"userType",jsonObject.getString("userType"));
                                handler.sendEmptyMessage(200);
                            }else if(errorCode == 113){
                                //用户密码密码错误
                                handler.sendEmptyMessage(113);
                            }else if(errorCode == 114){
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

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 200){
                showToast("登录成功");
                startActivity(new Intent(getActivity(),HomeActivity.class));
                getActivity().finish();
                getActivity().onBackPressed();//销毁自己
                loadingDialog.dismiss();
            }else if(msg.what == 114){
                showToast("用户不存在,请先注册");
                loadingDialog.dismiss();
            } else if(msg.what == 113){
                showToast("密码错误");
                loadingDialog.dismiss();
            }else if(msg.what == 404){
                showToast("用户没有访问权限,请联系物业管理人员");
                loadingDialog.dismiss();
            }else if(msg.what == 400){
                showToast("请检查网络");
                loadingDialog.dismiss();
            }
        }
    };
}
