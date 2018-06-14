package com.xhwl.xhwlownerapp.activity.View.VideoView.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.xhwl.xhwlownerapp.Entity.VideoEntity.VideoEntity;
import com.xhwl.xhwlownerapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/9.
 */

public class PictureRecordGridViewAdapter extends BaseAdapter {
    private List<VideoEntity> list = new ArrayList<>();
    private Context context;

    public PictureRecordGridViewAdapter(List<VideoEntity> list, Context context) {
        this.list = list;
        this.context = context;
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
        //将布局文件转换成View
       ViewHolder viewHolder;
       if (view == null){
           view = LayoutInflater.from(context).inflate(R.layout.ac_item_img,null);
           viewHolder = new ViewHolder();
           viewHolder.hk_img = (ImageView) view.findViewById(R.id.hk_img);
           viewHolder.check_box = (ImageView) view.findViewById(R.id.check_box);
           view.setTag(viewHolder);
       }else {
           viewHolder = (ViewHolder) view.getTag();
       }
        viewHolder.hk_img.setImageResource(list.get(i).getVimg());
        viewHolder.check_box.setImageResource(R.drawable.radio_unchecked);
       return view;
    }

    class ViewHolder{
        ImageView hk_img,check_box;
    }
}
