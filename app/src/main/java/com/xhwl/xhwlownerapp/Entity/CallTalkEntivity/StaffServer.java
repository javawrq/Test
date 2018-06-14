package com.xhwl.xhwlownerapp.Entity.CallTalkEntivity;

/**
 * Created by Administrator on 2018/5/7.
 */

public class StaffServer {
    private String name;
    private String workCode;
    private String uid;

    public StaffServer(String name, String workCode, String uid) {
        this.name = name;
        this.workCode = workCode;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
