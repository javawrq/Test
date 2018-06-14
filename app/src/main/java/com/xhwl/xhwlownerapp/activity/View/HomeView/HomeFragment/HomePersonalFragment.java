package com.xhwl.xhwlownerapp.activity.View.HomeView.HomeFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xhwl.xhwlownerapp.Entity.UserEntity.User;
import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.CircleImageView;
import com.xhwl.xhwlownerapp.UIUtils.MyAPP;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.UIUtils.ToastUtil;
import com.xhwl.xhwlownerapp.activity.View.LoginView.LoginActivity;
import com.xhwl.xhwlownerapp.activity.View.Shop.XQShopActivity;
import com.xhwl.xhwlownerapp.activity.View.UserInfoView.AccountInfo.AccountSecurityActivity;
import com.xhwl.xhwlownerapp.activity.View.UserInfoView.BasicsInfo.MyInfoActivity;
import com.xhwl.xhwlownerapp.activity.View.UserInfoView.DeviceInfo.MyDeviceActivity;
import com.xhwl.xhwlownerapp.activity.View.UserInfoView.FamilyInfo.MyFamilyActivity;
import com.xhwl.xhwlownerapp.activity.View.UserInfoView.ResidentialInfo.MyResidentialActivity;
import com.xhwl.xhwlownerapp.net.Constant;
import com.xhwl.xhwlownerapp.net.HttpConnectionInter;
import com.xhwl.xhwlownerapp.net.HttpConnectionTools;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 个人中心
 */
public class HomePersonalFragment extends Fragment implements View.OnClickListener {
    private View view;
    private CircleImageView mMyInfoHeadImg;
    /**
     * 高龙群
     */
    private TextView mMyInfoName;
    private AutoLinearLayout mMyInfoFamily;
    private AutoLinearLayout mMyInfoResident;
    private AutoLinearLayout mMyInfoXqshare;
    private AutoLinearLayout mMyInfoDevice;
    private AutoLinearLayout mMyInfoCar;
    private AutoLinearLayout mMyInfoPay;
    private AutoLinearLayout mMyInfoAccountSecurity;
    private String token,userType;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the rb_nobtn_selector for this fragment
        view = inflater.inflate(R.layout.fragment_home_personal, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        user = new User();
        userType = SPUtils.get(getActivity(),"userType","");
        token = SPUtils.get(getActivity(),"userToken","");
        mMyInfoHeadImg = (CircleImageView) view.findViewById(R.id.my_info_head_img);
        mMyInfoHeadImg.setOnClickListener(this);
        mMyInfoName = (TextView) view.findViewById(R.id.my_info_name);
        mMyInfoFamily = (AutoLinearLayout) view.findViewById(R.id.my_info_family);
        mMyInfoFamily.setOnClickListener(this);
        mMyInfoResident = (AutoLinearLayout) view.findViewById(R.id.my_info_resident);
        mMyInfoResident.setOnClickListener(this);
        mMyInfoXqshare = (AutoLinearLayout) view.findViewById(R.id.my_info_xqshare);
        mMyInfoXqshare.setOnClickListener(this);
        mMyInfoDevice = (AutoLinearLayout) view.findViewById(R.id.my_info_device);
        mMyInfoDevice.setOnClickListener(this);
        mMyInfoCar = (AutoLinearLayout) view.findViewById(R.id.my_info_car);
        mMyInfoCar.setOnClickListener(this);
        mMyInfoPay = (AutoLinearLayout) view.findViewById(R.id.my_info_pay);
        mMyInfoPay.setOnClickListener(this);
        mMyInfoAccountSecurity = (AutoLinearLayout) view.findViewById(R.id.my_info_account_security);
        mMyInfoAccountSecurity.setOnClickListener(this);
        getUserInfoByToken(token);
        Log.e("getUserInfoByToken","getUserInfoByToken");
    }

    @Override
    public void onStart() {
        super.onStart();
        getUserInfoByToken(token);
    }

    private void initDate() {
        //设置头像
        Picasso.with(getActivity())
                .load(user.getImageUrl())
                .error(R.drawable.headimg).into(mMyInfoHeadImg);
        if(user.getNickName() != "null"){
            mMyInfoName.setText(user.getNickName());
        }else {
            mMyInfoName.setText("未设置");
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            default:
                break;
            case R.id.my_info_head_img:
                //用户基础信息
                intent.setClass(getContext(), MyInfoActivity.class);
                //intent.putExtra("user",user);
                startActivity(intent);
                break;
            case R.id.my_info_family:
                //我的家庭
                if(!"visitor".equals(userType)){
                    intent.setClass(getContext(), MyFamilyActivity.class);
                    startActivity(intent);
//                    ToastUtil.show("暂未开放，尽请期待！");
                }else {
                    ToastUtil.show("您还不是业主哦");
                }

                break;
            case R.id.my_info_resident:
                //我的小区
                if(!"visitor".equals(userType)){
                    intent.setClass(getContext(), MyResidentialActivity.class);
                    startActivity(intent);
                    //ShowToast.show("暂未开放，尽请期待！");
                }else {
                    ToastUtil.show("您还不是业主哦");
                }
                break;
            case R.id.my_info_xqshare:
                //小七之家
                intent.setClass(getContext(), XQShopActivity.class);
                startActivity(intent);
//                ShowToast.show("暂未开放，尽请期待！");
                break;
            case R.id.my_info_device:
                //我的设备
                if(!"visitor".equals(userType)){
                    intent.setClass(getContext(), MyDeviceActivity.class);
                    startActivity(intent);
//                    ToastUtil.show("暂未开放，尽请期待！");
                }else {
                    ToastUtil.show("您还不是业主哦");
                }
                break;
            case R.id.my_info_car:
                //我的爱车
                ToastUtil.show("暂未开放，尽请期待！");
                break;
            case R.id.my_info_pay:
                //缴费账单
                ToastUtil.show("暂未开放，尽请期待！");
                break;
            case R.id.my_info_account_security:
                //账号安全
                intent.setClass(getContext(), AccountSecurityActivity.class);
                startActivity(intent);
                break;
        }
    }

    //获取个人信息
    private void getUserInfoByToken(String token){
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.GETUSERINFO,
                HttpConnectionTools.HttpData("token", token), new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if(errorCode == 200){
                                //成功
                                String result = jsonObject.getString("result");
                                jsonObject = new JSONObject(result);
                                String id = jsonObject.getString("id");
                                String sysUserName = jsonObject.getString("sysUserName");
                                String imageUrl = jsonObject.getString("imageUrl");
                                String nickName = jsonObject.getString("nickName");
                                String weiboNickName = jsonObject.getString("weiboNickName");
                                String qqnickName = jsonObject.getString("qqnickName");
                                String weChatNickName = jsonObject.getString("weChatNickName");
                                String sex = jsonObject.getString("sex");
                                String name = jsonObject.getString("name");
                                String token = jsonObject.getString("token");
                                SPUtils.put(getContext(),"imageUrl",imageUrl);
                                SPUtils.put(getContext(),"nickName",nickName);
                                SPUtils.put(getContext(),"id",id);
                                SPUtils.put(getContext(),"sex",sex);
                                SPUtils.put(getContext(),"name",name);
                                SPUtils.put(getContext(),"sysUserName",sysUserName);
                                SPUtils.put(getContext(),"weiboNickName",weiboNickName);
                                SPUtils.put(getContext(),"weChatNickName",weChatNickName);
                                SPUtils.put(getContext(),"qqnickName",qqnickName);
                                SPUtils.put(getContext(),"token",token);
                                user.setImageUrl(imageUrl);
                                user.setNickName(nickName);
                                user.setQqnickName(qqnickName);
                                user.setSysAccountId(id);
                                user.setSex(sex);
                                user.setTelephone(name);
                                user.setUserName(sysUserName);
                                user.setWeiboNickName(weiboNickName);
                                user.setWeChatNickName(weChatNickName);
                                user.setToken(token);
                                handler.sendEmptyMessage(200);
                            }else if(errorCode == 116){
                                //账户不存在，修改失败
                                handler.sendEmptyMessage(116);
                            }else if(errorCode == 400){
                                //token已过期，请重新登录
                                handler.sendEmptyMessage(400);
                            }else if(errorCode == 401){
                                //您已退出登录，请重新登录
                                handler.sendEmptyMessage(401);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Intent intent = new Intent();
            if(msg.what == 200){
                //成功
                initDate();
                //ShowToast.show("成功");
            }else if(msg.what == 116){
                //账户不存在，修改失败
                ToastUtil.showSingleToast("账户不存在");
            }else if(msg.what == 400){
                //您已退出登录，请重新登录
                ToastUtil.showSingleToast("您已退出登录，请重新登录");
                MyAPP.Logout(getActivity());
                intent.setClass(getActivity(), LoginActivity.class);
                startActivity(intent);
            }else if(msg.what == 401){
                //token已过期，请重新登录
                ToastUtil.showSingleToast("token已过期，请重新登录");
                MyAPP.Logout(getActivity());
                intent.setClass(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        }
    };

}
