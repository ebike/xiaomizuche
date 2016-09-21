package com.xiaomizuche.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomizuche.R;
import com.xiaomizuche.activity.HomeActivity;
import com.xiaomizuche.activity.LoginActivity;
import com.xiaomizuche.activity.ManageCardActivity;
import com.xiaomizuche.activity.MyCardActivity;
import com.xiaomizuche.base.BaseFragment;
import com.xiaomizuche.bean.ResponseBean;
import com.xiaomizuche.bean.UserInfoBean;
import com.xiaomizuche.bean.ValidateCodeBean;
import com.xiaomizuche.callback.DCommonCallback;
import com.xiaomizuche.constants.AppConfig;
import com.xiaomizuche.http.DHttpUtils;
import com.xiaomizuche.http.DMethods;
import com.xiaomizuche.http.DRequestParamsUtils;
import com.xiaomizuche.http.HttpConstants;
import com.xiaomizuche.utils.CommonUtils;
import com.xiaomizuche.utils.T;
import com.xiaomizuche.view.CustomDialog;
import com.xiaomizuche.view.TopBarView;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 首页
 */
public class HomeFragment extends BaseFragment {

    @ViewInject(R.id.top_bar_view)
    TopBarView topBarView;
    @ViewInject(R.id.web_view)
    WebView webView;
    @ViewInject(R.id.tv_manage_card)
    TextView manageCardView;

    private Handler handler;
    private Runnable runnable;
    private int minute = 60;
    private ValidateCodeBean validateCodeBean;

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

        if (AppConfig.userInfoBean != null && AppConfig.userInfoBean.getVip() == 2) {
            manageCardView.setText("我的租车卡");
        } else {
            manageCardView.setText("办理租车卡");
        }

        webView.loadUrl("http://api.xiaomiddc.com/app/h5/service_descrip.html");
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
        if (AppConfig.userInfoBean != null && AppConfig.userInfoBean.getVip() == 2) {
            startActivity(new Intent(getActivity(), MyCardActivity.class));
        } else {
            startActivity(new Intent(getActivity(), ManageCardActivity.class));
        }
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
                View backView = LayoutInflater.from(getActivity()).inflate(R.layout.view_back_car, null, false);
                final EditText validatecodeText = (EditText) backView.findViewById(R.id.et_validatecode);
                final Button validatecodeButton = (Button) backView.findViewById(R.id.btn_validatecode);
                Button submitButton = (Button) backView.findViewById(R.id.btn_submit);
                final CustomDialog dialog = CommonUtils.showCustomDialog1(getActivity(), "", backView);
                validatecodeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        Map<String, String> paramsMap = new HashMap<>();
                        paramsMap.put("phone", AppConfig.userInfoBean.getPhone());
                        paramsMap.put("id", AppConfig.userInfoBean.getCarRecord().getId());
                        RequestParams params = DRequestParamsUtils.getRequestParams(HttpConstants.sendBackCarCode(), paramsMap);
                        DHttpUtils.post_String((HomeActivity) getActivity(), true, params, new DCommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                ResponseBean<ValidateCodeBean> responseBean = new Gson().fromJson(result, new TypeToken<ResponseBean<ValidateCodeBean>>() {
                                }.getType());
                                if (responseBean.getCode() == 1) {
                                    validateCodeBean = responseBean.getData();
                                    T.showShort(getActivity(), "验证码已发送至手机");
                                    handler = new Handler();
                                    runnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            if (minute > 0) {
                                                validatecodeButton.setEnabled(false);
                                                validatecodeButton.setText(minute + "s后可重发");
                                                minute--;
                                                handler.postDelayed(this, 1000);
                                            } else {
                                                validatecodeButton.setText("获取验证码");
                                                minute = 60;
                                                validatecodeButton.setEnabled(true);
                                            }
                                        }
                                    };
                                    handler.postDelayed(runnable, 1000);
                                } else {
                                    showShortText(responseBean.getErrmsg());
                                }
                            }
                        });
                    }
                });
                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String validatecode = validatecodeText.getText().toString().trim();
                        if (CommonUtils.strIsEmpty(validatecode)) {
                            T.showShort(getActivity(), "请输入验证码");
                            return;
                        }
                        if (validateCodeBean != null
                                && new Date().getTime() < validateCodeBean.expireTime
                                && validateCodeBean.code.equals(validatecode)) {
                            dialog.cancel();
                            DMethods.backCar(getActivity(), "1", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    T.showShort(getActivity(), "还车成功");
                                }
                            });
                        } else {
                            T.showShort(getActivity(), "验证码不正确");
                        }
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
        if (AppConfig.userInfoBean != null && AppConfig.userInfoBean.getVip() == 2) {
            manageCardView.setText("我的租车卡");
        } else {
            manageCardView.setText("办理租车卡");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        EventBus.getDefault().unregister(this);
    }
}
