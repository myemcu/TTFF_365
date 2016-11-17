package com.myemcu.ttff_365;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.myemcu.ttff_365.adapter.HomePagerAdapter;
import com.myemcu.ttff_365.fragment.CollectionFragment;
import com.myemcu.ttff_365.fragment.HomeFragment;
import com.myemcu.ttff_365.fragment.MessageFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ViewPager view_pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 主页面全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        // 主页解决方案：ViewPager+Fragment+RadioButton(栗如：BJNews)(本例所用)
        // TabHost+Fragment(过时)
        // ViewGroup+Fragment+动态切换(Button加载Fragment)(不建议，因：不断地销毁和创建)

        // 1 完善activity_main.xml

        // 2 设置ViewPager适配器

        // 2.1 实例化ViewPager
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        // 2.2 创建用来装Fragment的集合
        ArrayList<Fragment> fragments = new ArrayList<>();
        // 2.3 向集合中添加Fragment
        fragments.add(new HomeFragment());
        fragments.add(new CollectionFragment());
        fragments.add(new MessageFragment());
        fragments.add(new MessageFragment());
        // 2.4 设置适配器(座到此处便能看到ViewPager的滑动效果)
        HomePagerAdapter adapter = new HomePagerAdapter(getSupportFragmentManager(),fragments); // Fragment管理与集合，getSupportFragmentManager()必须使用AppCompatActivity而非Activity
        view_pager.setAdapter(adapter);
    }
}
