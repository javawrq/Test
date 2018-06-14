package com.xhwl.xhwlownerapp.net;

import android.util.Log;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


public class HttpConnectionTools {

    public static void HttpServler(final String path , final String data , final HttpConnectionInter hct){
        new Thread(){
            @Override
            public void run() {
                try {

                    URL url = new URL(path);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("POST"); //默认请求 就是get  要大写

                    conn.setConnectTimeout(5000);

                    conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                    conn.setRequestProperty("Content-Length", data.length()+"");

                    conn.setDoOutput(true);//设置一个标记 允许输出

                    conn.getOutputStream().write(data.getBytes());

                    int code = conn.getResponseCode(); //200  代表获取服务器资源全部成功  206请求部分资源
                    Log.e("code",code+"网络请求码");
                    if (code == 200) {

                        InputStream inputStream = conn.getInputStream();

                        String content = StreamTools.readStream(inputStream);

                        if(hct != null){
                            hct.onFinish(content);
                            Log.e("tga1",content);
                        }
                    }else{
                        Log.e("tga1","no200");
                    }
                } catch (Exception e) {
                    if(hct!=null){
                        hct.onError(e);
                    }
                    Log.e("tga","异常");
                }
            }
        }.start();
    }

    public static void HttpServler(final String path , final HttpConnectionInter hct){

        new Thread(){
            @Override
            public void run() {
                try {
                    URL url = new URL(path);

                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setRequestMethod("GET"); //默认请求 就是get  要大写

                    conn.setConnectTimeout(5000);

                    int code = conn.getResponseCode(); //200  代表获取服务器资源全部成功  206请求部分资源
                    Log.e("tga",code+"请求码");
                    if (code == 200) {

                        InputStream inputStream = conn.getInputStream();

                        String content = StreamTools.readStream(inputStream);
                        Log.e("tga",content);

                        if(hct != null){
                            hct.onFinish(content);
                        }
                    }else{
                        Log.e("tga1","no200");
                    }

                } catch (Exception e) {
                    if(hct!=null){
                        hct.onError(e);
                    }
                    Log.e("tga","异常");
                }
            }
        }.start();
    }


    public static String HttpData(String dataname, String datas){
        String data = "";
        try {
            String a = dataname+"=" + URLEncoder.encode(datas+"", "utf-8");
            data = a;
        } catch (UnsupportedEncodingException e) {
            Log.e("异常","数据异常");
            e.printStackTrace();
        }
        return data ;
    }
    public static String HttpData(String dataname, String datas , String datanametwo, String datastwo){
        String data = "";
        try {
            String a = dataname+"=" + URLEncoder.encode(datas+"", "utf-8")//
                    +"&"+datanametwo+"=" + URLEncoder.encode(datastwo+"","utf-8");
            data = a;
        } catch (UnsupportedEncodingException e) {
            Log.e("异常","数据异常");
            e.printStackTrace();
        }
        return data ;
    }
    public static String HttpData(String dataname, int datas , String datanametwo, String datastwo){
        String data = "";
        try {
            String a = dataname+"=" + datas//
                    +"&"+datanametwo+"=" + URLEncoder.encode(datastwo+"","utf-8");
            data = a;
            Log.e("data",data);
        } catch (UnsupportedEncodingException e) {
            Log.e("异常","数据异常");
            e.printStackTrace();
        }
        return data ;
    }

    public static String HttpData(String dataname, String datas , String datanametwo, int datastwo , String datanametr, int datastr ){
        String data = "";
        try {
            String a = dataname+"=" + URLEncoder.encode(datas+"", "utf-8")//
                    +"&"+datanametwo+"=" + datastwo
                    +"&"+datanametr+"=" + datastr;
            data = a;
        } catch (UnsupportedEncodingException e) {
            Log.e("异常","数据异常");
            e.printStackTrace();
        }
        return data ;
    }

    public static String HttpData(String dataname, String datas , String datanametwo, int datastwo , String datanametr, String datastr ){
        String data = "";
        try {
            String a = dataname+"=" + URLEncoder.encode(datas+"", "utf-8")//
                    +"&"+datanametwo+"=" + datastwo
                    +"&"+datanametr+"=" +URLEncoder.encode(datastr+"", "utf-8");
            data = a;
        } catch (UnsupportedEncodingException e) {
            Log.e("异常","数据异常");
            e.printStackTrace();
        }
        return data ;
    }

    public static String HttpData(String dataname, String datas , String datanametwo, String datastwo , String datanametr, boolean datastr ){
        String data = "";
        try {
            String a = dataname+"=" + URLEncoder.encode(datas+"", "utf-8")//
                    +"&"+datanametwo+"=" + URLEncoder.encode(datastwo+"","utf-8")//
                    +"&"+datanametr+"=" + datastr;
            data = a;
        } catch (UnsupportedEncodingException e) {
            Log.e("异常","数据异常");
            e.printStackTrace();
        }
        return data ;
    }

    public static String HttpData(String dataname, String datas , String datanametwo, String datastwo , String datanametr, String datastr ){
        String data = "";
        try {
            String a = dataname+"=" + URLEncoder.encode(datas+"", "utf-8")//
                    +"&"+datanametwo+"=" + URLEncoder.encode(datastwo+"","utf-8")//
                    +"&"+datanametr+"=" + URLEncoder.encode(datastr+"","utf-8");
            data = a;
        } catch (UnsupportedEncodingException e) {
            Log.e("异常","数据异常");
            e.printStackTrace();
        }
        return data ;
    }

    public static String HttpData(String dataname, String datas , String datanametwo, String datastwo , String datanametr, String datastr , String datanamefr, String datasfr ){
        String data = "";
        try {
            String a = dataname+"=" + URLEncoder.encode(datas+"", "utf-8")//
                    +"&"+datanametwo+"=" + URLEncoder.encode(datastwo+"","utf-8")//
                    +"&"+datanametr+"=" + URLEncoder.encode(datastr+"","utf-8")//
                    +"&"+datanamefr+"=" + URLEncoder.encode(datasfr+"","utf-8");
            data = a;
        } catch (UnsupportedEncodingException e) {
            Log.e("异常","数据异常");
            e.printStackTrace();
        }
        return data ;
    }

    public static String HttpData(String dataname, String datas , String datanametwo, String datastwo , String datanametr, String datastr , String datanamefr, String datasfr , String datanamefou, String datasfou  ){
        String data = "";
        try {
            String a = dataname+"=" + URLEncoder.encode(datas+"", "utf-8")//
                    +"&"+datanametwo+"=" + URLEncoder.encode(datastwo+"","utf-8")//
                    +"&"+datanametr+"=" + URLEncoder.encode(datastr+"","utf-8")//
                    +"&"+datanamefr+"=" + URLEncoder.encode(datasfr+"","utf-8")//
                    +"&"+datanamefou+"=" + URLEncoder.encode(datasfou+"","utf-8");
            data = a;
        } catch (UnsupportedEncodingException e) {
            Log.e("异常","数据异常");
            e.printStackTrace();
        }
        return data ;
    }

    public static String HttpData(String dataname, int datas , String datanametwo, String datastwo , String datanametr, String datastr , String datanamefr, String datasfr , String datanamefou, String datasfou  ){
        String data = "";
        try {
            String a = dataname+"=" + datas//
                    +"&"+datanametwo+"=" + URLEncoder.encode(datastwo+"","utf-8")//
                    +"&"+datanametr+"=" + URLEncoder.encode(datastr+"","utf-8")//
                    +"&"+datanamefr+"=" + URLEncoder.encode(datasfr+"","utf-8")//
                    +"&"+datanamefou+"=" + URLEncoder.encode(datasfou+"","utf-8");
            data = a;
        } catch (UnsupportedEncodingException e) {
            Log.e("异常","数据异常");
            e.printStackTrace();
        }
        return data ;
    }

    public static String HttpData(String dataname, String datas , String datanametwo, String datastwo , String datanametr, String datastr , String datanamefr, String datasfr , String datanamefou, String datasfou , String datanames, String datass ){
        String data = "";
        try {
            String a = dataname+"=" + URLEncoder.encode(datas+"", "utf-8")//
                    +"&"+datanametwo+"=" + URLEncoder.encode(datastwo+"","utf-8")//
                    +"&"+datanametr+"=" + URLEncoder.encode(datastr+"","utf-8")//
                    +"&"+datanamefr+"=" + URLEncoder.encode(datasfr+"","utf-8")//
                    +"&"+datanamefou+"=" + URLEncoder.encode(datasfou+"","utf-8")//
                    +"&"+datanames+"=" + URLEncoder.encode(datass+"","utf-8");
            data = a;
        } catch (UnsupportedEncodingException e) {
            Log.e("异常","数据异常");
            e.printStackTrace();
        }
        return data ;
    }

    public static String HttpData(String dataname, String datas , String datanametwo, String datastwo , String datanametr, String datastr , String datanamefr, String datasfr , String datanamefou, String datasfou , String datanames, int datass ){
        String data = "";
        try {
            String a = dataname+"=" + URLEncoder.encode(datas+"", "utf-8")//
                    +"&"+datanametwo+"=" + URLEncoder.encode(datastwo+"","utf-8")//
                    +"&"+datanametr+"=" + URLEncoder.encode(datastr+"","utf-8")//
                    +"&"+datanamefr+"=" + URLEncoder.encode(datasfr+"","utf-8")//
                    +"&"+datanamefou+"=" + URLEncoder.encode(datasfou+"","utf-8")//
                    +"&"+datanames+"=" + datass;
            data = a;
        } catch (UnsupportedEncodingException e) {
            Log.e("异常","数据异常");
            e.printStackTrace();
        }
        return data ;
    }

    public static String HttpData(String onename,String onedata,String dataname, String datas , String datanametwo, String datastwo , String datanametr, String datastr , String datanamefr, String datasfr , String datanamefou, String datasfou , String datanames, int datass ){
        String data = "";
        try {
            String a = onename+"=" + URLEncoder.encode(onedata+"", "utf-8")
                    +"&"+dataname+"=" + URLEncoder.encode(datas+"", "utf-8")//
                    +"&"+datanametwo+"=" + URLEncoder.encode(datastwo+"","utf-8")//
                    +"&"+datanametr+"=" + URLEncoder.encode(datastr+"","utf-8")//
                    +"&"+datanamefr+"=" + URLEncoder.encode(datasfr+"","utf-8")//
                    +"&"+datanamefou+"=" + URLEncoder.encode(datasfou+"","utf-8")//
                    +"&"+datanames+"=" + datass;
            data = a;
        } catch (UnsupportedEncodingException e) {
            Log.e("异常","数据异常");
            e.printStackTrace();
        }
        return data ;
    }

    public static String HttpData(String dataname, String datas , String datanametwo, String datastwo , String datanametr, String datastr , String datanamefr, String datasfr , String datanamefou, String datasfou , String datanames, String[] datass ){
        String data = "";
        try {
            String a = dataname+"=" + URLEncoder.encode(datas+"", "utf-8")//
                    +"&"+datanametwo+"=" + URLEncoder.encode(datastwo+"","utf-8")//
                    +"&"+datanametr+"=" + URLEncoder.encode(datastr+"","utf-8")//
                    +"&"+datanamefr+"=" + URLEncoder.encode(datasfr+"","utf-8")//
                    +"&"+datanamefou+"=" + URLEncoder.encode(datasfou+"","utf-8")//
                    +"&"+datanames+"=" + URLEncoder.encode(datass+"","utf-8");
            data = a;
        } catch (UnsupportedEncodingException e) {
            Log.e("异常","数据异常");
            e.printStackTrace();
        }
        return data ;
    }

    public static String HttpData(String dataname, String datas , String datanametwo, String datastwo , String datanametr, String datastr ,
                                  String datanamefr, String datasfr , String datanamefou, String datasfou , String datanames,
                                  String datass,String datanameseven ,String dataseven,String datanameeight,String dataeight){
        String data = "";
        try {
            String a = dataname+"=" + URLEncoder.encode(datas+"", "utf-8")//
                    +"&"+datanametwo+"=" + URLEncoder.encode(datastwo+"","utf-8")//
                    +"&"+datanametr+"=" + URLEncoder.encode(datastr+"","utf-8")//
                    +"&"+datanamefr+"=" + URLEncoder.encode(datasfr+"","utf-8")//
                    +"&"+datanamefou+"=" + URLEncoder.encode(datasfou+"","utf-8")//
                    +"&"+datanames+"=" + URLEncoder.encode(datass+"","utf-8")
                    +"&"+datanameseven+"=" + URLEncoder.encode(dataseven+"","utf-8")
                    +"&"+datanameeight+"=" + URLEncoder.encode(dataeight+"","utf-8");
            data = a;
        } catch (UnsupportedEncodingException e) {
            Log.e("异常","数据异常");
            e.printStackTrace();
        }
        return data ;
    }

    public static String HttpData(String dataname, String datas , String datanametwo, String datastwo ,
                                  String datanametr, String datastr , String datanamefr, String datasfr ,
                                  String datanamefou, String datasfou , String datanamefi,String datafi,
                                  String datanames, String datass, String datanamesix,String datasix,
                                  String datanameseven,String dataseven){
        String data = "";
        try {
            String a = dataname+"=" + URLEncoder.encode(datas+"", "utf-8")//
                    +"&"+datanametwo+"=" + URLEncoder.encode(datastwo+"","utf-8")//
                    +"&"+datanametr+"=" + URLEncoder.encode(datastr+"","utf-8")//
                    +"&"+datanamefr+"=" + URLEncoder.encode(datasfr+"","utf-8")//
                    +"&"+datanamefou+"=" + URLEncoder.encode(datasfou+"","utf-8")//
                    +"&"+datanames+"=" + URLEncoder.encode(datass+"","utf-8")
                    +"&"+datanamefi+"=" + URLEncoder.encode(datafi+"","utf-8")
                    +"&"+datanamesix+"=" + URLEncoder.encode(datasix+"","utf-8")
                    +"&"+datanameseven+"=" + URLEncoder.encode(dataseven+"","utf-8");
            data = a;
        } catch (UnsupportedEncodingException e) {
            Log.e("异常","数据异常");
            e.printStackTrace();
        }
        return data ;
    }

    public static String HttpData(String dataname, String datas , String datanametwo, String datastwo ,
                                  String datanametr, String datastr , String datanamefr, String datasfr ,
                                  String datanamefou, String datasfou , String datanamefi,String datafi,
                                  String datanames, String datass, String datanamesix,String datasix,
                                  String datanameseven,String dataseven,String datanameight,String dataeight){
        String data = "";
        try {
            String a = dataname+"=" + URLEncoder.encode(datas+"", "utf-8")//
                    +"&"+datanametwo+"=" + URLEncoder.encode(datastwo+"","utf-8")//
                    +"&"+datanametr+"=" + URLEncoder.encode(datastr+"","utf-8")//
                    +"&"+datanamefr+"=" + URLEncoder.encode(datasfr+"","utf-8")//
                    +"&"+datanamefou+"=" + URLEncoder.encode(datasfou+"","utf-8")//
                    +"&"+datanames+"=" + URLEncoder.encode(datass+"","utf-8")
                    +"&"+datanamefi+"=" + URLEncoder.encode(datafi+"","utf-8")
                    +"&"+datanamesix+"=" + URLEncoder.encode(datasix+"","utf-8")
                    +"&"+datanameseven+"=" + URLEncoder.encode(dataseven+"","utf-8")
                    +"&"+datanameight+"=" + URLEncoder.encode(dataeight+"","utf-8");
            data = a;
        } catch (UnsupportedEncodingException e) {
            Log.e("异常","数据异常");
            e.printStackTrace();
        }
        return data ;
    }


}
