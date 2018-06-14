package com.xhwl.xhwlownerapp.activity.View.BackgroundMusic;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.MyAPP;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.UIUtils.dialog.SelfDialog;
import com.xhwl.xhwlownerapp.activity.View.BackgroundMusic.adapter.AlreadAdapter;
import com.xhwl.xhwlownerapp.activity.View.BackgroundMusic.entity.MediaDevice;
import com.xhwl.xhwlownerapp.activity.View.BackgroundMusic.entity.MediaPlayList;
import com.xhwl.xhwlownerapp.activity.View.BackgroundMusic.entity.PageInfo;
import com.xhwl.xhwlownerapp.activity.View.LoginView.LoginActivity;
import com.xhwl.xhwlownerapp.net.Constant;
import com.xhwl.xhwlownerapp.net.HttpConnectionInter;
import com.xhwl.xhwlownerapp.net.HttpConnectionTools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 已点歌曲
 */
public class MusicAlreadyActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private LinearLayout mPageBackLayout;
    private TextView mPageScan;
    private ImageView mPageHomepager;
    private LinearLayout mPageHomepagerLayout;
    /**
     * 当前播放：消愁
     */
    private TextView mMusicCurrentPlayName;
    private ListView mAlreadyList;

    private AlreadAdapter alreadAdapter;
    private List<MediaPlayList> mediaSongsList = new ArrayList<>();
    private String token;
    private int pageSize = 50;//每页数量
    private int pageNumber = 1;//当前页码
    private PageInfo Info = new PageInfo();
    private MediaPlayList mediaPlayList;
    private SelfDialog dialog;
    private MediaDevice device;
    private String mediaExten;//设备号
    private int currentSize;
    private int nextSize;

    //点歌---获取当前设备当前播放歌曲信息
    private String musicName;
    private String subName;
    private String mediaName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_already);
        initView();
        getMediaList(token,pageSize,pageNumber);//获取已点歌曲
        getMediaSong(mediaExten);
        handler.postDelayed(runnable, 5000);//每两秒执行一次runnable.
    }

    private void initView() {
        mediaExten = SPUtils.get(MusicAlreadyActivity.this,"exten","");
        token = SPUtils.get(MusicAlreadyActivity.this,"userToken","");
        mPageBackLayout = (LinearLayout) findViewById(R.id.page_back_layout);
        mPageBackLayout.setOnClickListener(this);
        mPageScan = (TextView) findViewById(R.id.page_scan);
        mPageScan.setText("已点歌曲");
        mPageHomepager = (ImageView) findViewById(R.id.page_homepager);
        mPageHomepager.setVisibility(View.GONE);
        mPageHomepagerLayout = (LinearLayout) findViewById(R.id.page_homepager_layout);
        mMusicCurrentPlayName = (TextView) findViewById(R.id.music_current_play_name);
        mAlreadyList = (ListView) findViewById(R.id.already_list);
        //mAlreadyList.setInterface(this);
        mAlreadyList.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.page_back_layout:
                finish();
                break;
        }
    }

    //获取点歌记录
    private void getMediaList(String token,int pageSize,int pageNumber){
        HttpConnectionTools.HttpServler(Constant.MUSICHOST2 + Constant.MUSICALREDYLIST,
                HttpConnectionTools.HttpData("token", token, "pageSize", pageSize,"pageNumber", pageNumber),
                new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            JSONObject jsonObject = new JSONObject(content);
                            int errorCode = jsonObject.getInt("errorCode");
                            if(errorCode == 200){
                                String result = jsonObject.getString("result");
                                jsonObject = new JSONObject(result);
                                String pageInfo = jsonObject.getString("pageInfo");
                                jsonObject = new JSONObject(pageInfo);
                                Info.setCurrentPage(jsonObject.getInt("currentPage"));
                                Info.setPageSize(jsonObject.getInt("pageSize"));
                                Info.setTotalNum(jsonObject.getInt("totalNum"));
                                Info.setTotalPage(jsonObject.getInt("totalPage"));
                                JSONArray jsonArray = jsonObject.getJSONArray("rows");
                                for (int j = 0;j<jsonArray.length();j++){
                                    JSONObject jobject = jsonArray.getJSONObject(j);
                                    mediaPlayList = new MediaPlayList();
                                    mediaPlayList.setSongName(jobject.getString("medianame"));
                                    mediaPlayList.setSongsId(jobject.getInt("mediaid"));
                                    mediaPlayList.setMediaPlayListId(jobject.getInt("id"));
                                    mediaSongsList.add(mediaPlayList);
                                }
                                handler.sendEmptyMessage(200);
                               /*if(currentSize == 0){
                                    currentSize = mediaSongsList.size();
                                }else if(currentSize != mediaSongsList.size()){
                                    handler.sendEmptyMessage(200);
                                }*/
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
                            handler.sendEmptyMessage(500);
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
            if(msg.what == 200){
                alreadAdapter = new AlreadAdapter(MusicAlreadyActivity.this,mediaSongsList);
                mAlreadyList.setAdapter(alreadAdapter);
            }else if(msg.what == 115){
                showToast("您没有该权限");
            }else if(msg.what == 201){
                showToast("系统异常，操作失败");
            }else if(msg.what == 400){
                showToast("您已退出登录，请重新登录");
                startToAIctivity(LoginActivity.class);
                MyAPP.Logout(MusicAlreadyActivity.this);
                finish();
            }else if(msg.what == 401){
                showToast("token已过期，请重新登录");
                startToAIctivity(LoginActivity.class);
                MyAPP.Logout(MusicAlreadyActivity.this);
                finish();
            }
            if(msg.what == 500) {
                showToast("请检查网络");
            }
            if(msg.what == -3) {
                showToast("此歌曲不存在");
            }else if(msg.what == 0x123) {
                showToast("删除成功");
                mediaSongsList.clear();
                alreadAdapter.notifyDataSetChanged();
                getMediaList(token,pageSize,pageNumber);//获取已点歌曲
            }

            if(msg.what == 0x200){
                //去除尾号.mp3符号
                String[] str = mediaName.split(("\\."));
                subName = str[0];
                //musicNameList.add(subName);
                if(subName.contains("-")){
                    //歌名、歌手分开展示
                    String[] str1 = subName.split("-");
                    musicName = str1[1];
                    mMusicCurrentPlayName.setText(musicName);//设置歌名
                }else{
                    mMusicCurrentPlayName.setText(subName);//设置歌名
                }
            }else if(msg.what == 0x3){
                mMusicCurrentPlayName.setText("当前没有播放任务");//设置歌名
            }
        }
    };

    //删除歌曲
    private void remove(int mediaPlayListId,String token){
        HttpConnectionTools.HttpServler(Constant.MUSICHOST2+ Constant.MUSICREMOVE,
                HttpConnectionTools.HttpData( "mediaplaylistId", mediaPlayListId,"token", token),
                new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        try {
                            JSONObject object = new JSONObject(content);
                            int errorCode = object.getInt("errorCode");
                            if(errorCode == 200){
                                handler.sendEmptyMessage(0x123);
                            }else if(errorCode == 115){
                                handler.sendEmptyMessage(115);
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

    //当前播放的音乐
    public void getMediaSong(String deviceNos){
        HttpConnectionTools.HttpServler(Constant.MUSICHOST2 +Constant.MUSICCURRENTMEDIA + deviceNos, new HttpConnectionInter() {
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
                        mediaName = jsonObject.getString("mediaName");
                        handler.sendEmptyMessage(0x200);
                    }else if(errorCode == -3){
                        handler.sendEmptyMessage(0x3);
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

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
        dialog = new SelfDialog(MusicAlreadyActivity.this);
        dialog.setMessage("确定删除"+mediaSongsList.get(i).getSongName()+"此歌曲吗");
        dialog.setTitle("提示");
        dialog.setYesOnclickListener("确定", new SelfDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                remove(mediaSongsList.get(i).getMediaPlayListId(),token);
                dialog.dismiss();
            }
        });
        dialog.setNoOnclickListener("取消", new SelfDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //定时任务
            if (mediaSongsList.size() != 0) {
                mediaSongsList.clear();
            }
            //alreadAdapter.notifyDataSetChanged();
            getMediaList(token,pageSize,pageNumber);//获取已点歌曲
            getMediaSong(mediaExten);
            handler.postDelayed(this, 5000);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);//停止runnable
    }

//    @Override
//    public void onReflash() {
//        if (mediaSongsList.size() != 0) {
//            mediaSongsList.clear();
//        }
//        //获取最新数据
//        for (int i = 1; i < Info.getTotal()/pageSize; i++) {
//            getMediaList(token,pageSize,i);//获取已点歌曲
//        }
//        //通知listview刷新数据完毕
//        //mAlreadyList.reflashComplete();
//    }
}
