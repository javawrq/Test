package com.xhwl.xhwlownerapp.activity.View.VideoView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.SDKConstant;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.utils.FileUtils;
import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.CustomSurfaceView;
import com.xhwl.xhwlownerapp.UIUtils.DateUtils;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.UIUtils.VideoConstants;
import com.xhwl.xhwlownerapp.net.Constant;
import com.zhy.autolayout.AutoLinearLayout;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static java.lang.String.valueOf;

public class LiveActivity extends BaseActivity implements View.OnClickListener {

    private AutoLinearLayout mTopBack;
    private TextView mTopTitle;
    private CustomSurfaceView mSurfaceView;
    private ImageView mVideoOpenSound;
    /**
     * 抓拍
     */
    private Button mVideoScreenshot;
    private ImageView mTopRecord;

    /**
     * 获取监控点信息成功
     */
    private static final int GET_CAMERA_INFO_SUCCESS = 1;
    /**
     * 获取监控点信息失败
     */
    private static final int GET_CAMERA_INFO_FAILURE = 2;

    /**
     * 视图更新处理Handler
     */
    private Handler mHandler = null;
    //控制点资源
    private SubResourceNodeBean nodeBean = null;

    private boolean stopLiveResult;

    /**
     * 语音对讲是否开启
     */
    private boolean mIsAudioOpen;

    private String token,proId;

    //云瞳抓拍图片上传
    public static final String TYPE = "image/*";
    private OkHttpClient client;

    private String userPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        initView();
        initDate();
        startVideo();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLIve();
    }

    private void initView() {
        token = SPUtils.get(this,"userToken","");
        proId = SPUtils.get(this,"proID","");
        Log.e("token",token+" = token");
        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopTitle.setText("云瞳监控");
        mSurfaceView = (CustomSurfaceView) findViewById(R.id.surfaceView);
        mVideoOpenSound = (ImageView) findViewById(R.id.video_open_sound);
        mVideoOpenSound.setOnClickListener(this);
        mVideoScreenshot = (Button) findViewById(R.id.video_screenshot);
        mVideoScreenshot.setOnClickListener(this);
        mTopRecord = (ImageView) findViewById(R.id.top_record);
        mTopRecord.setBackgroundResource(R.drawable.video_img_record);
        mTopRecord.setVisibility(View.VISIBLE);
        mTopRecord.setOnClickListener(this);
    }

    private void initDate() {
        nodeBean = (SubResourceNodeBean) getIntent().getSerializableExtra(VideoConstants.IntentKey.CAMERA);
        mHandler = new MyHandler(LiveActivity.this);
        userPhone = SPUtils.get(this,"userTelephone","");
    }

    //视图更新处理
    private static class MyHandler extends Handler {
        WeakReference<LiveActivity> reference;

        MyHandler(LiveActivity activity) {
            reference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LiveActivity listActivity = reference.get();
            if (listActivity != null) {
                switch (msg.what) {
                    case GET_CAMERA_INFO_SUCCESS:
                        Toast.makeText(listActivity, "获取流成功", Toast.LENGTH_SHORT).show();
                        break;
                    case GET_CAMERA_INFO_FAILURE:
                        Toast.makeText(listActivity, "获取流失败", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            super.handleMessage(msg);
        }
    }

    private void startVideo() {
        //开始预览按钮点击操作
        if (null == nodeBean) {
            mHandler.sendEmptyMessage(GET_CAMERA_INFO_FAILURE);
            return;
        }
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                VMSNetSDK.getInstance().startLiveOpt(valueOf(nodeBean.getId()), nodeBean.getSysCode(), mSurfaceView, 1, new OnVMSNetSDKBusiness() {
                    @Override
                    public void onFailure() {
                        mHandler.sendEmptyMessage(GET_CAMERA_INFO_FAILURE);
                    }

                    @Override
                    public void onSuccess(Object obj) {
                        mHandler.sendEmptyMessage(GET_CAMERA_INFO_SUCCESS);
                    }
                });
                Looper.loop();
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.top_back:
                //返回
                stopLIve();
                finish();
                break;
            case R.id.video_open_sound:
                //开启声音
                //音频按钮点击操作
                if (mIsAudioOpen) {
                    boolean audioOpt = VMSNetSDK.getInstance().stopLiveAudioOpt();
                    if (audioOpt) {
                        //音频处于关闭状态 开启音频
                        mIsAudioOpen = false;
                        showToast("关闭音频");
                        //UIUtils.showToast(this, R.string.stop_Audio);
                        mVideoOpenSound.setBackgroundResource(R.drawable.video_mute);
                    }
                } else {
                    boolean ret = VMSNetSDK.getInstance().startLiveAudioOpt();
                    if (!ret) {
                        //音频处于关闭状态 开启音频
                        mIsAudioOpen = false;
                        showToast("开启音频失败");
                        //UIUtils.showToast(LiveActivity.this, R.string.start_Audio_fail);
                        mVideoOpenSound.setBackgroundResource(R.drawable.video_mute);
                    } else {
                        //音频已经开启，关闭音频
                        mIsAudioOpen = true;
                        // 开启音频成功，并不代表一定有声音，需要设备开启声音。
                        showToast("开启音频成功");
                        //UIUtils.showToast(LiveActivity.this, R.string.start_Audio_success);
                        mVideoOpenSound.setBackgroundResource(R.drawable.video_voice);
                    }
                }
                break;
            case R.id.video_screenshot:
                //抓拍
                //抓拍按钮点击操作
                int opt = VMSNetSDK.getInstance().captureLiveOpt(FileUtils.getPictureDirPath().getAbsolutePath()+"/"+userPhone,
                        "Picture" + DateUtils.getCurrentTime_Today_ss() + ".jpg");
                Log.e("路径",""+FileUtils.getPictureDirPath().getAbsolutePath()+"/"+userPhone+ "/Picture"+ DateUtils.getCurrentTime_Today_ss()+".jpg");
                String filenameTime = DateUtils.getCurrentTime_Today_ss()+".jpg";
                Log.e("状态码",opt+"");
                switch (opt) {
                    case SDKConstant.LiveSDKConstant.SD_CARD_UN_USABLE:
                        //UIUtils.showToast(this, R.string.sd_card_fail);
                        showToast("SD卡不可用");
                        break;
                    case SDKConstant.LiveSDKConstant.SD_CARD_SIZE_NOT_ENOUGH:
                        showToast("SD卡空间不足");
                        //UIUtils.showToast(this, R.string.sd_card_not_enough);
                        break;
                    case SDKConstant.LiveSDKConstant.CAPTURE_FAILED:
                        //Utils.showToast(this, R.string.capture_fail);
                        showToast("抓拍失败");
                        break;
                    case SDKConstant.LiveSDKConstant.CAPTURE_SUCCESS:
                        showToast("抓拍成功");
                        //抓拍成功，上传到后台服务器
                        String url = Constant.HOST2+Constant.SERVERNAME+Constant.INTERFACEVERSION+Constant.HKUPLOADIMG;
                        Map map = new HashMap();
                        map.put("token",token);
                        map.put("projectId",proId);
                        //post_file(url,map,FileUtils.getPictureDirPath());
                        post_Img(url);
                        //UIUtils.showToast(this, R.string.capture_success);
                        break;
                }
                break;
            case R.id.top_record:
                //抓拍记录
                startToAIctivity(PictureRecordActivity.class);
                break;
        }
    }

    private void stopLIve() {
        stopLiveResult = VMSNetSDK.getInstance().stopLiveOpt();
        if (stopLiveResult) {
            Toast.makeText(this, "停止成功", Toast.LENGTH_SHORT).show();
        }
    }

    //单击返回，不退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            //停止预览按钮点击操作
            stopLIve();
            finish();
        }
        return true;
    }

    private void post_Img(String url){
        client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        File file = new File(FileUtils.getPictureDirPath().getAbsolutePath()+"/"+userPhone, "/Picture"+DateUtils.getCurrentTime_Today_ss()+".jpg");

        Log.e("file",file.getAbsolutePath());
        if (!file.exists()) {
            Toast.makeText(LiveActivity.this, "文件不存在", Toast.LENGTH_SHORT).show();
        } else {
            //MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
            RequestBody fileBody = RequestBody.create(MediaType.parse(TYPE), file);
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("token", token)
                    .addFormDataPart("projectId", proId)
                    .addFormDataPart("filename", file.getName(), fileBody).build();

            Request requestPostFile = new Request.Builder()
                    .url(url)
                    .post(requestBody)
                    .build();
            client.newCall(requestPostFile).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("lfq" ,"onFailure");
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String str = response.body().string();
                        Log.e("lfq", response.message() + "  body " + str);

                    } else {
                        Log.e("lfq" ,response.message() + " error : body " + response.body().string());
                    }
                }
            });
        }
    }

}
