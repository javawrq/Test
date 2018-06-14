package com.xhwl.xhwlownerapp.activity.View.UserInfoView.BasicsInfo;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.xhwl.xhwlownerapp.R;
import com.xhwl.xhwlownerapp.UIUtils.BaseActivity;
import com.xhwl.xhwlownerapp.UIUtils.MyAPP;
import com.xhwl.xhwlownerapp.UIUtils.SPUtils;
import com.xhwl.xhwlownerapp.UIUtils.ShowToast;
import com.xhwl.xhwlownerapp.activity.View.LoginView.LoginActivity;
import com.xhwl.xhwlownerapp.net.Constant;
import com.xhwl.xhwlownerapp.net.HttpConnectionInter;
import com.xhwl.xhwlownerapp.net.HttpConnectionTools;
import com.zhy.autolayout.AutoLinearLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SetHeadImgActivity extends BaseActivity implements View.OnClickListener {
    private Context context;

    private AutoLinearLayout mTopBack;
    private TextView mTopTitle;
    private ImageView mModifyHeadimg;
    private TextView mTopBtn;
    private String imgUrl;
    private Bitmap head;// 头像Bitmap
    private String headImgPath;//存在本地的头像路径
    private String id, token;
    public static final String TYPE = "image/*";
    private OkHttpClient client;
    private String serverHeadImg;//服务器返回的头像地址，最终设置头像的地址
    private Uri uritempFile;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_head_img);
        initView();
        initDate();
    }

    private void initDate() {
        imgUrl = SPUtils.get(SetHeadImgActivity.this, "imageUrl", "");
        //设置头像
        Picasso.with(this)
                .load(imgUrl)
                .error(R.drawable.headimg).into(mModifyHeadimg);
    }

    //单击返回键返回，不退出
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            finish();
        }
        return true;
    }

    private void initView() {
        id = SPUtils.get(SetHeadImgActivity.this, "id", "");
        token = SPUtils.get(SetHeadImgActivity.this, "token", "");
        mTopBack = (AutoLinearLayout) findViewById(R.id.top_back);
        mTopBack.setOnClickListener(this);
        mTopTitle = (TextView) findViewById(R.id.top_title);
        mTopTitle.setText("个人头像");
        mModifyHeadimg = (ImageView) findViewById(R.id.modify_headimg);
        mTopBtn = (TextView) findViewById(R.id.top_btn);
        mTopBtn.setText("修改");
        mTopBtn.setVisibility(View.VISIBLE);
        mTopBtn.setOnClickListener(this);
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
                //修改头像
                showTypeDialog();
                break;
        }
    }

    //选择相册or拍照
    private void showTypeDialog() {
        //显示对话框
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_select_photo, null);
        TextView tv_select_gallery = (TextView) view.findViewById(R.id.tv_select_gallery);
        TextView tv_select_camera = (TextView) view.findViewById(R.id.tv_select_camera);
        tv_select_gallery.setOnClickListener(new View.OnClickListener() {// 在相册中选取
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_PICK, null);
                //打开文件
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent1, 1);
                dialog.dismiss();
            }
        });
        tv_select_camera.setOnClickListener(new View.OnClickListener() {// 调用照相机
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                //String filePath = Environment.getExternalStorageDirectory() + "head.png";
                File file = null;
                try {
                    file = new File(getExternalCacheDir().getCanonicalFile() + "/" + "head.png");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //解决7.0拍照、裁剪图片FileUriExposedException 异常
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(SetHeadImgActivity.this, getPackageName() + ".fileProvider",
                            file);
                } else {
                    uri = Uri.fromFile(file);

                }
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent2, 2);
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    cropPhoto(data.getData());// 裁剪图片
                }
                break;
            case 2:
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(SetHeadImgActivity.this, "取消了拍照", Toast.LENGTH_LONG).show();
                    return;
                } else if (resultCode == RESULT_OK) {
                    cropPhoto(uri);// 裁剪图片
                }
                break;
            case 3:
                //将Uri图片转换为Bitmap
                try {
                    Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uritempFile));
                    setPicToView(bitmap);// 保存在SD卡中
                    mModifyHeadimg.setImageBitmap(bitmap);// 用ImageButton显示出来

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
//                if (data != null) {
//                    Bundle extras = data.getExtras();
//                    if(extras != null){
//                        head = extras.getParcelable("data");
//                        if (head != null) {
//                            /**
//                             * 上传服务器代码
//                             */
//                            setPicToView(head);// 保存在SD卡中
//                            mModifyHeadimg.setImageBitmap(head);// 用ImageButton显示出来
//                            headImgUpload(headImgPath,Constant.HOST2+Constant.SERVERNAME+Constant.INTERFACEVERSION+Constant.FILEUPLOAD);
//                            Log.e("headImg",headImgPath);
//                        }else {
//                            Toast.makeText(SetHeadImgActivity.this, "取消了裁剪", Toast.LENGTH_LONG).show();
//                        }
//                    }else {
//                        Toast.makeText(SetHeadImgActivity.this, "取消了裁剪", Toast.LENGTH_LONG).show();
//                    }
//                }
                break;
            default:
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 调用系统的裁剪功能
     *
     * @param uri
     */
    public void cropPhoto(Uri uri) {
        //有些损毁的图片，会导致闪退
        if (uri.toString().equals("file:///")) {
            Toast toast = Toast.makeText(this, "此文件不可用", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
//        Bitmap bitmap = null;
//        try {
//            //bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
//            bitmap = BitmapUtils.ImageCrop( MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri));
//        } catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        //这里做了判断  如果图片大于 512KB 就进行压缩
//        if(bitmap.getRowBytes()*bitmap.getHeight() > 512*1024){
//            bitmap = BitmapUtils.compressImage(bitmap);
//            uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null,null));
//        }

        //7.0之后裁剪照片
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("output", uri);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1.2);  // 裁剪框比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 400);  // 输出图片大小
        intent.putExtra("outputY", 300);
        intent.putExtra("scale", true);  // 去黑边
        intent.putExtra("scaleUpIfNeeded", true);  // 去黑边
        //裁剪后的图片Uri路径，uritempFile为Uri类变量
        //uritempFile = Uri.parse("file://" +  "/" + Environment.getExternalStorageDirectory() + "/" + "head.png");
        File file = null;
        try {
            file = new File(getExternalCacheDir().getCanonicalFile() + "/" + "head.png");
        } catch (IOException e) {
            e.printStackTrace();
        }
        uritempFile = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uritempFile);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        ComponentName componentName = intent.resolveActivity(getPackageManager());
        if (componentName != null) {
            startActivityForResult(intent, 3);
        } else {
            Log.e(TAG, "无法解析intent，捕获崩溃");
        }
    }

    private static final String TAG = "SetHeadImgActivity";

    //保存图片
    private void setPicToView(Bitmap mBitmap) {
//        File SDFile = Environment.getExternalStorageDirectory();
//        String path = SDFile.getAbsolutePath() + File.separator + "myHead/";
        try {
            String path = getExternalCacheDir().getCanonicalFile() + "/" + "myHead/";
            FileOutputStream b = null;
            //File file = new File(path);
            File file  = new File(path);
            file.mkdirs();// 创建文件夹
            String fileName = path + "head.png";// 图片名字
            headImgPath = path;
            try {
                b = new FileOutputStream(fileName);
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    // 关闭流
                    b.flush();
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            headImgUpload(headImgPath, Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.FILEUPLOAD);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //将头像上传到服务器获取路径
    private void headImgUpload(String imgUrl, String serverUrl) {
        client = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        Log.e("file", imgUrl);
        File file = new File(imgUrl, "head.png");
        if (!file.exists()) {
            Toast.makeText(SetHeadImgActivity.this, "文件不存在", Toast.LENGTH_SHORT).show();
        } else {
            //MultipartBody.Builder requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM);
            RequestBody fileBody = RequestBody.create(MediaType.parse(TYPE), file);
            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("files", file.getName(), fileBody)
                    .build();

            Request requestPostFile = new Request.Builder()
                    .url(serverUrl)
                    .post(requestBody)
                    .build();
            client.newCall(requestPostFile).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("lfq", "onFailure");
                    handler1.sendEmptyMessage(0x400);
                }

                @Override
                public void onResponse(Call call, final Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String str = response.body().string();
                        try {
                            JSONObject jsonObject = new JSONObject(str);
                            int errorCode = jsonObject.getInt("errorCode");
                            if (errorCode == 200) {
                                String result = jsonObject.getString("result");
                                jsonObject = new JSONObject(result);
                                serverHeadImg = jsonObject.getString("url");
                                //获取到服务器的头像地址，再去修改用户的头像
                                modifyHeadImg(token, id, serverHeadImg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.e("lfq", response.message() + " error : body " + response.body().string());
                        handler1.sendEmptyMessage(0x400);
                    }
                }
            });
        }
    }


    //将头像上传到服务器
    private void modifyHeadImg(String token, String id, String imageUrl) {
        HttpConnectionTools.HttpServler(Constant.HOST2 + Constant.SERVERNAME + Constant.INTERFACEVERSION + Constant.UPDATEUSERINFO,
                HttpConnectionTools.HttpData("id", id, "token", token, "imageUrl", imageUrl), new HttpConnectionInter() {
                    @Override
                    public void onFinish(String content) {
                        //网络请求成功
                        try {
                            JSONObject obj = new JSONObject(content);
                            int errorCode = obj.getInt("errorCode");
                            if (errorCode == 200) {
                                handler1.sendEmptyMessage(0x1);
                            } else if (errorCode == 116) {
                                handler1.sendEmptyMessage(0x2);
                            } else if (errorCode == 400) {
                                handler1.sendEmptyMessage(0x3);
                            } else if (errorCode == 401) {
                                handler1.sendEmptyMessage(0x4);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler1.sendEmptyMessage(201);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        handler1.sendEmptyMessage(0x122);
                    }
                });
    }

    Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x1) {
                ShowToast.show("修改成功");
                SPUtils.put(SetHeadImgActivity.this, "imageUrl", serverHeadImg);
                //finish();
            } else if (msg.what == 0x2) {
                ShowToast.show("账户不存在");
            } else if (msg.what == 0x3) {
                ShowToast.show("您已退出登陆");
                startToAIctivity(LoginActivity.class);
                MyAPP.Logout(SetHeadImgActivity.this);
                finish();
            } else if (msg.what == 0x4) {
                ShowToast.show("token已经过期，请重新登录");
                startToAIctivity(LoginActivity.class);
                MyAPP.Logout(SetHeadImgActivity.this);
                finish();
            } else if (msg.what == 0x122) {
                ShowToast.show("请检查网络");
            } else {
                ShowToast.show("修改失败");
            }
        }
    };
}
