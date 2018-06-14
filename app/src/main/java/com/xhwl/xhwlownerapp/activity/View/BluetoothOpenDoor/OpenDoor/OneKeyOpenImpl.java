package com.xhwl.xhwlownerapp.activity.View.BluetoothOpenDoor.OpenDoor;

import android.util.Log;

import com.xhwl.xhwlownerapp.net.Constant;
import com.xhwl.xhwlownerapp.net.HttpConnectionInter;
import com.xhwl.xhwlownerapp.net.HttpConnectionTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xiaowu on 2018/3/23 0023.
 */

public class OneKeyOpenImpl implements IOnekeyOpenModel {

    private MatchDoorVo matchDoorVo = new MatchDoorVo();
    private MatchDoorVo.Door door;
    private String doorID;
    private String doorName;
    private String doorPath;
    private String connectionKey;
    private String keyID;
    private MatchDoorVo.Door.OtherInfo otherInfo;
    private String doorTyp;

    @Override
    public void getMatchDoorList(String token, String projectCode, String userName, String phone, final onGetMatchDoorListListener listener) {
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.GETDOORBYPHONE,
                HttpConnectionTools.HttpData("token", token, "projectCode", projectCode, "userName", userName, "phone", phone),
                new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if(errorCode == 200){
                                String result = jsonObject.getString("result");
                                jsonObject = new JSONObject(result);
                                matchDoorVo.openData = jsonObject.optString("openData");
                                JSONArray jsonArray = jsonObject.getJSONArray("doorList");
                                for(int j = 0;j<jsonArray.length();j++){
                                    jsonObject = jsonArray.getJSONObject(j);
                                    doorID =  jsonObject.optString("doorID");
                                    doorName =  jsonObject.optString("doorName");
                                    doorPath =  jsonObject.optString("doorPath");
                                    connectionKey =  jsonObject.optString("connectionKey");
                                    keyID =  jsonObject.optString("keyID");
                                    doorTyp = jsonObject.optString("doorTyp");

                                    String otherinfo = jsonObject.optString("otherInfo");
                                    if(otherinfo != "null"){
                                        jsonObject = new JSONObject(otherinfo);
                                        String password = jsonObject.optString("passWord");
                                        String mac = jsonObject.optString("mac");
                                        otherInfo = new MatchDoorVo.Door.OtherInfo(password,mac);
                                        door = new MatchDoorVo.Door(doorID,doorName,doorPath,connectionKey,keyID,doorTyp,otherInfo);
                                        matchDoorVo.doorList.add(door);
                                    }
                                    door = new MatchDoorVo.Door(doorID,doorName,doorPath,connectionKey,keyID,doorTyp);
                                    matchDoorVo.doorList.add(door);
                                }
                                listener.onGetMatchDoorListSuccess(matchDoorVo);
                            }else {
                                listener.onGetMatchDoorListFailed("无门禁列表");
                                Log.e("OneKeyOpenImpl","无门禁列表");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        listener.onGetMatchDoorListFailed("无门禁列表");
                    }
                });
    }

    @Override
    public void getAllDoorList(String projectCode, String sign, String time, final onGetAllDoorListListener listener) {
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.GETDOORBYPROJECTCODE,
                HttpConnectionTools.HttpData("projectCode", projectCode, "sign", sign, "time", time),
                new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if (errorCode == 200) {
                                String result = jsonObject.getString("result");
                                jsonObject = new JSONObject(result);
                                JSONArray jsonArray = jsonObject.getJSONArray("doorList");
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    jsonObject = jsonArray.getJSONObject(j);
                                    doorID = jsonObject.getString("doorID");
                                    doorName = jsonObject.getString("doorName");
                                    doorPath = jsonObject.getString("doorPath");
                                    connectionKey = jsonObject.getString("connectionKey");
                                    keyID = jsonObject.getString("keyID");
                                    doorTyp = jsonObject.getString("doorTyp");

                                    String otherinfo = jsonObject.getString("otherInfo");
                                    if (otherinfo != "null") {
                                        jsonObject = new JSONObject(otherinfo);
                                        String password = jsonObject.getString("passWord");
                                        String mac = jsonObject.getString("mac");
                                        otherInfo = new MatchDoorVo.Door.OtherInfo(password, mac);
                                        door = new MatchDoorVo.Door(doorID, doorName, doorPath, connectionKey, keyID, doorTyp, otherInfo);
                                        matchDoorVo.doorList.add(door);
                                    } else {
                                        door = new MatchDoorVo.Door(doorID, doorName, doorPath, connectionKey, keyID, doorTyp);
                                        matchDoorVo.doorList.add(door);
                                    }
                                }
                                listener.onGetAllDoorListSuccess(matchDoorVo);
                            } else {
                                listener.onGetAllDoorListFailed("无门禁列表");
                                Log.e("OneKeyOpenImpl","无门禁列表");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }

                    public void onError(Exception e) {
                        listener.onGetAllDoorListFailed("无门禁列表");
                    }
                });
    }

    @Override
    public void getDoorList(String projectCode, String sign, String time, String userName, String phone, final onGetDoorListListener listener) {
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.GETDOORBYPHONENEW,
                HttpConnectionTools.HttpData("projectCode", projectCode, "sign", sign, "time", time,
                        "userName",userName , "phone",phone ), new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if (errorCode == 200) {
                                String result = jsonObject.getString("result");
                                jsonObject = new JSONObject(result);
                                matchDoorVo.openData = jsonObject.getString("openData");
                                JSONArray jsonArray = jsonObject.getJSONArray("doorList");
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    jsonObject = jsonArray.getJSONObject(j);
                                    doorID = jsonObject.getString("doorID");
                                    doorName = jsonObject.getString("doorName");
                                    doorPath = jsonObject.getString("doorPath");
                                    connectionKey = jsonObject.getString("connectionKey");
                                    keyID = jsonObject.getString("keyID");
                                    doorTyp = jsonObject.getString("doorTyp");

                                    String otherinfo = jsonObject.getString("otherInfo");
                                    if (otherinfo != "null") {
                                        jsonObject = new JSONObject(otherinfo);
                                        String password = jsonObject.getString("passWord");
                                        String mac = jsonObject.getString("mac");
                                        otherInfo = new MatchDoorVo.Door.OtherInfo(password, mac);
                                        door = new MatchDoorVo.Door(doorID, doorName, doorPath, connectionKey, keyID, doorTyp, otherInfo);
                                        matchDoorVo.doorList.add(door);
                                    } else {
                                        door = new MatchDoorVo.Door(doorID, doorName, doorPath, connectionKey, keyID, doorTyp);
                                        matchDoorVo.doorList.add(door);
                                    }
                                }
                                listener.onGetDoorListSuccess(matchDoorVo);
                            } else  {
                                listener.onGetDoorListFailed("无门禁列表");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        listener.onGetDoorListFailed("无门禁列表");
                    }
                });
    }
}
