package com.xhwl.xhwlownerapp.activity.model.LoginModel;

import com.xhwl.xhwlownerapp.activity.presenter.LoginPresenter.OnLoginFinishedListener;

/**
 * Created by Administrator on 2018/1/23.
 * 登录
 */

public interface LoginModel {
    void login(String userName, String passWord, OnLoginFinishedListener listener);
}
