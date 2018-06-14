package com.xhwl.xhwlownerapp.activity.View.CloudTalk.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.Entity.TalkEntity.TalkingHistory;
import com.xhwl.xhwlownerapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/20.
 */

public class TalkBackAdapter extends BaseAdapter {
    private List<TalkingHistory> list = new ArrayList<>();
    private Context context;

    public TalkBackAdapter(List<TalkingHistory> list, Context context) {
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
            view = LayoutInflater.from(context).inflate(R.layout.talk_back_history_list,null);
            viewHolder.addressName = (TextView) view.findViewById(R.id.addressName);
            viewHolder.mImageView = (ImageView) view.findViewById(R.id.calling);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.addressName.setText(list.get(i).getName());
        if("calling".equals(list.get(i).getCallType())){
            viewHolder.mImageView.setBackgroundResource(R.drawable.calling);
        }else if("called".equals(list.get(i).getCallType())){
            viewHolder.mImageView.setBackgroundResource(R.drawable.called);
        }
        return view;
    }

    class ViewHolder{
        TextView addressName;
        ImageView mImageView;
    }
}
