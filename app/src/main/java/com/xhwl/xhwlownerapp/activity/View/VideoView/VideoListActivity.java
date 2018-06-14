package com.xhwl.xhwlownerapp.activity.View.VideoView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.SDKConstant;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.hikvision.sdk.net.bean.SubResourceParam;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.UIUtils.VideoConstants;
import com.xhwl.xhwlownerapp.activity.View.VideoView.Adapter.VideoListAdapter;
import com.zhy.autolayout.AutoLinearLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class VideoListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private AutoLinearLayout mTopBack;
    private TextView mTopTitle;
    private TextView mTopBtn;
    private ImageView mTopRecord;
    private ListView mVideoList;
    private String nodeCode;
    private String nodeType;
    private VideoListAdapter adapter;
    /**
     * 发送消息的对象
     */
    private ViewHandler mHandler = null;
    private boolean isChild = false;

    private List<String> mData = new ArrayList<>();

    /**
     * 资源源数据
     */
    private ArrayList<Object> mSource = new ArrayList<>();
    /**
     * 关闭加载进度条
     */
    public static final int CANCEL_LOADING_PROGRESS = 1;
    /**
     * 加载成功
     */
    public static final int LOADING_SUCCESS = 2;
    /**
     * 加载失败
     */
    public static final int LOADING_FAILED = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);
        initView();
    }

    /***
     * UI处理Handler
     */
    private static class ViewHandler extends Handler {

        private final WeakReference<VideoListActivity> mActivity;

        ViewHandler(VideoListActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case CANCEL_LOADING_PROGRESS:
                    Log.e("UIUils","加载完毕");
                    break;
                case LOADING_SUCCESS:
                    Log.e("UIUils","取消");
                    mActivity.get().adapter.notifyDataSetChanged();
                    break;
                case LOADING_FAILED:
                   // UIUtils.cancelProgressDialog();
                    Log.e("UIUils","加载失败");
                    //UIUtils.showToast(mActivity.get(), R.string.loading_failed);
                    break;
            }
        }
    }

    private void initView() {
        mHandler = new ViewHandler(this);
        nodeType = SPUtils.get(VideoListActivity.this,"nodeType","");
        nodeCode = SPUtils.get(VideoListActivity.this,"nodeID","");

        Log.e("nodeType",nodeType+nodeCode+"");
        adapter = new VideoListAdapter(mData,this);
        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopTitle.setText("云瞳监控列表");
        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mTopRecord = (ImageView) findViewById(R.id.top_record);
        mVideoList = (ListView) findViewById(R.id.video_list);
        mVideoList.setOnItemClickListener(this);

        if("".equals(nodeCode) || "".equals(nodeType) || nodeCode == "null" ||nodeCode == null ){
            showToast("请先选择项目");
            finish();
        }else {
            int parentNodeType = Integer.parseInt(nodeType);
            int pId = Integer.parseInt(nodeCode);
            Log.e("parentNodeType",parentNodeType+pId+"");

            getSubResourceList(parentNodeType, pId);
        }
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

    //监控点的点击事件
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        int nodeType;
        final Object node = mSource.get(position);
        // 请求此item的下级资源
        //下面这些代码是原来选择两级目录的，现在是直接把选择一级父目录去掉了，直接选择二级目录，选择播放点播放。
        if (node instanceof SubResourceNodeBean) {
            nodeType = ((SubResourceNodeBean) node).getNodeType();
            if (SDKConstant.NodeType.TYPE_CAMERA_OR_DOOR == nodeType) {
                // 构造camera对象
                final SubResourceNodeBean cameraData = (SubResourceNodeBean) node;
                int isOnline = cameraData.getIsOnline();
                //goLive(cameraData);
                if (isOnline == 1) {

                    if (VMSNetSDK.getInstance().checkLivePermission(cameraData)) {
                        goLive(cameraData);//开启预览
                    } else {
                        showToast("没有权限");
                        //UIUtils.showToast(VideoListActivity.this, R.string.no_permission);
                    }
                } else {
                    showToast("设备不在线");
                    //UIUtils.showToast(VideoListActivity.this, R.string.device_not_online);
                }
            } else {
                // 请求此item的下级资源
                isChild = true;
                getSubResourceList(((SubResourceNodeBean) node).getNodeType(), ((SubResourceNodeBean) node).getId());
                Log.e("二级目录",((SubResourceNodeBean) node).getNodeType()+"====="+((SubResourceNodeBean) node).getId());
            }
        }
    }

    /**
     * 获取父节点资源列表
     *
     * @param parentNodeType 父节点类型
     * @param pId            父节点ID
     */
    private void getSubResourceList(int parentNodeType, int pId) {
//        UIUtils.showLoadingProgressDialog(this, R.string.loading_process_tip);
        VMSNetSDK.getInstance().getSubResourceList(1, 999, SDKConstant.SysType.TYPE_VIDEO, parentNodeType, String.valueOf(pId), new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                super.onFailure();
                mHandler.sendEmptyMessage(LOADING_FAILED);
            }

            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (obj instanceof SubResourceParam) {
                    List<SubResourceNodeBean> list = ((SubResourceParam) obj).getNodeList();
                    mData.clear();
                    mSource.clear();
                    if (list != null && list.size() > 0) {
                        for (SubResourceNodeBean bean : list) {
                            mData.add(bean.getName());
                            mSource.add(bean);
                        }
                        mHandler.sendEmptyMessage(LOADING_SUCCESS);

                        if (adapter != null) {
                            if (isChild) {//true點擊的是子節點
                                adapter.notifyDataSetChanged();
                            } else {
                                mVideoList.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                } else {
                    mHandler.sendEmptyMessage(LOADING_FAILED);
                }
            }
        });

    }
    /**
     * 预览监控点
     *
     * @param subResourceNodeBean 监控点资源
     */
    private void goLive(SubResourceNodeBean subResourceNodeBean) {
        if (subResourceNodeBean != null) {
            Intent it = new Intent(this, LiveActivity.class);
            it.putExtra(VideoConstants.IntentKey.CAMERA, subResourceNodeBean);
            startActivity(it);
        } else {
            showToast("数据为空");
            //UIUtils.showToast(this, R.string.empty);
        }
    }

    //单击返回，不退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
        }
        return true;
    }
}
