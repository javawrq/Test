package com.xhwl.xhwlownerapp.activity.View.VideoView.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.R;

import java.util.List;

/**
 * Created by Administrator on 2018/4/4.
 */

public class VideoListAdapter extends BaseAdapter {
    private List<String> videoEntityList;
    private Context context;

    public VideoListAdapter(List<String> videoEntityList, Context context) {
        this.videoEntityList = videoEntityList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return videoEntityList.size();
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
        ViewHolder holder;
        if(view == null){
            holder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.video_list_layout,null);
            holder.doorName = (TextView) view.findViewById(R.id.video_list_doorname);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        holder.doorName.setText(videoEntityList.get(i));
        return view ;
    }

    class ViewHolder{
        TextView doorName;
    }

}
