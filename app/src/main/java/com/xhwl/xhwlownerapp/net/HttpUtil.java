package com.xhwl.xhwlownerapp.net;

import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2017/3/13 0013.
 */

public class HttpUtil {
    
    public static String doGet(String urlpath){
        HttpURLConnection conn = null;
        StringBuffer sb = new StringBuffer();
        try {

            URL url = new URL(urlpath);

            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            conn.setConnectTimeout(5000);

            if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStream is = conn.getInputStream();
                int len;
                byte [] b = new byte[1024];
                while((len = is.read(b)) != -1){
                    sb.append(new String(b,0,len));
                }
                is.close();
                conn.disconnect();
            }else{
                Log.e("TAG","请求失败");
            }
        } catch (Exception e) {
            Log.e("TAG","异常");
            e.printStackTrace();
        }finally {
            conn.disconnect();
        }
        return sb.toString();
    }

    public static String doPost(final String path, final String data) {

        try {
            URL url = new URL(path);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST"); //默认请求 就是get  要大写

            conn.setConnectTimeout(5000);

            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            conn.setRequestProperty("Content-Length", data.length()+"");

            conn.setDoOutput(true);// 设置一个标记 允许输出

            conn.getOutputStream().write(data.getBytes());

            int code = conn.getResponseCode(); //200  代表获取服务器资源全部成功  206请求部分资源

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
