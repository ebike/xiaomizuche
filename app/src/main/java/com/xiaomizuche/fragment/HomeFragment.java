package com.xiaomizuche.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xiaomizuche.R;
import com.xiaomizuche.activity.HomeActivity;
import com.xiaomizuche.activity.LoginActivity;
import com.xiaomizuche.activity.ManageCardActivity;
import com.xiaomizuche.base.BaseFragment;
import com.xiaomizuche.bean.UserInfoBean;
import com.xiaomizuche.constants.AppConfig;
import com.xiaomizuche.utils.CommonUtils;
import com.xiaomizuche.utils.T;
import com.xiaomizuche.view.TopBarView;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import de.greenrobot.event.EventBus;

/**
 * 首页
 */
public class HomeFragment extends BaseFragment {

    @ViewInject(R.id.top_bar_view)
    TopBarView topBarView;
    @ViewInject(R.id.web_view)
    WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        x.view().inject(this, view);
        isPrepared = true;
        EventBus.getDefault().register(this);

        if (AppConfig.userInfoBean != null) {
            topBarView.setRightTextEnabled(false);
        } else {
            topBarView.setRightCallback(new TopBarView.TopBarRightCallback() {
                @Override
                public void setRightOnClickListener() {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            });
        }

        webView.loadUrl("http://www.gnets.cn:8088/xmzc_api/app/h5/service_descrip.html");
        // 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // 返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);// 启用支持javascript
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);// 优先使用缓存
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);// 不使用缓存
        // 判断页面加载过程
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
                } else {
                    // 加载中
                }

            }
        });
        return view;
    }

    @Override
    public void requestDatas() {

    }

    @Event(value = R.id.tv_manage_card)
    private void manageCard(View view) {
        startActivity(new Intent(getActivity(), ManageCardActivity.class));
    }

    @Event(value = R.id.tv_hire_car)
    private void hireCar(View view) {
        ((HomeActivity) getActivity()).changeToHireCar();
    }

    @Event(value = R.id.tv_back_car)
    private void backCar(View view) {
        //判断是否登录状态
        if (AppConfig.userInfoBean != null) {
            if (AppConfig.userInfoBean.getCarRecord() != null) {
                CommonUtils.backCar(getActivity(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        T.showShort(getActivity(), "还车成功");
                    }
                });
            } else {
                T.showShort(getActivity(), "您没有租车");
            }
        } else {
            T.showShort(getActivity(), "请您先登录账号，再进行还车");
        }
    }

    public void onEvent(UserInfoBean bean) {
        topBarView.setRightTextEnabled(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
