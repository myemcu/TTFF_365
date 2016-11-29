package com.myemcu.ttff_365.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.myemcu.ttff_365.R;
import com.myemcu.ttff_365.adapter.HomeViewPagerAdapter;
import com.myemcu.ttff_365.fragment.CollectionFragment;
import com.myemcu.ttff_365.fragment.HomeFragment;
import com.myemcu.ttff_365.fragment.MessageFragment;
import com.myemcu.ttff_365.fragment.MyselfFragment;
import com.myemcu.ttff_365.utils.ActivityManagerUtil;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ViewPager view_pager;
    private RadioButton rb_home,rb_collection,rb_message,rb_myself;

    private boolean is_Login  = false;
    private int clickPosition;      // 记录RadioButton点击位置

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 把MainActivity添加到Activity管理器中进行统一管理
        ActivityManagerUtil.getInstance().addActivity(this);

        setContentView(R.layout.activity_main);

        // 主页面全屏
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        initView();

        initData(); // 设置ViewPager适配器

        // 4 设置RadioButton点击事件
        rb_home.setOnClickListener(this);
        rb_collection.setOnClickListener(this);
        rb_message.setOnClickListener(this);
        rb_myself.setOnClickListener(this);

        view_pager.addOnPageChangeListener(this);
    }

    private void initView() {
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        rb_home = (RadioButton) findViewById(R.id.rb_home);
        rb_collection = (RadioButton) findViewById(R.id.rb_collection);
        rb_message = (RadioButton) findViewById(R.id.rb_message);
        rb_myself = (RadioButton) findViewById(R.id.rb_myself);
    }

    private void initData() {
        // 1 创建用来装Fragment的集合(ViewPager适配器装的是Fragment集合)
        ArrayList<Fragment> fragments = new ArrayList<>();
        // 2 向集合中添加Fragment
        fragments.add(new HomeFragment());
        fragments.add(new CollectionFragment());
        fragments.add(new MessageFragment());
        fragments.add(new MyselfFragment());
        // 3 设置适配器(做到此处已能看到ViewPager的滑动效果)
        HomeViewPagerAdapter adapter = new HomeViewPagerAdapter(getSupportFragmentManager(),fragments); // Fragment管理与集合，getSupportFragmentManager()必须使用AppCompatActivity而非Activity
        view_pager.setAdapter(adapter);
    }

    @Override   // 按钮选择指定Fragment页
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_home:      clickPosition=0;
                                    view_pager.setCurrentItem(0,true);  // false可禁止滑动效果
                                    break;

            case R.id.rb_collection:// 判断是否已登陆
                                    clickPosition=0;// 故意设置点击位置为首页，避免在收藏页中直接点返回而看到收藏页的内容(确保该页只能是在登陆成功后才能看)
                                    SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
                                    is_Login = sp.getBoolean("is_Login", false);    // 默认false

                                    if (is_Login) {
                                        view_pager.setCurrentItem(1,true);
                                    }else {
                                        Intent intent = new Intent(this,UserLoginActivity.class);
                                        startActivity(intent);
                                        // 找到原来的位置并恢复
                                        onPageSelected(view_pager.getCurrentItem());
                                    }
                                    break;

            case R.id.rb_message:   clickPosition=2;
                                    view_pager.setCurrentItem(2,true);
                                    break;

            case R.id.rb_myself:    clickPosition=3;
                                    view_pager.setCurrentItem(3,true);
                                    break;

            default:break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0: rb_home.setChecked(true);
                break;

            case 1: rb_collection.setChecked(true); // 收藏RadioButton设为选中

                    SharedPreferences sp = getSharedPreferences("info", MODE_PRIVATE);
                    is_Login = sp.getBoolean("is_Login", false);   // 读取登陆标志

                    if (is_Login) {
                        view_pager.setCurrentItem(1);
                    }else {
                        Intent intent = new Intent(MainActivity.this,UserLoginActivity.class);
                        startActivity(intent);
                        view_pager.setCurrentItem(clickPosition);	// 直接退出登录界面后的处理
                }

                break;

            case 2: rb_message.setChecked(true);
                break;

            case 3: rb_myself.setChecked(true);
                break;

        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
