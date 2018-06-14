package com.xhwl.xhwlownerapp.activity.View.BluetoothOpenDoor.OpenDoor;


import com.maxcloud.bluetoothsdklib.BleDevice;

import java.util.List;

/**
 * Created by xiaowu on 2018/3/22 0022.
 */

public interface IOpenDoorView {
    void getMatchDoorListSuccess(MatchDoorVo matchDoorVo);
    void getMatchDoorListFailed(String msg);

    void getScanedDeviceListSuccess(List<BleDevice> devices);
    void getScanedDeviceListFailed(String msg);

    void getAllDoorListSuccess(MatchDoorVo matchDoorVo);
    void getAllDoorListFailed(String msg);

    void getDoorListSuccess(MatchDoorVo matchDoorVo);
    void getDoorListFailed(String msg);

}
