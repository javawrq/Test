package com.xhwl.xhwlownerapp.Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.wilddog.client.SyncReference;
import com.wilddog.client.WilddogSync;
import com.wilddog.video.base.LocalStream;
import com.wilddog.video.base.LocalStreamOptions;
import com.wilddog.video.base.WilddogVideoError;
import com.wilddog.video.base.WilddogVideoInitializer;
import com.wilddog.video.call.CallStatus;
import com.wilddog.video.call.Conversation;
import com.wilddog.video.call.RemoteStream;
import com.wilddog.video.call.WilddogVideoCall;
import com.wilddog.video.call.stats.LocalStreamStatsReport;
import com.wilddog.video.call.stats.RemoteStreamStatsReport;
import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogauth.core.Task;
import com.wilddog.wilddogauth.core.listener.OnCompleteListener;
import com.wilddog.wilddogauth.core.result.AuthResult;
import com.wilddog.wilddogauth.core.result.GetTokenResult;
import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.UIUtils.ToastUtil;
import com.xhwl.xhwlownerapp.activity.View.CloudTalk.ConversationActivity;
import com.xhwl.xhwlownerapp.activity.View.HomeView.HomeActivity;
import com.xhwl.xhwlownerapp.net.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MyService extends Service {
    public static final String TAG = "MyService";
    private WilddogVideoCall video;
    private LocalStream localStream;
    private String proCode;
    private boolean isInConversation = false;

    public MyService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate() executed");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand() executed");
        //初始化Video
        WilddogVideoInitializer.initialize(getApplicationContext(), Constant.VIDEO_APPID,
                WilddogAuth.getInstance().getCurrentUser().getToken(false).getResult().getToken());
        //获取video对象
        video = WilddogVideoCall.getInstance();
        video.start();
        video.setListener(inviteListener);
        //createAndShowLocalStream();

        proCode = SPUtils.get(this, "proCode", "");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // video 断开与服务端链接,将无法收到请求,需要重新 start 因为video在本界面会重新初始化故释放资源
        video.stop();
        video.setListener(null);
        // 将写入数据实时引擎的数据用户在线状态数据移除
        WilddogSync.getInstance().goOffline();
        Log.e(TAG, "onDestroy() executed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //AlertDialog列表  对方邀请
    private WilddogVideoCall.Listener inviteListener = new WilddogVideoCall.Listener() {
        @Override
        public void onCalled(final Conversation conversation, String s) {

            if (!TextUtils.isEmpty(s)) {
                //ToastUtil.showSingleToast("对方邀请时候携带的信息是:" + s);
                try {
                    JSONObject jsobj = new JSONObject(s);
                    int type = jsobj.getInt("type");
                    Constant.Information = jsobj.getString("name");
                    SPUtils.put(MyService.this, "webType", type);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Constant.conversation = conversation;
            Constant.conversation.setConversationListener(conversationListener);
            Constant.conversation.setStatsListener(statsListener);
            Constant.mediaPlayer01 = MediaPlayer.create(MyService.this, R.raw.ringtones);
            Constant.mediaPlayer01.setLooping(true);
            Constant.mediaPlayer01.start();

            Intent myIntent = new Intent(MyService.this, HomeActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Intent intent = new Intent(MyService.this, ConversationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            Intent[] intents = {myIntent, intent};
            startActivities(intents);

            // 将来意图，用于点击通知之后的操作,内部的new intent()可用于跳转等操作
            @SuppressLint("WrongConstant")
            PendingIntent mPendingIntent = PendingIntent.getActivities(MyService.this, 1, intents, Notification.FLAG_AUTO_CANCEL);
            //全局通知管理者，通过获取系统服务获取
            Constant.mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            //通知栏构造器,创建通知栏样式
            Notification.Builder mBuilder = new Notification.Builder(MyService.this);
            //设置通知栏标题
            mBuilder.setContentTitle("小七当家")
                    //设置通知栏显示内容
                    .setContentText(Constant.Information+"向您发起通话")
                    //设置通知栏点击意图
                    .setContentIntent(mPendingIntent)
                    //通知首次出现在通知栏，带上升动画效果的
                    .setTicker(Constant.Information+"向您发起通话")
                    //通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setWhen(System.currentTimeMillis())
                    //设置该通知优先级
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    //设置这个标志当用户单击面板就可以让通知将自动取消
                    .setAutoCancel(true)
                    //使用当前的用户默认设置
                    .setDefaults(Notification.DEFAULT_VIBRATE)
                    //设置通知小ICON(应用默认图标)
                    .setSmallIcon(R.drawable.logo_mid_1);
            Constant.mNotificationManager.notify(1, mBuilder.build());

        }

        @Override
        public void onTokenError(WilddogVideoError wilddogVideoError) {
            // 处理token过期
            if (wilddogVideoError.getErrCode() == 41001) {
                WilddogAuth.getInstance().signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> var1) {
                        if (var1.isSuccessful()) {
                            String token = ((GetTokenResult) ((AuthResult) var1.getResult()).getWilddogUser().getToken(false).getResult()).getToken();
                            WilddogVideoInitializer.getInstance().setToken(token);
                            String uid = ((AuthResult) var1.getResult()).getWilddogUser().getUid();
                            Map<String, Object> map = new HashMap();
                            map.put(uid, Boolean.valueOf(true));
                            SyncReference userRef = WilddogSync.getInstance().getReference(proCode + "/user");
                            SyncReference.goOnline();
                            userRef.updateChildren(map);
                            userRef.child(uid).onDisconnect().removeValue();
                        }
                    }
                });
            } else {
                // token 无效
                ToastUtil.showSingleToast("token无效");
            }
        }
    };

    private Conversation.StatsListener statsListener = new Conversation.StatsListener() {
        @Override
        public void onLocalStreamStatsReport(LocalStreamStatsReport localStreamStatsReport) {
            //changeLocalData(localStreamStatsReport);
        }

        @Override
        public void onRemoteStreamStatsReport(RemoteStreamStatsReport remoteStreamStatsReport) {
            //changeRemoteData(remoteStreamStatsReport);
        }
    };

    //呼叫监听
    private Conversation.Listener conversationListener = new Conversation.Listener() {
        @Override
        public void onCallResponse(CallStatus callStatus) {
            switch (callStatus) {
                case ACCEPTED:
                    isInConversation = true;
                    break;
                case REJECTED:
                    handler.sendEmptyMessage(1);
                    break;
                case BUSY:
                    handler.sendEmptyMessage(2);
                    break;
                case TIMEOUT:
                    handler.sendEmptyMessage(3);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onStreamReceived(RemoteStream remoteStream) {
            //remoteStream.attach(mRemoteVideoView);//显示对方的视频流
            Constant.remoteStream = remoteStream;
            //SPUtils.put(MyService.this,"remoteStream", Constant.remoteStream);
            remoteStream.enableAudio(true);
        }

        @Override
        public void onClosed() {
            Log.e(TAG, "onClosed");
            if (Constant.mediaPlayer01 != null) {
                Constant.mediaPlayer01.stop();
            }
            isInConversation = false;
            handler.sendEmptyMessage(4);

        }

        @Override
        public void onError(final WilddogVideoError wilddogVideoError) {
            // 41007 表示超时,在接受前表示呼叫超时,在接受后表示对方异常退出
            if (wilddogVideoError != null && 41007 == wilddogVideoError.getErrCode()) {
                if (isInConversation) {
                    // 处理异常退出逻辑
                } else {
                    // 处理超时逻辑
                }
                handler.sendEmptyMessage(5);

                if (isInConversation) {
                    isInConversation = false;
                }
            } else {
                handler.sendEmptyMessage(6);
                // 其他类型错误

                if (isInConversation) {
                    isInConversation = false;
                }
            }
        }
    };

    //关闭连接
    private void closeConversation() {

        if (Constant.conversation != null) {
            Constant.conversation.close();
            Constant.conversation = null;
        }
        //mBtnInviteTxt.setText("用户列表");
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                ToastUtil.showSingleToast("对方拒绝你的邀请");
                isInConversation = false;
            } else if (msg.what == 2) {
                ToastUtil.showSingleToast("对方正在通话中,稍后再呼叫");
                isInConversation = false;
            } else if (msg.what == 3) {
                ToastUtil.showSingleToast("呼叫对方超时,请稍后再呼叫");
                isInConversation = false;
            } else if (msg.what == 4) {
                closeConversation();
                ToastUtil.showSingleToast("对方挂断");
//                if(Constant.activity == null){
//                    Intent home=new Intent(Intent.ACTION_MAIN);
//                    home.addCategory(Intent.CATEGORY_HOME);
//                    startActivity(home);
//                }else {
//
//                }
                Intent intent = new Intent(MyService.this, HomeActivity.class);
                //Log.e("Activity.Name",Constant.activity.getName()+"");
                startActivity(intent);
            } else if (msg.what == 5) {
                closeConversation();
                if (isInConversation) {
                    ToastUtil.showSingleToast("对方异常退出,等待超时");
                } else {
                    ToastUtil.showSingleToast("对方呼叫超时,本端未作相应");
                }
            } else if (msg.what == 6) {
                closeConversation();
                ToastUtil.showSingleToast("通话中出错,请查看日志");
            }
        }
    };

    private void createAndShowLocalStream() {
        LocalStreamOptions.Builder builder = new LocalStreamOptions.Builder();
        LocalStreamOptions options = builder.dimension(LocalStreamOptions.Dimension.DIMENSION_480P).build();
        //创建本地视频流，通过video对象获取本地视频流
        localStream = LocalStream.create(options);
        //开启音频/视频，设置为 false 则关闭声音或者视频画面
        localStream.enableAudio(true);
        localStream.enableVideo(true);
    }
}
