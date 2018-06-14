package com.xhwl.xhwlownerapp.activity.View.BluetoothOpenDoor.OpenDoor;

import com.maxcloud.bluetoothsdklib.BleDevice;
import com.maxcloud.bluetoothsdklib.ScanDeviceHelper;
import com.xhwl.xhwlownerapp.UIUtils.ShowToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaowu on 2018/3/22 0022.
 */

public class OneKeyOpenDoorPresenterImpl implements IOneKeyOpenPresenter, IOnekeyOpenModel.onGetMatchDoorListListener,
        ScanDeviceHelper.OnBluetoothScanCallback, IOnekeyOpenModel.onGetAllDoorListListener, IOnekeyOpenModel.onGetDoorListListener {
    private IOpenDoorView openDoorView;
    private IOnekeyOpenModel onekeyOpenModel;
    private boolean isRefreshing = false;
    private List<BleDevice> mScanedBleDevice;


    private ScanDeviceHelper mScanDeviceHelper = new ScanDeviceHelper();


    public OneKeyOpenDoorPresenterImpl(IOpenDoorView openDoorView) {
        this.openDoorView = openDoorView;
        this.onekeyOpenModel = new OneKeyOpenImpl();
        mScanedBleDevice = new ArrayList<>();
    }

    @Override
    public void getMatchDoorList(String token, String projectCode, String userName, String phone) {
        onekeyOpenModel.getMatchDoorList(token, projectCode, userName, phone, this);
    }

    @Override
    public void getAllDoorList(String projectCode, String sign, String time) {
        onekeyOpenModel.getAllDoorList(projectCode, sign,time, this);
    }

    @Override
    public void getDoorList(String projectCode, String sign, String time, String userName, String phone) {
        onekeyOpenModel.getDoorList(projectCode, sign,time,userName,phone, this);
    }

    @Override
    public void onScanBle(boolean enable, int scanningTime) {

        if (enable) {
            if (isRefreshing) {
                return;
            }
            mScanDeviceHelper.startLeScan(this, scanningTime);
            isRefreshing = true;
        } else {
            isRefreshing = false;
            mScanDeviceHelper.stopLeScan();
        }
    }

    @Override
    public void onGetMatchDoorListSuccess(MatchDoorVo doorVo) {
        openDoorView.getMatchDoorListSuccess(doorVo);
    }

    @Override
    public void onGetMatchDoorListFailed(String msg) {
        openDoorView.getMatchDoorListFailed(msg);
    }

    @Override
    public void onGetAllDoorListSuccess(MatchDoorVo doorVo) {
        openDoorView.getAllDoorListSuccess(doorVo);
    }

    @Override
    public void onGetAllDoorListFailed(String msg) {
        openDoorView.getAllDoorListFailed(msg);
    }

    @Override
    public void onGetDoorListSuccess(MatchDoorVo doorVo) {
        openDoorView.getDoorListSuccess(doorVo);
    }

    @Override
    public void onGetDoorListFailed(String msg) {
        openDoorView.getDoorListFailed(msg);
    }

    //蓝牙扫描
    @Override
    public void onScanDevice(BleDevice bleDevice) {
        boolean needAdd = true;
        for (BleDevice aMScanedBleDevice : mScanedBleDevice) {
            if (aMScanedBleDevice.getAddress().equals(bleDevice.getAddress())) {
                needAdd = false;
                break;
            }
        }
        if (needAdd) {
            mScanedBleDevice.add(bleDevice);
        }
    }

    @Override
    public void onScanFinish() {
        if (mScanedBleDevice.size() > 0) {
            openDoorView.getScanedDeviceListSuccess(mScanedBleDevice);
            onScanBle(false, AutoOpenDoorActivity.scanningTime);
        } else {
            ShowToast.show("未扫描到设备！");
            ((AutoOpenDoorActivity) openDoorView).finish();
            //onScanBle(true,AutoOpenDoorActivity.scanningTime);
        }
    }

    @Override
    public void onScanFailed(String s) {
        openDoorView.getScanedDeviceListFailed(s);
        mScanedBleDevice.clear();
        onScanBle(false, AutoOpenDoorActivity.scanningTime);
        ((AutoOpenDoorActivity) openDoorView).finish();
    }

}
