package com.xhwl.xhwlownerapp.activity.View.CloudTalk;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wilddog.client.ChildEventListener;
import com.wilddog.client.SyncReference;
import com.wilddog.client.WilddogSync;
import com.wilddog.wilddogauth.WilddogAuth;
import com.xhwl.xhwlownerapp.Entity.TalkEntity.DoorInfoList;
import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.Receiver.NetWorkStateReceiver;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;

import java.util.ArrayList;
import java.util.List;


public class UserListActivity extends BaseActivity {

    private SyncReference mRef;
    private List<String> doorLists = new ArrayList<>();
    private ChildEventListener childEventListener;
    private String mUid;
    private String participantId;
    private MyAdapter adapter;
    private ListView mLvUserList;
    private String proCode,token,roomCode;
    private DoorInfoList doorInfo;
    private List<DoorInfoList> doorInfoLists = new ArrayList<>();
    private List<String> doorInfoListUid = new ArrayList<>();
    private List<DoorInfoList> doorInfoList = new ArrayList<>();
    private int callType = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        initView();
        //显示项目下所有的用户列表
        proCode = SPUtils.get(this,"proCode","");
        token = SPUtils.get(this,"userToken","");
        mRef = WilddogSync.getInstance().getReference(proCode+"/door");
        mUid = WilddogAuth.getInstance().getCurrentUser().getUid();
        mLvUserList = (ListView) findViewById(R.id.lv_user_list);
        initDate();
//        //获取野狗门口机在线列表
//        childEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                if (dataSnapshot != null) {
//                    //获取用户Wilddog ID并添加到用户列表中
//                    String uid = dataSnapshot.getKey();
//                    if (!mUid.equals(uid)) {
//                        userList.add(uid);
//                    }
//                    initDate();
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                if (dataSnapshot != null) {
//                    //用户离开时，从用户列表中删除用户数据
//                    String uid = dataSnapshot.getKey();
//                    if (!mUid.equals(uid)) {
//                        userList.remove(uid);
//                    }
//                }
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(SyncError wilddogError) {
//
//            }
//        };
//        mRef.addChildEventListener(childEventListener);
    }

    private void initDate() {
        //筛选出该房号下在线的门口机
        doorInfoListUid.retainAll(doorLists);
        if(doorInfoListUid.size()>0){
            //监听door节点
            for (int i= 0;i<doorInfoListUid.size();i++){
                for (int j = 0;j<doorInfoLists.size();j++){
                    if(doorInfoLists.get(j).getUid().equals(doorInfoListUid.get(i))){
                        doorInfo = new DoorInfoList();
                        doorInfo.setUid(doorInfoLists.get(j).getUid());
                        doorInfo.setProjectCode(doorInfoLists.get(j).getProjectCode());
                        doorInfo.setPathId(doorInfoLists.get(j).getPathId());
                        doorInfo.setMachineName(doorInfoLists.get(j).getMachineName());
                        doorInfo.setId(doorInfoLists.get(j).getId());
                        doorInfoList.add(doorInfo);
                    }
                }
            }
            adapter = new MyAdapter(doorInfoList, UserListActivity.this);
            mLvUserList.setAdapter(adapter);
        }else {
            showToast("该房号下暂无在线门口机设备");
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mRef.removeEventListener(childEventListener);
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

    private void initView() {
        doorInfoLists = (List<DoorInfoList>) getIntent().getSerializableExtra("doorInfoLists");
        doorInfoListUid = (List<String>) getIntent().getSerializableExtra("doorInfoListUid");
        doorLists =  (List<String>) getIntent().getSerializableExtra("doorLists");
        callType = getIntent().getIntExtra("callType",0);
        mLvUserList = (ListView) findViewById(R.id.lv_user_list);
    }

    class MyAdapter extends BaseAdapter {
        private List<DoorInfoList> mList = new ArrayList<>();
        private LayoutInflater mInflater;

        MyAdapter(List<DoorInfoList> userList, Context context) {
            mList = userList;
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int i) {
            return mList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            view = mInflater.inflate(R.layout.layout_participent_list, null);
            TextView doorName = view.findViewById(R.id.talk_doorname);
            ImageView callBtn =  view.findViewById(R.id.talk_door_callbtn);
            doorName.setText(mList.get(i).getMachineName());
            callBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    participantId = mList.get(i).getUid();
                    Intent intent = new Intent(UserListActivity.this,ConversationActivity.class);
                    intent.putExtra("participantId", participantId);
                    intent.putExtra("callType",callType);
                    startActivity(intent);
                    finish();
                }
            });
            return view;
        }
    }

    //单击返回键返回，不退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
        }
        return true;
    }

}
