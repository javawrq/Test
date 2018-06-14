package com.xhwl.xhwlownerapp.activity.View.UserInfoView.ResidentialInfo.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.Entity.HouseEntiy.House;
import com.xhwl.xhwlownerapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/21.
 */

public class ResidentialAdapter extends BaseAdapter {
    private List<House> list = new ArrayList<>();
    private Context context;

    public ResidentialAdapter(List<House> list, Context context) {
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
            view = LayoutInflater.from(context).inflate(R.layout.house_list_item,null);
            viewHolder.houseBanNumber = (TextView) view.findViewById(R.id.my_info_house_banNumber);
            viewHolder.houserRoomNumber = (TextView) view.findViewById(R.id.my_info_house_room_number);
            viewHolder.houserUnitNumber = (TextView) view.findViewById(R.id.my_info_house_unit_number);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.houseBanNumber.setText(list.get(i).getHouseBanNumber());
        viewHolder.houserUnitNumber.setText(list.get(i).getHouseUnitNumber());
        viewHolder.houserRoomNumber.setText(list.get(i).getHouseRoomNumber());

        return view;
    }

    class ViewHolder{
        TextView houseBanNumber,houserRoomNumber,houserUnitNumber;
    }
}
