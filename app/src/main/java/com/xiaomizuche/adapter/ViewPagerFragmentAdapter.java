package com.xiaomizuche.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.xiaomizuche.bean.TabIndicator;

import java.util.List;

public class ViewPagerFragmentAdapter extends FragmentPagerAdapter {

    // ViewPager包含Fragment个数的集合
    private List<TabIndicator> list;
    // ViewPager中Fragment集合
    private List<Fragment> fragmentList;

    public ViewPagerFragmentAdapter(FragmentManager fm, List<TabIndicator> list, List<Fragment> fragmentList) {
        super(fm);
        this.list = list;
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = fragmentList.get(list.get(position).type);
        return fragment;
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
