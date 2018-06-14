package com.xhwl.xhwlownerapp.Entity.TalkEntity;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/5/12.
 */

public class DoorInfoList implements Serializable {
    private String uid;//云对讲UID
    private int id;//门口机id
    private String pathId;//门口机路址
    private String projectCode;//项目id
    private String machineName;//门口机name

    public DoorInfoList() {
    }

    public DoorInfoList(String uid, int id, String pathId, String projectCode, String machineName) {
        this.uid = uid;
        this.id = id;
        this.pathId = pathId;
        this.projectCode = projectCode;
        this.machineName = machineName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPathId() {
        return pathId;
    }

    public void setPathId(String pathId) {
        this.pathId = pathId;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }
}
