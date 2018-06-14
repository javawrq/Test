package com.xhwl.xhwlownerapp.activity.View.HomeView.SelectProAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.Entity.ProjectEntity.Project;
import com.xhwl.xhwlownerapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/3.
 */

public class SelectProjectAdapter extends BaseAdapter {
    private List<Project> list = new ArrayList<>();
    private Context context;

    public SelectProjectAdapter(List<Project> list, Context context) {
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
            viewHolder.proCity = (TextView) view.findViewById(R.id.select_list_item_city);
            viewHolder.proName = (TextView) view.findViewById(R.id.select_list_item_proname);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.proName.setText(list.get(i).getProName());
        viewHolder.proCity.setText(list.get(i).getDivisionName());

        return view;
    }

    class ViewHolder{
        TextView proCity,proName;
    }
}
