package com.xhwl.xhwlownerapp.activity.View.CloudTalk.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.Entity.HouseEntiy.RoomInfo;
import com.xhwl.xhwlownerapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/12.
 */

public class LocationRoomAdapter extends BaseAdapter {
    private List<RoomInfo> list = new ArrayList<>();
    private Context context;

    public LocationRoomAdapter(List<RoomInfo> list, Context context) {
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
        ViewHolder viewHolder;
        if(view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.select_project_list_layout,null);
            viewHolder.proName = (TextView) view.findViewById(R.id.select_list_item_proname);
            viewHolder.roomId = (TextView) view.findViewById(R.id.select_list_item_city);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.proName.setText(list.get(i).getName());
        viewHolder.roomId.setText(list.get(i).getCode());
        return view;
    }

    class ViewHolder{
        TextView proName,roomId;
    }
}
