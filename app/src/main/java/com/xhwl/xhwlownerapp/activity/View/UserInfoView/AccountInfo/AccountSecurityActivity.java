package com.xhwl.xhwlownerapp.activity.View.UserInfoView.AccountInfo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
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
import com.xhwl.xhwlownerapp.UIUtils.dialog.SelfDialog;
import com.xhwl.xhwlownerapp.net.Constant;
import com.xhwl.xhwlownerapp.net.HttpConnectionInter;
import com.xhwl.xhwlownerapp.net.HttpConnectionTools;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class AccountSecurityActivity extends BaseActivity implements View.OnClickListener {

    private AutoLinearLayout mTopBack;
    private TextView mTopTitle;
    /**
     * 未绑定
     */
    private TextView mAccountWechatNickname;
    private AutoLinearLayout mAccountWechatLinear;
    /**
     * 未绑定
     */
    private TextView mAccountQqNickname;
    private AutoLinearLayout mAccountQqLinear;
    /**
     * 未绑定
     */
    private TextView mAccountWeiboNickname;
    private AutoLinearLayout mAccountWeiboLinear;
    private AutoLinearLayout mAccountModifypwdLinear;
    //用户第三方昵称
    private String wechatNickName,QQNickName,weiboNickName;
    //用户id
    private String id;
    private JSONObject jsonObject;
    private boolean isWechat = false;//判断是否绑定第三方，默认未绑定
    private boolean isQQ = false;//判断是否绑定第三方，默认未绑定
    private boolean isWeibo = false;//判断是否绑定第三方，默认未绑定
    //第三方类型； 1微信 2QQ 3微博
    private final int WECHATTYPE = 1;
    private final int QQTYPE = 2;
    private final int WEIBOTYPE = 3;
    private final String TAG = "AccountSecurityActivity";
    private ThreeParty threeParty;
    private int type;
    private SelfDialog dialog;
    private boolean flag = true;
    private synchronized void setFlag() {
        flag = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_security);
        initView();
        initDate();
    }

    private void initDate() {
        id = SPUtils.get(this,"userSysAccount","");
        Log.e("id",id+"sasac");
        wechatNickName = SPUtils.get(this,"weChatNickName","");
        QQNickName = SPUtils.get(this,"qqnickName","");
        weiboNickName = SPUtils.get(this,"weiboNickName","");
        Log.e("weiboNickName",wechatNickName+"qq");
        if(!"null".equals(wechatNickName)){
            //设置微信昵称
            mAccountWechatNickname.setText(wechatNickName);
            isWechat = true;//已经绑定了则赋值true
        }

        if(!"null".equals(QQNickName)){
            //设置QQ昵称
            mAccountQqNickname.setText(QQNickName);
            isQQ = true;//已经绑定了则赋值true
        }

        if(!"null".equals(weiboNickName)){
            //设置微博昵称
            mAccountWeiboNickname.setText(weiboNickName);
            isWeibo = true;//已经绑定了则赋值true
        }
    }

    private void initView() {
        threeParty = new ThreeParty();
        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopTitle.setText("账号安全");
        mAccountWechatNickname = (TextView) findViewById(R.id.account_wechat_nickname);
        mAccountWechatLinear = (AutoLinearLayout) findViewById(R.id.account_wechat_linear);
        mAccountWechatLinear.setOnClickListener(this);
        mAccountQqNickname = (TextView) findViewById(R.id.account_qq_nickname);
        mAccountQqLinear = (AutoLinearLayout) findViewById(R.id.account_qq_linear);
        mAccountQqLinear.setOnClickListener(this);
        mAccountWeiboNickname = (TextView) findViewById(R.id.account_weibo_nickname);
        mAccountWeiboLinear = (AutoLinearLayout) findViewById(R.id.account_weibo_linear);
        mAccountWeiboLinear.setOnClickListener(this);
        mAccountModifypwdLinear = (AutoLinearLayout) findViewById(R.id.account_modifypwd_linear);
        mAccountModifypwdLinear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.top_back:
                //返回
                finish();
                break;
            case R.id.account_wechat_linear:
                if (flag) {
                    if(StringUtils.isWeixinAvilible(AccountSecurityActivity.this)){
                        //解绑微信or绑定微信
                        Log.e("isWechat",isWechat+"aa");
                        if(isWechat == true){
                            dialog = new SelfDialog(AccountSecurityActivity.this);
                            dialog.setMessage("确定解除账号与微信的绑定吗？");
                            dialog.setTitle("解除后将无法通过微信登陆账号");
                            dialog.setYesOnclickListener("确定", new SelfDialog.onYesOnclickListener() {
                                @Override
                                public void onYesClick() {
                                    //已经绑定了微信，点击则执行解绑操作
                                    threePartyUnBind(id,WECHATTYPE);
                                    dialog.dismiss();
                                }
                            });
                            dialog.setNoOnclickListener("取消", new SelfDialog.onNoOnclickListener() {
                                @Override
                                public void onNoClick() {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }else{
                            dialog = new SelfDialog(AccountSecurityActivity.this);
                            dialog.setTitle("确定与微信绑定吗？");
                            dialog.setMessage("绑定后可以直接使用微信登录");
                            dialog.setYesOnclickListener("确定", new SelfDialog.onYesOnclickListener() {
                                @Override
                                public void onYesClick() {
                                    //未绑定微信，点击则执行绑定操作
                                    authorization(SHARE_MEDIA.WEIXIN,WECHATTYPE);
                                    dialog.dismiss();
                                }
                            });
                            dialog.setNoOnclickListener("取消", new SelfDialog.onNoOnclickListener() {
                                @Override
                                public void onNoClick() {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }
                    }else {
                        ToastUtil.showSingleToast("请先安装微信");
                    }

                    type = WECHATTYPE;
                    setFlag();
                    // do some things
                    new TimeThread().start();
                }

                break;
            case R.id.account_qq_linear:
                if (flag) {
                    if(StringUtils.isQQClientAvailable(AccountSecurityActivity.this)){
                        //解绑QQor绑定QQ
                        Log.e("isWechat",isQQ+"aa");
                        if(isQQ == true){
                            dialog = new SelfDialog(AccountSecurityActivity.this);
                            dialog.setMessage("确定解除账号与QQ的绑定吗？");
                            dialog.setTitle("解除后将无法通过QQ登陆账号");
                            dialog.setYesOnclickListener("确定", new SelfDialog.onYesOnclickListener() {
                                @Override
                                public void onYesClick() {
                                    ///已经绑定了QQ，点击则执行解绑操作
                                    threePartyUnBind(id,QQTYPE);
                                    dialog.dismiss();
                                }
                            });
                            dialog.setNoOnclickListener("取消", new SelfDialog.onNoOnclickListener() {
                                @Override
                                public void onNoClick() {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }else{
                            dialog = new SelfDialog(AccountSecurityActivity.this);
                            dialog.setTitle("确定与QQ绑定吗？");
                            dialog.setMessage("绑定后可以直接使用QQ登录");
                            dialog.setYesOnclickListener("确定", new SelfDialog.onYesOnclickListener() {
                                @Override
                                public void onYesClick() {
                                    //未绑定QQ，点击则执行绑定操作
                                    authorization(SHARE_MEDIA.QQ,QQTYPE);
                                    dialog.dismiss();
                                }
                            });
                            dialog.setNoOnclickListener("取消", new SelfDialog.onNoOnclickListener() {
                                @Override
                                public void onNoClick() {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }
                        type = QQTYPE;
                    }else {
                        ToastUtil.showSingleToast("请先安装QQ");
                    }

                    setFlag();
                    // do some things
                    new TimeThread().start();
                }

                break;
            case R.id.account_weibo_linear:
//                if (flag) {
//                    Log.e("isWechat",isWeibo+"aa");
//                    if(isWeibo == true){
//                        dialog = new SelfDialog(AccountSecurityActivity.this);
//                        dialog.setMessage("确定解除账号与微博的绑定吗？");
//                        dialog.setTitle("解除后将无法通过微博登陆账号");
//                        dialog.setYesOnclickListener("确定", new SelfDialog.onYesOnclickListener() {
//                            @Override
//                            public void onYesClick() {
//                                //已经绑定了微博，点击则执行解绑操作
//                                threePartyUnBind(id,WEIBOTYPE);
//                            }
//                        });
//                        dialog.setNoOnclickListener("取消", new SelfDialog.onNoOnclickListener() {
//                            @Override
//                            public void onNoClick() {
//                                dialog.dismiss();
//                            }
//                        });
//                        dialog.show();
//                    }else{
//                        //未绑定微博，点击则执行绑定操作
//                        authorization(SHARE_MEDIA.SINA,WEIBOTYPE);
//                    }
//                    type = WEIBOTYPE;
//                    setFlag();
//                    // do some things
//                    new TimeThread().start();
//                }
                showToast("正在开放中");
                break;
            case R.id.account_modifypwd_linear:
                //修改密码
                startToAIctivity(ModifyPwdActivity.class);
                break;
        }
    }

    /**
     * 计时线程（防止在一定时间段内重复点击按钮）
     */
    private class TimeThread extends Thread {
        public void run() {
            try {
                Thread.sleep(3000);
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 绑定第三方
     * @param id 登录账号id
     * @param type 第三方类型； 1微信 2QQ 3微博
     * @param openId 第三方唯一标识，绑定时传，解绑时不传
     * @param nickName 第三方昵称，绑定时传，解绑时不传
     * @param imageUrl 第三方头像，绑定时传，解绑时不传
     */
    private void threePartyBind(String id,int type,String openId,String nickName,String imageUrl){
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.THREEPARTYBIND,
                HttpConnectionTools.HttpData("type", type,"id", id,"openId", openId, "nickName",
                        nickName, "imageUrl", imageUrl), new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if(errorCode == 200){
                                handler.sendEmptyMessage(0x200);
                            }else if(errorCode == 111){
                                handler.sendEmptyMessage(111);
                            }else if(errorCode == 201){
                                handler.sendEmptyMessage(201);
                            }else if(errorCode == 112){
                                handler.sendEmptyMessage(112);
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
     * 解绑第三方
     * @param id 登录账号id
     * @param type 第三方类型； 1微信 2QQ 3微博
     */
    private void threePartyUnBind(String id,int type){
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.THREEPARTYBIND,
                HttpConnectionTools.HttpData("type", type, "id", id), new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if(errorCode == 200){
                                handler.sendEmptyMessage(200);
                            }else if(errorCode == 111){
                                handler.sendEmptyMessage(111);
                            }else if(errorCode == 201){
                                handler.sendEmptyMessage(201);
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
     * 第三方授权
     * 拿到用户的第三方信息
     * @param share_media 平台名称
     */
    private void authorization(SHARE_MEDIA share_media, final int type) {
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

                //拿到信息去请求登录接口。。。
                if(SHARE_MEDIA.QQ.equals(share_media) || SHARE_MEDIA.WEIXIN.equals(share_media))
                    threePartyBind(id,type,threeParty.getOpenid(),threeParty.getName(),threeParty.getIconurl());
                else if (SHARE_MEDIA.SINA.equals(share_media))
                    threePartyBind(id,type,threeParty.getUid(),threeParty.getName(),threeParty.getIconurl());
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

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0x200){
                showToast("绑定成功");
                if(type == WECHATTYPE){
                    mAccountWechatNickname.setText(threeParty.getName());
                    isWechat = true;
                }else if(type == QQTYPE){
                    mAccountQqNickname.setText(threeParty.getName());
                    isQQ = true;
                }else if(type == WEIBOTYPE){
                    mAccountWeiboNickname.setText(threeParty.getName());
                    isWeibo = true;
                }
            }else if(msg.what == 112){
                showToast("该账号已被绑定");
            }
            if(msg.what == 200){
                showToast("解绑成功");
                dialog.dismiss();
                if(type == WECHATTYPE){
                    mAccountWechatNickname.setText("未绑定");
                    isWechat = false;
                }else if(type == QQTYPE){
                    mAccountQqNickname.setText("未绑定");
                    isQQ = false;
                }else if(type == WEIBOTYPE){
                    mAccountWeiboNickname.setText("未绑定");
                    isWeibo = false;
                }
            }else if(msg.what == 201){
                showToast("操作失败，请稍后重试");
            }else if(msg.what == 111){
                showToast("数据有误，操作失败");
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
