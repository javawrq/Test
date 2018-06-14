package com.xhwl.xhwlownerapp.activity.View.BackgroundMusic;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.IPAddressUtil;
import com.xhwl.xhwlownerapp.UIUtils.MyAPP;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.UIUtils.ShowToast;
import com.xhwl.xhwlownerapp.UIUtils.StringUtils;
import com.xhwl.xhwlownerapp.UIUtils.ToastUtil;
import com.xhwl.xhwlownerapp.activity.View.BackgroundMusic.adapter.ServiceAdapter;
import com.xhwl.xhwlownerapp.activity.View.BackgroundMusic.entity.MediaDevice;
import com.xhwl.xhwlownerapp.activity.View.BackgroundMusic.entity.MediaSongs;
import com.xhwl.xhwlownerapp.activity.View.LoginView.LoginActivity;
import com.xhwl.xhwlownerapp.net.Constant;
import com.xhwl.xhwlownerapp.net.HttpConnectionInter;
import com.xhwl.xhwlownerapp.net.HttpConnectionTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 点歌服务
 */
public class MusicServiceActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private ImageView mPageBack;
    private LinearLayout mPageBackLayout;
    private TextView mPageScan;
    private ImageView mPageHomepager;
    private LinearLayout mPageHomepagerLayout;
    private LinearLayout mMusicSearchBtn;
    private ListView mMusicList;
    private ImageView mMusicSingerImg;
    /**
     * we don’t talk anymore
     */
    private TextView mMusicSongName;
    /**
     * CHarlie Puth
     */
    private TextView mMusicSingerName;
    private ImageView mMusicSongProgress;
    private ImageView mMusicSongNext;

    private String token;
    private ServiceAdapter adapter;
    private String mediaName;
    public List<MediaSongs> list = new ArrayList<>();
    private MediaSongs mediaSongs;
    private Intent intent;
    private String mediaExten;
    private MediaDevice mediaDevice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_service);
        initView();
        getMediaDevice();
        getMusicList();
        handler.postDelayed(runnable, 2000);//每两秒执行一次runnable.
    }

    private void initView() {
        mediaDevice = new MediaDevice();
        token = SPUtils.get(MusicServiceActivity.this,"userToken","");
        mPageBack = (ImageView) findViewById(R.id.page_back);
        mPageBackLayout = (LinearLayout) findViewById(R.id.page_back_layout);
        mPageBackLayout.setOnClickListener(this);
        mPageScan = (TextView) findViewById(R.id.page_scan);
        mPageScan.setText("点歌服务");
        mPageHomepager = (ImageView) findViewById(R.id.page_homepager);
        mPageHomepagerLayout = (LinearLayout) findViewById(R.id.page_homepager_layout);
        mPageHomepagerLayout.setVisibility(View.VISIBLE);
        mPageHomepagerLayout.setOnClickListener(this);
        mMusicSearchBtn = (LinearLayout) findViewById(R.id.music_server_search_btn);
        mMusicSearchBtn.setOnClickListener(this);
        mMusicList = (ListView) findViewById(R.id.music_list);
        mMusicList.setOnItemClickListener(this);
        mMusicSingerImg = (ImageView) findViewById(R.id.music_singer_img);
        mMusicSongName = (TextView) findViewById(R.id.music_song_name);
        mMusicSingerName = (TextView) findViewById(R.id.music_singer_name);
        mMusicSongProgress = (ImageView) findViewById(R.id.music_song_progress);
        mMusicSongNext = (ImageView) findViewById(R.id.music_song_next);
        mMusicSongNext.setOnClickListener(this);
        mMusicSongNext.setVisibility(View.GONE);
    }

    //点歌---获取所有在线的媒体播放设备
    private void getMediaDevice() {
        String currentIp = IPAddressUtil.getIPAddress(MusicServiceActivity.this);
        Log.e("currentIp", currentIp + "");
        Log.e("url", Constant.MUSICHOST2 + Constant.MUSICGETALLDEVICE + currentIp + "/" + token);
        HttpConnectionTools.HttpServler(Constant.MUSICHOST2 + Constant.MUSICGETALLDEVICE + currentIp + "/" + token,
                new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        //成功
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if (errorCode == 200) {
                                String result = jsonObject.getString("result");
                                jsonObject = new JSONObject(result);
                                JSONArray jsonArray = jsonObject.getJSONArray("rows");
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    JSONObject jobject = jsonArray.getJSONObject(j);
                                    //将获取的设备信息保存到实体
                                    mediaDevice.setTerminal_type(jobject.getString("terminal_type"));
                                    mediaDevice.setName(jobject.getString("name"));
                                    mediaDevice.setDeciceId(jobject.getInt("id"));
                                    mediaDevice.setStatus(jobject.getString("status"));
                                    mediaDevice.setExten(jobject.getString("exten"));
                                    mediaDevice.setClient_ip(jobject.getString("client_ip"));
                                    mediaDevice.setAccess_token(jobject.getString("access_token"));
                                }
                                SPUtils.put(MusicServiceActivity.this, "exten", mediaDevice.getExten());
                                handler1.sendEmptyMessage(200);
                            } else if (errorCode == -3) {
                                //该网段下没有在线设备
                                handler1.sendEmptyMessage(-3);
                            } else if (errorCode == 201) {
                                //	系统异常，操作失败
                                handler1.sendEmptyMessage(201);
                            } else if (errorCode == 400) {
                                //您已退出登录，请重新登录
                                handler1.sendEmptyMessage(400);
                            } else if (errorCode == 401) {
                                //	token已过期，请重新登录
                                handler1.sendEmptyMessage(401);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler1.sendEmptyMessage(500);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        //失败
                        handler1.sendEmptyMessage(500);
                    }
                });
    }

    //获取歌曲列表
    private void getMusicList(){
        HttpConnectionTools.HttpServler(Constant.MUSICHOST2 + Constant.MUSICLIST + token, new HttpConnectionInter() {
            @Override
            public void onFinish(String content) {
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    int errorCode = jsonObject.getInt("errorCode");
                    if(errorCode == 200){
                        String result = jsonObject.getString("result");
                        jsonObject = new JSONObject(result);
                        JSONArray jsonArray = jsonObject.getJSONArray("rows");
                        for (int j = 0;j<jsonArray.length();j++){
                            JSONObject jobject = jsonArray.getJSONObject(j);
                            mediaSongs = new MediaSongs();
                            mediaSongs.setSongName(jobject.getString("name"));
                            mediaSongs.setSongsId(jobject.getInt("id"));
                            list.add(mediaSongs);
                        }
                        handler.sendEmptyMessage(200);
                    }else if(errorCode == 115){
                        handler.sendEmptyMessage(115);
                    }else if(errorCode == 201){
                        handler.sendEmptyMessage(201);
                    }else if(errorCode == 400){
                        handler.sendEmptyMessage(400);
                    }else if(errorCode == 401){
                        handler.sendEmptyMessage(401);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                handler.sendEmptyMessage(500);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.page_back_layout:
                finish();
                break;
            case R.id.music_song_next:
                //下一首
                NextOne(token,mediaPlayListId,mediaExten);
                break;
            case R.id.page_homepager_layout:
                //已点歌曲
                intent = new Intent(MusicServiceActivity.this,MusicAlreadyActivity.class);
                startActivity(intent);
                break;
            case R.id.music_server_search_btn:
                //搜索
                intent = new Intent(MusicServiceActivity.this,MusicSearchActivity.class);
                intent.putExtra("musicList", (Serializable) list);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        mediaExten = SPUtils.get(MusicServiceActivity.this, "exten","");
        if(!StringUtils.isEmpty(mediaExten)){
            addMediaList(token,list.get(i).getSongsId()+"",list.get(i).getSongName(),mediaExten);
        }else {
            ToastUtil.showSingleToast("该网段下没有在线设备");
        }
    }

    //切歌 只能切换当前播放时自己的歌曲
    private void NextOne(String token,int mediaPlayListId,String deviceNos){
        HttpConnectionTools.HttpServler(Constant.MUSICHOST2 + Constant.MUSICSWITCH,
                HttpConnectionTools.HttpData("token", token, "mediaplaylistId", mediaPlayListId,
                        "deviceNos", deviceNos), new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            JSONObject object = new JSONObject(content);
                            int errorCode  = object.getInt("errorCode");
                            if(errorCode == 200){
                                handler.sendEmptyMessage(0x124);
                            }else if(errorCode == -3){
                                handler.sendEmptyMessage(0x3);
                            }else if(errorCode == 201){
                                handler.sendEmptyMessage(0x201);
                            }else if(errorCode == 115){
                                handler.sendEmptyMessage(0x115);
                            }else if(errorCode == 400){
                                handler.sendEmptyMessage(0x400);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
    }

    //点击歌曲添加到点歌记录
    private void addMediaList(String token,String mediaId,String mediaName,String deviceNos){
        HttpConnectionTools.HttpServler(Constant.MUSICHOST2 + Constant.MUSICADDLIST,
                HttpConnectionTools.HttpData("token", token, "mediaId", mediaId,
                        "mediaName", mediaName, "deviceNos", deviceNos), new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if(errorCode == 200){
                                handler.sendEmptyMessage(0x123);
                            }else if(errorCode == 115){
                                handler.sendEmptyMessage(115);
                            }else if(errorCode == 201){
                                handler.sendEmptyMessage(201);
                            }else if(errorCode == 400){
                                handler.sendEmptyMessage(400);
                            }else if(errorCode == 401){
                                handler.sendEmptyMessage(401);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        handler.sendEmptyMessage(500);
                    }
                });
    }

    //获取当前设备当前播放歌曲信息
    private String musicName;
    private String singerName;
    private String subName;
    private int mediaPlayListId;
    public void getMediaSong(String deviceNos){
        HttpConnectionTools.HttpServler(Constant.MUSICHOST2 + Constant.MUSICCURRENTMEDIA + deviceNos, new HttpConnectionInter() {
            @Override
            public void onFinish(String content) {
                try {
                    JSONObject jsonObject = new JSONObject(content);
                    int errorCode = jsonObject.getInt("errorCode");
                    if(errorCode == 200){
                        String result = jsonObject.getString("result");
                        jsonObject = new JSONObject(result);
                        String currentPlayMedia = jsonObject.getString("currentPlayMedia");
                        jsonObject = new JSONObject(currentPlayMedia);
                        int mediaId = jsonObject.getInt("mediaId");
                        mediaPlayListId = jsonObject.getInt("id");
                        mediaName = jsonObject.getString("mediaName");
                        handler.sendEmptyMessage(0x200);
                    }else if(errorCode == -3){
                        handler.sendEmptyMessage(-3);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Exception e) {
                handler.sendEmptyMessage(500);
            }
        });

    }

    //单击返回键返回，不退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
        }
        return true;
    }

   Handler handler1 = new Handler(){
       @Override
       public void handleMessage(Message msg) {
           if (msg.what == 200) {
               if(!StringUtils.isEmpty(mediaDevice.getExten().toString())){
                   getMediaSong(mediaDevice.getExten());
               }else {
                   ShowToast.show("该网段下没有在线设备");
               }
           } else if (msg.what == -3) {
               ShowToast.show("该网段下没有在线设备");
           } else if (msg.what == 201) {
               ShowToast.show("系统异常，操作失败");
           } else if (msg.what == 400) {
               ShowToast.show("您已退出登录，请重新登录");
               intent.setClass(MusicServiceActivity.this, LoginActivity.class);
               startActivity(intent);
               MyAPP.Logout(MusicServiceActivity.this);
           } else if (msg.what == 401) {
               intent.setClass(MusicServiceActivity.this, LoginActivity.class);
               startActivity(intent);
               MyAPP.Logout(MusicServiceActivity.this);
               ShowToast.show("token已过期，请重新登录");
           }
       }
   };

   Handler handler = new Handler(){
       @Override
       public void handleMessage(Message msg) {
           if(msg.what == 200){
               adapter = new ServiceAdapter(MusicServiceActivity.this,list);
               mMusicList.setAdapter(adapter);
           }else if(msg.what == 115){
               showToast("您没有该权限");
           }else if(msg.what == 201){
               showToast("系统异常，操作失败");
           }else if(msg.what == 400){
               showToast("您已退出登录，请重新登录");
               startToAIctivity(LoginActivity.class);
               MyAPP.Logout(MusicServiceActivity.this);
           }else if(msg.what == 401){
               showToast("token已过期，请重新登录");
               startToAIctivity(LoginActivity.class);
               MyAPP.Logout(MusicServiceActivity.this);
           }
           if(msg.what == 0x123){
               showToast("添加成功");
           }
           if(msg.what == 500){
               showToast("请检查网络");
           }
           if(msg.what == 0x200){
                //去除尾号.mp3符号
               String[] str = mediaName.split(("\\."));
               subName = str[0];
               //musicNameList.add(subName);
               if(subName.contains("-")){
                   //歌名、歌手分开展示
                   String[] str1 = subName.split("-");
                   singerName = str1[0];
                   musicName = str1[1];
                   mMusicSingerName.setText(singerName);//设置歌手
                   mMusicSongName.setText(musicName);//设置歌名
                   mMusicSongNext.setVisibility(View.VISIBLE);
               }else{
                   mMusicSingerName.setText("未知");//设置歌手
                   mMusicSongName.setText(subName);//设置歌名
                   mMusicSongNext.setVisibility(View.VISIBLE);
               }
           }else if(msg.what == -3){
               mMusicSingerName.setText("");//设置歌手
               mMusicSongName.setText("当前没有播放任务");//设置歌名
               mMusicSongNext.setVisibility(View.GONE);
           }

           //切歌
           if(msg.what == 0x3){
               showToast("列表没有下一首可播放歌曲");
           }else if(msg.what == 0x201){
               showToast("切歌失败");
           }else if(msg.what == 0x115){
               showToast("无权限操作");
           }else if(msg.what == 0x124){
               showToast("下一首");
           }else if(msg.what == 0x400){
               showToast("您已退出登录，请重新登录");
               startToAIctivity(LoginActivity.class);
               MyAPP.Logout(MusicServiceActivity.this);
               finish();
           }else if(msg.what == 0x401){
               showToast("token已过期，请重新登录");
               startToAIctivity(LoginActivity.class);
               MyAPP.Logout(MusicServiceActivity.this);
               finish();
           }
       }
   };

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //定时任务
            mediaExten = SPUtils.get(MusicServiceActivity.this, "exten","");
            if(!StringUtils.isEmpty(mediaExten)){
                getMediaSong(mediaDevice.getExten());
                handler.postDelayed(this, 2000);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);//停止runnable
    }
}
