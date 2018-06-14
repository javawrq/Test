package com.xhwl.xhwlownerapp.activity.View.BackgroundMusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.activity.View.BackgroundMusic.entity.MediaSongs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/14.
 */

public class ServiceAdapter extends BaseAdapter {
    private Context context;
    private List<MediaSongs> list = new ArrayList<>();
    private String musicName;
    private String singerName;
    private String subName;

    public ServiceAdapter(Context context, List<MediaSongs> list){
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
            view = LayoutInflater.from(context).inflate(R.layout.music_list,null);
            viewHolder.singer_img = (ImageView) view.findViewById(R.id.music_list_singer_img);
            viewHolder.musicname = (TextView) view.findViewById(R.id.music_list_musicname);
            viewHolder.singername = (TextView) view.findViewById(R.id.music_list_singername);
            view.setTag(viewHolder);
        }else{
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
                viewHolder.singername.setText(singerName);//设置歌手
                viewHolder.musicname.setText(musicName);//设置歌名
            }else{
                viewHolder.singername.setText("未知");//设置歌手
                viewHolder.musicname.setText(subName);//设置歌名
            }
        }
        return view;
    }

    class ViewHolder{
        ImageView singer_img;
        TextView musicname;
        TextView singername;
    }
}
