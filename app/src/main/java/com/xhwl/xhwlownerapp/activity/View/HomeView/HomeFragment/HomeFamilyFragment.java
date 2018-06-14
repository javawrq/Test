package com.xhwl.xhwlownerapp.activity.View.HomeView.HomeFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.HorizontalListView.HorizontalListView;
import com.xhwl.xhwlownerapp.UIUtils.HorizontalListView.HorizontalListViewAdapter;
import com.xhwl.xhwlownerapp.UIUtils.ShowToast;
import com.xhwl.xhwlownerapp.activity.View.HomeView.DeviceActivity.AddDeviceActivity;
import com.xhwl.xhwlownerapp.activity.View.HomeView.ViewPagerAdapter.ViewPagerAdapter;
import com.xhwl.xhwlownerapp.activity.transformer.GalleryTransformer;
import com.zhy.autolayout.AutoLinearLayout;
import com.zhy.autolayout.AutoRelativeLayout;

import java.util.ArrayList;

/**
 * 我的家庭
 */
public class HomeFamilyFragment extends Fragment implements View.OnClickListener, GestureDetector.OnGestureListener {

    private View view;
    private ImageView mFamilyHomeHeadimg;
    private ImageView mFamilyHomeAdd;
    private ViewPager mFamilyHomeViewpager;
    private HorizontalListView mFamilyHomeHlistview;
    private HorizontalListViewAdapter listViewAdapter;
    private PagerAdapter adapter;
    private AutoRelativeLayout mFamilyHomeViewpagerRelat;
    /**
     * 装分页显示的view的数组
     */
    private ArrayList<View> framilyPageViews;
    private ImageView mAddDevice;
    private ImageView mAllRoom;
    private ImageView mAllScene;
    private AutoLinearLayout mAddLinear;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the rb_nobtn_selector for this fragment
        view = inflater.inflate(R.layout.fragment_home_family, container, false);
        initView(view);
        initDate();
        return view;
    }

    private void initDate() {
        //将要分页显示的View装入数组中
        LayoutInflater inflater = getLayoutInflater();
        framilyPageViews = new ArrayList<View>();
        framilyPageViews.add(inflater.inflate(R.layout.family_viewpager_item_1, null));
        framilyPageViews.add(inflater.inflate(R.layout.family_viewpager_item_2, null));
        framilyPageViews.add(inflater.inflate(R.layout.family_viewpager_item_3, null));
        framilyPageViews.add(inflater.inflate(R.layout.family_viewpager_item_4, null));
        framilyPageViews.add(inflater.inflate(R.layout.family_viewpager_item_5, null));
        framilyPageViews.add(inflater.inflate(R.layout.family_viewpager_item_6, null));
        adapter = new ViewPagerAdapter(getContext(), framilyPageViews);
        mFamilyHomeViewpager.setOffscreenPageLimit(3);
        //mFamilyHomeViewpager.setPageMargin(30);
        mFamilyHomeViewpager.setAdapter(adapter);
        mFamilyHomeViewpager.setPageTransformer(false, new GalleryTransformer());

        //事件分发，处理页面滑动问题
        mFamilyHomeViewpagerRelat.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return mFamilyHomeViewpager.dispatchTouchEvent(event);
            }
        });

        listViewAdapter = new HorizontalListViewAdapter(getActivity());
        listViewAdapter.notifyDataSetChanged();
        mFamilyHomeHlistview.setAdapter(listViewAdapter);
    }

    private void initView(View view) {
        mFamilyHomeHeadimg = (ImageView) view.findViewById(R.id.family_home_headimg);
        mFamilyHomeHeadimg.setOnClickListener(this);
        mFamilyHomeAdd = (ImageView) view.findViewById(R.id.family_home_add);
        mFamilyHomeAdd.setOnClickListener(this);
        mFamilyHomeViewpager = (ViewPager) view.findViewById(R.id.family_home_viewpager);
        mFamilyHomeViewpagerRelat = (AutoRelativeLayout) view.findViewById(R.id.family_home_viewpager_relat);
        mFamilyHomeHlistview = (HorizontalListView) view.findViewById(R.id.family_home_hlistview);
        mAddDevice = (ImageView) view.findViewById(R.id.add_device);
        mAddDevice.setOnClickListener(this);
        mAllRoom = (ImageView) view.findViewById(R.id.all_room);
        mAllRoom.setOnClickListener(this);
        mAllScene = (ImageView) view.findViewById(R.id.all_scene);
        mAllScene.setOnClickListener(this);
        mAddLinear = (AutoLinearLayout) view.findViewById(R.id.add_linear);
        mAddLinear.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.family_home_headimg:

                break;
            case R.id.family_home_add:
                //add 按钮
                mAddLinear.setVisibility(View.VISIBLE);
                break;
            case R.id.add_device:
                //添加设备
                startActivity(new Intent(getActivity(), AddDeviceActivity.class));
                break;
            case R.id.all_room:
                //全部房间
                ShowToast.show("正在开放中");
                //startActivity(new Intent(getActivity(), AllRoomActivity.class));
                break;
            case R.id.all_scene:
                //全部场景
                ShowToast.show("正在开放中");
                //startActivity(new Intent(getActivity(), AddDeviceActivity.class));
                break;
            case R.id.add_linear:
                //点击隐藏
                mAddLinear.setVisibility(View.GONE);

                break;
        }
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
}
