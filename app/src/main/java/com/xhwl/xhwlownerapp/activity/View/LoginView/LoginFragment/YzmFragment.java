package com.xhwl.xhwlownerapp.activity.View.LoginView.LoginFragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseFrament;
import com.xhwl.xhwlownerapp.UIUtils.ClearEditText;
import com.xhwl.xhwlownerapp.UIUtils.PhoneJudeg;
import com.xhwl.xhwlownerapp.activity.View.LoginView.VerificationCodeActivity;
import com.xhwl.xhwlownerapp.net.Constant;
import com.xhwl.xhwlownerapp.net.HttpConnectionInter;
import com.xhwl.xhwlownerapp.net.HttpConnectionTools;
import com.zyao89.view.zloading.ZLoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import static com.zyao89.view.zloading.Z_TYPE.SINGLE_CIRCLE;

/**
 * 验证码登录
 */
public class YzmFragment extends BaseFrament implements View.OnClickListener {

    private View view;
    /**
     * 请输入手机号码
     */
    private ClearEditText mLoginYzmUserphone;
    /**
     * 登陆
     */
    private Button mYzmLoginBtn;

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
        view = inflater.inflate(R.layout.fragment_yzm, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        phoneJudeg = new PhoneJudeg(getActivity());
        mLoginYzmUserphone = (ClearEditText) view.findViewById(R.id.login_yzm_userphone);
        mYzmLoginBtn = (Button) view.findViewById(R.id.yzm_login_btn);
        mYzmLoginBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.yzm_login_btn:
                if (flag) {
                    //验证码登录
                    if(TextUtils.isEmpty(mLoginYzmUserphone.getText().toString().trim())){
                        showToast("请输入手机号");
                        return;
                    }else if(!phoneJudeg.judgePhoneNums(mLoginYzmUserphone.getText().toString())){
                        //判断手机号是否为正确格式
                        return;
                    } else{
                        yzmLogin(0);
                        loadingDialog = new ZLoadingDialog(getActivity());
                        loadingDialog.setLoadingBuilder(SINGLE_CIRCLE)//设置类型
                                .setLoadingColor(Color.BLUE)//颜色
                                .setHintText("Loading...")
                                .setHintTextSize(16) // 设置字体大小 dp
                                .setHintTextColor(Color.GRAY)  // 设置字体颜色
                                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                                .setCanceledOnTouchOutside(false)
                                //.setDialogBackgroundColor(Color.parseColor("#CCffffff")) // 设置背景色，默认白色
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
     * 短信验证码登录
     */
    private void yzmLogin(int type){
        //获取验证码
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.GETVERIFYCODEBYTYPE,
                HttpConnectionTools.HttpData("type",type,"telephone",mLoginYzmUserphone.getText().toString().trim()
                        ), new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if(errorCode == 200){
                                //发送成功
                                handler.sendEmptyMessage(0x200);
                            }else if(errorCode == -1){
                                //该手机已经注册
                                handler.sendEmptyMessage(-1);
                            }else if(errorCode == -2){
                                //该手机未注册
                                handler.sendEmptyMessage(-2);
                            }else if(errorCode == -3){
                                //请勿频繁操作
                                handler.sendEmptyMessage(-3);
                            }else if(errorCode == -4){
                                //短信发送失败
                                handler.sendEmptyMessage(-4);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.e("TAG","短信发送失败");
                    }
                });
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0x200){
                //发送成功  跳转页面验证验证码
                Intent intent = new Intent(getActivity(),VerificationCodeActivity.class);
                intent.putExtra("telephone",mLoginYzmUserphone.getText().toString().trim());
                startActivity(intent);
                loadingDialog.dismiss();
            }else if(msg.what == -1){
                //该手机已经注册
                showToast("该手机已经注册");
                loadingDialog.dismiss();
            }else if(msg.what == -2){
                //该手机未注册
                showToast("该手机未注册");
                loadingDialog.dismiss();
            }else if(msg.what == -3){
                //请勿频繁操作
                showToast("请勿频繁操作");
                loadingDialog.dismiss();
            }else if(msg.what == -4){
                //短信发送失败
                showToast("短信发送失败");
                loadingDialog.dismiss();
            }
        }
    };
}
