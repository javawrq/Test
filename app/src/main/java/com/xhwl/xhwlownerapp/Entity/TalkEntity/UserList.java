package com.xhwl.xhwlownerapp.Entity.TalkEntity;

/**
 * Created by Administrator on 2018/5/10.
 */

public class UserList {
    private String name;
    private String workCode;
    private String uid;

    public UserList() {
    }

    public UserList(String name, String workCode, String uid) {
        this.name = name;
        this.workCode = workCode;
        this.uid = uid;
    }

    public String getWorkCode() {
        return workCode;
    }

    public void setWorkCode(String workCode) {
        this.workCode = workCode;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
