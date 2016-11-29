package com.myemcu.ttff_365.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.myemcu.ttff_365.R;
import com.myemcu.ttff_365.javabean.UserLoginResult;
import com.myemcu.ttff_365.javabean.UserRequestCodeResult;

import com.myemcu.ttff_365.ui.VerificationCodeButton;
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
public class UserRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_user_phone,et_user_code,et_user_password;   // 手机号，验证码，密码
    private CheckBox check_box_password;                            // 小眼睛密码显示隐藏Checkbox

    private VerificationCodeButton btn_send_agree_code;             // 自定义验证码Button
    private Button btn_register;                                    // 注册按钮

    private TextView tv_agreement;                                  // 我已阅读并同意...TextView

    private UserLoginResult loginResult;                            // 登录结果的dataBean对象

    private Handler handler = new Handler();                        // 原版这里有个static

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        findViews();

        // 1 小眼睛密码监测，CheckBox的状态监听
        check_box_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean checked) {
                if(checked){
                    // 显式密码
                    et_user_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{
                    // 隐式密码
                    et_user_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

                // 把光标移动到EditText最后
                Editable etext = et_user_password.getText();
                Selection.setSelection(etext, etext.length());
            }
        });

        // 2 发送验证码的Button处理
        btn_send_agree_code.setOnClickListener(this);
        btn_send_agree_code.bindPhoneEditText(et_user_phone);   // 与EditText绑定(绑定后会导致按钮变灰)(当输入完整的手机号后才变绿)

        // 3 注册Button的处理
        btn_register.setOnClickListener(this);

        // R.color.main_color(找颜色)
        // 3 html方式设置文字样式不一
        tv_agreement.setText(Html.fromHtml("我已阅读并同意<font color='#24cfa2'>《天天防腐用户协议》</font>"));
    }

    private void findViews() {
        et_user_phone = (EditText) findViewById(R.id.et_user_phone);
        et_user_password = (EditText) findViewById(R.id.et_user_password);
        et_user_code = (EditText) findViewById(R.id.et_user_code);

        check_box_password = (CheckBox) findViewById(R.id.check_box_password);

        btn_send_agree_code = (VerificationCodeButton) findViewById(R.id.btn_send_agree_code); // 发送验证码Button
        btn_register = (Button) findViewById(R.id.btn_register);                    // 注册Button

        tv_agreement = (TextView) findViewById(R.id.tv_agreement);                  // 建议阅读TextView
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_send_agree_code:  /*按钮点击，向后台发请求*/
                                            // 1 按钮设置为请稍后状态(用户已经正确输入完了手机号)
                                            btn_send_agree_code.startLoad();
                                            // 2 请求后台信息
                                            requestUserCode();
                                            break;

            case R.id.btn_register:         userRegister();
                                            break;
        }
    }

    // 用户注册
    private void userRegister() {
        // 1.本地验证
        String userPhone = et_user_phone.getText().toString().trim();   // 手机号
        String password = et_user_password.getText().toString().trim(); // 密码
        String userCode = et_user_code.getText().toString().trim();     // 验证码

        // 2 各种判空
        if(TextUtils.isEmpty(userPhone)){
            Toast.makeText(this,"请输入用户名",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"请输入密码",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(userCode)){
            Toast.makeText(this,"请输入验证码",Toast.LENGTH_LONG).show();
            return;
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("appid", "1");
        builder.addFormDataPart("verify_code",userCode);
        builder.addFormDataPart("cell_phone",userPhone);
        builder.addFormDataPart("password", password);  // 接口文档中不要求MD5 AES

        Request request = new Request.Builder()
                                     .url("http://v2.ffu365.com/index.php?m=Api&c=Member&a=register")
                                     .post(builder.build())
                                     .build();

        // 4 发送一个请求
        okHttpClient.newCall(request).enqueue(new Callback() {// 请求的回调

            @Override
            public void onFailure(Call call, IOException e) {
                // 失败
            }

            @Override   // 这个不是运行在主线程中
            public void onResponse(Call call, Response response) throws IOException {
                // 成功  数据在response里面  获取后台给我们的JSON 字符串
                // 注册完之后不需要用户再次登录  返回的结果就是登录的结果
                String result = response.body().string();
                Log.e("TAG", result);
                Gson gson = new Gson();
                final UserLoginResult loginResult = gson.fromJson(result, UserLoginResult.class);

                // 不进行县城处理会报错
                /*Can't create handler inside thread that has not called Looper.prepare()*/

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        dealLoginResult(loginResult);
                    }
                });

            }
        });
    }

    // 用户验证
    private void requestUserCode() {
        // 1 本地验证
        String userPhone = et_user_phone.getText().toString().trim();   // .trim()支持空格输入

        if (TextUtils.isEmpty(userPhone)) { // 输入判空
            Toast.makeText(this,"请输入手机号",Toast.LENGTH_SHORT).show();
            return;
        }

        // 2 提交数据到服务器

        // 1 构建Okhttp客户端连接对象
        OkHttpClient okHttpClient = new OkHttpClient();
        // 2 构建参数Body为：MultipartBody.FORM 表单形式并封装参数
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        builder.addFormDataPart("appid","1");// 用于PC看网址的(http://v2.ffu365.com/index.php?m=Api&c=Index&a=home&appid=1)
        builder.addFormDataPart("sms_type","3");// 参照接口文档
        builder.addFormDataPart("cell_phone",userPhone); //  添加多个参数(艹，之前忘了写这个)

        // 3 构建一个请求，Post:提交
        Request request = new   Request.Builder()
                .url("http://v2.ffu365.com/index.php?m=Api&c=Util&a=sendVerifyCode")  // 参照接口文档(获取验证码)
                .post(builder.build())
                .build();

        // 4 发送一个请求(记得要开网络权限)
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override   // 请求失败
            public void onFailure(Call call, IOException e) {
                Log.e("TAG",e+"");
            }

            @Override   // 请求成功，这个不是运行在主线程中
            public void onResponse(Call call, Response response) throws IOException {

                // Toast.makeText(UserLoginActivity.this,"登陆成功",Toast.LENGTH_SHORT).show(); // 放这里也要崩

                String result = response.body().string();
                Log.e("TAG",result);    // 验证时，初始账号密码为：14726932514-12456(做到这里的时候，先要去看服务端的返回数据)

                /*点发送验证码后的json返回数据如下：
                * {
                    "errcode": 1,
                    "errmsg": "操作成功",
                    "errdialog": 0
                  }
                * */

                // Gson的json解析
                Gson gson = new Gson(); // json结果保存到codeResult对象中
                final UserRequestCodeResult codeResult = gson.fromJson(result, UserRequestCodeResult.class);  // json转对象

                // 3 获取后台返回值后，如果成功则倒计时,否则要把按钮的状态设置为又可以点
                //   处理登陆结果(这个要搞到主线程中)(联网请求时个耗时任务，不再主线程即UI线程)
                /*   不加线程的报错结果：
                *    Only the original thread that created a view hierarchy can touch its views.
                * */
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        delCodeResult(codeResult);
                    }
                });
            }
        });
    }

    // 根据服务端返回信息处理结果
    private void delCodeResult(final UserRequestCodeResult result) {
                if (result.errcode == 1) {  // 按钮进入倒计时
                    btn_send_agree_code.aginAfterTime(60);
                }else {
                    Toast.makeText(UserRegisterActivity.this,result.errmsg,Toast.LENGTH_SHORT).show();
                    btn_send_agree_code.setNoraml();    // 按钮又能点(恢复正常)
                }
    }

    // is_Login:登陆标志
    // user_info:登陆后的用户数据
    private void dealLoginResult(UserLoginResult result) {
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

            // 3 执行完成，退出这个页面 关闭登录界面
            finish();
        }else {
            // 登录失败(显示“手机号码已被注册”，该信息由服务端返回)
            Toast.makeText(this,result.getErrmsg(),Toast.LENGTH_SHORT).show();
        }
    }
}
