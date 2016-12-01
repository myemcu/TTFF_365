package com.myemcu.ttff_365.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.myemcu.ttff_365.R;
import com.myemcu.ttff_365.javabean.UserLoginResult;
import com.myemcu.ttff_365.ui.RoundImageView;
import com.myemcu.ttff_365.utils.MD5Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/11/24 0024.
 */
public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private RoundImageView user_logo;               // 用户图片

    private static final int ABLE_OK = 0x0011;      // 选择请求码
    private static final int CUT_OK  = 0x0022;      // 裁剪请求码
    private static final int CAMERA_OK = 0x0033;    // 拍照请求码

    private File   tempFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ActivityManagerUtil.getInstance().addActivity(this);
        setContentView(R.layout.activity_user_info);

        user_logo = (RoundImageView) findViewById(R.id.user_logo);
        user_logo.setOnClickListener(this);

        // 临时存放拍照的文件
        tempFile = new File(Environment.getExternalStorageDirectory(),"temp.png");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.user_logo:// 弹出选择照片or拍照的dialog
                                showDialog();
                                break;
        }
    }

    // 自定义对话框
    private void showDialog() {

        final Dialog dialog = new Dialog(this,R.style.dialog);    // 引入Style是为了去掉原生的丑(去头部)
        dialog.setContentView(R.layout.photo_choose_dialog);

        // 从底部弹出动画(view才能控制动画)
        // 1 获取弹出框的view
        Window window = dialog.getWindow();
        // 2 设置动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        // 3 底部定位
        window.setGravity(Gravity.BOTTOM);
        // 4 宽度全屏
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        // 5 点击事件
        Button image_depot = (Button) dialog.findViewById(R.id.image_depot);
        image_depot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 从手机中选择照片 利用系统 隐式意图
                Intent ableIntent = new Intent(Intent.ACTION_PICK);
                ableIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(ableIntent,ABLE_OK); // 这个执行完后会回调onAcyivityResult()方法，请求码ABLE_OK
                dialog.dismiss();
            }
        });
        Button photo_camre = (Button) dialog.findViewById(R.id.photo_camre);
        photo_camre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 拍照(加外存读写权限)
                Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(tempFile)); // 系统拍完之后，放到tempFile路径中
                startActivityForResult(getImageByCamera, CAMERA_OK);
                dialog.cancel();
            }
        });
        Button user_cancel = (Button) dialog.findViewById(R.id.user_cancel);    // user_cancel之前死活有红线，直接烧写OK掉
        user_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {  // 如果返回成功，resultCode是相册应用Activity给我们的
            if (requestCode == ABLE_OK) {
                // 相片选择成功之后，数据在data里面
                Uri uri = data.getData();   // 相片路径，通过uri获取，再通过BitmapFactory解析Bitmap
                // 一定要1:1方式裁剪 调用系统裁剪
                clipImage(uri);
            }
        }
        if (requestCode == CUT_OK) {
            // 获取裁剪的图片数据
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap bitmap = extras.getParcelable("data");
                user_logo.setImageBitmap(bitmap);

                // 保存裁剪好的图片到机身内存，名为temp.png是也(因为上传服务器的时候是以文件方式上传的)
                saveBitmapToFile(bitmap);

                // 提交图片文件到服务器(退出登录，关App重启，即可看到你的新头像，爽吧)
                upLoadImage();
            }
        }
        if (requestCode == CAMERA_OK) { // 对拍照的临时文件进行裁剪
            clipImage(Uri.fromFile(tempFile));
        }
    }

    // 上传图片文件到服务器
    private void upLoadImage() {    //  提交数据到服务器
        // 1 构建Okhttp客户端连接对象
        OkHttpClient okHttpClient = new OkHttpClient();
        // 2 构建参数Body为：MultipartBody.FORM 表单形式并封装参数
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("appid","1");// 用于PC看网址的(http://v2.ffu365.com/index.php?m=Api&c=Index&a=home&appid=1)
        // 获取uid
        String userInfostr = getSharedPreferences("info", Context.MODE_PRIVATE).getString("user_info",null); // 此时，读出来的是json字符串
        if (!TextUtils.isEmpty(userInfostr)) {
            Gson gson = new Gson(); // 使用Gson工具
            UserLoginResult.DataBean userInfo = gson.fromJson(userInfostr, UserLoginResult.DataBean.class); // 把读出的json串转为对象(左为json串，右为数据Bean)
            builder.addFormDataPart("uid",userInfo.getMember_info().getUid());

            Log.e("TAGuid",userInfo.getMember_info().getUid()); // 测试时的返回结果为219(http://v2.ffu365.com/index.php?m=Api&c=Member&a=memberInfoDetail&appid=1&uid=219)
        }
        // 如果要上传文件(.mp3.png等都需要单独封装)(记死)
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), tempFile);
        builder.addFormDataPart("file", tempFile.getName(), fileBody);

        // 3 构建一个请求，Post:提交
        Request request = new   Request.Builder()
                .url("http://v2.ffu365.com/index.php?m=Api&c=Member&a=userUploadAvatar")  // 仅适合于手机，PC打开该Web无效
                .post(builder.build())
                .build();
        // 4 发送一个请求(记得要开网络权限)
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override   // 请求失败
            public void onFailure(Call call, IOException e) {
                Log.e("TAG",e+"");
            }

            @Override   // 请求成功，服务端若保存上，则会显示操作成功
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Log.e("TAG",result);
            }
        });
    }

    private void saveBitmapToFile(Bitmap bitmap) {
        try {
            FileOutputStream fos = new FileOutputStream(tempFile.getAbsolutePath());
            bitmap.compress(Bitmap.CompressFormat.PNG,100,fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 调用系统方法裁剪相片
    private void clipImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");   // 隐式意图
        intent.setDataAndType(uri,"image/*");

        // Intent传值
        intent.putExtra("crop","true");

        // 宽高比例，正方形1:1
        intent.putExtra("aspectX",1);
        intent.putExtra("aspectY",1);

        // 裁剪图片宽高
        intent.putExtra("outputX",150);
        intent.putExtra("outputY",150);
        intent.putExtra("return-data",true);

        startActivityForResult(intent,CUT_OK); // Ctrl+Alt+C
    }
}
