package com.xhwl.xhwlownerapp.activity.View.UserInfoView.FamilyInfo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.Entity.FamilyEntivity.Family;
import com.xhwl.xhwlownerapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/21.
 */

public class FamilyAdapter extends BaseAdapter {
    private List<Family> list = new ArrayList<>();
    private Context context;

    public FamilyAdapter(List<Family> list, Context context) {
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
            view = LayoutInflater.from(context).inflate(R.layout.family_list_item,null);
            viewHolder.family_name = (TextView) view.findViewById(R.id.list_item_family_name);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.family_name.setText(list.get(i).getFamilyName());
        return view;
    }

    class ViewHolder{
        TextView family_name;
    }
}
