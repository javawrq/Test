package com.xhwl.xhwlownerapp.activity.View.Guide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.activity.View.LoginView.LoginActivity;

import java.util.ArrayList;

public class GuideActivity extends BaseActivity {

    private ViewPager mGuidePages;
    /**装分页显示的view的数组*/
    private ArrayList<View> pageViews;
    private ImageView imageView;
    //包裹滑动图片的LinearLayout
    private ViewGroup viewPics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
    }

    private void initView() {

        //将要分页显示的View装入数组中
        LayoutInflater inflater = getLayoutInflater();
        pageViews = new ArrayList<View>();
        pageViews.add(inflater.inflate(R.layout.viewpager_page1, null));
        pageViews.add(inflater.inflate(R.layout.viewpager_page2, null));
//        pageViews.add(inflater.inflate(R.layout.viewpager_page3, null));
//        pageViews.add(inflater.inflate(R.layout.viewpager_page4, null));
//        pageViews.add(inflater.inflate(R.layout.viewpager_page5, null));

        //从指定的XML文件加载视图
        viewPics = (ViewGroup) inflater.inflate(R.layout.activity_guide, null);

        mGuidePages = (ViewPager) viewPics.findViewById(R.id.guidePages);

        //添加小圆点的图片
        for (int i = 0; i < pageViews.size(); i++) {
            imageView = new ImageView(GuideActivity.this);
            //设置小圆点imageview的参数
            imageView.setLayoutParams(new ViewGroup.LayoutParams(20, 20));//创建一个宽高均为20 的布局
            imageView.setPadding(80, 20, 80, 20);
        }
        //显示滑动图片的视图
        setContentView(viewPics);

        //设置viewpager的适配器和监听事件
        mGuidePages.setAdapter(new GuidePageAdapter());
        mGuidePages.setOnPageChangeListener(new GuidePageChangeListener());
    }
    private Button.OnClickListener Button_OnClickListener = new Button.OnClickListener() {
        public void onClick(View v) {
            //设置已经引导
            setGuided();
            //跳转
            Intent mIntent = new Intent();
            mIntent.setClass(GuideActivity.this, LoginActivity.class);
            GuideActivity.this.startActivity(mIntent);
            GuideActivity.this.finish();
        }
    };
    private static final String SHAREDPREFERENCES_NAME = "my_pref";
    private static final String KEY_GUIDE_ACTIVITY = "guide_activity";
    private void setGuided(){
        SharedPreferences settings = getSharedPreferences(SHAREDPREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_GUIDE_ACTIVITY, "false");
        editor.commit();
    }

    class GuidePageAdapter extends PagerAdapter {
        //销毁position位置的界面
        @Override
        public void destroyItem(View v, int position, Object arg2) {
            // TODO Auto-generated method stub
            ((ViewPager)v).removeView(pageViews.get(position));

        }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

        //获取当前窗体界面数
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return pageViews.size();
        }

        //初始化position位置的界面
        @Override
        public Object instantiateItem(View v, int position) {
            // TODO Auto-generated method stub
            ((ViewPager) v).addView(pageViews.get(position));

            // 测试页卡1内的按钮事件
            if (position == 1) {
                Button btn = (Button) v.findViewById(R.id.btn_close_guide);
                btn.setOnClickListener(Button_OnClickListener);
            }

            return pageViews.get(position);
        }

        // 判断是否由对象生成界面
        @Override
        public boolean isViewFromObject(View v, Object arg1) {
            // TODO Auto-generated method stub
            return v == arg1;
        }

        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public int getItemPosition(Object object) {
            // TODO Auto-generated method stub
            return super.getItemPosition(object);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub

        }

        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }
    }

    class GuidePageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int position) {
            // TODO Auto-generated method stub

        }
    }

    //单击返回键返回，不退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
        }
        return true;
    }
}
