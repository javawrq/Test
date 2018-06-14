package com.xhwl.xhwlownerapp.activity.presenter.LoginPresenter;

/**
 * Created by Administrator on 2018/1/23.
 */

public interface LoginPresenter {
    void validateCredentials(String username, String password);

    void onDestroy();
}
