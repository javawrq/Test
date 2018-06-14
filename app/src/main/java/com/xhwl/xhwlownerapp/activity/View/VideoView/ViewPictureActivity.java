package com.xhwl.xhwlownerapp.activity.View.VideoView;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhwl.xhwlownerapp.Entity.UserEntity.LoadImage;
import com.xhwl.xhwlownerapp.R;
import com.zhy.autolayout.AutoLinearLayout;

public class ViewPictureActivity extends AppCompatActivity implements View.OnClickListener {

    private AutoLinearLayout mTopBack;
    private TextView mTopTitle;
    private ImageView mTopRecord;
    private ImageView mViewPicture;
    private LoadImage loadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_picture);
        initView();
    }

    private void initView() {
        loadImage = (LoadImage) getIntent().getSerializableExtra("loadimg");
        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopTitle.setText("云瞳监控");
        mTopRecord = (ImageView) findViewById(R.id.top_record);
        mTopRecord.setOnClickListener(this);
        mViewPicture = (ImageView) findViewById(R.id.view_picture);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.top_back:
                finish();
                break;
            case R.id.top_record:

                break;
        }
    }
}
