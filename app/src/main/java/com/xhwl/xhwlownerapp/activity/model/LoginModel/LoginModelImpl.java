package com.xhwl.xhwlownerapp.activity.model.LoginModel;

import android.os.Handler;
import android.text.TextUtils;

import com.xhwl.xhwlownerapp.activity.presenter.LoginPresenter.OnLoginFinishedListener;

/**
 * Created by Administrator on 2018/1/23.
 */

public class LoginModelImpl implements LoginModel {
    @Override
    public void login(final String userName, final String passWord, final OnLoginFinishedListener listener) {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                boolean error = false;
                if (TextUtils.isEmpty(userName)){
                    listener.onUsernameError();//model层里面回调listener
                    error = true;
                }
                if (TextUtils.isEmpty(passWord)){
                    listener.onPasswordError();
                    error = true;
                }
                if (!error){
                    listener.onSuccess();
                }
            }
        }, 2000);
    }
}
