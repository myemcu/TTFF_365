package com.myemcu.ttff_365.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.myemcu.ttff_365.R;
import com.myemcu.ttff_365.activity.MainActivity;
import com.myemcu.ttff_365.activity.UserLoginActivity;
import com.myemcu.ttff_365.javabean.UserLoginResult;

/**
 * Created by Administrator on 2016/11/17 0017.
 */

public class MyselfFragment extends Fragment implements View.OnClickListener {

    private View view;
    private TextView tv_user_login,tv_user_exit,tv_user_name,tv_user_location;   // 带图片的Textview
    private LinearLayout ll_user_login;
    private Context context;

    private ImageView iv_user_header;

    private boolean isVisable = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        //判断Fragment中的ListView时候存在，判断该Fragment时候已经正在前台显示  通过这两个判断，就可以知道什么时候去加载数据了
        if (getUserVisibleHint() && isVisible()) {
            isVisable = true;
        }else{
            isVisable = false;
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Nullable
    @Override   // Fragment视图(类似Activity的setContentView())
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_myself,null);
        return view;
    }

    @Override   // Fragment逻辑
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        if (isVisable) {
            // 当用户切换到本Fragment时，再加载数据
            Toast.makeText(context,"MyselfFragment",Toast.LENGTH_SHORT).show();
        }
        super.onActivityCreated(savedInstanceState);

        context=getActivity();  // 取Fragment的上下文

        findViews();            // 找控件

        setOnClickListener();   // 控件事件监听
    }

    private void setOnClickListener() {
        tv_user_login.setOnClickListener(this);     // 监听登陆TextView
        tv_user_exit.setOnClickListener(this);      // 监听退出TextView
    }

    private void findViews() {
        tv_user_login = (TextView) view.findViewById(R.id.tv_user_login);  // 之前监听不成功是因为id号没整对，娘的
        tv_user_exit = (TextView) view.findViewById(R.id.tv_user_exit);
        ll_user_login = (LinearLayout) view.findViewById(R.id.ll_user_login);
        iv_user_header = (ImageView) view.findViewById(R.id.iv_user_header);
        tv_user_name = (TextView) view.findViewById(R.id.tv_user_name);
        tv_user_location = (TextView) view.findViewById(R.id.tv_user_location);
    }

    @Override   // 从另外的页面(UserLoginActivity)登陆后，回到本页面回调onResume()方法
    public void onResume() {
        super.onResume();

        // 从SharedPreferences中读取登录状态——(判断用户是否登陆，如果登陆则显示登陆头，否则显示未登陆头)
        SharedPreferences sp = context.getSharedPreferences("info", Context.MODE_PRIVATE);
        boolean is_Login = sp.getBoolean("is_Login", false);

        // 判断登录状态
        if (is_Login) {
            ll_user_login.setVisibility(View.VISIBLE);  // 线性布局登陆头(显示)
            tv_user_login.setVisibility(View.GONE);     // 带图片的TextView登陆头(隐藏)

            /*设置用户信息*/

            // 读取用户信息
            String userInfostr = context.getSharedPreferences("info",Context.MODE_PRIVATE).getString("user_info",null); // 此时，读出来的是json字符串
            // 判空
            if (!TextUtils.isEmpty(userInfostr)) {

                Gson gson = new Gson(); // 使用Gson工具
                UserLoginResult.DataBean userInfo = gson.fromJson(userInfostr, UserLoginResult.DataBean.class); // 把读出的json串转为对象(左为json串，右为数据Bean)
                // 设置图片
                Glide.with(context).load(userInfo.getMember_info().getMember_avatar()).into(iv_user_header);    // 这是哥圆角图片
                // 设置文本
                tv_user_name.setText(userInfo.getMember_info().getMember_name());
                tv_user_location.setText(userInfo.getMember_info().getMember_location_text());
            }
        }else {
            tv_user_login.setVisibility(View.VISIBLE);
            ll_user_login.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.tv_user_login:
                                        Intent intent = new Intent(context,UserLoginActivity.class);  // 跳转到登陆(注意，里面传的是getActivity())
                                        startActivity(intent);
                                        break;

            case R.id.tv_user_exit:  // 登出后，状态设为false
                                        SharedPreferences sp = context.getSharedPreferences("info", Context.MODE_PRIVATE);
                                        sp.edit().putBoolean("is_Login",false).apply();
                                        Toast.makeText(context,"已退出登录",Toast.LENGTH_SHORT).show();
                                        break;
        }
    }
}
