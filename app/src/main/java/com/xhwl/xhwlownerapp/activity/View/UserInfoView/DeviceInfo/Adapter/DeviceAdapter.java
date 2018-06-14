package com.xhwl.xhwlownerapp.activity.View.UserInfoView.DeviceInfo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.Entity.DeviceEntivity.Device;
import com.xhwl.xhwlownerapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/21.
 */

public class DeviceAdapter extends BaseAdapter {
    private List<Device> list = new ArrayList<>();
    private Context context;

    public DeviceAdapter(List<Device> list, Context context) {
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
        ViewHolder viewHolder = null;
        if(view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.device_list_item,null);
            viewHolder.device_name = (TextView) view.findViewById(R.id.list_item_device_name);
            view.setTag(viewHolder);
        }else{
             viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.device_name.setText(list.get(i).getDeviceName());
        return view;
    }

    class ViewHolder{
        TextView device_name;
    }
}
