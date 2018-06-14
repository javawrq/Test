package com.xhwl.xhwlownerapp.activity.View.BluetoothOpenDoor.OpenDoor;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import com.xhwl.xhwlownerapp.UIUtils.dialog.SelfDialog;

/**
 * Project Name Estate
 * 蓝牙权限的相关请求
 * <p> Created by xiaowu on 2018/4/10 0010.</p>
 */

public class BleCheckUtils {
 /*   04EE——证书版
    05EE——蓝牙控制模块
    06EE——蓝牙控制一体机*/
    public static final int REQUEST_OPEN_LOCATION = 0x0001;
    private static SelfDialog mSelfDialog;
    private static boolean checkBleEnable(final Activity context) {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!defaultAdapter.isEnabled()) {
            mSelfDialog = new SelfDialog(context);
            mSelfDialog.setCanceledOnTouchOutside(false);
            mSelfDialog.setCancelable(false);
            mSelfDialog.setMessage("立即前往开启蓝牙");
            mSelfDialog.setTitle("蓝牙未开启");
            mSelfDialog.setYesOnclickListener("确定", new SelfDialog.onYesOnclickListener() {
                @Override
                public void onYesClick() {
                    Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                    context.startActivity(intent);
                }
            });
            mSelfDialog.setNoOnclickListener("取消", new SelfDialog.onNoOnclickListener() {
                @Override
                public void onNoClick() {
                    context.finish();
                }
            });
            mSelfDialog.show();

            return false;
        }
        return true;
    }

    public static void openBlueTooth(Activity context, String[] permissions) {
        if (checkBleEnable(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PermissionUtil.checkLocationPermission(context);
            }
            if (!checkLocationOn(context)) {
                turnOnGPS(context);
            }
        }
    }

    public static boolean checkLocationOn(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        return true;
    }

    public static void turnOnGPS(final Activity context) {
        if (!checkLocationOn(context)) {
            new AlertDialog.Builder(context)
                    .setMessage("我们需要打开位置服务才可以搜索蓝牙设备，请打开位置服务")
                    .setCancelable(false)
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            context.finish();
                        }
                    })
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openGPS(context);
                        }
                    }).show();
        }
    }

    public static void openGPS(Activity context) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivityForResult(intent, REQUEST_OPEN_LOCATION);
    }

}
