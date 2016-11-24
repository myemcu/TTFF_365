package com.myemcu.ttff_365.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.myemcu.ttff_365.R;
import com.myemcu.ttff_365.activity.UserLoginActivity;

/**
 * Created by Administrator on 2016/11/17 0017.
 */

public class MyselfFragment extends Fragment implements View.OnClickListener {

    private View view;
    private TextView tv_user_login;   // 带图片的Textview
    private Context context;

    @Nullable
    @Override   // Fragment视图(类似Activity的setContentView())
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_myself,null);
        return view;
    }

    @Override   // Fragment逻辑
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context=getActivity();
        tv_user_login = (TextView) view.findViewById(R.id.tv_user_login);   // 之前监听不成功是因为id号没整对，娘的
        tv_user_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context,UserLoginActivity.class);  // 跳转到登陆(注意，里面传的是getActivity())
        startActivity(intent);
    }
}
