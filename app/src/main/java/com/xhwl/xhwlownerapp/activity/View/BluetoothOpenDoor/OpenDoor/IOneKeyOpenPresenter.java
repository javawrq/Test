package com.xhwl.xhwlownerapp.activity.View.BluetoothOpenDoor.OpenDoor;

/**
 * Created by xiaowu on 2018/3/22 0022.
 * 一键开门
 */

public interface IOneKeyOpenPresenter {
    void getMatchDoorList(String token, String projectCode, String userName, String phone);

    void onScanBle(boolean enable, int scanningTime);

    void getAllDoorList(String projectCode,String sign,String time);

    void getDoorList(String projectCode,String sign,String time,String userName,String phone);
}
