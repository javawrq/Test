package com.xhwl.xhwlownerapp.activity.View.BluetoothOpenDoor.OpenDoor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiaowu on 2018/3/22 0022.
 */

public class MatchDoorVo {
    public List<Door> doorList = new ArrayList<>();
    public String openData;

    public static class Door{
        private String doorID;
        private String doorName;
        private String doorPath;
        private String connectionKey;
        private String keyID;
        private OtherInfo otherInfo;
        private String doorTyp;

        public OtherInfo getOtherInfo() {
            return otherInfo;
        }

        public Door setOtherInfo(OtherInfo otherInfo) {
            this.otherInfo = otherInfo;
            return this;
        }

        public Door(String doorID, String doorName, String doorPath, String connectionKey, String keyID, String doorTyp, OtherInfo otherInfo) {
            this.doorID = doorID;
            this.doorName = doorName;
            this.doorPath = doorPath;
            this.connectionKey = connectionKey;
            this.keyID = keyID;
            this.doorTyp = doorTyp;
            this.otherInfo = otherInfo;
        }

        public Door(String doorID, String doorName, String doorPath, String connectionKey, String keyID, String doorTyp) {
            this.doorID = doorID;
            this.doorName = doorName;
            this.doorPath = doorPath;
            this.connectionKey = connectionKey;
            this.keyID = keyID;
            this.doorTyp = doorTyp;
        }


        public Door() {
        }

        public String getDoorID() {
            return doorID;
        }

        public void setDoorID(String doorID) {
            this.doorID = doorID;
        }

        public String getDoorName() {
            return doorName;
        }

        public void setDoorName(String doorName) {
            this.doorName = doorName;
        }

        public String getDoorPath() {
            return doorPath;
        }

        public void setDoorPath(String doorPath) {
            this.doorPath = doorPath;
        }

        public String getConnectionKey() {
            return connectionKey;
        }

        public void setConnectionKey(String connectionKey) {
            this.connectionKey = connectionKey;
        }

        public String getKeyID() {
            return keyID;
        }

        public void setKeyID(String keyID) {
            this.keyID = keyID;
        }

        public static class OtherInfo{
            private String passWord;
            private String mac;

            public OtherInfo(String passWord, String mac) {
                this.passWord = passWord;
                this.mac = mac;
            }

            public OtherInfo() {
            }

            public String getPassWord() {
                return passWord;
            }

            public OtherInfo setPassWord(String passWord) {
                this.passWord = passWord;
                return this;
            }

            public String getMac() {
                return mac;
            }

            public OtherInfo setMac(String mac) {
                this.mac = mac;
                return this;
            }
        }

        public String getDoorTyp() {
            return doorTyp;
        }

        public Door setDoorTyp(String doorTyp) {
            this.doorTyp = doorTyp;
            return this;
        }
    }
}
