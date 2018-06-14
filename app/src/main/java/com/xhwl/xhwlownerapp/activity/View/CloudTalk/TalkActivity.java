package com.xhwl.xhwlownerapp.activity.View.CloudTalk;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wilddog.client.ChildEventListener;
import com.wilddog.client.DataSnapshot;
import com.wilddog.client.SyncError;
import com.wilddog.client.SyncReference;
import com.wilddog.client.WilddogSync;
import com.wilddog.video.base.LocalStream;
import com.wilddog.video.base.WilddogVideoView;
import com.wilddog.video.call.Conversation;
import com.wilddog.video.call.WilddogVideoCall;
import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogauth.core.Task;
import com.wilddog.wilddogauth.core.listener.OnCompleteListener;
import com.wilddog.wilddogauth.core.result.AuthResult;
import com.xhwl.xhwlownerapp.Entity.TalkEntity.DoorInfoList;
import com.xhwl.xhwlownerapp.Entity.TalkEntity.Talk;
import com.xhwl.xhwlownerapp.Entity.TalkEntity.TalkingHistory;
import com.xhwl.xhwlownerapp.Entity.TalkEntity.UserList;
import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.Receiver.NetWorkStateReceiver;
import com.xhwl.xhwlownerapp.Service.MyService;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.MyAPP;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.UIUtils.StringUtils;
import com.xhwl.xhwlownerapp.UIUtils.ToastUtil;
import com.xhwl.xhwlownerapp.activity.View.CloudTalk.Adapter.TalkBackAdapter;
import com.xhwl.xhwlownerapp.net.Constant;
import com.xhwl.xhwlownerapp.net.HttpConnectionInter;
import com.xhwl.xhwlownerapp.net.HttpConnectionTools;
import com.zhy.autolayout.AutoFrameLayout;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TalkActivity extends BaseActivity implements View.OnClickListener {

    private AutoLinearLayout mTopBack;
    private TextView mTopTitle;
    private ImageView mTopRecord;
    /**
     * 点我选择业主
     */
    private TextView mTalkSelectOwner;
    /**
     * 呼叫
     */
    private Button mTalkCallOwnerBtn;
    /**
     * 客服中心
     */
    private Button mCallCustomerService;
    /**
     * 项目经理
     */
    private Button mCallProjectManager;
    /**
     * 单元门口机
     */
    private Button mCallDoorway;
    /**
     * 安管主任
     */
    private Button mCallSafetyDirector;
    private ListView mTalkHositoryList;
    private AutoLinearLayout mTalkLocationLinear;

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
    private AutoLinearLayout mTalkLiner;
    private Chronometer mCallServerWiddogTalkTime;
    private AutoLinearLayout mCallServerWiddogTalkTimeLiner;
    private AutoFrameLayout mCallServerWiddogFrame;
    private AutoRelativeLayout mCallServerFullscreenContent;
    private AutoRelativeLayout mCallRelat;
    /**
     * 请选择所在房号
     */
    private TextView mTalkLocation;
    private int callType;
    private ChildEventListener childLis;
    private List<String> staffList = new ArrayList<>();//野狗节点监听数据
    private List<UserList> userLists = new ArrayList<>();//后台返回数据
    private UserList userList;//后台返回数据实体类

    //呼叫门口机
    private DoorInfoList doorInfo;
    private String proCode, token,userName,userPhone;
    private String roomId, roomName, roomCode;
    private List<DoorInfoList> doorInfoLists = new ArrayList<>();
    private List<String> doorInfoListUid= new ArrayList<>();
    private List<DoorInfoList> doorInfoList = new ArrayList<>();
    private List<String> doorLists = new ArrayList<>();

    private Talk talk = new Talk();
    private List<TalkingHistory> list = new ArrayList<>();
    private TalkingHistory talkHistory;
    private TalkBackAdapter adapter;
    private boolean flag = true;//处理在短时间内多次点击同一组件
    private synchronized void setFlag() {
        flag = false;
    }
    private NetWorkStateReceiver netWorkStateReceiver;
    private boolean isInlogin = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
        initView();
    }

    private void initView() {
        token = SPUtils.get(TalkActivity.this, "userToken", "");
        proCode = SPUtils.get(TalkActivity.this, "proCode", "");
        userName = SPUtils.get(TalkActivity.this,"userName","");
        userPhone = SPUtils.get(this, "userTelephone", "");
        roomId = SPUtils.get(TalkActivity.this, "roomId", "");
        roomName = SPUtils.get(TalkActivity.this, "roomName", "");
        roomCode = SPUtils.get(TalkActivity.this, "roomCode", "");
        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopTitle.setText("云对讲");
        mTopRecord = (ImageView) findViewById(R.id.top_record);
        mTopRecord.setOnClickListener(this);
        mTalkSelectOwner = (TextView) findViewById(R.id.talk_select_owner);
        mTalkSelectOwner.setOnClickListener(this);
        mTalkCallOwnerBtn = (Button) findViewById(R.id.talk_call_owner_btn);
        mTalkCallOwnerBtn.setOnClickListener(this);
        mCallCustomerService = (Button) findViewById(R.id.call_customer_service);
        mCallCustomerService.setOnClickListener(this);
        mCallProjectManager = (Button) findViewById(R.id.call_project_manager);
        mCallProjectManager.setOnClickListener(this);
        mCallDoorway = (Button) findViewById(R.id.call_doorway);
        mCallDoorway.setOnClickListener(this);
        mCallSafetyDirector = (Button) findViewById(R.id.call_safety_director);
        mCallSafetyDirector.setOnClickListener(this);
        mTalkHositoryList = (ListView) findViewById(R.id.talk_hository_list);
        mTalkLocationLinear = (AutoLinearLayout) findViewById(R.id.talk_location_linear);
        mTalkLocationLinear.setOnClickListener(this);
        mTalkLocation = (TextView) findViewById(R.id.talk_location);
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
        mTalkLiner = (AutoLinearLayout) findViewById(R.id.talk_liner);
        mCallServerWiddogTalkTime = (Chronometer) findViewById(R.id.call_server_widdog_talk_time);
        mCallServerWiddogTalkTimeLiner = (AutoLinearLayout) findViewById(R.id.call_server_widdog_talk_time_liner);
        mCallServerWiddogFrame = (AutoFrameLayout) findViewById(R.id.call_server_widdog_frame);
        mCallServerFullscreenContent = (AutoRelativeLayout) findViewById(R.id.call_server_fullscreen_content);
        mCallRelat = (AutoRelativeLayout) findViewById(R.id.call_relat);
        if(roomName != ""){
            mTalkLocation.setText(roomName);
        }
        if(roomId != ""){
            talkDoorUID(token,roomId);//获取当前房号下的门口机
        }
        setCallListener("staffweb");
        setCallListener("door");
        getTalkingBackHistory(token,proCode,"500","1");//获取对讲记录
        getTokenLogin(proCode + "-user-" + userPhone);
    }
//
    @Override
    protected void onRestart() {
        super.onRestart();
        roomId = SPUtils.get(TalkActivity.this, "roomId", "");
        roomName = SPUtils.get(TalkActivity.this, "roomName", "");
        roomCode = SPUtils.get(TalkActivity.this, "roomCode", "");
        if(roomName != ""){
            mTalkLocation.setText(roomName);
        }
        if(roomId != ""){
            talkDoorUID(token,roomId);//获取当前房号下的门口机
        }
    }

    /**
     * 初始化云对讲ID
     *
     * @param uid
     */
    public void getTokenLogin(String uid) {
        String url = Constant.HOST2 + Constant.SERVERNAME + Constant.WILDDOGGETTOKEN + uid;
        OkHttpClient mOkHttpClient = new OkHttpClient();
        Request request = (new okhttp3.Request.Builder()).url(url).build();
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
                Log.e("error", "error");
            }

            public void onResponse(Call call, Response response) throws IOException {
                String htmlStr = response.body().string();

                try {
                    JSONObject js = new JSONObject(htmlStr);
                    String result = js.getString("result");
                    js = new JSONObject(result);
                    String talkToken = js.getString("token");
                    login(talkToken, proCode);
                } catch (JSONException var6) {
                    var6.printStackTrace();
                }
            }
        });
    }

    public void login (String token, final String proCode) {
        if (!isInlogin) {
            isInlogin = true;
            MyAPP.auth = WilddogAuth.getInstance();
            MyAPP.auth.signInWithCustomToken(token).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String uid = MyAPP.auth.getCurrentUser().getUid();
                        Map<String, Object> map = new HashMap();
                        //map.put(uid, Boolean.valueOf(true));
                        map.put(uid, userName);
                        MyAPP.userRef = WilddogSync.getInstance().getReference(proCode + "/user");
                        SyncReference var1 = MyAPP.userRef;
                        SyncReference.goOnline();
                        MyAPP.userRef.updateChildren(map);
                        MyAPP.userRef.child(uid).onDisconnect().removeValue();
                        if (!TextUtils.isEmpty(uid)) {
                            isInlogin = false;
                            Intent intent = new Intent(TalkActivity.this, MyService.class);
                            startService(intent);
                        }
                    } else {
                        Log.e("error", task.getException().getMessage());
                        showToast("云对讲登录失败!");
                        isInlogin = false;
                    }
                }
            });
        }
    }

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
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            default:
                break;
            case R.id.top_back:
                //返回
                finish();
                break;
            case R.id.top_record:
                //好友列表
                ToastUtil.showSingleToast("暂未开放，敬请期待");
                break;
            case R.id.talk_select_owner:
                //选择业主
                ToastUtil.showSingleToast("暂未开放，敬请期待");
                break;
            case R.id.talk_call_owner_btn:
                //呼叫业主
                ToastUtil.showSingleToast("暂未开放，敬请期待");
//                callType = 3;
//                intent.setClass(TalkActivity.this,ConversationActivity.class);
//                intent.putExtra("callType",callType);
//                startActivity(intent);
                break;
            case R.id.call_customer_service:
                //呼叫客服;
                if (flag) {
                    Log.e("TAG", staffList.size() + " == ");
                    if (staffList.size() != 0) {
                        callType = 1;
                        intent.setClass(TalkActivity.this, ConversationActivity.class);
                        intent.putExtra("callType", callType);
                        intent.putExtra("staffList", (Serializable) staffList);
                        startActivity(intent);
                    }else {
                        ToastUtil.showSingleToast("暂无客服人员在线");
                    }
                    setFlag();
                    // do some things
                    new TimeThread().start();
                }
                break;
            case R.id.call_project_manager:
                //呼叫项目经理
                ToastUtil.showSingleToast("暂未开放，敬请期待");
                break;
            case R.id.call_doorway:
                //呼叫门口机
                if (flag) {
                    if(!StringUtils.isEmpty(roomName)){
                        callType = 2;
                        //showLoginUsers();
                        intent.setClass(TalkActivity.this,UserListActivity.class);
                        intent.putExtra("callType",callType);
                        intent.putExtra("doorInfoLists", (Serializable) doorInfoLists);
                        intent.putExtra("doorInfoListUid", (Serializable) doorInfoListUid);
                        intent.putExtra("doorLists", (Serializable) doorLists);
                        startActivity(intent);
                    }else {
                        ToastUtil.showSingleToast("请选择房号");
                    }
                    setFlag();
                    // do some things
                    new TimeThread().start();
                }
                break;
            case R.id.call_safety_director:
                //呼叫安管人员
                ToastUtil.showSingleToast("暂未开放，敬请期待");
                break;
            case R.id.talk_location_linear:
                //选择当前所在的房号
                startToAIctivity(LocationRoomActivity.class);
                break;

            case R.id.server_widdog_refuse:
                //挂断
                mTalkLiner.setVisibility(View.VISIBLE);
                break;
            case R.id.server_widdog_answer:
                //扬声器
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

    //显示用户列表
    private void showLoginUsers() {
        startActivityForResult(new Intent(TalkActivity.this, UserListActivity.class), 0);
    }

    //获取客服wilddog在线节点
    private void setCallListener(String node) {
        MyAPP.userRef = WilddogSync.getInstance().getReference(proCode + "/" + node);
        childLis = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String uid = dataSnapshot.getKey();
                String value = dataSnapshot.getValue().toString();
                if (uid.contains("staffweb")) {
                    if (!staffList.contains(uid)) {
                        staffList.add(uid);
                    }
                }else if(uid.contains("door")){
                    doorLists.add(uid);
                }
//
//                //ToastUtil.showCenter(MainActivity.this, "新增节点：" + dataSnapshot.getValue().toString());
//                if (uid.equals(proCode + "-door-" + doorId)) {
//                    openDoorManager.openDoor(MainActivity.this, 5);//开门
//                } else if (uid.contains("staff") && !value.equals("true")) {
//                    if (!staffList.contains(uid)) {
//                        staffList.add(uid);
//                    }
//                } else if (uid.contains("user") || uid.contains("indoor")) {
//                    if (!userList.contains(uid)) {
//                        userList.add(uid);
//                    }
//                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    //用户离开时，从用户列表中删除用户数据
                    String uid = dataSnapshot.getKey();
                    if (uid.contains("staffweb")) {
                        staffList.remove(uid);
                    } else if (uid.contains("door")) {
                        doorLists.remove(uid);
                    }
                }
                //ToastUtil.showCenter(MainActivity.this, "删除节点：" + dataSnapshot.getKey().toString());
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(SyncError syncError) {
            }
        };
        MyAPP.userRef.addChildEventListener(childLis);
    }

    //获取当前房号下的门口机设备
    private void talkDoorUID(String token, String roomId) {
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.GETDOORLIST,
                HttpConnectionTools.HttpData("token", token, "roomId", roomId), new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            String result = jsonObject.getString("result");
                            jsonObject = new JSONObject(result);
                            JSONArray jsonArray = jsonObject.getJSONArray("doorList");
                            if(doorInfoLists.size() == 0){
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    doorInfo = new DoorInfoList();
                                    jsonObject = jsonArray.getJSONObject(i);
                                    doorInfo.setId(jsonObject.getInt("id"));
                                    doorInfo.setMachineName(jsonObject.getString("machineName"));
                                    doorInfo.setPathId(jsonObject.getString("pathId"));
                                    doorInfo.setProjectCode(jsonObject.getString("projectCode"));
                                    doorInfo.setUid(jsonObject.getString("uid"));
                                    doorInfoLists.add(doorInfo);
                                    doorInfoListUid.add(jsonObject.getString("uid"));
                                }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyAPP.userRef.removeEventListener(childLis);
    }

    private void getTalkingBackHistory(String token,String projectCode,String pageSize,String pageNumber){
        HttpConnectionTools.HttpServler(Constant.HOST2 +Constant.SERVERNAME+Constant.INTERFACEVERSION + Constant.GETHISTORY,
                HttpConnectionTools.HttpData("token", token, "projectCode", projectCode,
                        "pageSize", pageSize, "pageNumber", pageNumber), new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if(errorCode == 200){
                                String result = jsonObject.getString("result");
                                jsonObject = new JSONObject(result);
                                String pageInfo = jsonObject.getString("pageInfo");
                                JSONArray jsonArray = jsonObject.getJSONArray("rows");
                                jsonObject = new JSONObject(pageInfo);
                                talk.setTotal(jsonObject.getInt("totalCount"));
                                talk.setPageSize(jsonObject.getInt("pageSize"));
                                talk.setPageNumber(jsonObject.getInt("pageNumber"));
                                for (int j = 0;j<jsonArray.length();j++){
                                    JSONObject jobject = jsonArray.getJSONObject(j);
                                    talkHistory = new TalkingHistory();
                                    talkHistory.setCallType(jobject.getString("callType"));
                                    talkHistory.setId(jobject.getInt("id"));
                                    talkHistory.setName(jobject.getString("name"));
                                    talkHistory.setUid(jobject.getString("uid"));
                                    list.add(talkHistory);
                                }
                                handler.sendEmptyMessage(200);
                            }else if(errorCode == 201){
                                handler.sendEmptyMessage(201);
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

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 200){
                adapter = new TalkBackAdapter(list,TalkActivity.this);
                mTalkHositoryList.setAdapter(adapter);
                int totalHeight = 0;                                    // 定义、初始化listview总高度值
                for (int i = 0; i < adapter.getCount(); i++) {
                    View listItem = adapter.getView(i, null, mTalkHositoryList);          // 获取单个item
                    listItem.setLayoutParams(new AutoLinearLayout.LayoutParams(
                            AutoLinearLayout.LayoutParams.WRAP_CONTENT, AutoLinearLayout.LayoutParams.WRAP_CONTENT));// 设置item高度为适应内容
                    listItem.measure(0, 0);                                        // 测量现在item的高度
                    totalHeight += listItem.getMeasuredHeight();                   // 总高度增加一个listitem的高度
                }
                ViewGroup.LayoutParams params = mTalkHositoryList.getLayoutParams();
                params.height = totalHeight + (mTalkHositoryList.getDividerHeight() * (adapter.getCount() - 1)); // 将分割线高度加上总高度作为最后listview的高度
                mTalkHositoryList.setLayoutParams(params);
            }else if(msg.what == 201){
                ToastUtil.showSingleToast("获取失败");
            }else if(msg.what == 500){
                ToastUtil.showSingleToast("请检查网络");
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
