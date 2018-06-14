package com.xhwl.xhwlownerapp.activity.View.HomeView.HomeFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.HttpConstants;
import com.hikvision.sdk.net.bean.LoginData;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.utils.SDKUtil;
import com.xhwl.xhwlownerapp.Entity.ProjectEntity.Project;
import com.xhwl.xhwlownerapp.Entity.VideoEntity.TempDatas;
import com.xhwl.xhwlownerapp.Entity.Weather.WeatherClass;
import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.CircleUIUtil.UpCircleMenuLayout;
import com.xhwl.xhwlownerapp.UIUtils.DateUtils;
import com.xhwl.xhwlownerapp.UIUtils.MD5Utils;
import com.xhwl.xhwlownerapp.UIUtils.MyAPP;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.UIUtils.ShowToast;
import com.xhwl.xhwlownerapp.UIUtils.ToastUtil;
import com.xhwl.xhwlownerapp.UIUtils.VideoConstants;
import com.xhwl.xhwlownerapp.activity.View.BackgroundMusic.MusicServiceActivity;
import com.xhwl.xhwlownerapp.activity.View.BackgroundMusic.entity.MediaDevice;
import com.xhwl.xhwlownerapp.activity.View.BluetoothOpenDoor.OpenDoor.MatchDoorVo;
import com.xhwl.xhwlownerapp.activity.View.CloudTalk.TalkActivity;
import com.xhwl.xhwlownerapp.activity.View.GrantCard.DoorCardManagerActivity;
import com.xhwl.xhwlownerapp.activity.View.GrantCard.VisitorInvitationActivity;
import com.xhwl.xhwlownerapp.activity.View.HomeView.SelectionProjectActivity;
import com.xhwl.xhwlownerapp.activity.View.LoginView.LoginActivity;
import com.xhwl.xhwlownerapp.activity.View.VideoView.VideoListActivity;
import com.xhwl.xhwlownerapp.net.Constant;
import com.xhwl.xhwlownerapp.net.HttpConnectionInter;
import com.xhwl.xhwlownerapp.net.HttpConnectionTools;
import com.zhy.autolayout.AutoLinearLayout;
import com.zyao89.view.zloading.ZLoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import static com.zyao89.view.zloading.Z_TYPE.SINGLE_CIRCLE;


/**
 * 我的小区
 */
public class HomeResidentialFragment extends Fragment implements View.OnClickListener, UpCircleMenuLayout.OnMenuItemClickListener {

    private static final String TAG = "HomeResidentialFragment";
    private View view;
    private ImageView mResidentialLocationImg;
    /**
     * 中海华庭
     */
    private TextView mResidentialLocation;
    private AutoLinearLayout mResidentialLocationLinear;
    private ImageView mResidentialSanQrcode;
    /**
     * 18℃
     */
    private TextView mResidentialTemp;
    /**
     * 小雨
     */
    private TextView mResidentialWeather;
    /**
     * 在所有的天气里我最喜欢晴天,在所有物是人非中我最喜欢你
     */
    private TextView mResidentialMood;
    //定位到城市
    private String city;
    private double Longitude, Latitude;
    private WeatherClass weatherClass = new WeatherClass();
    private JSONArray jsonArray;
    private JSONObject jsonObject;
    public String maxAndmin;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private String term, Code, description;

    private AutoLinearLayout mResidentialCloudtlakLinear;
    private AutoLinearLayout mResidentialVideoLinear;
    private AutoLinearLayout mResidentialVisitorLinear;
    private AutoLinearLayout mResidentialDoorcardLinear;
    private AutoLinearLayout mResidentialTravelrecordLinear;
    private AutoLinearLayout mResidentialMusicLinear;
    //项目名、项目编号
    private String proName;
    private String proCode;
    private Intent intent;
    //云瞳需要的参数
    private String nodeType;
    private String nodeCode;
    private String entranceCode;
    private String token;
    /**
     * 登录成功
     */
    public static final int LOGIN_SUCCESS = 1;
    /**
     * 登录失败
     */
    public static final int LOGIN_FAILED = 2;
    /***
     * SharedPreferences
     */
    private static SharedPreferences sharedPreferences;
    private static ViewHandler mHandler;
    /**
     * 圆形菜单
     */
    private UpCircleMenuLayout mIdMymenulayout;

    /***
     * UI处理Handler
     */
    private static class ViewHandler extends Handler {
        private final WeakReference<Context> mActivity;

        ViewHandler(Context activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOGIN_SUCCESS:
                    // 登录成功
                    Log.e("LOGIN_SUCCESS", "成功");
                    break;
                case LOGIN_FAILED:
                    // 登录失败
                    ShowToast.show("云瞳登录失败");
                    break;
                default:
                    break;
            }
        }
    }

    //背景音乐
    private MediaDevice mediaDevice;

    private String phone, username;

    private MatchDoorVo matchDoorVo = new MatchDoorVo();;
    private MatchDoorVo.Door door;
    private String doorID;
    private String doorName;
    private String doorPath;
    private String connectionKey;
    private String keyID;
    private MatchDoorVo.Door.OtherInfo otherInfo;
    private String doorTyp;
    private String openData;
    private boolean isWorkstation;//检测是否有工作站

    private String[] mItemTexts;
    private int[] mItemivs;

    private String roomList;//房产列表
    private String unitCode;//单元Code
    private String buildCode;//楼栋Code

    private String userType;//游客身份登录
    private String result;
    private JSONObject object;
    private Project project;
    private List<Project> projectList = new ArrayList<>();

    private boolean flag = true;//处理在短时间内多次点击同一组件

    private synchronized void setFlag() {
        flag = false;
    }

    private ZLoadingDialog loadingDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the rb_nobtn_selector for this fragment
        sharedPreferences = getActivity().getSharedPreferences(VideoConstants.APP_DATA, Context.MODE_PRIVATE);
        view = inflater.inflate(R.layout.fragment_home_residential, container, false);
        mHandler = new ViewHandler(getActivity());
        initView();//初始化id
        initDate();//初始化数据
        initWeather();//心情天气--获取定位
        return view;
    }

    private void initDate() {
        Log.e("initDate", "initDate");
        isWorkstation = SPUtils.get(getActivity(),"isWorkstation",true);
        userType = SPUtils.get(getActivity(), "userType", "");
        roomList = SPUtils.get(getActivity(), "roomList", "");
        username = SPUtils.get(getActivity(), "userName", "");
        phone = SPUtils.get(getActivity(), "userTelephone", "");
        token = SPUtils.get(getContext(), "userToken", "");
        proCode = SPUtils.get(getActivity(), "proCode", "");
        proName = SPUtils.get(getActivity(), "proName", "");
        nodeType = SPUtils.get(getActivity(), "nodeType", "");
        nodeCode = SPUtils.get(getActivity(), "nodeID", "");
        matchDoorVo.doorList = (List<MatchDoorVo.Door>) SPUtils.get(getActivity(),"matchDoorVoDoorList",matchDoorVo.doorList);
        if ("visitor".equals(userType)) {
            //游客身份登录
            mResidentialLocation.setText("游客，您好");
            setDefaultDoor();
        } else {
            if (proName != "") {
                mResidentialLocation.setText(proName);
            } else {
                mResidentialLocation.setText("请选择项目");
            }
            if (proCode != "") {
                if(isWorkstation){
                    getDoorByPhoneNew();//获取门禁列表
                }else {
                    getAllDoor();//获取全部门禁列表
                }
            }
        }
        loadingDialog = new ZLoadingDialog(getActivity());
    }

    private void setDefaultDoor() {
        //游客身份登录
        mItemTexts = new String[]{"东门","西门","正门","南门","北门","北门","南门","正门","西门","东门"};
        mItemivs = new int[]{R.drawable.dot_1,R.drawable.dot_1,R.drawable.dot_1,R.drawable.dot_1,R.drawable.dot_1,
               R.drawable.dot_1,R.drawable.dot_1,R.drawable.dot_1,R.drawable.dot_1,R.drawable.dot_1,};
        mIdMymenulayout.setMenuItemIconsAndTexts(mItemivs,mItemTexts);
    }

    private int size;
    //设置门禁列表
    private void setDoorList() {
        mIdMymenulayout.setTotal(10);
        size =  matchDoorVo.doorList.size();
        Log.e("size",size+" ");
        if (matchDoorVo.doorList.size() >= 10) {
            mItemTexts = new String[10];
            for (int i = 0; i < 10; i++) {
                mItemTexts[i] = matchDoorVo.doorList.get(i).getDoorName();
            }
        } else {
            //判断门禁数据小于10，则填空白。
            mItemTexts = new String[10];
            mItemivs = new int[10];
            for (int i = 0; i < 10 - size; i++) {
                mItemTexts[i] = "";
            }
            for (int i = 0; i < size ; i++) {
                mItemTexts[i + 10 - size] = matchDoorVo.doorList.get(i).getDoorName();
                mItemivs[i + 10 - size] = R.drawable.dot_1;
            }
        }
        mIdMymenulayout.setMenuItemIconsAndTexts(mItemivs,mItemTexts);
      }

    private void initView() {
        intent = new Intent();
        mediaDevice = new MediaDevice();

        mResidentialLocationImg = (ImageView) view.findViewById(R.id.residential_location_img);
        mResidentialLocation = (TextView) view.findViewById(R.id.residential_location);
        mResidentialLocationLinear = (AutoLinearLayout) view.findViewById(R.id.residential_location_linear);
        mResidentialLocationLinear.setOnClickListener(this);
        mResidentialSanQrcode = (ImageView) view.findViewById(R.id.residential_san_qrcode);
        mResidentialTemp = (TextView) view.findViewById(R.id.residential_temp);
        mResidentialWeather = (TextView) view.findViewById(R.id.residential_weather);
        mResidentialMood = (TextView) view.findViewById(R.id.residential_mood);
        mResidentialCloudtlakLinear = (AutoLinearLayout) view.findViewById(R.id.residential_cloudtlak_linear);
        mResidentialCloudtlakLinear.setOnClickListener(this);
        mResidentialVideoLinear = (AutoLinearLayout) view.findViewById(R.id.residential_video_linear);
        mResidentialVideoLinear.setOnClickListener(this);
        mResidentialVisitorLinear = (AutoLinearLayout) view.findViewById(R.id.residential_visitor_linear);
        mResidentialVisitorLinear.setOnClickListener(this);
        mResidentialDoorcardLinear = (AutoLinearLayout) view.findViewById(R.id.residential_doorcard_linear);
        mResidentialDoorcardLinear.setOnClickListener(this);
        mResidentialTravelrecordLinear = (AutoLinearLayout) view.findViewById(R.id.residential_travelrecord_linear);
        mResidentialTravelrecordLinear.setOnClickListener(this);
        mResidentialMusicLinear = (AutoLinearLayout) view.findViewById(R.id.residential_music_linear);
        mResidentialMusicLinear.setOnClickListener(this);
        mIdMymenulayout = (UpCircleMenuLayout) view.findViewById(R.id.id_mymenulayout);

        mIdMymenulayout.setOnMenuItemClickListener(this);
    }

    private void initWeather() {
        //天气
        //实例化LoctionClient定位对象
        mLocationClient = new LocationClient(getActivity());
        //注册监听事件
        mLocationClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);//打开gps
        option.setIsNeedAddress(true);//设置是否需要地址
        option.setCoorType("bd09ll");//设置坐标类型
        mLocationClient.setLocOption(option);
        mLocationClient.start();//启动定位
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            default:
                break;
            case R.id.residential_location_linear:
                //点击选择切换项目
                if (!"visitor".equals(userType)) {
                    intent.setClass(getContext(), SelectionProjectActivity.class);
                    startActivity(intent);
                } else {
                    ToastUtil.showSingleToast("小主，您还没有权限哟~^-^！");
                }
                break;
            case R.id.residential_cloudtlak_linear:
                //云对讲
                if (!"visitor".equals(userType)) {
                    if (proCode == "") {
                        ToastUtil.showSingleToast("请先选择项目");
                    } else {
                        intent.setClass(getActivity(), TalkActivity.class);
                        startActivity(intent);
                    }
                } else {
                    ToastUtil.showSingleToast("您还不是业主哦");
                }

                break;
            case R.id.residential_video_linear:
                //云瞳
                if (flag) {
                    if (!"visitor".equals(userType)) {
                        if (proCode.equals("123")) {
                            loginOpt();
                        } else {
                            ToastUtil.showSingleToast("当前所在项目尚未开放此功能，敬请期待");
                        }
                    } else {
                        ToastUtil.showSingleToast("您还不是业主哦");
                    }
                    setFlag();
                    // do some things
                    new TimeThread().start();
                }
                break;
            case R.id.residential_visitor_linear:
                loadingDialog.setLoadingBuilder(SINGLE_CIRCLE)//设置类型
                        .setLoadingColor(Color.BLUE)//颜色
                        .setHintText("Loading...")
                        .setHintTextSize(16) // 设置字体大小 dp
                        .setHintTextColor(Color.GRAY)  // 设置字体颜色
                        .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                        .setDialogBackgroundColor(Color.parseColor("#CCffffff")) // 设置背景色，默认白色
                        .show();
                //访客邀请
                if (!"visitor".equals(userType)) {
                    if (proCode == "") {
                        ToastUtil.showSingleToast("请先选择项目");
                        loadingDialog.dismiss();
                    } else {
                        //判断是否是在本部项目
                        if (proCode.equals("123")) {
                            intent.setClass(getActivity(), VisitorInvitationActivity.class);
                            startActivity(intent);
                            loadingDialog.dismiss();
                        } else {
                            ToastUtil.showSingleToast("小主，您的社区暂无设备支持哟~^-^！");
                            loadingDialog.dismiss();
                        }
                    }
                } else {
                    ToastUtil.showSingleToast("您还不是业主哦");
                    loadingDialog.dismiss();
                }
                break;
            case R.id.residential_doorcard_linear:
                loadingDialog.setLoadingBuilder(SINGLE_CIRCLE)//设置类型
                        .setLoadingColor(Color.BLUE)//颜色
                        .setHintText("Loading...")
                        .setHintTextSize(16) // 设置字体大小 dp
                        .setHintTextColor(Color.GRAY)  // 设置字体颜色
                        .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                        .setDialogBackgroundColor(Color.parseColor("#CCffffff")) // 设置背景色，默认白色z
                        .show();
                //门卡管理
                if (!"visitor".equals(userType)) {
                    if (proCode == "") {
                        loadingDialog.dismiss();
                        ToastUtil.showSingleToast("请先选择项目");
                    } else {
                        intent.setClass(getActivity(), DoorCardManagerActivity.class);
                        startActivity(intent);
                        loadingDialog.dismiss();
                    }
                } else {
                    ToastUtil.showSingleToast("您还不是业主哦");
                    loadingDialog.dismiss();
                }
                break;
            case R.id.residential_travelrecord_linear:
                //出行记录
                if (!"visitor".equals(userType)) {
                    ToastUtil.showSingleToast("暂未开放，敬请期待。");
                }
                break;
            case R.id.residential_music_linear:
                //背景音乐
                if (!"visitor".equals(userType)) {
                    if (proCode.equals("123")) {
                        intent.setClass(getActivity(), MusicServiceActivity.class);
                        startActivity(intent);
                    } else {
                        ToastUtil.showSingleToast("小主，您的社区暂无设备支持哟~^-^！");
                    }
                } else {
                    ToastUtil.showSingleToast("您还不是业主哦");
                }
                break;
        }
    }

    @Override
    public void itemClick(int pos) {
        //ToastUtil.showSingleToast(pos+"");
    }

    @Override
    public void itemCenterClick(View view, int position) {
        //ToastUtil.showSingleToast(position+"");
        if (flag) {
            //远程开门
            if (!"visitor".equals(userType)) {
                if(size>=10){
                    remoteOpenDoor(proCode, matchDoorVo.doorList.get(position).getDoorID(), "6");
                }else {
                    Log.e("itemCenterClick",(position)+" ");
                    Log.e("itemCenterClick",(position - (10-size) )+" ");
                    if(position - (10-size) >= 0 ){
                        Log.e("itemCenterClick",matchDoorVo.doorList.get(position  - (10-size)).getDoorName());
                        if(!"".equals(matchDoorVo.doorList.get(position - (10-size)).getDoorID())){
                            remoteOpenDoor(proCode, matchDoorVo.doorList.get(position - (10-size)).getDoorID(), "6");
                        }
                    }
                }
            } else {
                ToastUtil.showSingleToast("您还不是业主哦");
            }
            setFlag();
            // do some things
            new TimeThread().start();
        }
    }

    /**
     * 计时线程（防止在一定时间段内重复点击按钮）
     */
    private class TimeThread extends Thread {
        public void run() {
            try {
                Thread.sleep(2000);
                flag = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //获取授权门列表
    private void getDoor() {
        String time = DateUtils.getCurrentTime_Today_Min();
        String token = MD5Utils.encode(time + "adminXH");
        matchDoorVo = new MatchDoorVo();
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.GETDOORBYPHONE,
                HttpConnectionTools.HttpData("projectCode", proCode, "token", token,
                        "userName", username, "phone", phone), new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if (errorCode == 200) {
                                String result = jsonObject.getString("result");
                                jsonObject = new JSONObject(result);
                                matchDoorVo.openData = jsonObject.getString("openData");
                                JSONArray jsonArray = jsonObject.getJSONArray("doorList");
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    jsonObject = jsonArray.getJSONObject(j);
                                    doorID = jsonObject.getString("doorID");
                                    doorName = jsonObject.getString("doorName");
                                    doorPath = jsonObject.getString("doorPath");
                                    connectionKey = jsonObject.getString("connectionKey");
                                    keyID = jsonObject.getString("keyID");
                                    doorTyp = jsonObject.getString("doorTyp");

                                    String otherinfo = jsonObject.getString("otherInfo");
                                    if (otherinfo != "null") {
                                        jsonObject = new JSONObject(otherinfo);
                                        String password = jsonObject.getString("passWord");
                                        String mac = jsonObject.getString("mac");
                                        otherInfo = new MatchDoorVo.Door.OtherInfo(password, mac);
                                        door = new MatchDoorVo.Door(doorID, doorName, doorPath, connectionKey, keyID, doorTyp, otherInfo);
                                        matchDoorVo.doorList.add(door);
                                    } else {
                                        door = new MatchDoorVo.Door(doorID, doorName, doorPath, connectionKey, keyID, doorTyp);
                                        matchDoorVo.doorList.add(door);
                                    }
                                }
                                handler2.sendEmptyMessage(200);
                            } else if (errorCode == 201) {
                                handler2.sendEmptyMessage(201);
                            } else if (errorCode == 111) {
                                handler2.sendEmptyMessage(111);
                            } else if (errorCode == -1) {
                                handler2.sendEmptyMessage(-1);
                            } else if (errorCode == 2) {
                                handler2.sendEmptyMessage(2);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler2.sendEmptyMessage(500);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        handler2.sendEmptyMessage(500);
                    }
                });
    }

    //获取用户授权门禁列表（新）
    private void getDoorByPhoneNew(){
        String time = DateUtils.getTimes();
        String sign = MD5Utils.encode("userName="+username+"&phone="+phone+"&projectCode="+proCode+"&time="+time);
        matchDoorVo = new MatchDoorVo();
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.GETDOORBYPHONENEW,
                HttpConnectionTools.HttpData("projectCode", proCode, "sign", sign, "time", time,
                        "userName", username, "phone", phone), new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            Log.e(TAG, "onFinish: "+content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if (errorCode == 200) {
                                String result = jsonObject.getString("result");
                                SPUtils.put(getActivity(),"doorListResult",result);
                                jsonObject = new JSONObject(result);
                                matchDoorVo.openData = jsonObject.getString("openData");
                                JSONArray jsonArray = jsonObject.getJSONArray("doorList");
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    jsonObject = jsonArray.getJSONObject(j);
                                    doorID = jsonObject.getString("doorID");
                                    doorName = jsonObject.getString("doorName");
                                    doorPath = jsonObject.getString("doorPath");
                                    connectionKey = jsonObject.getString("connectionKey");
                                    keyID = jsonObject.getString("keyID");
                                    doorTyp = jsonObject.getString("doorTyp");

                                    String otherinfo = jsonObject.getString("otherInfo");
                                    if (otherinfo != "null") {
                                        jsonObject = new JSONObject(otherinfo);
                                        String password = jsonObject.getString("passWord");
                                        String mac = jsonObject.getString("mac");
                                        otherInfo = new MatchDoorVo.Door.OtherInfo(password, mac);
                                        door = new MatchDoorVo.Door(doorID, doorName, doorPath, connectionKey, keyID, doorTyp, otherInfo);
                                        matchDoorVo.doorList.add(door);
                                    } else {
                                        door = new MatchDoorVo.Door(doorID, doorName, doorPath, connectionKey, keyID, doorTyp);
                                        matchDoorVo.doorList.add(door);
                                    }
                                }
                                handler2.sendEmptyMessage(200);
                            } else if (errorCode == 201) {
                                handler2.sendEmptyMessage(201);
                            } else if (errorCode == 111) {
                                handler2.sendEmptyMessage(111);
                            } else if (errorCode == -1) {
                                handler2.sendEmptyMessage(-1);
                            } else if (errorCode == 2) {
                                handler2.sendEmptyMessage(2);
                            }else {
                                handler2.sendEmptyMessage(0);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler2.sendEmptyMessage(500);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        handler2.sendEmptyMessage(0x500);
                    }
                });
    }

    //获取全部门禁列表
    private void getAllDoor(){
        String time = DateUtils.getTimes();
        String sign = MD5Utils.encode("&projectCode="+proCode+"&time="+time);
        matchDoorVo = new MatchDoorVo();
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.GETDOORBYPROJECTCODE,
                HttpConnectionTools.HttpData("projectCode", proCode, "sign", sign, "time", time),
                new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if (errorCode == 200) {
                                String result = jsonObject.getString("result");
                                SPUtils.put(getActivity(),"doorListResult",result);
                                jsonObject = new JSONObject(result);
                                JSONArray jsonArray = jsonObject.getJSONArray("doorList");
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    jsonObject = jsonArray.getJSONObject(j);
                                    doorID = jsonObject.getString("doorID");
                                    doorName = jsonObject.getString("doorName");
                                    doorPath = jsonObject.getString("doorPath");
                                    connectionKey = jsonObject.getString("connectionKey");
                                    keyID = jsonObject.getString("keyID");
                                    doorTyp = jsonObject.getString("doorTyp");

                                    String otherinfo = jsonObject.getString("otherInfo");
                                    if (otherinfo != "null") {
                                        jsonObject = new JSONObject(otherinfo);
                                        String password = jsonObject.getString("passWord");
                                        String mac = jsonObject.getString("mac");
                                        otherInfo = new MatchDoorVo.Door.OtherInfo(password, mac);
                                        door = new MatchDoorVo.Door(doorID, doorName, doorPath, connectionKey, keyID, doorTyp, otherInfo);
                                        matchDoorVo.doorList.add(door);
                                    } else {
                                        door = new MatchDoorVo.Door(doorID, doorName, doorPath, connectionKey, keyID, doorTyp);
                                        matchDoorVo.doorList.add(door);
                                    }
                                }
                                handler2.sendEmptyMessage(200);
                            } else if (errorCode == 201) {
                                handler2.sendEmptyMessage(201);
                            } else if (errorCode == 111) {
                                handler2.sendEmptyMessage(111);
                            } else if (errorCode == -1) {
                                handler2.sendEmptyMessage(-1);
                            } else if (errorCode == 2) {
                                handler2.sendEmptyMessage(2);
                            }else {
                                handler2.sendEmptyMessage(0);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler2.sendEmptyMessage(500);
                        }
                    }

                    public void onError(Exception e) {
                        handler2.sendEmptyMessage(500);
                    }
                });
    }

    //业主远程开门
    private void remoteOpenDoor(String projectCode, String doorId, String type) {
        String token = MD5Utils.encode(DateUtils.getCurrentTime_Today_Min() + "adminXH");
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.REMOTEOPENDOOR,
                HttpConnectionTools.HttpData("projectCode", projectCode, "token", token,
                        "doorId", doorId, "type", type), new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if (errorCode == 200) {
                                handler.sendEmptyMessage(0x200);
                            } else if (errorCode == 201) {
                                handler.sendEmptyMessage(0x201);
                            } else if (errorCode == 111) {
                                handler.sendEmptyMessage(111);
                            } else if (errorCode == -1) {
                                handler.sendEmptyMessage(-1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
    }

    /**
     * 获取位置信息
     */
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            city = location.getCity();
            Latitude = location.getLatitude();
            Longitude = location.getLongitude();
            Log.e("城市", city + "," + Latitude + "," + Longitude);
            //根据经纬度获取天气
            String path = "https://free-api.heweather.com/v5/weather?city=" + city + "&key=86832713f8b4491b98e0cbe2b1ecd03a";
            HttpConnectionTools.HttpServler(path, new HttpConnectionInter() {
                @Override
                public void onFinish(String content) {
                    //成功
                    try {
                        jsonObject = new JSONObject(content);
                        jsonArray = jsonObject.getJSONArray("HeWeather5");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            String now = object.getString("now");
                            object = new JSONObject(now);
                            weatherClass.setTmp(object.getString("tmp"));//获得当前气温
                            weatherClass.setHum(object.getString("hum"));//获得当前湿度
                            weatherClass.setPres(object.getString("pres"));//获得气压
                            String cond = object.getString("cond");
                            object = new JSONObject(cond);
                            weatherClass.setTxt(object.getString("txt"));//获取当前的天气状况
                            weatherClass.setCode(object.getString("code"));//获取当前状态码

                            jsonObject = jsonArray.getJSONObject(i);
                            JSONArray array = jsonObject.getJSONArray("daily_forecast");
                            for (int j = 0; j < array.length(); j++) {
                                JSONObject jobject = array.getJSONObject(0);
                                weatherClass.setUv(jobject.getString("uv"));//获得紫外线
                                String tmp = jobject.getString("tmp");
                                jobject = new JSONObject(tmp);
                                weatherClass.setMax(jobject.getString("max"));//获得最高气温
                                weatherClass.setMin(jobject.getString("min"));//获得最低气温
                            }
                        }
                        handler11.sendEmptyMessage(1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        handler11.sendEmptyMessage(2);
                    }
                }

                @Override
                public void onError(Exception e) {
                    //失败
                    handler11.sendEmptyMessage(0x122);
                }
            });
        }
    }

    /**
     * 获取心情天气
     */
    private void getWeatherMood() {
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.MOODWEATHER, HttpConnectionTools.HttpData("code", Code), new HttpConnectionInter() {
            @Override
            public void onFinish(String content) {
                try {
                    Log.e("content", content);
                    JSONObject jsonObject = new JSONObject(content);
                    String result = jsonObject.getString("result");
                    jsonObject = new JSONObject(result);
                    String rows = jsonObject.getString("rows");

                    JSONArray jsonArray = new JSONArray(rows);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        term = jsonObject.getString("term");
                        description = jsonObject.getString("description");
                        SPUtils.put(getContext(), "term", term);
                        SPUtils.put(getContext(), "description", description);
                    }
                    handler.sendEmptyMessage(0x123);
                } catch (JSONException e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(500);
                }
            }

            @Override
            public void onError(Exception e) {
                handler.sendEmptyMessage(500);
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 200) {
                intent.setClass(getActivity(), MusicServiceActivity.class);
                startActivity(intent);
                loadingDialog.dismiss();
            } else if (msg.what == -3) {
                loadingDialog.dismiss();
                ToastUtil.show("该网段下没有在线设备");
            } else if (msg.what == 201) {
                loadingDialog.dismiss();
                ToastUtil.show("系统异常，操作失败");
            } else if (msg.what == 400) {
                loadingDialog.dismiss();
                ToastUtil.show("您已退出登录，请重新登录");
                intent.setClass(getActivity(), LoginActivity.class);
                startActivity(intent);
                MyAPP.Logout(getActivity());
            } else if (msg.what == 401) {
                loadingDialog.dismiss();
                intent.setClass(getActivity(), LoginActivity.class);
                startActivity(intent);
                MyAPP.Logout(getActivity());
                ToastUtil.show("token已过期，请重新登录");
            }

            //设置心情天气
            if (msg.what == 0x123) {
                mResidentialMood.setText(description);
            }

            //远程开门成功
            if (msg.what == 0x200) {
                ToastUtil.show("开门成功");
            } else if (msg.what == 111) {
                ToastUtil.show("开门失败");
            } else if (msg.what == -1) {
                ToastUtil.show("开门失败");
            } else if (msg.what == 0x201) {
                ToastUtil.show("开门失败");
            }
        }
    };

    Handler handler11 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                String Hum = weatherClass.getHum() + "%";
                String Mood = weatherClass.getTxt();
                String Tmp = weatherClass.getTmp() + "°";
                String Uv = weatherClass.getUv();
                String Txt = weatherClass.getTxt();
                Code = weatherClass.getCode();
                SPUtils.put(getActivity(), "Hum", Hum);
                SPUtils.put(getActivity(), "Mood", Mood);
                SPUtils.put(getActivity(), "Tmp", Tmp);
                SPUtils.put(getActivity(), "Uv", Uv);
                SPUtils.put(getActivity(), "Code", Code);
                getWeatherMood();//获取心情天气
                maxAndmin = weatherClass.getMin() + "°~" + weatherClass.getMax() + "°";
                mResidentialTemp.setText(Tmp);//设置当前温度
                mResidentialWeather.setText(Txt);//设置当前天气情况
                Log.e("maxAndmin", Code);
            } else if (msg.what == 1) {
                ToastUtil.show("获取天气失败");
            } else if (msg.what == 0x122) {
                ToastUtil.show("请检查网络");
            }
        }
    };

    Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 200) {
                //设置门禁列表
                setDoorList();
                SPUtils.put(getActivity(), "matchDoorVoDoorList", matchDoorVo.doorList);
                Log.e("matchDoorVo.doorList",matchDoorVo.doorList.size()+" ");
            } else if (msg.what == 201) {
                SPUtils.put(getActivity(),"doorListResult","");
                ToastUtil.show("获取门禁列表失败");
                setDefaultDoor();
            } else if (msg.what == 111) {
                SPUtils.put(getActivity(),"doorListResult","");
                ToastUtil.show("数据有误");
                setDefaultDoor();
            } else if (msg.what == -1) {
                SPUtils.put(getActivity(),"doorListResult","");
                ToastUtil.show("请重新选择项目获取门禁列表");
                setDefaultDoor();
            } else if (msg.what == 2) {
                SPUtils.put(getActivity(),"doorListResult","");
                ToastUtil.show("无授权门禁");
                setDefaultDoor();
            } else if (msg.what == 500) {
                SPUtils.put(getActivity(),"doorListResult","");
                ToastUtil.show("请检查网络");
            }else {
                SPUtils.put(getActivity(),"doorListResult","");
            }

            if(msg.what == 0x200){
                setDoorList();
                SPUtils.put(getActivity(), "matchDoorVoDoorList", matchDoorVo.doorList);
                Log.e("matchDoorVo.doorList",matchDoorVo.doorList.size()+" ");
            }else if (msg.what == 0x201) {
                SPUtils.put(getActivity(),"doorListResult","");
                ToastUtil.show("获取门禁列表失败");
                setDefaultDoor();
            } else if (msg.what == 0x111) {
                ToastUtil.show("数据有误");
                setDefaultDoor();
                SPUtils.put(getActivity(),"doorListResult","");
            } else if (msg.what == 0x1) {
                ToastUtil.show("请重新选择项目获取门禁列表");
                SPUtils.put(getActivity(),"doorListResult","");
                setDefaultDoor();
            } else if (msg.what == 0x2) {
                ToastUtil.show("无授权门禁");
                SPUtils.put(getActivity(),"doorListResult","");
                setDefaultDoor();
            } else if (msg.what == 0x500) {
                ToastUtil.show("请检查网络");
                SPUtils.put(getActivity(),"doorListResult","");
            }else {
                SPUtils.put(getActivity(),"doorListResult","");
            }
        }
    };

    //云瞳登录判断
    public void loginOpt() {
        loadingDialog.setLoadingBuilder(SINGLE_CIRCLE)//设置类型
                .setLoadingColor(Color.BLUE)//颜色
                .setHintText("Loading...")
                .setHintTextSize(16) // 设置字体大小 dp
                .setHintTextColor(Color.GRAY)  // 设置字体颜色
                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                .setDialogBackgroundColor(Color.parseColor("#CCffffff")) // 设置背景色，默认白色
                .show();
        String macAddress = MyAPP.getIns().getMacAddress();
        //登录请求
        String loginAddress = HttpConstants.HTTPS + Constant.HKurl;
        VMSNetSDK.getInstance().Login(loginAddress, Constant.HKusername, Constant.HKpassword, macAddress, new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                //登录失败
                mHandler.sendEmptyMessage(LOGIN_FAILED);
                loadingDialog.dismiss();
            }

            @Override
            public void onSuccess(Object obj) {
                if (obj instanceof LoginData) {
                    mHandler.sendEmptyMessage(LOGIN_SUCCESS);
                    TempDatas.getIns().setLoginData((LoginData) obj);
                    TempDatas.getIns().setLoginAddr(Constant.HKurl);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(VideoConstants.USER_NAME, Constant.HKusername);
                    editor.putString(VideoConstants.PASSWORD, Constant.HKpassword);
                    editor.putString(VideoConstants.ADDRESS_NET, Constant.HKurl);
                    editor.apply();
                    //登录成功
                    String appVersion = ((LoginData) obj).getVersion();
                    SDKUtil.analystVersionInfo(appVersion);
                    //跳转资源界面
                    Intent in = new Intent(getContext(), VideoListActivity.class);
                    startActivity(in);
                    loadingDialog.dismiss();
                }
                super.onSuccess(obj);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        /**
         * 退出登录
         */
        VMSNetSDK.getInstance().Logout(new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                super.onFailure();
            }
        });
    }
}
