package com.myemcu.ttff_365.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/11/17 0017.
 */

// 主页的ViewPager适配器，继承自FragmentPagerAdapter
public class HomePagerAdapter extends FragmentPagerAdapter{

    private final ArrayList<Fragment> fragments;

    // 构造器
    public HomePagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {// 新增参数：fragments(待传入的Fragment集)
        super(fm);

        this.fragments=fragments;
    }

    @Override   // fragments的位置
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override   // fragments的大小
    public int getCount() {
        return fragments.size();
    }
}
