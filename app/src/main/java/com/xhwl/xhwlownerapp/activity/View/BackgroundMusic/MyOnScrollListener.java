package com.xhwl.xhwlownerapp.activity.View.BackgroundMusic;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.AbsListView;

import com.xhwl.xhwlownerapp.activity.View.BackgroundMusic.entity.MediaSongs;
import com.xhwl.xhwlownerapp.activity.View.BackgroundMusic.entity.PageInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/17.
 */

public class MyOnScrollListener implements AbsListView.OnScrollListener {

    //ListView总共显示多少条
    private int totalItemCount;

    //ListView最后的item项
    private int lastItem;

    //用于判断当前是否在加载
    private boolean isLoading;

    //底部加载更多布局
    private View footer;

    //接口回调的实例
    private OnloadDataListener listener;

    //数据
    private List<MediaSongs> data = new ArrayList<>();;

    public MyOnScrollListener(View footer) {
        this.footer = footer;
    }

    //设置接口回调的实例
    public void setOnLoadDataListener(OnloadDataListener listener) {
        this.listener = listener;
    }

    /**
     * 滑动状态变化
     *
     * @param absListView
     * @param scrollState 1 SCROLL_STATE_TOUCH_SCROLL是拖动
     *                    2 SCROLL_STATE_FLING是惯性滑动
     *                    0SCROLL_STATE_IDLE是停止 ,
     *                    只有当在不同状态间切换的时候才会执行
     */
    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        //如果数据没有加载，并且滑动状态是停止的，而且到达了最后一个item项
        if (!isLoading && lastItem == totalItemCount && scrollState == SCROLL_STATE_IDLE) {
            //显示加载更多
            footer.setVisibility(View.VISIBLE);
            Handler handler = new Handler();
            //模拟一个延迟两秒的刷新功能
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        //开始加载更多数据
                        loadMoreData();
                        //回调设置ListView的数据
                        listener.onLoadData(data);
                        //加载完成后操作什么
                        loadComplete();
                    }
                }
            }, 2000);
        }
    }
    /**
     * 当加载数据完成后，设置加载标志为false表示没有加载数据了
     * 并且设置底部加载更多为隐藏
     */
    private void loadComplete() {
        isLoading = false;
        footer.setVisibility(View.GONE);

    }

    /**
     * 开始加载更多新数据，这里每次只更新三条数据
     */
    private void loadMoreData() {
        isLoading = true;
        MediaSongs songs = null;
        for (int i = 0; i < 3; i++) {
            songs = new MediaSongs();
            songs.setSongsId(1);
            songs.setSongName("新性别" + i);
            data.add(songs);
        }
    }


    /**
     * 监听可见界面的情况
     *
     * @param view             ListView
     * @param firstVisibleItem 第一个可见的 item 的索引
     * @param visibleItemCount 可以显示的 item的条数
     * @param totalItemCount   总共有多少个 item
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        //当可见界面的第一个item  +  当前界面多有可见的界面个数就可以得到最后一个item项了
        lastItem = firstVisibleItem + visibleItemCount;
        //总listView的item个数
        this.totalItemCount = totalItemCount;
    }
    //回调接口
    public interface OnloadDataListener {
        void onLoadData(List<MediaSongs> data);
    }


    private int pageSize = 5;//每页数量
    private int pageNumber = 1;//当前页码
    private PageInfo Info = new PageInfo();
    private MediaSongs mediaSongs;
    private Context context;

//    //获取点歌记录
//    private void getMediaList(){
//        String token = SPUtils.get(context,"token","");
//        HttpConnectionTools.HttpServler(Constant.host3 + "appBusiness/MediaList/getAll",
//                HttpConnectionTools.HttpData("token", token, "pageSize", pageSize, "pageNumber", pageNumber),
//                new HttpConnectionInter() {
//                    @Override
//                    public void onFinish(String content) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(content);
//                            int errorCode = jsonObject.getInt("errorCode");
//                            if(errorCode == 200){
//                                String result = jsonObject.getString("result");
//                                jsonObject = new JSONObject(result);
//                                String pageInfo = jsonObject.getString("pageInfo");
//                                JSONArray jsonArray = jsonObject.getJSONArray("rows");
//                                jsonObject = new JSONObject(pageInfo);
//                                Info.setPageNumber(jsonObject.getInt("pageNumber"));
//                                Info.setPageSize(jsonObject.getInt("pageSize"));
//                                Info.setTotal(jsonObject.getInt("total"));
//                                for (int j = 0;j<jsonArray.length();j++){
//                                    JSONObject jobject = jsonArray.getJSONObject(j);
//                                    mediaSongs = new MediaSongs();
//                                    mediaSongs.setSongName(jobject.getString("mediaName"));
//                                    mediaSongs.setSongsId(jobject.getInt("mediaId"));
//                                    data.add(mediaSongs);
//                                }
//                                handler.sendEmptyMessage(200);
//                            }else if(errorCode == 115){
//                                handler.sendEmptyMessage(115);
//
//                            }else if(errorCode == 201){
//                                handler.sendEmptyMessage(201);
//                            }else if(errorCode == 400){
//                                handler.sendEmptyMessage(400);
//                            }else if(errorCode == 401){
//                                handler.sendEmptyMessage(401);
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Exception e) {
//                        handler.sendEmptyMessage(500);
//                    }
//                });
//    }
//
//    Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            if(msg.what == 200){
//                alreadAdapter = new AlreadAdapter(context,data);
//                mAlreadyList.setAdapter(alreadAdapter);
//
//            }else if(msg.what == 115){
//                T.show("您没有该权限");
//            }else if(msg.what == 201){
//                T.show("系统异常，操作失败");
//            }else if(msg.what == 400){
//                T.show("您已退出登录，请重新登录");
//            }else if(msg.what == 401){
//                T.show("token已过期，请重新登录");
//            }
//            if(msg.what == 500){
//                T.show("请检查网络");
//            }
//        }
//    };
}
