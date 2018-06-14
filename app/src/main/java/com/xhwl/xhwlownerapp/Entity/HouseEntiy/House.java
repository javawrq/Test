package com.xhwl.xhwlownerapp.Entity.HouseEntiy;

/**
 * Created by Administrator on 2018/4/20.
 */

public class House {
    private String houseName;//项目名
    private String houseBanNumber;//楼栋
    private String houseUnitNumber;//单元
    private String houseRoomNumber;//房号

    public House() {
    }

    public House(String houseName, String houseBanNumber, String houseUnitNumber, String houseRoomNumber) {
        this.houseName = houseName;
        this.houseBanNumber = houseBanNumber;
        this.houseUnitNumber = houseUnitNumber;
        this.houseRoomNumber = houseRoomNumber;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getHouseBanNumber() {
        return houseBanNumber;
    }

    public void setHouseBanNumber(String houseBanNumber) {
        this.houseBanNumber = houseBanNumber;
    }

    public String getHouseUnitNumber() {
        return houseUnitNumber;
    }

    public void setHouseUnitNumber(String houseUnitNumber) {
        this.houseUnitNumber = houseUnitNumber;
    }

    public String getHouseRoomNumber() {
        return houseRoomNumber;
    }

    public void setHouseRoomNumber(String houseRoomNumber) {
        this.houseRoomNumber = houseRoomNumber;
    }
}
