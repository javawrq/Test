package com.xhwl.xhwlownerapp.UIUtils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2017/8/26.
 * 显示吐司
 */

public class ShowToast {
    private static Context context = null;

    public ShowToast(Context context){
        this.context=context;
    }

    public static void show(String mess){
        if(context != null){
            Toast.makeText(context,mess, Toast.LENGTH_SHORT).show();
        }
    }
}
