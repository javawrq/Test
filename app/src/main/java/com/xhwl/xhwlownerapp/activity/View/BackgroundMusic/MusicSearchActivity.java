package com.xhwl.xhwlownerapp.activity.View.BackgroundMusic;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.ClearEditText;
import com.xhwl.xhwlownerapp.UIUtils.MyAPP;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.activity.View.BackgroundMusic.adapter.SearchAdapter;
import com.xhwl.xhwlownerapp.activity.View.BackgroundMusic.entity.MediaSongs;
import com.xhwl.xhwlownerapp.activity.View.LoginView.LoginActivity;
import com.xhwl.xhwlownerapp.net.Constant;
import com.xhwl.xhwlownerapp.net.HttpConnectionInter;
import com.xhwl.xhwlownerapp.net.HttpConnectionTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索歌曲
 */
public class MusicSearchActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private LinearLayout mPageBackLayout;
    private TextView mPageScan;
    private ImageView mPageHomepager;
    private LinearLayout mPageHomepagerLayout;
    private ListView mSearchList;
    private List<MediaSongs> musiclist = new ArrayList<>();
    private List<MediaSongs> list = new ArrayList<>();
    private MediaSongs songs;
    /**
     * 客官喜欢什么歌
     */
    private ClearEditText mMusicSearchEdit;
    /**
     * 搜索
     */
    private TextView mMusicSearchBtn;

    private SearchAdapter adapter;
    private String mediaExten,token;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_search);
        initView();
    }

    private void initView() {
        mediaExten = SPUtils.get(MusicSearchActivity.this,"exten","");
        token = SPUtils.get(MusicSearchActivity.this,"userToken","");
        mPageBackLayout = (LinearLayout) findViewById(R.id.page_back_layout);
        mPageBackLayout.setOnClickListener(this);
        mPageScan = (TextView) findViewById(R.id.page_scan);
        mPageScan.setText("搜索歌曲");
        mPageHomepager = (ImageView) findViewById(R.id.page_homepager);
        mPageHomepager.setBackgroundResource(R.drawable.music_already);
        mPageHomepagerLayout = (LinearLayout) findViewById(R.id.page_homepager_layout);
        mPageHomepagerLayout.setOnClickListener(this);
        mPageHomepagerLayout.setVisibility(View.VISIBLE);
        mSearchList = (ListView) findViewById(R.id.search_list);
        mSearchList.setOnItemClickListener(this);
        mMusicSearchEdit = (ClearEditText) findViewById(R.id.music_search_edit);
        mMusicSearchBtn = (TextView) findViewById(R.id.music_search_btn);
        mMusicSearchBtn.setOnClickListener(this);
        musiclist =  (List<MediaSongs>) getIntent().getSerializableExtra("musicList");
    }

    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.page_back_layout:
                finish();
                break;
            case R.id.music_search_btn:
                //搜索歌曲
                //if(){}
                if(TextUtils.isEmpty(mMusicSearchEdit.getText().toString().trim())){
                    showToast("请输入歌曲名");
                }else {
                    list.clear();
                    searchMusic(mMusicSearchEdit.getText().toString().trim());
                }
                break;
            case R.id.page_homepager_layout:
                //已点歌曲
                intent = new Intent(MusicSearchActivity.this,MusicAlreadyActivity.class);
                startActivity(intent);
                break;
        }
    }

    //搜索歌曲
    private void searchMusic(String editMusic){
        for (int i = 0;i < musiclist.size();i++){
            //判断音乐list列表中是否包含用户搜索的歌名
            //包含则将此歌添加到list中，显示歌名
           if(musiclist.get(i).getSongName().contains(editMusic)){
               songs = new MediaSongs();
               songs.setSongName(musiclist.get(i).getSongName());
               songs.setSongsId(musiclist.get(i).getSongsId());
               list.add(songs);
           }
        }
        if(list.size() == 0){
            list.clear();
            showToast("暂无此歌曲");
        }else {
            adapter = new SearchAdapter(MusicSearchActivity.this,list);
            mSearchList.setAdapter(adapter);
        }
    }

    //单击返回键返回，不退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        addMediaList(token,list.get(i).getSongsId()+"",list.get(i).getSongName(),mediaExten);
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

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 115){
                showToast("您没有该权限");
            }else if(msg.what == 201){
                showToast("系统异常，操作失败");
            }else if(msg.what == 400){
                showToast("您已退出登录，请重新登录");
                startToAIctivity(LoginActivity.class);
                MyAPP.Logout(MusicSearchActivity.this);
                finish();
            }else if(msg.what == 401){
                showToast("token已过期，请重新登录");
                startToAIctivity(LoginActivity.class);
                MyAPP.Logout(MusicSearchActivity.this);
                finish();
            }else if(msg.what == 0x123){
                showToast("添加成功");
            }
            if(msg.what == 500){
                showToast("请检查网络");
            }
        }
    };

}
