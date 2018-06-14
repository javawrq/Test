package com.xhwl.xhwlownerapp.activity.View.HomeView.DeviceActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.zxing.android.BeepManager;
import com.xhwl.xhwlownerapp.zxing.android.CaptureActivityHandler;
import com.xhwl.xhwlownerapp.zxing.android.FinishListener;
import com.xhwl.xhwlownerapp.zxing.android.InactivityTimer;
import com.xhwl.xhwlownerapp.zxing.bean.ZxingConfig;
import com.xhwl.xhwlownerapp.zxing.camera.CameraManager;
import com.xhwl.xhwlownerapp.zxing.common.Constant;
import com.xhwl.xhwlownerapp.zxing.decode.DecodeImgCallback;
import com.xhwl.xhwlownerapp.zxing.decode.DecodeImgThread;
import com.xhwl.xhwlownerapp.zxing.decode.ImageUtil;
import com.xhwl.xhwlownerapp.zxing.view.ViewfinderView;
import com.zhy.autolayout.AutoLinearLayout;

import java.io.IOException;

public class GatewayQRCodeLoginActivity extends BaseActivity implements SurfaceHolder.Callback, View.OnClickListener {

    private static final String TAG = "GatewayQRCodeLoginActivity";
    public ZxingConfig config;
    private AutoLinearLayout mTopBack;
    private TextView mTopTitle;
    private ImageView mTopRecord;
    private SurfaceView mPreviewView;
    private ViewfinderView mViewfinderView;
    private boolean hasSurface;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private SurfaceHolder surfaceHolder;
    private AppCompatImageView mFlashLightIv;
    /**
     * 打开闪光灯
     */
    private TextView mFlashLightTv;
    private LinearLayoutCompat mFlashLightLayout;
    private LinearLayoutCompat mBottomLayout;
    /**
     * 不，我要登录账号
     */
    private TextView mDevicePasswordLogin;
    private Intent mIntent;

    public ViewfinderView getViewfinderView() {
        return mViewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public void drawViewfinder() {
        mViewfinderView.drawViewfinder();
    }


    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*先获取配置信息*/
        try {
            config = (ZxingConfig) getIntent().getExtras().get(Constant.INTENT_ZXING_CONFIG);
        } catch (Exception e) {

            Log.i("config", e.toString());
        }

        if (config == null) {
            config = new ZxingConfig();
        }
        setContentView(R.layout.activity_gateway_qrcode_login);
        initView();
        hasSurface = false;

        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
        beepManager.setPlayBeep(config.isPlayBeep());
        beepManager.setVibrate(config.isShake());
    }

    //单击返回键返回，不退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
        }
        return true;
    }

    private void initView() {
        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopTitle.setText("扫码登录");
        mTopRecord = (ImageView) findViewById(R.id.top_record);
        mTopRecord.setVisibility(View.VISIBLE);
        mTopRecord.setBackgroundResource(R.drawable.album);
        mTopRecord.setOnClickListener(this);
        mPreviewView = (SurfaceView) findViewById(R.id.preview_view);
        mViewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
        mViewfinderView.setZxingConfig(config);

        mFlashLightIv = (AppCompatImageView) findViewById(R.id.flashLightIv);
        mFlashLightTv = (TextView) findViewById(R.id.flashLightTv);
        mFlashLightLayout = (LinearLayoutCompat) findViewById(R.id.flashLightLayout);
        mBottomLayout = (LinearLayoutCompat) findViewById(R.id.bottomLayout);
        mDevicePasswordLogin = (TextView) findViewById(R.id.device_password_login);
        mDevicePasswordLogin.setOnClickListener(this);
        //添加下划线
        mDevicePasswordLogin.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        int[] location = new int[2];

        // getLocationInWindow方法要在onWindowFocusChanged方法里面调用
        // 个人理解是onCreate时，View尚未被绘制，因此无法获得具体的坐标点
        mPreviewView.getLocationInWindow(location);

        // 模拟的mPreviewView的左右上下坐标坐标
        int left = mPreviewView.getLeft();
        int right = mPreviewView.getRight();
        int top = mPreviewView.getTop();
        int bottom = mPreviewView.getBottom();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.top_back:
                finish();
                break;
            case R.id.top_record:
                /*打开相册*/
                mIntent = new Intent();
                mIntent.setAction(Intent.ACTION_PICK);
                mIntent.setType("image/*");
                startActivityForResult(mIntent, Constant.REQUEST_IMAGE);
                break;
            case R.id.device_password_login:
                finish();
                break;
        }
    }

    /**
     * @param rawResult 返回的扫描结果
     */
    public void handleDecode(Result rawResult) {

        inactivityTimer.onActivity();

        beepManager.playBeepSoundAndVibrate();

        Intent intent = getIntent();
        intent.putExtra(Constant.CODED_CONTENT, rawResult.getText());
        setResult(RESULT_OK, intent);
        this.finish();
    }

    /**
     * @param flashState 切换闪光灯图片
     */
    public void switchFlashImg(int flashState) {

        if (flashState == Constant.FLASH_OPEN) {
            mFlashLightIv.setImageResource(R.drawable.ic_open);
            mFlashLightTv.setText("关闭闪光灯");
        } else {
            mFlashLightIv.setImageResource(R.drawable.ic_close);
            mFlashLightTv.setText("打开闪光灯");
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        cameraManager = new CameraManager(getApplication(), config);

        mViewfinderView.setCameraManager(cameraManager);
        handler = null;

        surfaceHolder = mPreviewView.getHolder();
        if (hasSurface) {

            initCamera(surfaceHolder);
        } else {
            // 重置callback，等待surfaceCreated()来初始化camera
            surfaceHolder.addCallback(this);
        }

        beepManager.updatePrefs();
        inactivityTimer.onResume();

    }


    @SuppressLint("LongLogTag")
    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            return;
        }
        try {
            // 打开Camera硬件设备
            cameraManager.openDriver(surfaceHolder);
            // 创建一个handler来打开预览，并抛出一个运行时异常
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager);
            }
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("扫一扫");
        builder.setMessage(getString(R.string.msg_camera_framework_bug));
        builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
        builder.setOnCancelListener(new FinishListener(this));
        builder.show();
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();

        if (!hasSurface) {

            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQUEST_IMAGE && resultCode == RESULT_OK) {
            String path = ImageUtil.getImageAbsolutePath(this, data.getData());

            new DecodeImgThread(path, new DecodeImgCallback() {
                @Override
                public void onImageDecodeSuccess(Result result) {
                    handleDecode(result);
                }

                @Override
                public void onImageDecodeFailed() {
                    Toast.makeText(GatewayQRCodeLoginActivity.this, "抱歉，解析失败,换个图片试试.", Toast.LENGTH_SHORT).show();
                }
            }).run();


        }
    }

}
