package com.xhwl.xhwlownerapp.Entity.ProjectEntity;

/**
 * Created by Administrator on 2018/4/3.
 */

public class Project {
    private String proName;//项目名
    private String code;//项目Code
    private String projectCode;//项目Code
    private String nodeType;//云瞳需要的参数
    private String nodeID;//云瞳列表
    private String divisionName;//地区名
    private String proId;//项目id
    private String entranceCode;//远程开门需要的Code
    private boolean isWorkstation;//是否有工作站

    public boolean getIsWorkstation() {
        return isWorkstation;
    }

    public void setIsWorkstation(boolean workstation) {
        isWorkstation = workstation;
    }

    public String getEntranceCode() {
        return entranceCode;
    }

    public void setEntranceCode(String entranceCode) {
        this.entranceCode = entranceCode;
    }

    public String getProId() {
        return proId;
    }

    public void setProId(String proId) {
        this.proId = proId;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getNodeID() {
        return nodeID;
    }

    public void setNodeID(String nodeID) {
        this.nodeID = nodeID;
    }

    public String getDivisionName() {
        return divisionName;
    }

    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }
}
