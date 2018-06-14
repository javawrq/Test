package com.xhwl.xhwlownerapp.activity.View.BluetoothOpenDoor.OpenDoor;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.maxcloud.bluetoothsdklib.BleDevice;
import com.maxcloud.bluetoothsdklib.Conversion;
import com.maxcloud.bluetoothsdklib.ProtocolHelper;
import com.maxcloud.bluetoothsdklib.ProtocolListener;
import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.DateUtils;
import com.xhwl.xhwlownerapp.UIUtils.MD5Utils;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.UIUtils.ShowToast;
import com.xhwl.xhwlownerapp.UIUtils.StringUtils;
import com.zyao89.view.zloading.ZLoadingDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.zyao89.view.zloading.Z_TYPE.SINGLE_CIRCLE;


/**
 * Created by xiaowu on 2018/3/22 0022.
 */
public class AutoOpenDoorActivity extends BaseActivity implements IOpenDoorView, ProtocolListener.onOpenDoorListener {

    private static final String TAG = "AutoOpenDoorActivity";
    private IOneKeyOpenPresenter iOneKeyOpenPresenter;
    private List<MatchDoorVo.Door> doorList;
    private List<BleDevice> scanedDevices;
    private static final long OPEN_PERIOD = 5000;// 刷卡时间5s
    private String openDataStr;
    private String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION};

    public static final int scanningTime = 2000;// 扫描时间20s
    private boolean isNeedDeal = true;
    private boolean reload;
    private MediaPlayer successMediaPlayer;
    private ZLoadingDialog zLoadingDialog;
    private String doorListResult;

    private MatchDoorVo matchDoorVo = new MatchDoorVo();;
    private MatchDoorVo.Door door;
    private String doorID;
    private String doorName;
    private String doorPath;
    private String connectionKey;
    private String keyID;
    private MatchDoorVo.Door.OtherInfo otherInfo;
    private String doorTyp;
    private boolean isWorkstation;
    private String proCode,time,sign,token,proName,username,phone;
    private String oldProCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_key_open);
        initView();
        initData();
    }

    private void initView() {
        successMediaPlayer = MediaPlayer.create(AutoOpenDoorActivity.this, R.raw.open_success);
        zLoadingDialog = new ZLoadingDialog(AutoOpenDoorActivity.this);
        zLoadingDialog.setLoadingBuilder(SINGLE_CIRCLE)//设置类型
                .setLoadingColor(Color.BLUE)//颜色
                .setHintText("开门中，请稍候")
                .setHintTextSize(16) // 设置字体大小 dp
                .setHintTextColor(Color.GRAY)  // 设置字体颜色
                .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                .setDialogBackgroundColor(Color.parseColor("#CCffffff")) // 设置背景色，默认白色
                .setCancelable(false)
                .setCanceledOnTouchOutside(false)
                .show();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    private void initData() {
        doorList = new ArrayList<>();
        //BleCheckUtils.openBlueTooth(this, permissions);
        iOneKeyOpenPresenter = new OneKeyOpenDoorPresenterImpl(this);
        iOneKeyOpenPresenter.onScanBle(true, scanningTime);
        doorListResult = SPUtils.get(this,"doorListResult","");
        token = MD5Utils.encode(DateUtils.getCurrentTime_Today_Min() + "adminXH");
        phone = SPUtils.get(this, "userTelephone", "");
        proCode  = SPUtils.get(this, "proCode", "");
        Log.e(TAG, "initData: "+proCode);
        proName = SPUtils.get(this, "proName", "");
        username = SPUtils.get(this, "userName", "");
        time = DateUtils.getTimes();
        sign = MD5Utils.encode("userName="+username+"&phone="+phone+"&projectCode="+proCode+"&time="+time);
        isWorkstation = SPUtils.get(this, "isWorkstation", true);
        isHaveDoorList();
        if (proCode != null) {
            if (isWorkstation) {
                iOneKeyOpenPresenter.getDoorList(proCode,sign,time,username,phone);
                oldProCode = proCode;
                //iOneKeyOpenPresenter.getMatchDoorList(token,proCode,username,proName);
            } else {
                iOneKeyOpenPresenter.getAllDoorList(proCode,sign,time);
                oldProCode = proCode;
            }
        }
    }

    //检查缓存中是否有门禁列表
    private void isHaveDoorList(){
        JSONObject jsonObject = null;
        if (doorListResult != null) {
            if(oldProCode != null && oldProCode.equals(proCode)){
                if (isWorkstation) {
                    try {
                        jsonObject = new JSONObject(doorListResult);
                        openDataStr = jsonObject.getString("openData");
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
                                doorList.add(door);
                            } else {
                                door = new MatchDoorVo.Door(doorID, doorName, doorPath, connectionKey, keyID, doorTyp);
                                doorList.add(door);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        jsonObject = new JSONObject(doorListResult);
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
                                doorList.add(door);
                            } else {
                                door = new MatchDoorVo.Door(doorID, doorName, doorPath, connectionKey, keyID, doorTyp);
                                doorList.add(door);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }else {
                if (proCode != null) {
                    if (isWorkstation) {
                        iOneKeyOpenPresenter.getDoorList(proCode,sign,time,username,phone);
                        oldProCode = proCode;
                        //iOneKeyOpenPresenter.getMatchDoorList(token,proCode,username,proName);
                    } else {
                        iOneKeyOpenPresenter.getAllDoorList(proCode,sign,time);
                        oldProCode = proCode;
                    }
                }
            }
        }else {
            if (proCode != null) {
                if (isWorkstation) {
                    iOneKeyOpenPresenter.getDoorList(proCode,sign,time,username,phone);
                    oldProCode = proCode;
                    //iOneKeyOpenPresenter.getMatchDoorList(token,proCode,username,proName);
                } else {
                    iOneKeyOpenPresenter.getAllDoorList(proCode,sign,time);
                    oldProCode = proCode;
                }
            }
        }
    }

    private void oneKeyOpenDoor(BleDevice bleDevice, int pos) {
        if ("04EE".equals(bleDevice.getModel())) {
            byte[] connectKey = Conversion.HexString2Bytes(doorList.get(pos).getConnectionKey());
            byte[] openData = Conversion.HexString2Bytes(openDataStr);
            iOneKeyOpenPresenter.onScanBle(false, scanningTime);
            ProtocolHelper.getInstance(this).openDoor(this, bleDevice.getAddress(), openData,
                    Conversion.HexString2Bytes(bleDevice.getRandomCast()), connectKey, OPEN_PERIOD);
        } else if ("05EE".equals(bleDevice.getModel()) || "06EE".equals(bleDevice.getModel())) {
            String passWord = doorList.get(pos).getOtherInfo().getPassWord();
            ProtocolHelper.getInstance(this).openDoor(this, bleDevice, passWord, OPEN_PERIOD);
        }
    }

    //获取可开门列表
    @Override
    public void getMatchDoorListSuccess(MatchDoorVo matchDoorVo) {
        if(openDataStr == null){
            openDataStr = matchDoorVo.openData;
        }
        if(doorList.size() == 0){
            doorList = matchDoorVo.doorList;
        }
        if (scanedDevices != null) {
            matchBleDevice(scanedDevices);
        } else if (isNeedDeal) {
            iOneKeyOpenPresenter.onScanBle(true, scanningTime);
        } else {
            Log.w(TAG, "未扫描到设备！");
            finish();
            zLoadingDialog.dismiss();
        }
    }

    @Override
    public void getMatchDoorListFailed(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ShowToast.show(msg);
                Log.w(TAG, msg + "");
                zLoadingDialog.dismiss();
                finish();
            }
        });
    }

    //获取全部门禁列表
    @Override
    public void getAllDoorListSuccess(MatchDoorVo matchDoorVo) {
        if(openDataStr == null){
            openDataStr = matchDoorVo.openData;
        }
        if(doorList.size() == 0){
            doorList = matchDoorVo.doorList;
        }
        if (scanedDevices != null) {
            matchBleDevice(scanedDevices);
        } else if (isNeedDeal) {
            iOneKeyOpenPresenter.onScanBle(true, scanningTime);
        } else {
            Log.w(TAG, "未扫描到设备！");
            finish();
            zLoadingDialog.dismiss();
        }
    }

    @Override
    public void getAllDoorListFailed(final String msg) {
        if (!StringUtils.isEmpty(doorListResult)){
            Log.e(TAG, "run: 无网络下开门" );
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ShowToast.show(msg);
                    Log.w(TAG, msg + "");
                    zLoadingDialog.dismiss();
                    finish();
                }
            });
        }
    }

    //获取用户授权门禁列表
    @Override
    public void getDoorListSuccess(MatchDoorVo matchDoorVo) {
        if(openDataStr == null){
            openDataStr = matchDoorVo.openData;
        }
        if(doorList.size() == 0){
            doorList = matchDoorVo.doorList;
        }
        if (scanedDevices != null) {
            matchBleDevice(scanedDevices);
        } else if (isNeedDeal) {
            iOneKeyOpenPresenter.onScanBle(true, scanningTime);
        } else {
            Log.w(TAG, "未扫描到设备！");
            finish();
            zLoadingDialog.dismiss();
        }
    }

    @Override
    public void getDoorListFailed(final String msg) {
        if (!StringUtils.isEmpty(doorListResult)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e(TAG, "run: 无网络下开门" );
                }
            });
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ShowToast.show(msg);
                    Log.w(TAG, msg + "");
                    zLoadingDialog.dismiss();
                    finish();
                }
            });
        }
    }

    //获取扫描到的蓝牙设备
    @Override
    public void getScanedDeviceListSuccess(List<BleDevice> devices) {
        scanedDevices = devices;
        isNeedDeal = false;
        if (scanedDevices != null) {
            matchBleDevice(scanedDevices);
        } else {
            Log.w(TAG, "未扫描到设备！");
            finish();
            zLoadingDialog.dismiss();
        }
    }

    @Override
    public void getScanedDeviceListFailed(String msg) {
        ShowToast.show(msg);
    }

    //获取唯一开门设备
    private void matchBleDevice(List<BleDevice> bleDevices) {
        Collections.sort(bleDevices, new Comparator<BleDevice>() {
            @Override
            public int compare(BleDevice o1, BleDevice o2) {
                return o2.getRssi() - o1.getRssi();
            }
        });
        int size = doorList.size();
        for (int i = 0; i < doorList.size(); i++) {
            size--;
            for (int j = 0; j < bleDevices.size(); j++) {
                BleDevice device = bleDevices.get(j);
                if (device.getKeyId() != null) {
                    String keyID = doorList.get(i).getKeyID();
                    if (device.getKeyId().equals(keyID)) {
                        oneKeyOpenDoor(device, i);
                        return;
                    }
                } else {
                    String deviceMac = device.getAddress().replaceAll(":", "").replaceAll("-", "");
                    MatchDoorVo.Door.OtherInfo otherInfo = doorList.get(i).getOtherInfo();
                    if (otherInfo != null) {
                        String doorMac = otherInfo.getMac().replaceAll("-", "").replaceAll(":", "");
                        if (deviceMac.equals(doorMac)) {
                            oneKeyOpenDoor(device, i);
                            return;
                        }
                    }
                }
            }
            if (size == 0) {
                ShowToast.show("无匹配门禁！");
                finish();
                zLoadingDialog.dismiss();
            }
        }
    }

    //open door
    @Override
    public void onSuccess() {
        ProtocolHelper.getInstance(this).close();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ShowToast.show("开门成功！");
                //开门成功，播放成功音效
                try {
//                    if (successMediaPlayer != null)
//                        successMediaPlayer.start();
                    finish();
                    zLoadingDialog.dismiss();
                } catch (Exception e) {
                    Log.e("OpenDoor", "Exception");
                    zLoadingDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void onFailed(final String s) {
        ProtocolHelper.getInstance(this).close();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //开门失败
                ShowToast.show(s);
                zLoadingDialog.dismiss();
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BleCheckUtils.REQUEST_OPEN_LOCATION:
                if (!BleCheckUtils.checkLocationOn(this)) {
                    finish();
                    zLoadingDialog.dismiss();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        ProtocolHelper.getInstance(this).close();
        successMediaPlayer.release();//释放MediaPlayer对象
        //gifDrawable.recycle();//释放内存
        zLoadingDialog.dismiss();
        super.onDestroy();
    }

    //单击返回键返回，不退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
            zLoadingDialog.dismiss();
        }
        return true;
    }
}
