package com.xhwl.xhwlownerapp.activity.View.CloudTalk;

import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wilddog.client.ChildEventListener;
import com.wilddog.client.DataSnapshot;
import com.wilddog.client.SyncError;
import com.wilddog.client.SyncReference;
import com.wilddog.client.WilddogSync;
import com.wilddog.video.base.LocalStream;
import com.wilddog.video.base.LocalStreamOptions;
import com.wilddog.video.base.WilddogVideoError;
import com.wilddog.video.base.WilddogVideoInitializer;
import com.wilddog.video.base.WilddogVideoView;
import com.wilddog.video.base.util.LogUtil;
import com.wilddog.video.base.util.logging.Logger;
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
import com.xhwl.xhwlownerapp.Entity.TalkEntity.UserList;
import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.Receiver.NetWorkStateReceiver;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.DateUtils;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.UIUtils.ToastUtil;
import com.xhwl.xhwlownerapp.activity.View.HomeView.HomeActivity;
import com.xhwl.xhwlownerapp.net.Constant;
import com.xhwl.xhwlownerapp.net.HttpConnectionInter;
import com.xhwl.xhwlownerapp.net.HttpConnectionTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.xhwl.xhwlownerapp.net.Constant.mNotificationManager;


public class ConversationActivity extends BaseActivity implements View.OnClickListener {
    private ImageView mWiddogRefuse;
    private ImageView mWiddogAnswer;
    private LinearLayout mWaitCall;

    private WilddogVideoView mRemoteVideoView;
    private WilddogVideoView mLocalVideoView;
    private ImageView mWiddogInviteCancelImageview;//挂断图标
    private LinearLayout mWiddogInviteCancelLayout;//挂断layout
    private ImageView mWiddogVoiceImageview;//语音
    private LinearLayout mWiddogVoiceLayout;//语音
    private ImageView mWiddogOpendoorImageview;//开门
    private LinearLayout mWiddogOpendoorLayout;//开门
    private ImageView mWiddogClosecameraImageview;//关闭摄像头
    private LinearLayout mWiddogClosecameraLayout;//关闭摄像头
    private ImageView mWiddogSwitchcameraImageview;//切换摄像头
    private LinearLayout mWiddogSwitchcameraLayout;//切换摄像头
    private FrameLayout frameLayout;

    private TextView mTvUid;
    /**
     * 用户列表
     */
    private ImageView mBtnInvite;
    private int isMic = 0;
    private int isDetach = 0;

    private static final String TAG = ConversationActivity.class.getSimpleName();

    private boolean isInConversation = false;
    private boolean isAudioEnable = true;
    private WilddogVideoCall video;
    private LocalStream localStream;
    private Map<Conversation, AlertDialog> conversationAlertDialogMap;
    private String remoteId = null;
    private SyncReference doorRef = null;
    private boolean talkFlag = true;//是否处于被呼、接听、拒绝状态
    private String doorID;
    private String startTime;//通话开始时间
    private String endTime;//通话结束时间
    private String login_Token, proCode;
    private int callType;
    private String uid;

    private Chronometer mWiddogTalkTime;
    /**
     * 1栋1单元101
     */
    private TextView mDoorName;
    private LinearLayout mAnswerBtnLiner;
    private LinearLayout mCallBtnLiner;
    private ImageView mCallWiddogRefuse;
    private ImageView mCallWiddogAnswer;
    private Chronometer mCallTalkTime;
    private LinearLayout mCallTalkTimeLiner;
    /**
     * 邀请你进行云对讲
     */
    private TextView mWaitJoinTalkText;

    private String token, userName;
    private String pageNumber = "1";
    private String pageSize = "100";
    private String roleType = "0";
    private String webFlag = "1";
    private List<String> staffList = new ArrayList<>();//野狗节点监听数据
    private List<UserList> userLists = new ArrayList<>();//后台返回数据
    private List<String> userIdLists = new ArrayList<>();//后台返回UID数据
    private List<Object> allLists = new ArrayList<>();//最终去交集的list
    private int randomNum = -1;//轮呼随机数
    private UserList userList;//后台返回数据实体类

    //呼叫门口机
    private String participantId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_conversation);
        initView();
        initDate();
        //获取野狗客服在线列表
        //setCallListener("staffweb");
    }

    private void initDate() {
        Constant.mediaPlayer02 = MediaPlayer.create(ConversationActivity.this, R.raw.ringtones);
        login_Token = SPUtils.get(ConversationActivity.this, "userToken", "");
        proCode = SPUtils.get(ConversationActivity.this, "proCode", "");
        token = SPUtils.get(ConversationActivity.this, "userToken", "");
        uid = WilddogAuth.getInstance().getCurrentUser().getUid();
        //mTvUid.setText(uid);
        LogUtil.setLogLevel(Logger.Level.DEBUG);

        //获取video对象
        video = WilddogVideoCall.getInstance();
        video.start();
        initVideoRender();
        createAndShowLocalStream();//创建本地流
        conversationAlertDialogMap = new HashMap<>();
        video.setListener(inviteListener);

        if (callType == 1) {
            //获取后台客服列表 //呼叫客服
            getServerList(token, proCode, roleType, webFlag, pageSize, pageNumber);
        } else if (callType == 2) {
            participantId = getIntent().getStringExtra("participantId");
            Log.e("participantId", participantId);
            callDoorTalk(participantId);//呼叫门口机
        } else if (callType == 3) {
            callOwnerTalk();//呼叫业主
        }
    }

    NetWorkStateReceiver netWorkStateReceiver;
    //在onResume()方法注册
    @Override
    protected void onResume() {
        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetWorkStateReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);
        System.out.println("注册");
        super.onResume();
    }

    //onPause()方法注销
    @Override
    protected void onPause() {
        unregisterReceiver(netWorkStateReceiver);
        System.out.println("注销");
        super.onPause();
    }

    @Override
    protected void onRestart() {
        if(Constant.mediaPlayer02 == null){
            Constant.mediaPlayer02 = MediaPlayer.create(ConversationActivity.this, R.raw.ringtones);
        }
        super.onRestart();
    }

    private void initView() {
        userName = SPUtils.get(this, "userName", "");
        callType = getIntent().getIntExtra("callType", 0);
        staffList = (List<String>) getIntent().getSerializableExtra("staffList");
        frameLayout = (FrameLayout) findViewById(R.id.widdog_frame);
        mRemoteVideoView = (WilddogVideoView) findViewById(R.id.remote_video_view);
        mLocalVideoView = (WilddogVideoView) findViewById(R.id.local_video_view);
        mTvUid = (TextView) findViewById(R.id.tv_uid);
        mBtnInvite = (ImageView) findViewById(R.id.btn_invite);
        mBtnInvite.setOnClickListener(this);
        mWiddogInviteCancelImageview = (ImageView) findViewById(R.id.widdog_invite_cancel_imageview);
        mWiddogInviteCancelLayout = (LinearLayout) findViewById(R.id.widdog_invite_cancel_layout);
        mWiddogInviteCancelLayout.setOnClickListener(this);
        mWiddogVoiceImageview = (ImageView) findViewById(R.id.widdog_voice_imageview);
        mWiddogVoiceLayout = (LinearLayout) findViewById(R.id.widdog_voice_layout);
        mWiddogVoiceLayout.setOnClickListener(this);
        mWiddogOpendoorImageview = (ImageView) findViewById(R.id.widdog_opendoor_imageview);
        mWiddogOpendoorLayout = (LinearLayout) findViewById(R.id.widdog_opendoor_layout);
        mWiddogOpendoorLayout.setOnClickListener(this);
        mWiddogClosecameraImageview = (ImageView) findViewById(R.id.widdog_closecamera_imageview);
        mWiddogClosecameraLayout = (LinearLayout) findViewById(R.id.widdog_closecamera_layout);
        mWiddogClosecameraLayout.setOnClickListener(this);
        mWiddogSwitchcameraImageview = (ImageView) findViewById(R.id.widdog_switchcamera_imageview);
        mWiddogSwitchcameraLayout = (LinearLayout) findViewById(R.id.widdog_switchcamera_layout);
        mWiddogSwitchcameraLayout.setOnClickListener(this);
        mWiddogRefuse = (ImageView) findViewById(R.id.widdog_refuse);
        mWiddogRefuse.setOnClickListener(this);
        mWiddogAnswer = (ImageView) findViewById(R.id.widdog_answer);
        mWiddogAnswer.setOnClickListener(this);
        mWaitCall = (LinearLayout) findViewById(R.id.wait_call);
        mWiddogTalkTime = (Chronometer) findViewById(R.id.widdog_talk_time);
        if (talkFlag) {
            timer.schedule(task, 1000, 1000);
        }
        mDoorName = (TextView) findViewById(R.id.door_name);
        mDoorName.setText(Constant.Information);
        mAnswerBtnLiner = (LinearLayout) findViewById(R.id.answer_btn_liner);
        mCallBtnLiner = (LinearLayout) findViewById(R.id.call_btn_liner);
        mCallWiddogRefuse = (ImageView) findViewById(R.id.call_widdog_refuse);
        mCallWiddogRefuse.setOnClickListener(this);
        mCallWiddogAnswer = (ImageView) findViewById(R.id.call_widdog_answer);
        mCallWiddogAnswer.setOnClickListener(this);
        mCallTalkTime = (Chronometer) findViewById(R.id.call_talk_time);
        mCallTalkTimeLiner = (LinearLayout) findViewById(R.id.call_talk_time_liner);
        mWaitJoinTalkText = (TextView) findViewById(R.id.wait_join_talk_text);
    }

    //获取后台数据，拿到所在项目所有的客服列表
    private void getServerList(String token, String projectCode, String roleType, String webFlag, String pageSize, String pageNumber) {
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.GETONLINELIST,
                HttpConnectionTools.HttpData("token", token, "projectCode", projectCode,
                        "roleType", roleType, "webFlag", webFlag, "pageSize", pageSize,
                        "pageNumber", pageNumber), new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            String result = jsonObject.getString("result");
                            jsonObject = new JSONObject(result);
                            JSONArray jsonArray = jsonObject.getJSONArray("users");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                userList = new UserList();
                                jsonObject = jsonArray.getJSONObject(i);
                                Log.e("name", jsonObject.getString("name") + " ++ ");
                                userList.setName(jsonObject.getString("name"));
                                userList.setUid(jsonObject.getString("uid"));
                                userList.setWorkCode(jsonObject.getString("workCode"));
                                userLists.add(userList);
                                userIdLists.add(jsonObject.getString("uid"));
                            }
                            handler.sendEmptyMessage(900);
                        } catch (JSONException e) {

                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        handler.sendEmptyMessage(0x500);
                    }
                });
    }

    //云对讲监听
    private Conversation.StatsListener statsListener = new Conversation.StatsListener() {
        @Override
        public void onLocalStreamStatsReport(LocalStreamStatsReport localStreamStatsReport) {
        }

        @Override
        public void onRemoteStreamStatsReport(RemoteStreamStatsReport remoteStreamStatsReport) {
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeConversation();
//                            ToastUtil.showSingleToast("对方拒绝你的邀请");
//                            isInConversation = false;
//                            if (callType == 1) {
//                                callServerClose();//呼叫客服关闭通话
//                            } else if (callType == 2) {
//                                //呼叫门口机关闭通话
//                                callDoorClose();
//                            }
                            if(callType == 1){
                                if(userIdLists.size() != 0){
                                    ToastUtil.showSingleToast("请稍后，正在发起下一轮呼叫");
                                    //轮呼前先把前一次呼叫的人员名单移除
                                    if(randomNum > -1){
                                        userIdLists.remove(randomNum);
                                    }
                                    //轮呼客服
                                    wheelCall();
                                }else {
                                    ToastUtil.showSingleToast("您拨打的客服正忙，请稍后再拨");
                                    isInConversation = false;
                                    callServerClose();//呼叫客服关闭通话
                                }
                            }else {
                                timer.cancel();
                                ToastUtil.showSingleToast("对方拒绝你的邀请");
                                isInConversation = false;
                            }
                        }
                    });
                    break;
                case BUSY:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeConversation();
                            if (callType == 1) {
                                if (userIdLists.size() >= 1) {
                                    ToastUtil.showSingleToast("请稍后，正在发起下一轮呼叫");
                                    //轮呼前先把前一次呼叫的人员名单移除
                                    if (randomNum > -1) {
                                        userIdLists.remove(randomNum);
                                    }
                                    //轮呼客服
                                    wheelCall();
                                } else {
                                    ToastUtil.showSingleToast("对方正在通话中,稍后再呼叫");
                                    isInConversation = false;
                                    callServerClose();//呼叫客服关闭通话
                                }
                            } else if (callType == 2) {
                                //呼叫门口机关闭通话
                                callDoorClose();
                                ToastUtil.showSingleToast("对方正在通话中,稍后再呼叫");
                                isInConversation = false;
                            } else {
                                timer.cancel();
                                ToastUtil.showSingleToast("对方正在通话中,稍后再呼叫");
                                isInConversation = false;
                            }
                        }
                    });
                    break;
                case TIMEOUT:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeConversation();
                            if (callType == 1) {
                                if (userIdLists.size() >= 1) {
                                    ToastUtil.showSingleToast("请稍后，正在发起下一轮呼叫");
                                    //轮呼前先把前一次呼叫的人员名单移除
                                    if (randomNum > -1) {
                                        userIdLists.remove(randomNum);
                                    }
                                    //轮呼客服
                                    wheelCall();
                                } else {
                                    ToastUtil.showSingleToast("呼叫对方超时,请稍后再呼叫");
                                    isInConversation = false;
                                    callServerClose();//呼叫客服关闭通话
                                }
                            } else if (callType == 2) {
                                //呼叫门口机关闭通话
                                callDoorClose();
                                ToastUtil.showSingleToast("呼叫对方超时,请稍后再呼叫");
                                isInConversation = false;
                            } else {
                                timer.cancel();
                                ToastUtil.showSingleToast("呼叫对方超时,请稍后再呼叫");
                                isInConversation = false;
                            }
                        }
                    });
                    break;
                default:
                    break;
            }
        }

        //对方加入云对讲
        @Override
        public void onStreamReceived(RemoteStream remoteStream) {
            remoteStream.attach(mRemoteVideoView);//显示对方的视频流
            remoteStream.enableAudio(true);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (Constant.mediaPlayer01 != null) {
                        Constant.mediaPlayer01.stop();
                    }
                    if (Constant.mediaPlayer02 != null) {
                        Constant.mediaPlayer02.stop();
                    }
                    if (callType == 1) {
                        //呼叫客服 客服加入云对讲
                        callServerJoin();
                    } else if (callType == 2) {
                        //呼叫门口机 门口机加入云对讲
                        callDoorJoin();
                    } else if (callType == 3) {
                        //呼叫业主 业主加入云对讲
                        callOwnerJoin();
                    }
                }
            });
        }

        @Override
        public void onClosed() {
            Log.e(TAG, "onClosed");
            if (Constant.mediaPlayer01 != null) {
                Constant.mediaPlayer01.stop();
            }
            if (Constant.mediaPlayer02 != null) {
                Constant.mediaPlayer02.stop();
            }
            isInConversation = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    endTime = DateUtils.getCurrentTime_Today();//门口机关闭云对讲，记录结束时间，获取当前系统时间即可；
                    //门口机触发挂断电话，判断是否已经接听过，有开始时间，则已接听，保存对讲记录
                    if (startTime != null) {
                        if (callType == 1) {
                            saveTalkBackHistory(login_Token, proCode, uid, userName, remoteId, Constant.Information, startTime, endTime);
                        } else if (callType == 2) {
                            saveTalkBackHistory(login_Token, proCode, uid, userName, remoteId, Constant.Information, startTime, endTime);
                        }
                    }
                    Intent intent = new Intent(ConversationActivity.this, HomeActivity.class);
                    startActivity(intent);
                    closeConversation();
                    ToastUtil.showSingleToast("对讲已结束");
                    finish();
                    mWiddogTalkTime.stop();
                    mCallTalkTime.stop();
                }
            });
        }

        @Override
        public void onError(final WilddogVideoError wilddogVideoError) {
            // 41007 表示超时,在接受前表示呼叫超时,在接受后表示对方异常退出
            if (wilddogVideoError != null && 41007 == wilddogVideoError.getErrCode()) {
                Log.e(TAG, "onError: "+wilddogVideoError.getErrCode() );
                if (isInConversation) {
                    // 处理异常退出逻辑
                } else {
                    // 处理超时逻辑
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isInConversation) {
                            ToastUtil.showSingleToast("对方异常退出,等待超时");
                            closeConversation();
                            if(callType == 1){
                                callServerClose();//呼叫客服关闭通话
                            } else if(callType == 2){
                                callServerClose();//呼叫门口机关闭通话
                            } else if(callType == 3){
                                callServerClose();//呼叫业主关闭通话
                            }

                        } else {
                            ToastUtil.showSingleToast("对方呼叫超时,本端未作响应");
                            closeConversation();
                            if(callType == 1){
                                callServerClose();//呼叫客服关闭通话
                            } else if(callType == 2){
                                callServerClose();//呼叫门口机关闭通话
                            } else if(callType == 3){
                                callServerClose();//呼叫业主关闭通话
                            }
                            if (Constant.mediaPlayer01 != null) {
                                Constant.mediaPlayer01.stop();
                            }
                            if (Constant.mediaPlayer02 != null) {
                                Constant.mediaPlayer02.stop();
                            }
                        }
                        Log.e("error", wilddogVideoError.getMessage());
                    }
                });
                if (isInConversation) {
                    isInConversation = false;
                }
            } else {
                // 其他类型错误
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                          ToastUtil.showSingleToast("通话中出错,请查看日志");
                        Log.e("error", wilddogVideoError.getMessage());
                    }
                });
                if (isInConversation) {
                    isInConversation = false;
                }
            }
        }
    };

    //AlertDialog列表 被呼叫监听
    private WilddogVideoCall.Listener inviteListener = new WilddogVideoCall.Listener() {
        @Override
        public void onCalled(final Conversation conversation, String s) {
            if (!TextUtils.isEmpty(s)) {
                //ToastUtil.showSingleToast("对方邀请时候携带的信息是：" + s);
                Log.e("s", s);
                try {
                    JSONObject jsobj = new JSONObject(s);
                    int type = jsobj.getInt("type");
                    Constant.Information = jsobj.getString("name");
                    if (type == 1 || type == 3) {
                        mWiddogOpendoorLayout.setVisibility(View.GONE);//隐藏开门按钮
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            Constant.conversation = conversation;
            Constant.conversation.setConversationListener(conversationListener);
            Constant.conversation.setStatsListener(statsListener);
            Constant.mediaPlayer02.setLooping(true);
            Constant.mediaPlayer02.start();

            Intent myIntent = new Intent(ConversationActivity.this, HomeActivity.class);
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Intent intent = new Intent(ConversationActivity.this, ConversationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            Intent[] intents = {myIntent, intent};
            startActivities(intents);
            ConversationActivity.this.finish();
        }

        @Override
        public void onTokenError(WilddogVideoError wilddogVideoError) {
            // 处理token过期
            if (wilddogVideoError.getErrCode() == 41001) {
                WilddogAuth.getInstance().signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> var1) {
                        if (var1.isSuccessful()) {
                            String token = var1.getResult().getWilddogUser().getToken(false).getResult().getToken();
                            WilddogVideoInitializer.getInstance().setToken(token);
                            final String uid = var1.getResult().getWilddogUser().getUid();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put(uid, userName);
                            SyncReference userRef = WilddogSync.getInstance().getReference(proCode + "/user");
                            userRef.goOnline();
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

    //拨打门口机云对讲
    private void callDoorTalk(String participantId) {
        inviteToConversation(participantId, 2);
        frameLayout.setVisibility(View.GONE);
        mWiddogClosecameraLayout.setVisibility(View.GONE);
        mWiddogSwitchcameraLayout.setVisibility(View.GONE);
        mWaitCall.setVisibility(View.GONE);
        mDoorName.setText("单元门口机");
        Constant.Information = "单元门口机";
        //detach();//停止播放视频流
        Constant.mediaPlayer02.setLooping(true);
        Constant.mediaPlayer02.start();
    }

    //门口机加入云对讲
    private void callDoorJoin() {
        timer.cancel();
        mWaitCall.setVisibility(View.GONE);
        mWiddogTalkTime.setBase(SystemClock.elapsedRealtime());//计时器清零
        int hour = (int) ((SystemClock.elapsedRealtime() - mWiddogTalkTime.getBase()) / 1000 / 60);
        mWiddogTalkTime.setFormat("0" + String.valueOf(hour) + ":%s");
        mWiddogTalkTime.start();
        if (Constant.mediaPlayer01 != null) {
            Constant.mediaPlayer01.stop();
        }
        if (Constant.mediaPlayer02 != null) {
            Constant.mediaPlayer02.stop();
        }
        remoteId = Constant.conversation.getRemoteUid();//接通后，获取对方的id
        if (remoteId.contains("door") || remoteId.contains("wall")) {
            doorID = remoteId.substring(remoteId.lastIndexOf("-") + 1);
            Log.e("doorID", doorID);
            frameLayout.setVisibility(View.GONE);
            mWiddogClosecameraLayout.setVisibility(View.GONE);
            mWiddogSwitchcameraLayout.setVisibility(View.GONE);
            detach();
        } else if (remoteId.contains("staffweb") || remoteId.contains("user")) {
            mWiddogOpendoorLayout.setVisibility(View.GONE);//隐藏开门按钮
        }
        startTime = DateUtils.getCurrentTime_Today();//门口机加入云对讲，记录开始时间，获取当前系统时间即可；
    }

    //呼叫门口机关闭云对讲
    private void callDoorClose() {
        if (Constant.mediaPlayer01 != null) {
            Constant.mediaPlayer01.stop();
        }
        if (Constant.mediaPlayer02 != null) {
            Constant.mediaPlayer02.stop();
        }
        timer.cancel();
        finish();
    }

    //拨打客服云对讲
    private void callServerTalk() {
        if (userIdLists.size() != 0) {
            //inviteToConversation(userIdLists.get(0), 1);
            wheelCall();
            //呼叫web客服
            mAnswerBtnLiner.setVisibility(View.GONE);
            mCallBtnLiner.setVisibility(View.VISIBLE);
            mDoorName.setText("客服人员");
            Constant.Information = "客服人员";
            Constant.mediaPlayer02.setLooping(true);
            Constant.mediaPlayer02.start();
            mWaitJoinTalkText.setText("正在等待对方接听云对讲");
        } else {
            timer.cancel();
            ToastUtil.showSingleToast("暂无客服在线");
            finish();
        }
    }

    //客服加入云对讲
    private void callServerJoin() {
        timer.cancel();
//        mWaitJoinTalkText.setVisibility(View.GONE);
//        mCallTalkTimeLiner.setVisibility(View.VISIBLE);
        mWaitCall.setVisibility(View.GONE);
        mWiddogOpendoorLayout.setVisibility(View.GONE);//隐藏开门按钮
        mWiddogTalkTime.setBase(SystemClock.elapsedRealtime());//计时器清零
        int hour = (int) ((SystemClock.elapsedRealtime() - mCallTalkTime.getBase()) / 1000 / 60);
        mWiddogTalkTime.setFormat("0" + String.valueOf(hour) + ":%s");
        mWiddogTalkTime.start();
        remoteId = Constant.conversation.getRemoteUid();//接通后，获取对方的id
        startTime = DateUtils.getCurrentTime_Today();//客服加入云对讲，记录开始时间，获取当前系统时间即可；
    }

    //呼叫客服关闭云对讲
    private void callServerClose() {
        if (Constant.mediaPlayer01 != null) {
            Constant.mediaPlayer01.stop();
        }
        if (Constant.mediaPlayer02 != null) {
            Constant.mediaPlayer02.stop();
        }
        timer.cancel();
        finish();
    }

    //拨打业主云对讲
    private void callOwnerTalk() {
        inviteToConversation("123-user-15107962485", 3);
        //呼叫业主
        mAnswerBtnLiner.setVisibility(View.GONE);
        mCallBtnLiner.setVisibility(View.VISIBLE);
        Constant.mediaPlayer02.setLooping(true);
        Constant.mediaPlayer02.start();
        mWaitJoinTalkText.setText("正在等待对方接听云对讲");
    }

    //业主加入云对讲
    private void callOwnerJoin() {
        timer.cancel();
        mWaitCall.setVisibility(View.GONE);
        mWiddogTalkTime.setBase(SystemClock.elapsedRealtime());//计时器清零
        int hour = (int) ((SystemClock.elapsedRealtime() - mWiddogTalkTime.getBase()) / 1000 / 60);
        mWiddogTalkTime.setFormat("0" + String.valueOf(hour) + ":%s");
        mWiddogTalkTime.start();
        if (Constant.mediaPlayer01 != null) {
            Constant.mediaPlayer01.stop();
        }
        if (Constant.mediaPlayer02 != null) {
            Constant.mediaPlayer02.stop();
        }
    }

    //轮呼
    private void wheelCall() {
        if (callType == 1) {
            //轮呼客服 随机取值
            Random random = new Random();
            if (userIdLists.size() != 0) {
                randomNum = random.nextInt(userIdLists.size());
                Log.e("n", "n= " + randomNum);
                Log.e("n", "list.get(n)= " + userIdLists.get(randomNum));
                inviteToConversation(userIdLists.get(randomNum), 1);
            } else {
                //轮呼全部人员，无人接听 自动挂断
                ToastUtil.showSingleToast("您拨打的客服正忙，请稍后再拨");
                if (Constant.mediaPlayer01 != null) {
                    Constant.mediaPlayer01.stop();
                }
                if (Constant.mediaPlayer02 != null) {
                    Constant.mediaPlayer02.stop();
                }
                inviteCancel();
            }
        } else if (callType == 2) {

        }
    }

    //创建音视频流
    private void createAndShowLocalStream() {
        LocalStreamOptions.Builder builder = new LocalStreamOptions.Builder();
        LocalStreamOptions options = builder.dimension(LocalStreamOptions.Dimension.DIMENSION_480P).build();
        //创建本地视频流，通过video对象获取本地视频流
        localStream = LocalStream.create(options);
        //开启音频/视频，设置为 false 则关闭声音或者视频画面
        localStream.enableAudio(true);
        localStream.enableVideo(true);
        mLocalVideoView.setCameraDistance(90);
        //为视频流绑定播放控件
        localStream.attach(mLocalVideoView);
    }

    //初始化视频展示控件
    private void initVideoRender() {
        //初始化视频展示控件位置，大小
        mLocalVideoView.setZOrderMediaOverlay(true);
        mLocalVideoView.setMirror(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_invite) {//用户列表
            invite();
        } else if (i == R.id.widdog_invite_cancel_layout) {//挂断
            Intent intent = new Intent(ConversationActivity.this,HomeActivity.class);
            startActivity(intent);
            timer.cancel();//关闭倒计时
            endTime = DateUtils.getCurrentTime_Today();
            mWiddogTalkTime.stop();
            mCallTalkTime.stop();
            if (Constant.mediaPlayer01 != null) {
                Constant.mediaPlayer01.stop();
            }
            if (Constant.mediaPlayer02 != null) {
                Constant.mediaPlayer02.stop();
            }
            inviteCancel();
            //判断用户是否接听，接听就保存对讲记录
            if (startTime != null) {
                if (callType == 1) {
                    //挂断客服通话，保存记录
                    saveTalkBackHistory(login_Token, proCode, uid, userName, remoteId, "客服人员", startTime, endTime);
                } else if (callType == 2) {
                    //挂断门口机通话，保存记录
                    saveTalkBackHistory(login_Token, proCode, uid, userName, remoteId, "单元门口机", startTime, endTime);
                } else {
                    saveTalkBackHistory(login_Token, proCode, uid, userName, remoteId, Constant.Information, startTime, endTime);
                }
            }
        } else if (i == R.id.widdog_voice_layout) {//静音
            mic();
        } else if (i == R.id.widdog_opendoor_layout) {//开门
            if (remoteId != null) {
                if (remoteId.contains("door")) {
                    //点击开门之后，需要等待门口机回应开门成功或者失败，才能再次点击开门
                    mWiddogOpendoorLayout.setEnabled(false);
                    //增加节点，实现电信号开门
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put(remoteId, true);
                    //添加openDoor节点，设置doorId的值为true
                    doorRef = WilddogSync.getInstance().getReference(proCode + "/openDoor/");
                    //手动恢复连接，开启自动重连。
                    doorRef.goOnline();
                    doorRef.updateChildren(map);
                    doorRef.child(remoteId).onDisconnect().removeValue();
                    doorRef.addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            //初始化监听或有新增子节点。
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            //	子节点数据发生更改
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            //子节点被删除。
                            Boolean openDoor = (Boolean) dataSnapshot.child(remoteId).getValue();
                            Toast.makeText(ConversationActivity.this, "已为您开门", Toast.LENGTH_SHORT).show();
                            mWiddogOpendoorLayout.setEnabled(true);
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                            //子节点排序发生变化。
                        }

                        @Override
                        public void onCancelled(SyncError syncError) {
                            //初始化监听或向指定节点及子节点数据发生变化
                        }
                    });

                    //远程网络开门
                    openDoorByCall();
                } else {
                    ToastUtil.showSingleToast("没有与门口机或单元机连接");
                }
            } else {
                ToastUtil.showSingleToast("没有与门口机或单元机连接");
            }
        } else if (i == R.id.widdog_closecamera_layout) {//关闭摄像头
            detach();
        } else if (i == R.id.widdog_switchcamera_layout) {//切换摄像头
            switchCamera();
        } else if (i == R.id.widdog_refuse) {//拒绝接听
            if (mNotificationManager != null) {
                mNotificationManager.cancel(1);
            }
            talkFlag = false;
            timer.cancel();
            if (Constant.conversation != null) {
                Constant.conversation.reject();
            }
            if (Constant.mediaPlayer01 != null) {
                Constant.mediaPlayer01.stop();
            }
            if (Constant.mediaPlayer02 != null) {
                Constant.mediaPlayer02.stop();
            }
            mWiddogTalkTime.stop();
            mCallTalkTime.stop();
            finish();
        } else if (i == R.id.widdog_answer) {//接听
            //startTime = DateUtils.getCurrentTime_Today();Constant
            if (mNotificationManager != null) {
                mNotificationManager.cancel(1);
            }

            talkFlag = false;
            timer.cancel();
            mWaitCall.setVisibility(View.GONE);
            mWiddogTalkTime.setBase(SystemClock.elapsedRealtime());//计时器清零
            int hour = (int) ((SystemClock.elapsedRealtime() - mWiddogTalkTime.getBase()) / 1000 / 60);
            mWiddogTalkTime.setFormat("0" + String.valueOf(hour) + ":%s");
            mWiddogTalkTime.start();
            //Constant.mediaPlayer01.stop();
            if (Constant.mediaPlayer01 != null) {
                Constant.mediaPlayer01.stop();
            }
            if (Constant.mediaPlayer02 != null) {
                Constant.mediaPlayer02.stop();
            }
            if (Constant.conversation != null) {
                Constant.conversation.setConversationListener(conversationListener);
                remoteId = Constant.conversation.getRemoteUid();//接通后，获取门口机/围墙机的id
                if (remoteId.contains("door") || remoteId.contains("wall")) {
                    doorID = remoteId.substring(remoteId.lastIndexOf("-") + 1);
                    Log.e("doorID", doorID);
                    frameLayout.setVisibility(View.GONE);
                    mWiddogClosecameraLayout.setVisibility(View.GONE);
                    mWiddogSwitchcameraLayout.setVisibility(View.GONE);
                    detach();
                } else if (remoteId.contains("staffweb") || remoteId.contains("user")) {
                    mWiddogOpendoorLayout.setVisibility(View.GONE);//隐藏开门按钮
                }
                conversationAlertDialogMap.remove(Constant.conversation);
                Constant.conversation.accept(localStream);//被叫方接受主叫方的呼叫
                isInConversation = true;
            }
        } else if (i == R.id.call_widdog_refuse) {//呼叫别人挂断
            if (Constant.mediaPlayer01 != null) {
                Constant.mediaPlayer01.stop();
            }
            if (Constant.mediaPlayer02 != null) {
                Constant.mediaPlayer02.stop();
            }
            mWiddogTalkTime.stop();
            mCallTalkTime.stop();
            timer.cancel();//关闭倒计时
            inviteCancel();//挂断

        } else if (i == R.id.call_widdog_answer) {//呼叫别人打开扬声器

        }
    }

    //用户列表
    public void invite() {
        showLoginUsers();
    }

    //关闭音频
    public void mic() {
        if (localStream != null) {
            isAudioEnable = !isAudioEnable;
            localStream.enableAudio(isAudioEnable);
        }
        if (isMic == 0) {
            //开启静音
            mWiddogVoiceImageview.setBackgroundResource(R.drawable.widdog_closevoice);
            isMic = 1;
        } else if (isMic == 1) {
            //关闭静音
            mWiddogVoiceImageview.setBackgroundResource(R.drawable.widdog_voice);
            isMic = 0;
        }
    }

    //挂断
    public void inviteCancel() {
        closeConversation();
        finish();
    }

    //停止播放
    public void detach() {
        if (localStream != null) {
            if (isDetach == 0) {
                localStream.enableVideo(false);
                isDetach = 1;
            } else if (isDetach == 1) {
                localStream.enableVideo(true);
                isDetach = 0;
            }
        }
    }

    //切换摄像头
    public void switchCamera() {
        if (localStream != null) {
            localStream.switchCamera();
        }
    }

    //关闭连接
    private void closeConversation() {
        timer.cancel();
        if (Constant.conversation != null) {
            Constant.conversation.close();
            Constant.conversation = null;
        }
    }

    //显示用户列表
    private void showLoginUsers() {
        startActivityForResult(new Intent(ConversationActivity.this, UserListActivity.class), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //选取用户列表中的用户，获得其 Wilddog UID
            String participant = data.getStringExtra("participant");
            //调用inviteToConversation 方法发起会话
            inviteToConversation(participant, 1);
            //mBtnInviteTxt.setText("用户列表");
        }
    }

    //呼叫方法
    private void inviteToConversation(String participant, int type) {
        /**
         *"type":1 , // 1:呼叫客服 2：呼叫门口机 3：业主
         "isVideo":true, //  true视频对讲， false 语音对讲
         "role":"室内机", // 角色：安管主任、门岗、项目经理、工程、业主、室内机
         "name":indoorInfoModel.name // 名字
         }
         */
        String data = "{\"type\":\"" + type + "\",\"name\":\"" + userName + "\",\"isVideo\":true,\"role\":\"业主\"}";
        //创建连接参数对象
        Constant.conversation = video.call(participant, localStream, data);
        Constant.conversation.setConversationListener(conversationListener);
        Constant.conversation.setStatsListener(statsListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //需要离开会话时调用此方法，并做资源释放和其他自定义操作
        if (mLocalVideoView != null) {
            mLocalVideoView.release();
            mLocalVideoView = null;
        }
        if (mRemoteVideoView != null) {
            mRemoteVideoView.release();
            mRemoteVideoView = null;
        }
        if (Constant.conversation != null) {
            Constant.conversation.close();
        }
        if (localStream != null) {
            if (!localStream.isClosed()) {
                localStream.close();
            }
        }
    }

    /**
     * 设置等待接听时间30s,未接听则自动挂断
     */
    private int recLen = 30;
    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            runOnUiThread(new Runnable() {      // UI thread
                @Override
                public void run() {
                    if (callType == 1) {
                        recLen--;
                        Log.e("recLen", "" + recLen);
                        if (recLen < 0) {
                            timer.cancel();
                        }
                    } else {
                        recLen--;
                        Log.e("recLen", "" + recLen);
                        if (recLen < 0) {
                            timer.cancel();
                            if (Constant.mediaPlayer01 != null) {
                                Constant.mediaPlayer01.stop();
                            }
                            if (Constant.mediaPlayer02 != null) {
                                Constant.mediaPlayer02.stop();
                            }
                            finish();
                        }
                    }
                }
            });
        }
    };

    //保存云对讲历史记录
    private void saveTalkBackHistory(String token, String projectCode, String callingUid, String callingName,
                                     String calledUid, final String calledName, String createTime, String endTime) {
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.TALKHISTORYSAVE,
                HttpConnectionTools.HttpData("token", token, "projectCode", projectCode, "callingUid", callingUid, "callingName", callingName,
                        "calledUid", calledUid, "calledName", calledName, "createTime", createTime, "endTime", endTime), new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if (errorCode == 200) {
                                handler.sendEmptyMessage(200);
                            } else if (errorCode == 201) {
                                handler.sendEmptyMessage(201);
                            } else if (errorCode == 405) {
                                handler.sendEmptyMessage(405);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        handler.sendEmptyMessage(500);
                    }
                });
    }

    //对讲时通过网络远程开门
    private void openDoorByCall() {
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.OPENDOORBYCALL,
                HttpConnectionTools.HttpData("id", Integer.parseInt(doorID), "token", login_Token),
                new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        //成功
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if (errorCode == 200) {
                                handler.sendEmptyMessage(0x200);
                            } else if (errorCode == 201) {
                                handler.sendEmptyMessage(0x201);
                            } else if (errorCode == 111) {
                                handler.sendEmptyMessage(0x111);
                            } else if (errorCode == -1) {
                                handler.sendEmptyMessage(-1);
                            } else if (errorCode == -2) {
                                handler.sendEmptyMessage(-2);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        //失败
                        handler.sendEmptyMessage(500);
                    }
                });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x200) {
                Log.e("200", "开门成功");
            } else if (msg.what == 0x201) {
                Log.e("201", "系统异常，操作失败");
            } else if (msg.what == 0x111) {
                Log.e("111", "数据有误");
            } else if (msg.what == -1) {
                Log.e("-1", "token过期");
            } else if (msg.what == 500) {
            } else if (msg.what == -2) {
                Log.e("-2", "远程开门失败");
            }

            //保存云对讲历史记录
            if (msg.what == 200) {
                Log.e("200", "保存成功");
            } else if (msg.what == 201) {
                Log.e("201", "系统异常");
                ToastUtil.showSingleToast("保存失败");
            } else if (msg.what == 405) {
                Log.e("405", "房号，项目编号不匹配");
                ToastUtil.showSingleToast("房号与项目编号不匹配");
            } else if (msg.what == 500) {
                ToastUtil.showSingleToast("请检查网络");
            }

            if (msg.what == 900) {
                Log.e("TAG", staffList.size() + " +++ ");
                Log.e("TAG", userIdLists.size() + " ++++ ");
                allLists.add(userIdLists.retainAll(staffList));
                Log.e("TAG", userIdLists.size() + " ++ ");
                callServerTalk();//呼叫客服
            }else if (msg.what == 0x500){
                ToastUtil.showSingleToast("网络出错，请检查网络");
                if (Constant.mediaPlayer01 != null) {
                    Constant.mediaPlayer01.stop();
                }
                if (Constant.mediaPlayer02 != null) {
                    Constant.mediaPlayer02.stop();
                }
                inviteCancel();
                timer.cancel();//关闭倒计时
            }
        }
    };

    //单击返回键返回，不退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
            inviteCancel();
            if (Constant.mediaPlayer01 != null) {
                Constant.mediaPlayer01.stop();
            }
            if (Constant.mediaPlayer02 != null) {
                Constant.mediaPlayer02.stop();
            }
        }
        return true;
    }
}
