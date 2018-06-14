package com.xhwl.xhwlownerapp.activity.View.LoginView;

import android.content.Intent;
import android.os.Bundle;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.MyAPP;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.UIUtils.dialog.SelfWhiteDialog;

public class OtherLoginDialogActivity extends BaseActivity {
    private SelfWhiteDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_login_dialog);
        SPUtils.put(this,"isLogin",false);
        MyAPP.Logout(OtherLoginDialogActivity.this);
        dialog = new SelfWhiteDialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setMessage("您的账号在另一台设备登录，是否重新登录");
        dialog.setTitle("下线通知");
        dialog.setYesOnclickListener("确定", new SelfWhiteDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                startActivity(new Intent(OtherLoginDialogActivity.this,LoginActivity.class));
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
    }
}
