package com.xhwl.xhwlownerapp.activity.View.VideoView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.hikvision.sdk.utils.FileUtils;
import com.xhwl.xhwlownerapp.Entity.UserEntity.LoadImage;
import com.xhwl.xhwlownerapp.Entity.VideoEntity.VideoEntity;
import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.UIUtils.dialog.SelfDialog;
import com.xhwl.xhwlownerapp.activity.View.VideoView.Adapter.PictureRecordGridViewAdapter;
import com.xhwl.xhwlownerapp.activity.View.VideoView.Adapter.RecordImageAdapter;
import com.zhy.autolayout.AutoLinearLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PictureRecordActivity extends BaseActivity implements View.OnClickListener{

    private AutoLinearLayout mTopBack;
    private TextView mTopTitle;
    private TextView mTopBtn;
    private GridView mHkImgRv;
    /**
     * 已选择:0
     */
    private TextView mHkSelectNumTv;
    /**
     * 全选
     */
    private TextView mHkSelectAllTv;
    /**
     * 删除
     */
    private Button mHkDeleteTv;
    private int mEditMode = MODE_CHECK;
    private AutoLinearLayout mBottomLay;
    private static final int MODE_CHECK = 0;//全选
    private static final int MODE_EDIT = 1;//编辑
    private PictureRecordGridViewAdapter gridViewAdapter;
    private boolean isSelectAll = false;
    private boolean editorStatus = false;
    private int index = 0;
    private SelfDialog alertView;
    private List<VideoEntity> videoEntityList;

    private List<String> imagePath=new ArrayList<String>();//图片文件的路径
    private static String[] imageFormatSet=new String[]{"jpg","png","gif"};//合法的图片文件格式

    private List<LoadImage> fileNameList = new ArrayList<LoadImage>();     //保存Adapter中显示的图片详情(要跟adapter里面的List要对应)
    private List<LoadImage> selectFileLs = new ArrayList<LoadImage>();      //保存选中的图片信息
    private RecordImageAdapter imgAdapter;

    private boolean isDbClick = false;   //是否正在长按状态
    private String userPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_record);
        initView();
        String sdpath= FileUtils.getPictureDirPath().getAbsolutePath()+"/"+userPhone;//获得SD卡的路径
        getFiles(sdpath);//调用getFiles()方法获取SD卡上的全部图片
        if(imagePath.size()<1){//如果不存在文件图片
            return;
        }
    }

    private void initView() {
        userPhone = SPUtils.get(this,"userTelephone","");
        videoEntityList = new ArrayList<>();
        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopTitle.setText("抓拍记录");
        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mTopBtn.setText("取消");
        mTopBtn.setVisibility(View.GONE);
        mTopBtn.setOnClickListener(this);
        mHkImgRv = (GridView) findViewById(R.id.hk_img_rv);
        mHkDeleteTv = (Button) findViewById(R.id.hk_delete_tv);
        mHkDeleteTv.setOnClickListener(delClickListener);
        mBottomLay = (AutoLinearLayout) findViewById(R.id.bottom_lay);

        imgAdapter = new RecordImageAdapter(this);
        mHkImgRv.setAdapter(imgAdapter);
        mHkImgRv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                LoadImage loadimg = fileNameList.get(position);
                RecordImageAdapter.ViewHolder holder = (RecordImageAdapter.ViewHolder) view.getTag();
                if (isDbClick) {
                    if (selectFileLs.contains(loadimg)) {
                        holder.image1.setImageDrawable(null);
                        holder.image2.setVisibility(View.GONE);
                        imgAdapter.delNumber(position + "");
                        selectFileLs.remove(loadimg);
                    } else {
                        holder.image2.setVisibility(View.VISIBLE);  //设置图片右上角的对号显示
                        imgAdapter.addNumber(position + ""); //把该图片添加到adapter的选中状态，防止滚动后就没有在选中状态了。
                        selectFileLs.add(loadimg);
                    }
                    //seclectNumView.setText("选中"+selectFileLs.size()+"张图片");
                } else {
                    //startActivity(new Intent(PictureRecordActivity.this, PictureViewAct.class).putExtra("flag", "upload").putExtra("imagePath", loadimg.getFileName()));
//                    Intent intent = new Intent(PictureRecordActivity.this,ViewPictureActivity.class);
//                    intent.putExtra("loadimg", loadimg);
//                    startActivity(intent);
                }
            }
        });
        mHkImgRv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                LoadImage loadimg = fileNameList.get(position);
                RecordImageAdapter.ViewHolder holder = (RecordImageAdapter.ViewHolder) view.getTag();
                if (!isDbClick) {
                    isDbClick = true;
                    //mHkImgRv.setPadding(0, 0, 0, 0);//长按后，让gridview上下都分出点空间，显示删除按钮之类的。看效果图就知道了。
                    holder.image2.setVisibility(View.VISIBLE);
                    imgAdapter.addNumber(position + "");
                    selectFileLs.add(loadimg);
                    mTopBtn.setVisibility(View.VISIBLE);
                    // seclectNumView.setText("选中1张图片");
                    return true;
                }
                return false;
            }
        });
        Toast.makeText(this, "加载图片中....", Toast.LENGTH_SHORT).show();
        new AsyncLoadedImage().execute();
//        BaseAdapter adapter=new BaseAdapter(){
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                ImageView iv;//声明ImageView的对象
//                if(convertView==null){
//                    iv=new ImageView(PictureRecordActivity.this);//实例化ImageView的对象
//                    /**************设置图像的宽度和高度**************/
//                    iv.setAdjustViewBounds(true);
//                    iv.setMaxWidth(500);
//                    iv.setMaxHeight(700);
//                    /****************************/
//                    iv.setPadding(5, 5, 5, 5);//设置ImageView的内边距
//                }else{
//                    iv=(ImageView)convertView;
//                }
//                //为ImageView设置要显示的图片
//                Bitmap bm= BitmapFactory.decodeFile(imagePath.get(position));
//                iv.setImageBitmap(bm);
//                return iv;
//
//            }
//
//            //获得数量
//            @Override
//            public int getCount() {
//                return imagePath.size();
//            }
//
//
//            //获得当前选项
//            @Override
//            public Object getItem(int position) {
//                return position;
//            }
//
//
//            //获得当前选项的id
//            @Override
//            public long getItemId(int position) {
//                return position;
//            }
//        };
//        mHkImgRv.setAdapter(adapter);//将适配器与GridView关联
    }

    /**
     * 删除监听器事件
     */
    private android.view.View.OnClickListener delClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(selectFileLs.isEmpty()) {
                Toast.makeText(PictureRecordActivity.this, "请长按选择图片删除", Toast.LENGTH_SHORT).show();
                return ;
            }
            for(LoadImage loadimg : selectFileLs){
                File file = new File(loadimg.getFileName());
                boolean isTrue = file.delete();
                Log.i("---------删除图片------", isTrue+"---------------");
            }
            imgAdapter.deletePhoto(selectFileLs);
            selectFileLs.clear();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            //停止预览按钮点击操作
            finish();
        }
        return true;
    }
    /**
     * 异步加载图片展示
     * @author： aokunsang
     * @date： 2012-8-1
     */
    class AsyncLoadedImage extends AsyncTask<Object, LoadImage, Boolean> {

        @Override
        protected Boolean doInBackground(Object... params) {
            File fileDir = new File(FileUtils.getPictureDirPath().getAbsolutePath()+"/"+userPhone);
            File[] files = fileDir.listFiles();
            boolean result = false;
            if(files!=null){
                for(File file:files){
                    String fileName = file.getName();
                    if (fileName.lastIndexOf(".") > 0
                            && fileName.substring(fileName.lastIndexOf(".") + 1,
                            fileName.length()).equals("jpg")){
                        Bitmap bitmap;
                        Bitmap newBitmap;
                        try {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inSampleSize = 0;
                            bitmap = BitmapFactory.decodeFile(file.getPath(), options);
                            newBitmap = ThumbnailUtils.extractThumbnail(bitmap, 176, 163);
                            bitmap.recycle();
                            if (newBitmap != null) {
                                LoadImage loadImage = new LoadImage(file.getPath(),newBitmap);
                                fileNameList.add(loadImage);
                                publishProgress(loadImage);
                                result = true;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            return result;
        }
        @Override
        public void onProgressUpdate(LoadImage... value) {
            for(LoadImage loadImage:value){
                imgAdapter.addPhoto(loadImage);
            }
        }
        @Override
        protected void onPostExecute(Boolean result) {
            if(!result){
                showDialog(1);
            }
        }
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        AlertDialog dialog = new AlertDialog.Builder(PictureRecordActivity.this).setTitle("温馨提示").setMessage("暂时还没有照片,请先采集照片！")
                .setPositiveButton("确定", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //startActivity(new Intent(PictureRecordActivity.this,TakePhotoAct.class));
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).show();
        return dialog;
    }

    /*
    * 方法:判断是否为图片文件
    * 参数:String path图片路径
    * 返回:boolean 是否是图片文件，是true，否false
    * */
    private static boolean isImageFile(String path){
        for(String format:imageFormatSet){//遍历数组
            if(path.contains(format)){//判断是否为合法的图片文件
                return true;
            }
        }
        return false;
    }

    /*
    * 方法:用于遍历指定路径
    * 参数:String url遍历路径
    * 无返回值
    * */
    private void getFiles(String url){
        File files=new File(url);//创建文件对象
        File[] file=files.listFiles();
        try {
            for(File f:file){//通过for循环遍历获取到的文件数组
                if(f.isDirectory()){//如果是目录，也就是文件夹
                    getFiles(f.getAbsolutePath());//递归调用
                }else{
                    if(isImageFile(f.getPath())){//如果是图片文件
                        imagePath.add(f.getPath());//将文件的路径添加到List集合中
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();//输出异常信息
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
            case R.id.top_back:
                //返回
                finish();
                break;
            case R.id.top_btn:
                //返回
                isDbClick = false;
                mTopBtn.setVisibility(View.GONE);
                //mHkImgRv.setPadding(0, 0, 0, 0);//退出编辑转台时候，使gridview全屏显示
                mTopBtn.setVisibility(View.GONE);
                selectFileLs.clear();
                imgAdapter.clear();
                break;
        }
    }
}
