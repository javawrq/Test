package com.xhwl.xhwlownerapp.activity.View.HomeView.HomeFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.activity.View.Shop.XQShopActivity;

/**
 * 小七商城
 */
public class HomeMallFragment extends Fragment implements View.OnClickListener {

    private View view;
    private ImageView mXqshopBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the rb_nobtn_selector for this fragment
        view = inflater.inflate(R.layout.fragment_home_mall, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mXqshopBtn = (ImageView) view.findViewById(R.id.xqshop_btn);
        mXqshopBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()) {
            default:
                break;
            case R.id.xqshop_btn:
                intent.setClass(getActivity(), XQShopActivity.class);
                startActivity(intent);
                break;
        }
    }
}
