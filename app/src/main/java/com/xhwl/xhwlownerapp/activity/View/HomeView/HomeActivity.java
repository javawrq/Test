package com.xhwl.xhwlownerapp.activity.View.HomeView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wilddog.client.SyncReference;
import com.wilddog.client.WilddogSync;
import com.wilddog.wilddogauth.WilddogAuth;
import com.wilddog.wilddogauth.core.Task;
import com.wilddog.wilddogauth.core.listener.OnCompleteListener;
import com.wilddog.wilddogauth.core.result.AuthResult;
import com.xhwl.xhwlownerapp.Entity.ProjectEntity.Project;
import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.Receiver.NetWorkStateReceiver;
import com.xhwl.xhwlownerapp.Service.MyService;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.MyAPP;
import com.xhwl.xhwlownerapp.UIUtils.NoScrollViewPager;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.UIUtils.SensorManagerHelper;
import com.xhwl.xhwlownerapp.UIUtils.dialog.SelfWhiteDialog;
import com.xhwl.xhwlownerapp.activity.View.BluetoothOpenDoor.OpenDoor.AutoOpenDoorActivity;
import com.xhwl.xhwlownerapp.activity.View.HomeView.HomeFragment.HomeFamilyFragment;
import com.xhwl.xhwlownerapp.activity.View.HomeView.HomeFragment.HomeMallFragment;
import com.xhwl.xhwlownerapp.activity.View.HomeView.HomeFragment.HomePersonalFragment;
import com.xhwl.xhwlownerapp.activity.View.HomeView.HomeFragment.HomeResidentialFragment;
import com.xhwl.xhwlownerapp.activity.View.LoginView.LoginActivity;
import com.xhwl.xhwlownerapp.net.Constant;
import com.xhwl.xhwlownerapp.net.HttpConnectionInter;
import com.xhwl.xhwlownerapp.net.HttpConnectionTools;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private NoScrollViewPager mHomeViewpager;
    private ImageView mHomeBottomFamilyImg;
    private AutoLinearLayout mHomeBottomFamily;
    /**
     * 家庭
     */
    private TextView mHomeBottomFamilyText;
    private ImageView mHomeBottomResidentialImg;
    /**
     * 小区
     */
    private TextView mHomeBottomResidentialText;
    private AutoLinearLayout mHomeBottomResidential;
    private ImageView mHomeBottomBluetoothOpendor;
    private ImageView mHomeBottomMallImg;
    /**
     * 商城
     */
    private TextView mHomeBottomMallText;
    private AutoLinearLayout mHomeBottomMall;
    private ImageView mHomeBottomPersonalImg;
    /**
     * 我的
     */
    private TextView mHomeBottomPersonalText;
    private AutoLinearLayout mHomeBottomPersonal;

    private FragmentManager fragmentManager;//碎片管理者对象
    private List<Fragment> fragmentList;

    private String token, userName;
    private Intent intent;
    private MediaPlayer startMediaPlayer;
    private String proCode, userPhone;
    private boolean isInlogin = false;

    //极光推送
    public static boolean isForeground = false;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    private String userType;
    private String result;
    private JSONObject object;
    private Project project;
    private List<Project> projectList = new ArrayList<>();
    //项目名、项目编号
    private String proName;
    //云瞳需要的参数
    private String nodeType;
    private String nodeCode;
    private String entranceCode;
    //检测网络
    private NetWorkStateReceiver netWorkStateReceiver;
    private SensorManagerHelper sensorHelper;
    private String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //进来判断 是否 需要登录
        token = SPUtils.get(HomeActivity.this, "userToken", "");
        String sUser = SPUtils.get(HomeActivity.this, "sysUser", "");
        if (TextUtils.isEmpty(token)) {
            //token 或者 用户信息 为空  则 需要重新登录
            startToAIctivity(LoginActivity.class);
            finish();
            return;
        }

        initView();
        initData();
        /**
         * 获取最新版本
         */
        getNewestVersion();
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
        Shake();//摇一摇开门
        if (proCode != "") {
            //初始化云对讲ID
            getTokenLogin(proCode + "-user-" + userPhone);
        } else {
            //获取默认项目
            getDefaultProject();
        }
        super.onResume();
    }

    @Override
    protected void onRestart() {
        /**
         * 获取最新版本
         */
        getNewestVersion();
        super.onRestart();
    }

    //onPause()方法注销
    @Override
    protected void onPause() {
        unregisterReceiver(netWorkStateReceiver);
        System.out.println("注销");
        sensorHelper.stop();//摇一摇停止监听
        super.onPause();
    }

    private void Shake() {
        startMediaPlayer = MediaPlayer.create(HomeActivity.this, R.raw.shake);
        //摇一摇开门
        sensorHelper = new SensorManagerHelper(this);
        sensorHelper.setOnShakeListener(new SensorManagerHelper.OnShakeListener() {
            @Override
            public void onShake() {
                // TODO Auto-generated method stub
                startMediaPlayer.start();
                //startToAIctivity(AutoOpenDoorActivity.class);
                startToAIctivity(AutoOpenDoorActivity.class);
                //Toast.makeText(HomePageActivity.this, "你在摇哦", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        intent = new Intent();
        mHomeViewpager = (NoScrollViewPager) findViewById(R.id.home_viewpager);
        //设置ViewPager不可左右滑动
        mHomeViewpager.setScanScroll(false);
        mHomeBottomFamilyImg = (ImageView) findViewById(R.id.home_bottom_family_img);
        mHomeBottomFamily = (AutoLinearLayout) findViewById(R.id.home_bottom_family);
        mHomeBottomFamily.setOnClickListener(this);
        mHomeBottomFamilyText = (TextView) findViewById(R.id.home_bottom_family_text);
        mHomeBottomResidentialImg = (ImageView) findViewById(R.id.home_bottom_residential_img);
        mHomeBottomResidentialText = (TextView) findViewById(R.id.home_bottom_residential_text);
        mHomeBottomResidential = (AutoLinearLayout) findViewById(R.id.home_bottom_residential);
        mHomeBottomResidential.setOnClickListener(this);
        mHomeBottomBluetoothOpendor = (ImageView) findViewById(R.id.home_bottom_bluetooth_opendor);
        mHomeBottomBluetoothOpendor.setOnClickListener(this);
        mHomeBottomMallImg = (ImageView) findViewById(R.id.home_bottom_mall_img);
        mHomeBottomMallText = (TextView) findViewById(R.id.home_bottom_mall_text);
        mHomeBottomMall = (AutoLinearLayout) findViewById(R.id.home_bottom_mall);
        mHomeBottomMall.setOnClickListener(this);
        mHomeBottomPersonalImg = (ImageView) findViewById(R.id.home_bottom_personal_img);
        mHomeBottomPersonalText = (TextView) findViewById(R.id.home_bottom_personal_text);
        mHomeBottomPersonal = (AutoLinearLayout) findViewById(R.id.home_bottom_personal);
        mHomeBottomPersonal.setOnClickListener(this);
        //权限申请
        showContacts();
    }

    private void initData() {
        reset();
        SPUtils.put(this, "isLogin", true);//判断是否登录
        JPushInterface.clearLocalNotifications(this);//登陆成功，清除所有通知
        userType = SPUtils.get(this, "userType", "");
        userName = SPUtils.get(this, "userName", "");
        proCode = SPUtils.get(this, "proCode", "");
        userPhone = SPUtils.get(this, "userTelephone", "");
        Log.e("userPhone", userPhone + " ");
        if (proCode != "") {
            //初始化云对讲ID
            getTokenLogin(proCode + "-user-" + userPhone);
        } else {
            //获取默认项目
            getDefaultProject();
        }
        /**
         * 极光推送
         * 登录以后才能接收推送通知
         */
        if (JPushInterface.isPushStopped(HomeActivity.this)) {
            JPushInterface.resumePush(HomeActivity.this);//恢复极光推送\
        }
        JPushInterface.setAlias(this, 0, userPhone);
        //添加数据源
        fragmentList = new ArrayList<>();
        fragmentList.add(new HomeFamilyFragment());
        fragmentList.add(new HomeResidentialFragment());
        fragmentList.add(new HomeMallFragment());
        fragmentList.add(new HomePersonalFragment());
        //获得管理者对象
        fragmentManager = getSupportFragmentManager();
        //碎片滑动，适配器
        mHomeViewpager.setAdapter(new FragmentPagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });

        mHomeViewpager.setCurrentItem(0);  //初始化显示第一个页面
//        mHomeBottomResidentialText.setTextColor(getResources().getColor(R.color.home_bottom));
//        mHomeBottomResidentialImg.setBackgroundResource(R.drawable.home_residential);
        mHomeBottomFamilyText.setTextColor(getResources().getColor(R.color.home_bottom));
        mHomeBottomFamilyImg.setBackgroundResource(R.drawable.home_family);
    }

    private void getDefaultProject() {
        result = SPUtils.get(this, "result", "");
        try {
            object = new JSONObject(result);
            JSONArray jsonArray = object.getJSONArray("projectList");
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
                projectList.add(project);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (projectList.size() != 0) {
            proName = projectList.get(0).getProName();
            proCode = projectList.get(0).getProjectCode();
            nodeCode = projectList.get(0).getNodeID();
            nodeType = projectList.get(0).getNodeType();
            entranceCode = projectList.get(0).getEntranceCode();
            SPUtils.put(this, "proCode", proCode);
            SPUtils.put(this, "proName", proName);
            SPUtils.put(this, "nodeType", nodeType);
            SPUtils.put(this, "nodeID", nodeCode);
            SPUtils.put(this, "entranceCode", entranceCode);
            getTokenLogin(proCode + "-user-" + userPhone);
        }
    }

    /**
     * 申请权限
     */
    public void showContacts() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "没有权限,请手动开启定位权限", Toast.LENGTH_SHORT).show();
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE}, 100);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.home_bottom_family:
                //家庭
                reset();
                mHomeViewpager.setCurrentItem(0);
                mHomeBottomFamilyText.setTextColor(getResources().getColor(R.color.home_bottom));
                mHomeBottomFamilyImg.setBackgroundResource(R.drawable.home_family);
                break;
            case R.id.home_bottom_residential:
                //小区
                reset();
                mHomeViewpager.setCurrentItem(1);
                mHomeBottomResidentialText.setTextColor(getResources().getColor(R.color.home_bottom));
                mHomeBottomResidentialImg.setBackgroundResource(R.drawable.home_residential);
                break;
            case R.id.home_bottom_bluetooth_opendor:
                //蓝牙开门
                if (proCode != "") {
                    startToAIctivity(AutoOpenDoorActivity.class);
                } else {
                    showToast("请先选择项目");
                }
                break;
            case R.id.home_bottom_mall:
                //商城
                reset();
                mHomeViewpager.setCurrentItem(2);
                mHomeBottomMallText.setTextColor(getResources().getColor(R.color.home_bottom));
                mHomeBottomMallImg.setBackgroundResource(R.drawable.home_mall);
                break;
            case R.id.home_bottom_personal:
                //个人中心
                reset();
                mHomeViewpager.setCurrentItem(3);
                mHomeBottomPersonalText.setTextColor(getResources().getColor(R.color.home_bottom));
                mHomeBottomPersonalImg.setBackgroundResource(R.drawable.home_personal);
                break;
        }
    }

    //改变图片 字体的颜色为暗色
    @SuppressLint("ResourceAsColor")
    private void reset() {
        //设置图片变为暗色
        //家庭
        mHomeBottomFamilyText.setTextColor(getResources().getColor(R.color.home_bottom_1));
        mHomeBottomFamilyImg.setBackgroundResource(R.drawable.home_family_1);
        //社区
        mHomeBottomResidentialText.setTextColor(getResources().getColor(R.color.home_bottom_1));
        mHomeBottomResidentialImg.setBackgroundResource(R.drawable.home_residential_1);
        //商城
        mHomeBottomMallText.setTextColor(getResources().getColor(R.color.home_bottom_1));
        mHomeBottomMallImg.setBackgroundResource(R.drawable.home_mall_1);
        //个人中心
        mHomeBottomPersonalText.setTextColor(getResources().getColor(R.color.home_bottom_1));
        mHomeBottomPersonalImg.setBackgroundResource(R.drawable.home_personal_1);
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

    public void login(String token, final String proCode) {
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
                            intent = new Intent(HomeActivity.this, MyService.class);
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

    /**
     * 版本更新
     */
    private String link, versionNoversionNo;
    private SelfWhiteDialog versionDialog;
    private String versionName;//本地版本号

    private void getNewestVersion() {
        versionName = getLocalVersionName(getApplicationContext());
        Log.e("versionName ", versionName + "");
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.NEWVERSION, HttpConnectionTools.HttpData("type", "yzAndroid"), new HttpConnectionInter() {
            @Override
            public void onFinish(String content) {
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    int errorCode = jsonObject.getInt("errorCode");
                    String result = jsonObject.getString("result");
                    if (errorCode == 200) {
                        jsonObject = new JSONObject(result);
                        link = jsonObject.getString("link");
                        versionNoversionNo = jsonObject.getString("versionNo");
                        Log.e("versionNoversionNo ", versionNoversionNo);
                        versionHandler.sendEmptyMessage(0x200);
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
     * 获取本地软件版本号名称
     */
    public static String getLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    Handler versionHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x200) {
                if (!versionNoversionNo.equals(versionName)) {
                    versionDialog = new SelfWhiteDialog(HomeActivity.this);
                    versionDialog.setMessage("您有新的版本APP，版本号" + versionNoversionNo + "是否更新？");
                    versionDialog.setTitle("版本更新");
                    versionDialog.setCanceledOnTouchOutside(false);
                    versionDialog.setCancelable(false);
                    versionDialog.setYesOnclickListener("确定", new SelfWhiteDialog.onYesOnclickListener() {
                        @Override
                        public void onYesClick() {
                            //Logout(token);
                            Intent intent = new Intent(HomeActivity.this, VersionActivity.class);
                            intent.putExtra("link", link);
                            startActivity(intent);
                            versionDialog.dismiss();
                        }
                    });
                    versionDialog.show();
                } else {
                    //showToast("未检测到最新版本");
                }
            }
        }
    };
}
