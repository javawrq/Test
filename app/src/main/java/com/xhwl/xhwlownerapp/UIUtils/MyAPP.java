package com.xhwl.xhwlownerapp.UIUtils;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.fbee.zllctl.DeviceInfo;
import com.fbee.zllctl.GatewayInfo;
import com.fbee.zllctl.Serial;
import com.hik.mcrsdk.MCRSDK;
import com.hik.mcrsdk.rtsp.RtspClient;
import com.hik.mcrsdk.talk.TalkClientSDK;
import com.hikvision.sdk.VMSNetSDK;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.wilddog.client.SyncReference;
import com.wilddog.client.WilddogSync;
import com.wilddog.video.call.WilddogVideoCall;
import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogcore.WilddogApp;
import com.wilddog.wilddogcore.WilddogOptions;
import com.xhwl.xhwlownerapp.net.Constant;

import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Administrator on 2018/2/26.MultiDexApplication
 * 初始化类
 */

public class MyAPP extends Application {

    private static MyAPP myApp;
    public static final String APP_ID = "wxc5e20b28cf976b5c";    //这个APP_ID就是注册APP的时候生成的
    public static final String APP_SECRET = "57039eb96054557def20c354f21a709c";
    public static final String QQAPP_ID = "1106374982";//这是QQ的APPid

    public static String projectCode = "";//项目Code
    public static String granterPhone = "";//用户手机号
    public static String proName = "";//项目名
    public static String userName = "";

    public static SyncReference syncReference = null;
    public static SyncReference userRef;
    public static WilddogAuth auth;
    public static WilddogSync sync;


    public static Serial mSerial;
    public static ArrayList<DeviceInfo> deviceInfos = new ArrayList<>();
    public static GatewayInfo gatewayInfo;
    public static int isConnect = 0;
    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        new ShowToast(this);
        initSerial();
        /**
         * 初始化友盟common库
         * 参数1:上下文，不能为空
         * 参数2:设备类型，UMConfigure.DEVICE_TYPE_PHONE为手机、UMConfigure.DEVICE_TYPE_BOX为盒子，默认为手机
         * 参数3:Push推送业务的secret
         */
        UMConfigure.init(this, UMConfigure.DEVICE_TYPE_PHONE, "");

        /**
         * 设置组件化的Log开关
         * 参数: boolean 默认为false，如需查看LOG设置为true
         */
        UMConfigure.setLogEnabled(true);

        /**
         * 设置日志加密
         * 参数：boolean 默认为false（不加密）
         */
        UMConfigure.setEncryptEnabled(false);

        /**
         * 配置三方平台的appkey：
         * 微信
         * QQ/QQzone
         * 新浪微博
         */
        PlatformConfig.setWeixin("wxc5e20b28cf976b5c", "57039eb96054557def20c354f21a709c");
        PlatformConfig.setQQZone("1106374982", "WHJuPOTTEWbGN2Ew");
        PlatformConfig.setSinaWeibo("3712619360", "98315301d48b04105873b563ef568402", "http://open.weibo.com/apps/3712619360/privilege/oauth");

        JPushInterface.setDebugMode(true); // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush

        //初始化友盟sdk
        UMShareAPI.get(this);
        //开启debug模式，方便定位错误，
        // 具体错误检查方式可以查看http://dev.umeng.com/social/android/quick-integration的报错必看，
        // 正式发布，请关闭该模式
        Config.DEBUG = true;
        /**
         * 初始化海康SDK
         */
        MCRSDK.init();
        // 初始化RTSP
        RtspClient.initLib();
        MCRSDK.setPrint(1, null);
        // 初始化语音对讲
        TalkClientSDK.initLib();
        // SDK初始化
        VMSNetSDK.init(this);

        //初始化WilddogApp,完成初始化之后可在项目任意位置通过getInstance()获取Sync & Auth对象
        WilddogOptions.Builder builder = new WilddogOptions.Builder().setSyncUrl("https://" + Constant.SYNC_APPID + ".wilddogio" + ".com");
        WilddogOptions options = builder.build();
        WilddogApp.initializeApp(getApplicationContext(), options);
    }
    //初始化Serial
    private void initSerial() {
        if (mSerial == null) {
            mSerial = new Serial();
        }
        mSerial.setmContext(getApplicationContext());
    }
    //释放Serial
    public void exit() {
        if (mSerial != null) {
            mSerial.releaseSource();
        }
        deviceInfos.clear();
    }

    public static MyAPP getIns() {
        return myApp;
    }

    /**
     * 获取登录设备mac地址
     *
     * @return Mac地址
     */
    public String getMacAddress() {
        WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo connectionInfo = wm.getConnectionInfo();
        String mac = connectionInfo.getMacAddress();
        return mac == null ? "" : mac;
    }

    //退出登录 清空数据
    public static void Logout(Context context) {
        SPUtils.put(context, "result", "");
        SPUtils.put(context, "sysUser", "");
        SPUtils.put(context, "userName", "");
        SPUtils.put(context, "userTelephone", "");
        SPUtils.put(context, "userSex", "");
        SPUtils.put(context, "userSysAccount", "");
        SPUtils.put(context, "userToken", "");
        SPUtils.put(context, "userWechatNickName", "");
        SPUtils.put(context, "userQQNickName", "");
        SPUtils.put(context, "userNickName", "");
        SPUtils.put(context, "userImageUrl", "");
        SPUtils.put(context, "userWeiboNickName", "");
        SPUtils.put(context, "proCode", "");
        SPUtils.put(context, "proName", "");
        SPUtils.put(context, "nodeType", "");
        SPUtils.put(context, "nodeID", "");
        SPUtils.put(context, "proID", "");
        SPUtils.put(context, "entranceCode", "");
        SPUtils.put(context, "imageUrl", "");
        SPUtils.put(context, "nickName", "");
        SPUtils.put(context, "id", "");
        SPUtils.put(context, "sex", "");
        SPUtils.put(context, "name", "");
        SPUtils.put(context, "sysUserName", "");
        SPUtils.put(context, "weiboNickName", "");
        SPUtils.put(context, "weChatNickName", "");
        SPUtils.put(context, "qqnickName", "");
        SPUtils.put(context, "token", "");
        SPUtils.put(context, "roomList", "");
        SPUtils.put(context, "exent", "");
        SPUtils.put(context, "term", "");
        SPUtils.put(context, "description", "");
        SPUtils.put(context, "Hum", "");
        SPUtils.put(context, "Mood", "");
        SPUtils.put(context, "Tmp", "");
        SPUtils.put(context, "Uv", "");
        SPUtils.put(context, "Code", "");
        SPUtils.put(context, "userType", "");
        SPUtils.put(context, "bingPhone", "");
        SPUtils.put(context, "isLogin", false);
        SPUtils.put(context, "matchDoorVoDoorList", "");
        SPUtils.put(context, "isWorkstation", true);
        SPUtils.put(context,"doorListResult","");
        JPushInterface.setAlias(context, 0, "");
        //删除别名
        JPushInterface.deleteAlias(context, 0);
        //云对讲下线
        myApp.userRef.goOffline();
        WilddogVideoCall.getInstance().stop();
        if(Constant.mediaPlayer01!=null){
            Constant.mediaPlayer01.stop();
        }
        if(Constant.mediaPlayer02!=null){
            Constant.mediaPlayer02.stop();
        }
        if(Constant.conversation != null){
            Constant.conversation.close();
        }
    }
}
