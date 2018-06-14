package com.xhwl.xhwlownerapp.net;



public interface HttpConnectionInter {
    void onFinish(String content);
    void onError(Exception e);
}
