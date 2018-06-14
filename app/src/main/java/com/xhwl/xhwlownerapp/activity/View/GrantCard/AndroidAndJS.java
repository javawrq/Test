package com.xhwl.xhwlownerapp.activity.View.GrantCard;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.StringUtils;
import com.xhwl.xhwlownerapp.UIUtils.ToastUtil;

/**
 * Created by Administrator on 2018/1/17.
 */

public class AndroidAndJS {
    private static Dialog mShareDialog;
    private static Context context;
    private static String shareURL,startTime,endTime,proName,userName;
    private static int sendtype = 0;

    AndroidAndJS(Context context, String proName, String userName){
        this.context = context;
        this.proName = proName;
        this.userName = userName;
    }
    static class AndroidAndJSInterface{
        @JavascriptInterface
        public void twoDimensionCode(String url,String sTime,String eTime){
            showDialog();
            shareURL = url;
            startTime = sTime;
            endTime = eTime;
            Log.e("url",shareURL);
        }
    }

    /**
     * 显示分享弹出框
     */
    private static void showDialog() {
        if (mShareDialog == null) {
            initShareDialog();
        }
        initShareDialog();
        mShareDialog.show();
    }

    /**
     * 初始化分享弹出框
     */
    private static void initShareDialog() {
        mShareDialog = new Dialog(context, R.style.dialog_bottom_full);
        mShareDialog.setCanceledOnTouchOutside(true);
        mShareDialog.setCancelable(true);
        Window window = mShareDialog.getWindow();
        window.setGravity(Gravity.BOTTOM);
        window.setWindowAnimations(R.style.share_animation);
        View view = View.inflate(context, R.layout.lay_share, null);
        view.findViewById(R.id.share_wechat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //微信分享
                if(StringUtils.isWeixinAvilible(context)){
                    WechatShar(SHARE_MEDIA.WEIXIN);
                }else {
                    ToastUtil.showSingleToast("请先安装微信");
                }

            }
        });
        view.findViewById(R.id.share_qq).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //QQ分享
                if(StringUtils.isQQClientAvailable(context)){
                    WechatShar(SHARE_MEDIA.QQ);
                }else {
                    ToastUtil.showSingleToast("请先安装QQ");
                }
            }
        });
        view.findViewById(R.id.share_sms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //短信分享
                sendSms();
            }
        });
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mShareDialog != null && mShareDialog.isShowing()) {
                    mShareDialog.dismiss();
                }
            }
        });
        window.setContentView(view);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);//设置横向全屏
    }

    private static void WechatShar(SHARE_MEDIA platform) {
        UMImage image = new UMImage(context, R.drawable.logo_mid_1);//资源文件
        UMWeb web = new UMWeb(shareURL);
        web.setTitle("小七当家");//标题
        web.setThumb(image);  //缩略图
        web.setDescription("你的好友"+userName+"邀请您"+ startTime+" 至 " +endTime+
                "前往"+proName+"做客。");//描述
        new ShareAction((Activity) context)
            .setPlatform(platform)//传入平台
            .withMedia(web)//分享的链接
            .withText("你的好友"+userName+"邀请您"+ startTime+" 至 " +endTime+
                    "前往"+proName+"做客。")//分享内容
            .setCallback(shareListener)//回调监听器
            .share();
    }

    private static UMShareListener shareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }
        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(context,"成功了",Toast.LENGTH_LONG).show();
        }
        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(context,"失败"+t.getMessage(),Toast.LENGTH_LONG).show();
        }
        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(context,"取消了",Toast.LENGTH_LONG).show();
        }
    };

    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private static boolean isWebchatAvaliable() {
        //检测手机上是否安装了微信
        try {
            context.getPackageManager().getPackageInfo("com.tencent.mm", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 短信分享
     */
     private static void sendSms(){
         Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
         intent.putExtra("sms_body", "【小七当家】"+userName+"邀请您于" +
                 startTime+" 至 " +endTime+
                 "前往"+proName+"做客。期间您可通过以下二维码出入小区。" +
                 "二维码链接：" + shareURL);
         context.startActivity(intent);
     }


}
