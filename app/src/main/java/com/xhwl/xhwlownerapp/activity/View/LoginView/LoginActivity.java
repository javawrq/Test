package com.xhwl.xhwlownerapp.activity.View.LoginView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.xhwl.xhwlownerapp.Entity.UserEntity.ThreeParty;
import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.EmojiFilter;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.UIUtils.StringUtils;
import com.xhwl.xhwlownerapp.UIUtils.ToastUtil;
import com.xhwl.xhwlownerapp.activity.View.HomeView.HomeActivity;
import com.xhwl.xhwlownerapp.activity.View.LoginView.LoginFragment.PasswordFragment;
import com.xhwl.xhwlownerapp.activity.View.LoginView.LoginFragment.YzmFragment;
import com.xhwl.xhwlownerapp.activity.View.RegisterView.SecurityProtocolActivity;
import com.xhwl.xhwlownerapp.activity.View.RegisterView.WechatRegBindPhoneActivity;
import com.xhwl.xhwlownerapp.net.Constant;
import com.xhwl.xhwlownerapp.net.HttpConnectionInter;
import com.xhwl.xhwlownerapp.net.HttpConnectionTools;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 密码和短信验证码登录
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 密码
     */
    private TextView mLoginPassword;
    /**
     * 验证码
     */
    private TextView mLoginYzm;
    private ViewPager mLoginViewPager;
    private AutoLinearLayout mLoginWechat;
    private AutoLinearLayout mLoginQq;
    private AutoLinearLayout mLoginWeibo;
    private FragmentManager fragmentManager;//碎片管理者对象
    private List<Fragment> fragmentList;
    private AutoLinearLayout mLoginAgreementAutoLayout;
    /**
     * <u>小七当家用户服务协议</u>
     */
    private TextView mLoginAgreementAutoLink;

    private UMShareAPI umShareAPI;

    private String TAG = this.getClass().getSimpleName();

    private ThreeParty threeParty;

    //第三方类别 1微信 2QQ 3微博
    private int type;

    private boolean flag = true;//处理在短时间内多次点击同一组件
    private synchronized void setFlag() {
        flag = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initData();
    }

    private void initData() {
        //添加数据源
        fragmentList = new ArrayList<>();
        fragmentList.add(new YzmFragment());
        fragmentList.add(new PasswordFragment());

        //获得管理者对象
        fragmentManager = getSupportFragmentManager();
        //碎片滑动，适配器
        mLoginViewPager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
        //点击事件
        mLoginViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @SuppressLint("ResourceAsColor")
            @Override
            public void onPageSelected(int position) {
                reset();//重置文字颜色
                switch (position) {
                    case 0:
                        mLoginYzm.setTextColor(getResources().getColor(R.color.login_yzm_1));
                        mLoginAgreementAutoLayout.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        mLoginPassword.setTextColor(getResources().getColor(R.color.login_yzm_1));
                        mLoginAgreementAutoLayout.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mLoginViewPager.setCurrentItem(0);  //初始化显示第一个页面
        mLoginYzm.setTextColor(getResources().getColor(R.color.login_yzm_1));
        mLoginAgreementAutoLayout.setVisibility(View.VISIBLE);

        //6.0申请权限
        if(Build.VERSION.SDK_INT>=23){
            String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.CALL_PHONE,
                    Manifest.permission.READ_LOGS,Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.SET_DEBUG_APP,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,Manifest.permission.GET_ACCOUNTS,
                    Manifest.permission.WRITE_APN_SETTINGS,Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this,mPermissionList,123);
        }
    }

    //改变图片 字体的颜色为暗色
    @SuppressLint("ResourceAsColor")
    private void reset() {
        //设置图片变为暗色
        mLoginPassword.setTextColor(getResources().getColor(R.color.login_yzm_0));
        mLoginYzm.setTextColor(getResources().getColor(R.color.login_yzm_0));
    }

    private void initView() {
        threeParty = new ThreeParty();
        mLoginPassword = (TextView) findViewById(R.id.login_password);
        mLoginPassword.setOnClickListener(this);
        mLoginYzm = (TextView) findViewById(R.id.login_yzm);
        mLoginYzm.setOnClickListener(this);
        mLoginViewPager = (ViewPager) findViewById(R.id.login_viewPager);
        mLoginWechat = (AutoLinearLayout) findViewById(R.id.login_wechat);
        mLoginWechat.setOnClickListener(this);
        mLoginQq = (AutoLinearLayout) findViewById(R.id.login_qq);
        mLoginQq.setOnClickListener(this);
        mLoginWeibo = (AutoLinearLayout) findViewById(R.id.login_weibo);
        mLoginWeibo.setOnClickListener(this);
        mLoginAgreementAutoLayout = (AutoLinearLayout) findViewById(R.id.login_agreement_autoLayout);
        mLoginAgreementAutoLink = (TextView) findViewById(R.id.login_agreement_autoLink);
        mLoginAgreementAutoLink.setOnClickListener(this);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.login_password:
                mLoginViewPager.setCurrentItem(1);
                mLoginPassword.setTextColor(getResources().getColor(R.color.login_yzm_1));
                mLoginYzm.setTextColor(getResources().getColor(R.color.login_yzm_0));
                mLoginAgreementAutoLayout.setVisibility(View.GONE);
                break;
            case R.id.login_yzm:
                mLoginViewPager.setCurrentItem(0);
                mLoginYzm.setTextColor(getResources().getColor(R.color.login_yzm_1));
                mLoginPassword.setTextColor(getResources().getColor(R.color.login_yzm_0));
                mLoginAgreementAutoLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.login_wechat:
                if (flag) {
                    //微信登录
                    type = 1;
                    ToastUtil.showSingleToast("微信登录");
                    if(StringUtils.isWeixinAvilible(LoginActivity.this)){
                        authorization(SHARE_MEDIA.WEIXIN);
                    }else {
                        ToastUtil.showSingleToast("请先安装微信");
                    }

                    setFlag();
                    // do some things
                    new TimeThread().start();
                }

                break;
            case R.id.login_qq:
                if (flag) {
                    //QQ登录
                    type = 2;
                    if(StringUtils.isQQClientAvailable(LoginActivity.this)){
                        authorization(SHARE_MEDIA.QQ);
                    }else {
                        ToastUtil.showSingleToast("请先安装QQ");
                    }
                   
                    setFlag();
                    // do some things
                    new TimeThread().start();
                }

                break;
            case R.id.login_weibo:
//                if (flag) {
//                    //微博登录
//                type = 3;
//                authorization(SHARE_MEDIA.SINA);
//                    setFlag();
//                    // do some things
//                    new TimeThread().start();
//                }

                showToast("正在开放中");
                break;
            case R.id.login_agreement_autoLink:
                //用户协议
                startToAIctivity(SecurityProtocolActivity.class);
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
     * 第三方授权
     * @param share_media 平台名称
     */
    private void authorization(SHARE_MEDIA share_media) {
        UMShareAPI.get(this).getPlatformInfo(this, share_media, new UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                Log.e(TAG, "onStart " + "授权开始");
            }

            @Override
            public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                Log.e(TAG, "onComplete " + "授权完成");
                //sdk是6.4.4的,但是获取值的时候用的是6.2以前的(access_token)才能获取到值,未知原因
                threeParty.setUid( map.get("uid"));
                threeParty.setOpenid( map.get("openid"));//微博没有
                threeParty.setUnionid( map.get("unionid"));//微博没有
                threeParty.setAccess_token( map.get("access_token"));
                threeParty.setRefresh_token( map.get("refresh_token"));//微信,qq,微博都没有获取到
                if(EmojiFilter.containsEmoji(map.get("name"))){
                    EmojiFilter.filterEmoji(map.get("name"));
                    threeParty.setName(EmojiFilter.filterEmoji(map.get("name")));//昵称
                }else {
                    threeParty.setName(map.get("name"));//昵称
                }

                threeParty.setIconurl( map.get("iconurl"));//头像地址
                threeParty.setGender( map.get("gender"));//性别
                //Toast.makeText(getApplicationContext(), "name=" + threeParty.getName() + ",gender=" + threeParty.getGender(), Toast.LENGTH_SHORT).show();
                //拿到信息去请求登录接口。。。
                    if(SHARE_MEDIA.QQ.equals(share_media) || SHARE_MEDIA.WEIXIN.equals(share_media))
                        threePartyLogin(threeParty.getOpenid(),"1");
                    else if (SHARE_MEDIA.SINA.equals(share_media))
                        threePartyLogin(threeParty.getUid(),"1");
                }

            @Override
            public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                Log.e(TAG, "onError " + "授权失败");
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media, int i) {
                Log.e(TAG, "onCancel " + "授权取消");
            }
        });
    }

    // QQ与新浪回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 6.0权限回调处理
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }
    /**
     * 第三方登录
     * @param openId 第三方的唯一标识
     */
    private void threePartyLogin(final String openId,String isNewVersion){
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION+Constant.THREEPARTYLOGIN,
                HttpConnectionTools.HttpData("openId", openId,"isNewVersion",isNewVersion), new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            Log.e("getOpenid",openId);
                            JSONObject jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if(errorCode == 200){
                                String result = jsonObject.getString("result");//获得结果集
                                SPUtils.put(LoginActivity.this,"result",result);
                                jsonObject = new JSONObject(result);//解析结果集
                                String sysAccount = jsonObject.getString("sysAccount");//获得用户基本信息
                                String roomList = jsonObject.getString("roomList");//获得用户基本信息
                                SPUtils.put(LoginActivity.this,"sysAccount",jsonObject.getString("sysAccount"));
                                SPUtils.put(LoginActivity.this,"roomList",jsonObject.getString("roomList"));
                                //业主登录成功
                                jsonObject = new JSONObject(sysAccount);//解析用户基本信息
                                SPUtils.put(LoginActivity.this,"userName",jsonObject.getString("sysUserName"));//业主用户名
                                SPUtils.put(LoginActivity.this,"userTelephone",jsonObject.getString("name"));//业主手机号
                                SPUtils.put(LoginActivity.this,"userSex",jsonObject.getString("sex"));//业主性别
                                SPUtils.put(LoginActivity.this,"userSysAccount",jsonObject.getString("id"));//业主accountID
                                SPUtils.put(LoginActivity.this,"userToken",jsonObject.getString("token"));//用户token
                                String userWechatNickName = jsonObject.getString("weChatNickName");
                                String weiboNickName = jsonObject.getString("weiboNickName");
                                String userQQNickName = jsonObject.getString("qqnickName");
                                URLEncoder.encode(userWechatNickName+"", "utf-8");
                                URLEncoder.encode(weiboNickName+"", "utf-8");
                                URLEncoder.encode(userQQNickName+"", "utf-8");
                                SPUtils.put(LoginActivity.this,"userWechatNickName",userWechatNickName);//微信昵称
                                SPUtils.put(LoginActivity.this,"userQQNickName",weiboNickName);//QQ昵称
                                SPUtils.put(LoginActivity.this,"userWeiboNickName",weiboNickName);//微博昵称
                                SPUtils.put(LoginActivity.this,"userNickName",jsonObject.getString("nickName"));//用户昵称
                                SPUtils.put(LoginActivity.this,"userImageUrl",jsonObject.getString("imageUrl"));//头像地址
                                SPUtils.put(LoginActivity.this,"userType",jsonObject.getString("userType"));
                                handler.sendEmptyMessage(200);
                            }else if(errorCode == 114){
                                handler.sendEmptyMessage(114);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent();
            if(msg.what == 200){
                showToast("登录成功");
                startToAIctivity(HomeActivity.class);
                finish();
            }else if(msg.what == 114){
                if(type == 1){
                    showToast("该微信账号没有绑定任何注册用户，请先绑定");
                }else if(type == 2){
                    showToast("该QQ账号没有绑定任何注册用户，请先绑定");
                }else if(type == 3){
                    showToast("该微博账号没有绑定任何注册用户，请先绑定");
                }

                intent.setClass(LoginActivity.this, WechatRegBindPhoneActivity.class);
                intent.putExtra("type",type);
                intent.putExtra("threeParty",threeParty);
                startActivity(intent);
            }else if(msg.what == 404){
                showToast("用户没有访问权限,请联系物业管理人员");
            }
        }
    };

}
