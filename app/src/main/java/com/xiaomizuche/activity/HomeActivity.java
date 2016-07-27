package com.xiaomizuche.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaomizuche.R;
import com.xiaomizuche.adapter.ViewPagerFragmentAdapter;
import com.xiaomizuche.base.BaseActivity;
import com.xiaomizuche.bean.TabIndicator;
import com.xiaomizuche.constants.AppConfig;
import com.xiaomizuche.fragment.HireCarFragment;
import com.xiaomizuche.fragment.HomeFragment;
import com.xiaomizuche.fragment.ShopFragment;
import com.xiaomizuche.utils.ViewPagerUtils;
import com.xiaomizuche.view.NotSlideViewPager;

import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.view_pager)
    NotSlideViewPager viewPager;
    @ViewInject(R.id.ll_home)
    LinearLayout homeLayout;
    @ViewInject(R.id.iv_home)
    ImageView homeImageView;
    @ViewInject(R.id.tv_home)
    TextView homeView;
    @ViewInject(R.id.ll_hire_car)
    LinearLayout hireCarLayout;
    @ViewInject(R.id.iv_hire_car)
    ImageView hireCarImageView;
    @ViewInject(R.id.tv_hire_car)
    TextView hireCarView;
    @ViewInject(R.id.ll_shop)
    LinearLayout shopLayout;
    @ViewInject(R.id.iv_shop)
    ImageView shopImageView;
    @ViewInject(R.id.tv_shop)
    TextView shopView;
    @ViewInject(R.id.ll_my)
    LinearLayout myLayout;
    @ViewInject(R.id.iv_my)
    ImageView myImageView;
    @ViewInject(R.id.tv_my)
    TextView myView;

    private List<ImageView> imageViews;
    private List<TextView> textViews;
    private List<Fragment> fragmentList;
    private List<TabIndicator> tabIndicatorList;
    private ViewPagerFragmentAdapter viewPagerFragmentAdapter;
    private int fragmentPosition = 0;
    private long mExitTime = 0;

    @Override
    public void loadXml() {
        setContentView(R.layout.activity_home);
    }

    @Override
    public void getIntentData(Bundle savedInstanceState) {

    }

    @Override
    public void init() {
        imageViews = new ArrayList<>();
        imageViews.add(homeImageView);
        imageViews.add(hireCarImageView);
        imageViews.add(shopImageView);
        imageViews.add(myImageView);
        textViews = new ArrayList<>();
        textViews.add(homeView);
        textViews.add(hireCarView);
        textViews.add(shopView);
        textViews.add(myView);

        viewPager.setOffscreenPageLimit(4);
        tabIndicatorList = ViewPagerUtils.getTabIndicator(4);
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(new HireCarFragment());
        fragmentList.add(new ShopFragment());
        fragmentList.add(new HomeFragment());
        viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), tabIndicatorList, fragmentList);
        viewPager.setAdapter(viewPagerFragmentAdapter);
    }

    @Override
    public void setListener() {
        homeLayout.setOnClickListener(this);
        hireCarLayout.setOnClickListener(this);
        shopLayout.setOnClickListener(this);
        myLayout.setOnClickListener(this);
    }

    @Override
    public void setData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_home:
                fragmentPosition = 0;
                break;
            case R.id.ll_hire_car:
                fragmentPosition = 1;
                break;
            case R.id.ll_shop:
                fragmentPosition = 2;
                break;
            case R.id.ll_my:
                fragmentPosition = 3;
                break;
        }
        viewPager.setCurrentItem(fragmentPosition, false);
        ViewPagerUtils.setBottomBar(this, fragmentPosition, textViews, imageViews);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                mExitTime = System.currentTimeMillis();
                showLongText("再按一次退出程序");
            } else {
                AppConfig.isExecuteVF = null;
                this.finish();
            }
            return true;
        }
        return false;
    }
}
