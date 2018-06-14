package com.xhwl.xhwlownerapp.activity.View.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.activity.View.music.entity.MediaSongs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/14.
 */

public class SearchAdapter extends BaseAdapter {

    private List<MediaSongs> list = new ArrayList<>();
    private Context context;
    private String musicName;
    private String singerName;
    private String subName;

    public SearchAdapter(Context context, List<MediaSongs> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.music_search_list,null);
            viewHolder.search_musicname = (TextView) view.findViewById(R.id.music_search_list_musicname);
            viewHolder.search_singername = (TextView) view.findViewById(R.id.music_search_list_singername);
            view.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) view.getTag();
        }
        for (int j = 0;j<=i;j++){
            //去除尾号.mp3符号
            String[] str = list.get(j).getSongName().split(("\\."));
            subName = str[0];
            //musicNameList.add(subName);
            if(subName.contains("-")){
                //歌名、歌手分开展示
                String[] str1 = subName.split("-");
                singerName = str1[0];
                musicName = str1[1];
                viewHolder.search_singername.setText(singerName);//设置歌手
                viewHolder.search_musicname.setText(musicName);//设置歌名
            }else{
                viewHolder.search_singername.setText("未知");//设置歌手
                viewHolder.search_musicname.setText(subName);//设置歌名
            }
        }
        return view;
    }

    class ViewHolder{
        TextView search_musicname;
        TextView search_singername;
    }
}
