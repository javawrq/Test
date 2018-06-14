package com.xhwl.xhwlownerapp.UIUtils;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.xhwl.xhwlownerapp.R;


/**
 * Created by Administrator on 2017/9/28.
 * fragment 基类
 */

public class BaseFrament extends Fragment {
    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.activity_in, R.anim.activity_in);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(R.anim.activity_in, R.anim.activity_in);
    }

    /**
     * Toast
     * */
    public void showToast(String mess){
        Toast.makeText(getActivity(), mess, Toast.LENGTH_SHORT).show();
    }

}
