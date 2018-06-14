package com.xhwl.xhwlownerapp.activity.View.UserInfoView.ResidentialInfo;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.Entity.HouseEntiy.House;
import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.activity.View.UserInfoView.ResidentialInfo.Adapter.ResidentialAdapter;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyResidentialActivity extends BaseActivity implements View.OnClickListener {

    private AutoLinearLayout mTopBack;
    private TextView mTopTitle;
    private ListView mResidentialList;
    private List<House> list = new ArrayList<>();
    private House house;
    private ResidentialAdapter adapter;
    private String result,proName;
    private JSONObject jsonObject;
    //解析门禁需要
    private JSONObject obj1,obj2,obj3,obj4;
    //单元名字
    private String unitListName = null;
    //楼栋名字
    private String sysBuildingName = null;
    //项目
    private String sysProjectName = null;
    //房号
    private String roomName = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_residential);
        initView();
        initDate();
        getHouseInfo();//获取房产信息
    }

    //初始化数据
    private void initDate() {
        result = SPUtils.get(this,"result","");
        proName = SPUtils.get(this,"proName","");
        Log.e("result",result+"  ");
    }

    private void initView() {
        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopTitle.setText("我的房产");
        mResidentialList = (ListView) findViewById(R.id.residential_list);
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
        }
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
                    unitListName = obj2.getString("name");//单元

                    sysBuildingName = obj3.getString("name");//楼栋

                    roomName = obj1.getString("name");//房号
                    house = new House(proName,sysBuildingName,unitListName,roomName);
                    list.add(house);
                }
            }
            adapter = new ResidentialAdapter(list,this);
            mResidentialList.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
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
