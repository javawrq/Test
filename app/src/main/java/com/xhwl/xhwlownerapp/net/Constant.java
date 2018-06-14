package com.xhwl.xhwlownerapp.net;


import android.app.NotificationManager;
import android.media.MediaPlayer;

import com.wilddog.video.call.Conversation;
import com.wilddog.video.call.RemoteStream;

/**
 * Created by longqun on 2017/3/13 0013.
 * 正式：http://202.105.104.105:8006
 * 测试：http://202.105.96.131:8006
 */

public class Constant {

    public static NotificationManager mNotificationManager = null;

    //云瞳登录账密
    public static final String HKurl = "202.105.104.109";
    public static final String HKusername = "wlwpt";
    public static final String HKpassword = "xinghai123";
    /**
     * 云对讲测试环境
     * VideoAppID: wd4414372708bcrqyf
     * SyncAppID: wd1435170719lvrpsm
     */
    public static final String VIDEO_APPID = "wd4414372708bcrqyf";
    public static final String SYNC_APPID = "wd1435170719lvrpsm";

    /**
     * 云对讲正式环境：
     * VideoAppID： wd3420885063wekxii
     * SyncAppID： wd2565313036qrpdim
     */
//    public static final String VIDEO_APPID = "wd3420885063wekxii";
//    public static final String SYNC_APPID = "wd2565313036qrpdim";

    public static String c = null;//用户手机号码
    public static Conversation conversation;
    public static MediaPlayer mediaPlayer01;//播放音频
    public static MediaPlayer mediaPlayer02;//播放音频
    public static String Information;
    public static RemoteStream remoteStream;

    /**
     * 服务器地址
     * 测试的 https://seven.xy-mind.com:8443    http://seven.xy-mind.com:8006
     * <p>
     * 正式的 https://seven.xhmind.com:8443     http://seven.xhmind.com:8006
     */
    //正式
    public static final String HOSTIP = "http://202.105.104.105:8006/";//120.77.83.190:8080  阿里云
    public static final String HOST = "http://seven.xhmind.com:8006/";
    //测试
    public static final String HOSTIP2 = "http://202.105.96.131:8006/";
    public static final String HOST2 = "http://seven.xy-mind.com:8006/";//http://192.168.200.116:8011
    public static final String HOST3 = "http://192.168.200.116:8011/";

    //背景音乐正式服务器
    public static final String MUSICHOST = "http://202.105.104.105:8007/";
    //背景音乐测试服务器
    public static final String MUSICHOST2 = "http://202.105.96.131:443/";

    //服务器名
    public static final String SERVERNAME = "ssh/";
    //接口版本号
    public static final String INTERFACEVERSION = "v1/";

    //维锐智能家居的常量
    public static final String ACTION_CALLBACK = "com.feibi.callback";

    public static final String ACTION_NEW_DEVICE = "com.feibi.callback.newDevice";

    public static final String ACTION_GET_GATEWAYINFO = "com.feibi.callback.getGateWayInfo";
    /**
     * API接口
     */
    //获取云对讲token
    public static final String WILDDOGGETTOKEN = "wilddog/getToken?uid=";
    //获取最近新版
    public static final String NEWVERSION = "version/getNewestVersion";
    //获取验证码
    public static final String GETVERIFYCODEBYTYPE = "appBase/getVerificatCodeByType";
    //验证验证码
    public static final String TESTVERIFYCODE = "appBase/register/testVerificatCode";
    //修改密码---通过手机号获取验证码修改密码
    public static final String MODIFYPWD_FORGETPWD = "appBase/modifyPassword/forgetOldPsw";
    //登录
    public static final String LOGIN = "appBase/loginNew";
    //注册
    public static final String REGISTER = "appBase/register";
    //退出登录
    public static final String LOGINOUT = "appBase/appLogout";
    //短信验证码登录
    public static final String VERIFYCODELOGIN = "appBase/verifyCodeLogin";
    //第三方登录
    public static final String THREEPARTYLOGIN = "appBase/ThreeParty/login";
    //第三方绑定用户时获取验证码
    public static final String THREEPARTYGETVERIFYCODE = "appBase/ThreeParty/getVerifyCode";
    //绑定账号时验证码 验证
    public static final String THREEPARTYTESTCODE = "appBase/ThreeParty/testVerifyCode";
    //第三方登录时注册用户
    public static final String THREEPARTYREGISTER = "appBase/ThreeParty/register";
    //文件上传
    public static final String FILEUPLOAD = "appBase/filesUpload";
    //修改密码
    public static final String MODIFYPWD = "appBase/modifyPassword";

    //根据手机号获取门禁列表
    public static final String GETDOORBYPHONE = "openDoor/getDoorByPhone";
    //获取用户授权门禁
    public static final String GETDOORBYPHONENEW = "openDoor/v2/getDoorByPhone";
    //获取项目下所有的门禁
    public static final String GETDOORBYPROJECTCODE = "openDoor/v2/getDoorByProjectCode";

    //远程开门
    public static final String REMOTEOPENDOOR = "/openDoor/openDoor";

    //云对讲保存历史记录
    public static final String TALKHISTORYSAVE = "wyBusiness/talkingBack/history/add";
    //门口机---通话时，远程开门
    public static final String OPENDOORBYCALL = "doorMachine/openDoorByCall";
    //获取对讲记录
    public static final String GETHISTORY = "wyBusiness/talkingBack/history/list";
    //云对讲获取客服列表
    public static final String GETONLINELIST = "wyBusiness/talkback/getUids";
    //云对讲获取门口机列表
    public static final String GETDOORLIST = "wyBusiness/getDoorMachineUidByRoomId";

    //云瞳上传图片
    public static final String HKUPLOADIMG = "wyBusiness/iot/video/upload";

    //获取该网段下的所有设备
    public static final String MUSICGETALLDEVICE = "backgroundMusic/mediaDevice/";
    //背景音乐 获取点歌记录
    public static final String MUSICALREDYLIST = "backgroundMusic/mediaplaylist/listPage";
    //背景音乐 删除歌曲
    public static final String MUSICREMOVE = "backgroundMusic/mediaplaylist/remove";
    //背景音乐 当前播放的音乐
    public static final String MUSICCURRENTMEDIA = "backgroundMusic/current/media/";
    //背景音乐 点击歌曲添加到点歌记录
    public static final String MUSICADDLIST = "backgroundMusic/mediaplaylist";
    //背景音乐 获取歌曲资源列表（20首）
    public static final String MUSICLIST = "backgroundMusic/mediaSongs/";
    //背景音乐 只能切换当前播放时自己的歌曲
    public static final String MUSICSWITCH = "backgroundMusic/media/switch";
    //个人信息---绑定/解绑 第三方账号；（微信、QQ、微博）
    public static final String THREEPARTYBIND = "appBase/ThreeParty/bind";

    //心情天气
    public static final String MOODWEATHER = "appBusiness/weather";
    //个人信息---获取个人信息
    public static final String GETUSERINFO = "appBase/getUserInfoByToken";
    //个人信息---最新修改账户个人信息接口
    public static final String UPDATEUSERINFO = "appBase/updateUserInfo";

}
