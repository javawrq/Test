package com.xhwl.xhwlownerapp.Entity.Weather;

/**
 * Created by Administrator on 2017/9/27.
 */

public class WeatherClass {
    private String tmp;//气温
    private String hum;//空气湿度
    private String pres;//气压
    private String txt;//天气状况
    private String max;//最高气温
    private String min;//最低气温
    private String uv;//紫外线
    private String code;//状态码

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTmp() {
        return tmp;
    }

    public void setTmp(String tmp) {
        this.tmp = tmp;
    }

    public String getHum() {
        return hum;
    }

    public void setHum(String hum) {
        this.hum = hum;
    }

    public String getPres() {
        return pres;
    }

    public void setPres(String pres) {
        this.pres = pres;
    }

    public String getTxt() {
        return txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getUv() {
        return uv;
    }

    public void setUv(String uv) {
        this.uv = uv;
    }
}
