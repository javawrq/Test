package com.xhwl.xhwlownerapp.activity.presenter.LoginPresenter;

/**
 * Created by Administrator on 2018/1/23.
 */

public interface OnLoginFinishedListener {
    void onUsernameError();//用户名错误

    void onPasswordError();//密码有误

    void onSuccess();//成功
}

