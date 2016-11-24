package com.myemcu.ttff_365.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.myemcu.ttff_365.R;

/**
 * Created by Administrator on 2016/11/24 0024.
 */
public class UserLoginActivity extends AppCompatActivity{

    private EditText et_user_phone,et_user_password;
    private CheckBox check_box_password;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        et_user_phone = (EditText) findViewById(R.id.et_user_phone);
        et_user_password = (EditText) findViewById(R.id.et_user_password);
        check_box_password = (CheckBox) findViewById(R.id.check_box_password);

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

                // 把光标移动到最后
                Editable etext = (Editable) et_user_password.getText();
                Selection.setSelection(etext, etext.length());
            }
        });

        // 2 处理点击提交数据
    }
}
