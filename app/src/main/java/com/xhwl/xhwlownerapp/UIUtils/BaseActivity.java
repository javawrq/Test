package com.xhwl.xhwlownerapp.UIUtils;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.xhwl.xhwlownerapp.R;


/**
 * Created by Administrator on 2017/7/31.
 * activity基类
 */

public class BaseActivity extends FragmentActivity {
    private long fristTime = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ManagerActivity.getInstance().addActivity(this);//添加Activity到容器
        /**
         * 设置状态栏透明
         * */
        final int sdk = Build.VERSION.SDK_INT;
        Window window = this.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();

        if (sdk >= Build.VERSION_CODES.KITKAT) {
            int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            // 设置透明状态栏
            if ((params.flags & bits) == 0) {
                params.flags |= bits;
                window.setAttributes(params);
            }
        }
        //禁止activity横屏
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
    /**
     * Toast
     * */
    public void showToast(String mess){
        Toast.makeText(this, mess, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        //overridePendingTransition(R.anim.activity_in, R.anim.activity_in);
        overridePendingTransition(R.anim.slide_in_form_right, R.anim.slide_out_to_left);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        //overridePendingTransition(R.anim.activity_in, R.anim.activity_in);
        overridePendingTransition(R.anim.slide_in_form_left, R.anim.slide_out_to_left);
    }

    @Override
    public void finish() {
        super.finish();
        //overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
        overridePendingTransition(R.anim.slide_in_form_left, R.anim.slide_out_to_right);

    }

    /**
     *启动跳转
     * */
    public void startToAIctivity(Class cla){
        startActivity(new Intent(this,cla));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long secondTime = System.currentTimeMillis();
            ;     //fristTime = System.currentTimeMillis();//将当前的时间赋值给首次按下的时间
            if(secondTime  - fristTime >2000){
                showToast("再按一次退出");
                fristTime = System.currentTimeMillis();
            }else{
                ManagerActivity.getInstance().exit();
            }
        }
        return false;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ManagerActivity.getInstance().removeActivity(this);
    }

}
