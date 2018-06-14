package com.xhwl.xhwlownerapp.Entity.HouseEntiy;

/**
 * Created by Administrator on 2018/5/12.
 */

public class RoomInfo {
    private String code;
    private String name;
    private String id;

    public RoomInfo(String code, String name, String id) {
        this.code = code;
        this.name = name;
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
