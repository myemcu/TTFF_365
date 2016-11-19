package com.myemcu.ttff_365;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;

import com.myemcu.ttff_365.adapter.HomeViewPagerAdapter;
import com.myemcu.ttff_365.fragment.CollectionFragment;
import com.myemcu.ttff_365.fragment.HomeFragment;
import com.myemcu.ttff_365.fragment.MessageFragment;
import com.myemcu.ttff_365.fragment.MyselfFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager view_pager;
    private RadioButton rb_home,rb_collection,rb_message,rb_myself;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 主页面全屏
        /*requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

        setContentView(R.layout.activity_main);

        // 主页解决方案：ViewPager+Fragment+RadioButton(栗如：BJNews)(本例所用)
        // TabHost+Fragment(过时)
        // ViewGroup+Fragment+动态切换(Button加载Fragment)(不建议，因：不断地销毁和创建)

        // 1 完善activity_main.xml

        // 2 初始化视图
        initView();

        // 3 初始化数据(设置ViewPager适配器)
        initData();

        // 4 设置RadioButton点击事件
        rb_home.setOnClickListener(this);
        rb_collection.setOnClickListener(this);
        rb_message.setOnClickListener(this);
        rb_myself.setOnClickListener(this);

        // 5 设置ViewPage滑动监控事件
        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override   // 页面被选中时
            public void onPageSelected(int position) {
                switch (position) {
                    case 0: rb_home.setChecked(true);
                            break;

                    case 1: rb_collection.setChecked(true);
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
        });
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
            case R.id.rb_home:
                                    view_pager.setCurrentItem(0,true);  // flase可禁止滑动效果
                                    break;

            case R.id.rb_collection:
                                    view_pager.setCurrentItem(1,true);
                                    break;

            case R.id.rb_message:
                                    view_pager.setCurrentItem(2,true);
                                    break;

            case R.id.rb_myself:
                                    view_pager.setCurrentItem(3,true);
                                    break;

            default:break;
        }
    }
}
