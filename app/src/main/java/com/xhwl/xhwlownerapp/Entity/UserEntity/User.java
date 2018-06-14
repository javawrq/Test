package com.xhwl.xhwlownerapp.Entity.UserEntity;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/2/26.
 */

public class User implements Serializable{
    private String userName;//用户姓名
    private String telephone;//用户手机号码
    private String sex;//用户性别
    private String token;//用户token
    private String sysAccountId;//用户ID
    private String imageUrl;//头像地址
    private String nickName;//用户昵称
    private String weiboNickName;//微博昵称
    private String qqnickName;//QQ昵称
    private String weChatNickName;//微信昵称
    private String weChat;//微信

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSysAccountId() {
        return sysAccountId;
    }

    public void setSysAccountId(String sysAccountId) {
        this.sysAccountId = sysAccountId;
    }

    public String getWeChatNickName() {
        return weChatNickName;
    }

    public void setWeChatNickName(String weChatNickName) {
        this.weChatNickName = weChatNickName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getWeiboNickName() {
        return weiboNickName;
    }

    public void setWeiboNickName(String weiboNickName) {
        this.weiboNickName = weiboNickName;
    }

    public String getQqnickName() {
        return qqnickName;
    }

    public void setQqnickName(String qqnickName) {
        this.qqnickName = qqnickName;
    }

    public String getWeChat() {
        return weChat;
    }

    public void setWeChat(String weChat) {
        this.weChat = weChat;
    }
}
