package com.xiaomizuche.activity;

import android.content.Intent;
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
import com.xiaomizuche.bean.AlarmMessageBean;
import com.xiaomizuche.bean.TabIndicator;
import com.xiaomizuche.constants.AppConfig;
import com.xiaomizuche.db.XUtil;
import com.xiaomizuche.event.ChangeLocationEvent;
import com.xiaomizuche.fragment.AlarmMessageFragment;
import com.xiaomizuche.fragment.ChartFragment;
import com.xiaomizuche.fragment.LocationFragment;
import com.xiaomizuche.fragment.NavigationFragment;
import com.xiaomizuche.utils.ViewPagerUtils;
import com.xiaomizuche.view.NotSlideViewPager;
import com.readystatesoftware.viewbadger.BadgeView;
import com.slidingmenu.lib.SlidingMenu;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.view_pager)
    NotSlideViewPager viewPager;
    @ViewInject(R.id.ll_location)
    LinearLayout locationLinearLayout;
    @ViewInject(R.id.iv_location)
    ImageView locationImageView;
    @ViewInject(R.id.tv_location)
    TextView locationTextView;
    @ViewInject(R.id.ll_alarm)
    LinearLayout alarmLinearLayout;
    @ViewInject(R.id.iv_alarm)
    ImageView alarmImageView;
    @ViewInject(R.id.tv_alarm)
    TextView alarmTextView;
    @ViewInject(R.id.ll_chart)
    LinearLayout chartLinearLayout;
    @ViewInject(R.id.iv_chart)
    ImageView chartImageView;
    @ViewInject(R.id.tv_chart)
    TextView chartTextView;
    private List<ImageView> imageViews;
    private List<TextView> textViews;
    private List<Fragment> fragmentList;
    private List<TabIndicator> tabIndicatorList;
    private ViewPagerFragmentAdapter viewPagerFragmentAdapter;
    private long mExitTime = 0;
    private int fragmentPosition = 0;
    private SlidingMenu menu;//左滑菜单

    @Override
    public void loadXml() {
        setContentView(R.layout.activity_main);
        //初始化左边栏
        initSlidingMenu();
    }

    private void initSlidingMenu() {
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
//        menu.setBehindOffset(DensityUtil.screenWidth() / 4);
        menu.setFadeDegree(0.35f);
        menu.setFadeEnabled(true);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.menu_frame);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.menu_frame, new NavigationFragment())
                .commit();
    }

    @Override
    public void getIntentData(Bundle savedInstanceState) {
        fragmentPosition = getIntent().getIntExtra("fragmentPosition", 0);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        fragmentPosition = intent.getIntExtra("fragmentPosition", 0);
        //设置初始显示界面
        viewPager.setCurrentItem(fragmentPosition, false);
        ViewPagerUtils.setBottomBar(MainActivity.this, fragmentPosition, textViews, imageViews);
    }

    @Override
    public void init() {
        //初始化缓存
        AppConfig.loginName = preferencesUtil.getPrefString(MainActivity.this, AppConfig.LOGIN_NAME, "");
        AppConfig.password = preferencesUtil.getPrefString(MainActivity.this, AppConfig.PASSWORD, "");
        //初始化viewpager
        imageViews = new ArrayList<ImageView>();
        imageViews.add(locationImageView);
        imageViews.add(chartImageView);
        imageViews.add(alarmImageView);
        textViews = new ArrayList<TextView>();
        textViews.add(locationTextView);
        textViews.add(chartTextView);
        textViews.add(alarmTextView);
        //初始化ViewPager
        viewPager.setOffscreenPageLimit(3);
        // 包含3个fragment界面
        tabIndicatorList = ViewPagerUtils.getTabIndicator(3);
        // 3个fragment界面封装
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(new LocationFragment());
        fragmentList.add(new ChartFragment(fragmentPosition));
        fragmentList.add(new AlarmMessageFragment(fragmentPosition));
        // 设置ViewPager适配器
        viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(getSupportFragmentManager(), tabIndicatorList, fragmentList);
        viewPager.setAdapter(viewPagerFragmentAdapter);
        //设置初始显示界面
        viewPager.setCurrentItem(fragmentPosition, false);
        ViewPagerUtils.setBottomBar(MainActivity.this, fragmentPosition, textViews, imageViews);
    }

    @Override
    public void setListener() {
        locationLinearLayout.setOnClickListener(this);
        alarmLinearLayout.setOnClickListener(this);
        chartLinearLayout.setOnClickListener(this);
    }

    @Override
    public void setData() {
        try {
            //气泡中显示的数字是本地数据库中未读的消息数量
            if (AppConfig.userInfoBean != null) {
                long count = XUtil.db.selector(AlarmMessageBean.class).where("carId", "=", AppConfig.userInfoBean.getCarId()).and("status", "=", "1").count();
                AppConfig.badge = new BadgeView(this, alarmLinearLayout);
                if (count > 0) {
                    //显示气泡
                    AppConfig.badge.setText(count + "");
                    AppConfig.badge.setBadgeMargin(0);
                    AppConfig.badge.show();
                }
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_location:
                if (fragmentPosition == 0) {//当前处于定位界面，且点击定位
                    //通知卫星定位界面切换到定位模式
                    EventBus.getDefault().post(new ChangeLocationEvent(true));
                }
                fragmentPosition = 0;
                viewPager.setCurrentItem(fragmentPosition, false);
                ViewPagerUtils.setBottomBar(MainActivity.this, fragmentPosition, textViews, imageViews);
                break;
            case R.id.ll_chart:
                fragmentPosition = 1;
                viewPager.setCurrentItem(fragmentPosition, false);
                ViewPagerUtils.setBottomBar(MainActivity.this, fragmentPosition, textViews, imageViews);
                break;
            case R.id.ll_alarm:
                fragmentPosition = 2;
                viewPager.setCurrentItem(fragmentPosition, false);
                ViewPagerUtils.setBottomBar(MainActivity.this, fragmentPosition, textViews, imageViews);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (menu.isMenuShowing()) {
            menu.showContent();
        } else {
            super.onBackPressed();
        }
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

    //伸缩左侧菜单
    public void setMenuToggle() {
        if (menu != null) {
            menu.toggle();
        }
    }
}
