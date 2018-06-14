package com.xhwl.xhwlownerapp.activity.View.CloudTalk;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.xhwl.xhwlownerapp.Entity.HouseEntiy.RoomInfo;
import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.activity.View.CloudTalk.Adapter.LocationRoomAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocationRoomActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ImageView mLosctionRoomBack;
    private ListView mLosctionRoomList;
    private List<RoomInfo> list = new ArrayList<>();
    private String proName,result;
    private JSONObject jsonObject;
    //解析门禁需要
    private JSONObject obj1,obj2,obj3,obj4;
    //单元名字
    private String unitListName = null;
    //楼栋名字
    private String sysBuildingName = null;
    //项目
    private String sysProjectName = null;
    private LocationRoomAdapter adapter;
    private RoomInfo roomInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_room);
        initView();
        initDate();
    }

    private void initView() {
        mLosctionRoomBack = (ImageView) findViewById(R.id.losction_room_back);
        mLosctionRoomBack.setOnClickListener(this);
        mLosctionRoomList = (ListView) findViewById(R.id.losction_room_list);
        mLosctionRoomList.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.losction_room_back:
                finish();
                break;
        }
    }
    //初始化数据
    private void initDate() {
        result = SPUtils.get(this,"result","");
        proName = SPUtils.get(this,"proName","");
        Log.e("result",result+"  ");
        getHouseInfo();//获取该项目下的所有房号
    }
    //获取房产信息
    private  void getHouseInfo(){
        try {
            jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("roomList");
            //JSONArray jsonArray = jsonObject.getJSONArray("unitList");
            for (int i = 0;i<jsonArray.length();i++) {
                obj1 = jsonArray.getJSONObject(i);
                String sysUnit = obj1.getString("sysUnit");

                obj2 = new JSONObject(sysUnit);
                String sysBuilding = obj2.getString("sysBuilding");

                obj3 = new JSONObject(sysBuilding);
                String sysProject = obj3.getString("sysProject");

                obj4 = new JSONObject(sysProject);
                sysProjectName = obj4.getString("name");//项目
                // remotionEntity.setSysProjectName(sysProjectName);
                //根据所选的项目名来获取该项目下的门禁列表
                if(proName.equals(sysProjectName)){

                    String roomId = obj1.getString("id");
                    String roomCode = obj1.getString("code");
                    String roomName = obj1.getString("name");
                    roomInfo = new RoomInfo(roomCode,roomName,roomId);
                    list.add(roomInfo);
                }
            }
            adapter = new LocationRoomAdapter(list,LocationRoomActivity.this);
            mLosctionRoomList.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        showToast("选择了"+list.get(i).getName());
        SPUtils.put(LocationRoomActivity.this,"roomId",list.get(i).getId());
        SPUtils.put(LocationRoomActivity.this,"roomCode",list.get(i).getCode());
        SPUtils.put(LocationRoomActivity.this,"roomName",list.get(i).getName());
        finish();
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
