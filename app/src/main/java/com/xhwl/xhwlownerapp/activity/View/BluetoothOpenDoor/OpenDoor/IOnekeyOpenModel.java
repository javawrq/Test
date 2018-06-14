package com.xhwl.xhwlownerapp.activity.View.BluetoothOpenDoor.OpenDoor;

/**
 * Created by xiaowu on 2018/3/22 0022.
 */

public interface IOnekeyOpenModel {
//    interface onScanBleListener{
//        void onScanBleSuccess(List<BleDevice> devices);
//        void onScanBleFailed(String msg);
//    }

    interface onGetMatchDoorListListener {
        void onGetMatchDoorListSuccess(MatchDoorVo doorVo);

        void onGetMatchDoorListFailed(String msg);
    }

    interface onGetAllDoorListListener {
        void onGetAllDoorListSuccess(MatchDoorVo doorVo);

        void onGetAllDoorListFailed(String msg);
    }

    interface onGetDoorListListener {
        void onGetDoorListSuccess(MatchDoorVo doorVo);

        void onGetDoorListFailed(String msg);
    }

    /***
     * 获取蓝牙扫码结果
     */
    //  void getScanBleResult( IHomeModel.onGetDoorListListener listener);

    /**
     * 获取用户授权门禁列表
     *
     * @param token
     * @param projectCode
     * @param userName
     * @param phone
     * @param listener
     */
    void getMatchDoorList(String token, String projectCode, String userName, String phone, onGetMatchDoorListListener listener);

    /**
     * 获取全部门禁列表
     *
     * @param projectCode
     * @param sign
     * @param time
     */
    void getAllDoorList(String projectCode, String sign, String time, onGetAllDoorListListener listener);

    /**
     * 获取用户授权列表（新）
     *
     * @param projectCode
     * @param sign
     * @param time
     * @param userName
     * @param phone
     */
    void getDoorList(String projectCode, String sign, String time, String userName, String phone, onGetDoorListListener listener);

}
