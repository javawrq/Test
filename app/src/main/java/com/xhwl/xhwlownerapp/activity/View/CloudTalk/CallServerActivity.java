package com.xhwl.xhwlownerapp.activity.View.CloudTalk;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wilddog.video.base.LocalStream;
import com.wilddog.video.base.LocalStreamOptions;
import com.wilddog.video.base.WilddogVideoError;
import com.wilddog.video.base.WilddogVideoInitializer;
import com.wilddog.video.base.WilddogVideoView;
import com.wilddog.video.call.CallStatus;
import com.wilddog.video.call.Conversation;
import com.wilddog.video.call.RemoteStream;
import com.wilddog.video.call.WilddogVideoCall;
import com.wilddog.video.call.stats.LocalStreamStatsReport;
import com.wilddog.video.call.stats.RemoteStreamStatsReport;
import com.wilddog.wilddogauth.WilddogAuth;
import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.net.Constant;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class CallServerActivity extends BaseActivity implements View.OnClickListener {

    private final static String TAG = "CallServerActivity";
    /**
     * 客服中心
     */
    private TextView mServerName;
    /**
     * 正在等待对方接受云对讲......
     */
    private TextView mServerCallState;
    private ImageView mServerWiddogRefuse;
    private ImageView mServerWiddogAnswer;
    private LinearLayout mServerWiddogAnswerLiner;
    private LinearLayout mWaitCall;

    DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private boolean isInConversation = false;
    private boolean isAudioEnable = true;
    private WilddogVideoCall video;
    private LocalStream localStream;
    private Conversation mConversation;
    private AlertDialog alertDialog;
    private Map<Conversation, AlertDialog> conversationAlertDialogMap;
    WilddogVideoView localView;
    WilddogVideoView remoteView;

    //单击返回键返回，不退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
        }
        return true;
    }

    private void initView() {
        mServerName = (TextView) findViewById(R.id.server_name);
        mServerCallState = (TextView) findViewById(R.id.server_call_state);
        mServerWiddogRefuse = (ImageView) findViewById(R.id.server_widdog_refuse);
        mServerWiddogRefuse.setOnClickListener(this);
        mServerWiddogAnswer = (ImageView) findViewById(R.id.server_widdog_answer);
        mServerWiddogAnswer.setOnClickListener(this);
        mServerWiddogAnswerLiner = (LinearLayout) findViewById(R.id.server_widdog_answer_liner);
        mWaitCall = (LinearLayout) findViewById(R.id.wait_call);
        remoteView = (WilddogVideoView) findViewById(R.id.call_server_remote_video_view);
        localView = (WilddogVideoView) findViewById(R.id.call_server_local_video_view);
    }

    private Conversation.StatsListener statsListener = new Conversation.StatsListener() {
        @Override
        public void onLocalStreamStatsReport(LocalStreamStatsReport localStreamStatsReport) {
            changeLocalData(localStreamStatsReport);
        }

        @Override
        public void onRemoteStreamStatsReport(RemoteStreamStatsReport remoteStreamStatsReport) {
            changeRemoteData(remoteStreamStatsReport);
        }
    };

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
                            Toast.makeText(CallServerActivity.this, "对方拒绝你的邀请", Toast.LENGTH_SHORT).show();
                            isInConversation = false;
                        }
                    });
                    break;
                case BUSY:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CallServerActivity.this, "对方正在通话中,稍后再呼叫", Toast.LENGTH_SHORT).show();
                            isInConversation = false;
                        }
                    });
                    break;
                case TIMEOUT:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CallServerActivity.this, "呼叫对方超时,请稍后再呼叫", Toast.LENGTH_SHORT).show();
                            isInConversation = false;
                        }
                    });
                    dismissDialog();
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onStreamReceived(RemoteStream remoteStream) {
            remoteStream.attach(remoteView);
            remoteStream.enableAudio(true);
        }

        @Override
        public void onClosed() {
            Log.e(TAG, "onClosed");
            dismissDialog();
            isInConversation = false;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    closeConversation();
                    Toast.makeText(CallServerActivity.this, "对方挂断", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onError(final WilddogVideoError wilddogVideoError) {
            // 41007 表示超时,在接受前表示呼叫超时,在接受后表示对方异常退出
            if (wilddogVideoError != null && 41007 == wilddogVideoError.getErrCode()) {
                if (isInConversation) {
                    // 处理异常退出逻辑
                } else {
                    // 处理超时逻辑
                    dismissDialog();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeConversation();
                        if (isInConversation) {
                            Toast.makeText(CallServerActivity.this, "对方异常退出,等待超时", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CallServerActivity.this, "对方呼叫超时,本端未作相应", Toast.LENGTH_SHORT).show();
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
                        closeConversation();
                        Toast.makeText(CallServerActivity.this, "通话中出错,请查看日志", Toast.LENGTH_SHORT).show();
                        Log.e("error", wilddogVideoError.getMessage());
                    }
                });
                if (isInConversation) {
                    isInConversation = false;
                }
            }
        }
    };
//    //AlertDialog列表
//    private WilddogVideoCall.Listener inviteListener = new WilddogVideoCall.Listener() {
//        @Override
//        public void onCalled(final Conversation conversation, String s) {
//            if (!TextUtils.isEmpty(s)) {
//                Toast.makeText(CallServerActivity.this, "对方邀请时候携带的信息是:" + s, Toast.LENGTH_SHORT).show();
//            }
//            mConversation = conversation;
//            mConversation.setConversationListener(conversationListener);
//            mConversation.setStatsListener(statsListener);
//            AlertDialog.Builder builder = new AlertDialog.Builder(CallServerActivity.this);
//            builder.setMessage("邀请你加入会话");
//            builder.setTitle("加入邀请");
//            builder.setNegativeButton("拒绝邀请", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    mConversation.reject();
//                }
//            });
//            builder.setPositiveButton("确认加入", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    conversationAlertDialogMap.remove(conversation);
//                    mConversation.accept(localStream);
//                    isInConversation = true;
//                }
//            });
//
//            alertDialog = builder.create();
//            alertDialog.setCanceledOnTouchOutside(false);
//            alertDialog.show();
//            conversationAlertDialogMap.put(conversation, alertDialog);
//        }
//
//        @Override
//        public void onTokenError(WilddogVideoError wilddogVideoError) {
//            // 处理token过期
//            if (wilddogVideoError.getErrCode() == 41001) {
//                WilddogAuth.getInstance().signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(Task<AuthResult> var1) {
//                        if (var1.isSuccessful()) {
//                            String token = var1.getResult().getWilddogUser().getToken(false).getResult().getToken();
//                            WilddogVideoInitializer.getInstance().setToken(token);
//                            final String uid = var1.getResult().getWilddogUser().getUid();
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    //tvUid.setText(uid);
//                                }
//                            });
//                            Map<String, Object> map = new HashMap<String, Object>();
//                            map.put(uid, true);
//                            SyncReference userRef = WilddogSync.getInstance().getReference("123/staffphone");
//                            userRef.goOnline();
//                            userRef.updateChildren(map);
//                            userRef.child(uid).onDisconnect().removeValue();
//                        }
//                    }
//                });
//            } else {
//                // token 无效
//                Toast.makeText(CallServerActivity.this, "token无效", Toast.LENGTH_SHORT).show();
//            }
//        }
//    };
    /**
     * 切换
     */
    private Button mBtnSwitchcamera;

    public void changeLocalData(final LocalStreamStatsReport localStats) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

            }
        });
    }

    public void changeRemoteData(final RemoteStreamStatsReport remoteStats) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });
    }

    public String convertToMB(BigInteger value) {
        float result = Float.parseFloat(String.valueOf(value)) / (1024 * 1024);
        return decimalFormat.format(result);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set window styles for fullscreen-window size. Needs to be done before
        // adding content.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams
                .FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View
                .SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_call_server);
        initView();
        //初始化Video
        WilddogVideoInitializer.initialize(getApplicationContext(), Constant.VIDEO_APPID, WilddogAuth.getInstance().getCurrentUser().getToken(false).getResult()
                .getToken());
        //获取video对象
        video = WilddogVideoCall.getInstance();
        video.start();

        initVideoRender();
        createAndShowLocalStream();
        conversationAlertDialogMap = new HashMap<>();
    }

    private void createAndShowLocalStream() {
        LocalStreamOptions.Builder builder = new LocalStreamOptions.Builder();
        LocalStreamOptions options = builder.dimension(LocalStreamOptions.Dimension.DIMENSION_480P).build();
        //创建本地视频流，通过video对象获取本地视频流
        localStream = LocalStream.create(options);
        //开启音频/视频，设置为 false 则关闭声音或者视频画面
        localStream.enableAudio(true);
        localStream.enableVideo(true);
        //为视频流绑定播放控件
        localStream.attach(localView);
    }

    //初始化视频展示控件
    private void initVideoRender() {
        //初始化视频展示控件位置，大小
        localView.setZOrderMediaOverlay(true);
        localView.setMirror(true);
    }

    private void inviteToConversation(String participant) {
        String data = "0801业主";
        //创建连接参数对象
        mConversation = video.call(participant, localStream, data);
        mConversation.setConversationListener(conversationListener);
        mConversation.setStatsListener(statsListener);
    }

    private void dismissDialog() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    private void closeConversation() {
        if (mConversation != null) {
            mConversation.close();
            mConversation = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //需要离开会话时调用此方法，并做资源释放和其他自定义操作
        if (localView != null) {
            localView.release();
            localView = null;
        }
        if (remoteView != null) {
            remoteView.release();
            remoteView = null;
        }
        if (mConversation != null) {
            mConversation.close();
        }
        if (localStream != null) {
            if (!localStream.isClosed()) {
                localStream.close();
            }
        }
        // video 断开与服务端链接,将无法收到请求,需要重新 start 因为video在本界面会重新初始化故释放资源
        video.stop();
        // 将写入数据实时引擎的数据用户在线状态数据移除
        //WilddogSync.getInstance().goOffline();
    }

    //切换摄像头
    public void switchCamera() {
        if (localStream != null) {
            localStream.switchCamera();
        }
    }

    //挂断
    public void inviteCancel() {
        closeConversation();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.server_widdog_refuse:
                //挂断
                inviteCancel();
                break;
            case R.id.server_widdog_answer:
                //扬声器
                break;
        }
    }
}
