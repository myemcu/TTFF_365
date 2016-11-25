package com.myemcu.ttff_365.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.myemcu.ttff_365.R;
import com.myemcu.ttff_365.fragment.MyselfFragment;
import com.myemcu.ttff_365.javabean.HomeDataResult;
import com.myemcu.ttff_365.javabean.UserLoginResult;
import com.myemcu.ttff_365.utils.MD5Util;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/11/24 0024.
 */
public class UserLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_user_phone,et_user_password;
    private CheckBox check_box_password;
    private Button btn_login;

    private UserLoginResult loginResult;    // dataBean对象

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        et_user_phone = (EditText) findViewById(R.id.et_user_phone);
        et_user_password = (EditText) findViewById(R.id.et_user_password);
        check_box_password = (CheckBox) findViewById(R.id.check_box_password);
        btn_login = (Button) findViewById(R.id.btn_login);

        // 1 完成基本功能(密码的显示或隐藏)，CheckBox的状态监听
        check_box_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean checked) {
                if(checked){
                    // 显示密码
                    et_user_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    // 隐藏密码
                    et_user_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

                // 把光标移动到EditText最后
                Editable etext = (Editable) et_user_password.getText();
                Selection.setSelection(etext, etext.length());
            }
        });

        // 2 处理点击提交数据
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                                UserLogin();
                                break;
        }
    }

    private void UserLogin() {
        // 1 本地验证
        String userPhone = et_user_phone.getText().toString().trim();   // .trim()支持空格输入
        String userPassword = et_user_password.getText().toString().trim();

        if (TextUtils.isEmpty(userPhone)) { // 输入判空
            Toast.makeText(this,"请输入手机号",Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(userPassword)) { // 输入判空
            Toast.makeText(this,"请输入登陆密码",Toast.LENGTH_SHORT).show();
            return;
        }

        // 2 提交数据到服务器

        // 1 构建Okhttp客户端连接对象
        OkHttpClient okHttpClient = new OkHttpClient();
        // 2 构建参数Body为：MultipartBody.FORM 表单形式并封装参数
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("appid","1");// 用于PC看网址的(http://v2.ffu365.com/index.php?m=Api&c=Index&a=home&appid=1)
        builder.addFormDataPart("cell_phone",userPhone);
        builder.addFormDataPart("password", MD5Util.strToMd5(userPassword));    // MD5，AES
        // 3 构建一个请求，Post:提交
        Request request = new   Request.Builder()
                .url("http://v2.ffu365.com/index.php?m=Api&c=Member&a=login")  // 仅适合于手机，PC打开该Web无效
                .post(builder.build())
                .build();
        // 4 发送一个请求(记得要开网络权限)
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override   // 请求失败
            public void onFailure(Call call, IOException e) {
                Log.e("TAG",e+"");
            }

            @Override   // 请求成功，数据在response中，就是获取到的json字符串(LogCat->Info->TAG:进行观察)
            public void onResponse(Call call, Response response) throws IOException {

                // Toast.makeText(UserLoginActivity.this,"登陆成功",Toast.LENGTH_SHORT).show(); // 放这里也要崩

                String result = response.body().string();
                Log.e("TAG",result);    // 验证时，初始账号密码为：14726932514-12456(做到这里的时候，先要去看服务端的返回数据)

                // Gson的json解析
                Gson gson = new Gson();
                loginResult = gson.fromJson(result, UserLoginResult.class);

                // 处理登陆结果
                delLoginResult(loginResult);
            }
        });
    }


    // is_Login:登陆标志
    // user_info:登陆后的用户数据
    private void delLoginResult(UserLoginResult result) {
        // 1 首先判断有没有登陆成功
        if (result.getErrcode() == 1) {

            // Toast.makeText(this,"欢迎您：",Toast.LENGTH_SHORT).show(); // 吐司要崩，居然

            // 1 保存登录状态标志 当前设置为已登陆
            SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
            sp.edit().putBoolean("is_Login",true).apply();// 放一个布尔值，值为true，代表已登陆

            // 2 向SharedPreferences保存所有用户数据
            UserLoginResult.DataBean userData = result.getData();   // 获取对象数据
            Gson gson = new Gson();                                 // 使用Gson工具
            String userInfoStr = gson.toJson(userData);             // 将对象数据转换为json字符串
            sp.edit().putString("user_info",userInfoStr).apply();   // 再将json字符串保存到SharedPreferences

            // 3 执行完成，退出这个页面
            finish();


        }else {
            Toast.makeText(this,result.getErrmsg(),Toast.LENGTH_SHORT).show();
        }
    }
}
