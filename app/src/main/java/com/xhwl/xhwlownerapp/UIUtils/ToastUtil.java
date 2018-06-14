package com.xhwl.xhwlownerapp.UIUtils;

import android.content.Context;
import android.widget.Toast;

import com.xhwl.xhwlownerapp.BuildConfig;


public class ToastUtil {

    private static Toast mToast = null;

    public static void show(Context context, String text) {
        if (mToast != null) {
            mToast.cancel();
        }
        if (context != null) {
            mToast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            mToast.setText(text);
            //mToast.setGravity(Gravity.BOTTOM, 0, 0);
            mToast.show();
        }
    }

    public static void show(Context context, int resId) {
        if (context != null) {
            context = context.getApplicationContext();
            show(context, context.getString(resId));
        }
    }

    public static void showDebug(String msg) {
        if (BuildConfig.DEBUG) {
            MyAPP appContext = MyAPP.getIns();
            Toast.makeText(appContext, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static void show(String msg) {
        MyAPP appContext = MyAPP.getIns();
        Toast.makeText(appContext, msg, Toast.LENGTH_SHORT).show();
    }

    public static void showSingleToast(String msg) {
        MyAPP appContext = MyAPP.getIns();
        if (mToast == null) {
            mToast = Toast.makeText(appContext, msg, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

    public static void showError(Context context, int error) {
        show("sssssss");
//        if (error == HttpCode.E_TOKEN ) {
//            show(context, R.string.login_failed);
//            MainApplication.get().setToken("");
//            MainApplication.get().setUser(null);
//            Intent intent = new Intent(context, LoginActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);//清空栈
//            context.startActivity(intent);
//            return;
//        } //else if(error == HttpCode.E_MISS_PARAM){//即缺少参数
//            showMiddle(context, R.string.net_error);
//            return;
//        } else if(error == HttpCode.E_INVAL_PARAM){
//            showMiddle(context, R.string.param_error);
//            return;
//        } else if(error == HttpCode.E_DATABASE){
//            showMiddle(context, R.string.data_error);
//            return;
//        } else if(error == HttpCode.E_INNER){
//            showMiddle(context, R.string.inner_error);
//            return;
//        } else if(error == HttpCode.E_CODE){
//            showMiddle(context, R.string.code_error);
//            return;
//        } else if(error == HttpCode.E_GET_CODE){
//            showMiddle(context, R.string.get_code_error);
//            return;
//        } else if(error == HttpCode.E_USED_PHONE){
//            showMiddle(context, R.string.phone_error);
//            return;
//        } else if(error < HttpCode.E_SERVER){
//            showMiddle(context, R.string.server_error);
//            return;
//        }
    }
}
