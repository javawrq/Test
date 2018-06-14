package com.xhwl.xhwlownerapp.Entity.TalkEntity;

/**
 * Created by Administrator on 2018/5/16.
 */

public class TalkingHistory {
    private String name;
    private int id;
    private String uid;
    private String callType;

    public TalkingHistory(String name, int id, String uid, String callType) {
        this.name = name;
        this.id = id;
        this.uid = uid;
        this.callType = callType;
    }

    public TalkingHistory() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }
}
