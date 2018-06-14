package com.xhwl.xhwlownerapp.activity.View.HomeView.ViewPagerAdapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/4/28.
 */

public class ViewPagerAdapter extends PagerAdapter {
    private Context mContext;
    /**装分页显示的view的数组*/
    private ArrayList<View> pageViews = new ArrayList<View>();;

    public ViewPagerAdapter(Context context, ArrayList<View> pageViews) {
        this.mContext = context;
        this.pageViews = pageViews;
    }

    @Override
    public int getCount() {
        return pageViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ((ViewPager) container).addView(pageViews.get(position));
//        ImageView imageView = new ImageView(mContext);
//        imageView.setLayoutParams(new ViewGroup.LayoutParams(DensityUtil.dip2px(mContext,200),DensityUtil.dip2px(mContext,400)));
//        imageView.setImageResource(pageViews.get(position));
//        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//        container.addView(imageView);
        return pageViews.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
