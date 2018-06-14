package com.xhwl.xhwlownerapp.activity.View.BackgroundMusic.entity;

/**
 * Created by Administrator on 2018/3/16.
 */

public class MediaDevice {
    private String terminal_type;
    private String name;
    private int deciceId;
    private String status;
    private String exten;
    private String client_ip;
    private String access_token;

    public String getTerminal_type() {
        return terminal_type;
    }

    public void setTerminal_type(String terminal_type) {
        this.terminal_type = terminal_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDeciceId() {
        return deciceId;
    }

    public void setDeciceId(int deciceId) {
        this.deciceId = deciceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExten() {
        return exten;
    }

    public void setExten(String exten) {
        this.exten = exten;
    }

    public String getClient_ip() {
        return client_ip;
    }

    public void setClient_ip(String client_ip) {
        this.client_ip = client_ip;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }
}
