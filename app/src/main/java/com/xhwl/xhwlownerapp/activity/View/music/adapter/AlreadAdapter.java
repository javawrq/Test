package com.xhwl.xhwlownerapp.activity.View.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.activity.View.music.entity.MediaPlayList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/17.
 */

public class AlreadAdapter extends BaseAdapter {
    private List<MediaPlayList> list = new ArrayList<>();
    private Context context;
    private String musicName;
    private String singerName;
    private String subName;

    public AlreadAdapter(Context context, List<MediaPlayList> list){
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
            view = LayoutInflater.from(context).inflate(R.layout.music_already_list,null);
            viewHolder.alreadyMusicname = (TextView) view.findViewById(R.id.music_already_list_musicname);
            viewHolder.alreadySingname = (TextView) view.findViewById(R.id.music_already_list_singername);
            viewHolder.alreadyDelete = (ImageView) view.findViewById(R.id.music_already_delete);
            viewHolder.alreadyDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
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
                viewHolder.alreadySingname.setText(singerName);//设置歌手
                viewHolder.alreadyMusicname.setText(musicName);//设置歌名
            }else{
                viewHolder.alreadySingname.setText("未知");//设置歌手
                viewHolder.alreadyMusicname.setText(subName);//设置歌名
            }
        }
        return view;
    }

    public static class ViewHolder{
        static TextView alreadyMusicname;
        static TextView alreadySingname;
        public static ImageView alreadyDelete;
    }
}
