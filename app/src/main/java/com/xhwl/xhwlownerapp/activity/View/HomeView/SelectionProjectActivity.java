package com.xhwl.xhwlownerapp.activity.View.HomeView;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.wilddog.video.call.WilddogVideoCall;
import com.xhwl.xhwlownerapp.Entity.ProjectEntity.Project;
import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.MyAPP;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.activity.View.HomeView.SelectProAdapter.SelectProjectAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择项目
 */
public class SelectionProjectActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ImageView mSelectProjctBack;
    private ListView mSelectProjectList;
    private SelectProjectAdapter adapter;
    private Project project;
    private List<Project> projectList = new ArrayList<>();
    private String result;
    private JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_project);
        initView();
        seletPro();
    }

    private void initView() {
        result = SPUtils.get(this,"result","");
        mSelectProjctBack = (ImageView) findViewById(R.id.select_projct_back);
        mSelectProjctBack.setOnClickListener(this);
        mSelectProjectList = (ListView) findViewById(R.id.select_project_list);
        mSelectProjectList.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.select_projct_back:
                finish();
                break;
        }
    }

    private void seletPro(){
        //获取项目名
        try {
            jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("projectList");
            for (int i = 0; i < jsonArray.length(); i++) {//遍历JSONArray
                project = new Project();
                JSONObject oj = jsonArray.getJSONObject(i);
                project.setProName(oj.getString("name"));
                project.setCode(oj.getString("code"));
                project.setDivisionName(oj.getString("divisionName"));
                project.setProjectCode(oj.getString("projectCode"));
                project.setNodeID(oj.getString("nodeID"));
                project.setNodeType(oj.getString("nodeType"));
                project.setProId(oj.getString("id"));
                project.setEntranceCode(oj.getString("entranceCode"));
                project.setIsWorkstation(oj.getBoolean("isWorkstation"));
                projectList.add(project);
            }
            adapter = new SelectProjectAdapter(projectList,SelectionProjectActivity.this);
            mSelectProjectList.setAdapter(adapter);
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        //切换项目时候云对讲下线
        MyAPP.userRef.goOffline();
        if (WilddogVideoCall.getInstance() != null) {
            WilddogVideoCall.getInstance().stop();
        }

        showToast("选择了"+projectList.get(i).getProName());
        SPUtils.put(SelectionProjectActivity.this,"proCode",projectList.get(i).getProjectCode());
        SPUtils.put(SelectionProjectActivity.this,"proName",projectList.get(i).getProName());
        SPUtils.put(SelectionProjectActivity.this,"nodeType",projectList.get(i).getNodeType());
        SPUtils.put(SelectionProjectActivity.this,"nodeID",projectList.get(i).getNodeID());
        SPUtils.put(SelectionProjectActivity.this,"proID",projectList.get(i).getProId());
        SPUtils.put(SelectionProjectActivity.this,"entranceCode",projectList.get(i).getEntranceCode());
        SPUtils.put(SelectionProjectActivity.this,"isWorkstation",projectList.get(i).getIsWorkstation());
        startToAIctivity(HomeActivity.class);
        finish();
    }
}
