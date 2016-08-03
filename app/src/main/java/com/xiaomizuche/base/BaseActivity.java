package com.xiaomizuche.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomizuche.activity.LoginActivity;
import com.xiaomizuche.bean.ResponseBean;
import com.xiaomizuche.callback.DCommonCallback;
import com.xiaomizuche.callback.DSingleDialogCallback;
import com.xiaomizuche.constants.AppConfig;
import com.xiaomizuche.event.FinishActivityEvent;
import com.xiaomizuche.event.OnlineExceptionEvent;
import com.xiaomizuche.http.DHttpUtils;
import com.xiaomizuche.http.HttpConstants;
import com.xiaomizuche.utils.CommonUtils;
import com.xiaomizuche.utils.DensityUtil;
import com.xiaomizuche.utils.PreferencesUtil;
import com.xiaomizuche.utils.SPUtils;
import com.xiaomizuche.utils.T;
import com.xiaomizuche.view.LoadingDialog;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import de.greenrobot.event.EventBus;


/**
 * 所有activity的父类
 */
public abstract class BaseActivity extends FragmentActivity {

    private static final int MSG_SET_ALIAS = 1001;
    private Handler mHandler = new Handler();
    private LoadingDialog mLoadingBar;
    protected PreferencesUtil preferencesUtil;
    protected DensityUtil densityUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesUtil = PreferencesUtil.getInstance();
        densityUtil = new DensityUtil(BaseActivity.this);
        EventBus.getDefault().register(this);
        loadXml();
        x.view().inject(this);
        getIntentData(savedInstanceState);
        init();
        setListener();
        setData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (AppConfig.isDisabled) {
            switch (AppConfig.eventType) {
                case 10:
                    CommonUtils.showCustomDialogSignle2(this, "", AppConfig.eventMsg, new DSingleDialogCallback() {
                        @Override
                        public void onPositiveButtonClick(String editText) {
                            logout();
                            AppConfig.isDisabled = false;
                            AppConfig.eventType = 0;
                            AppConfig.eventMsg = "";
                        }
                    });
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 设置xml文件
     */
    public abstract void loadXml();

    /**
     * 获取intent数据
     *
     * @param savedInstanceState
     */
    public abstract void getIntentData(Bundle savedInstanceState);

    /**
     * view 初始化
     */
    public abstract void init();

    /**
     * 设置view监听器
     */
    public abstract void setListener();

    /**
     * 数据设置
     */
    public abstract void setData();

    /**
     * 长文本提示
     */
    public void showLongText(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    /**
     * 短文本提示
     */
    public void showShortText(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    /***
     * 请求网络的时候开启loadding对话框
     */
    public void startLoadingProgress() {
        if (BaseActivity.this != null) {
            if (BaseActivity.this instanceof Activity) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup rootView = (ViewGroup) (BaseActivity.this).getWindow().getDecorView().findViewById(android.R.id.content);
                        if (rootView.getChildAt(rootView.getChildCount() - 1) instanceof LoadingDialog) {
                            return;
                        }
                        mLoadingBar = new LoadingDialog(BaseActivity.this);
                        mLoadingBar.getBackground().setAlpha(0);
                        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
                        rootView.addView(mLoadingBar, params);
                        mLoadingBar.hideSoftInput();

                    }
                });
            }
        }
    }

    /***
     * 请求网络完成时候关闭loadding对话框
     */
    public void dismissLoadingprogress() {
        if (BaseActivity.this != null && null != mLoadingBar) {
            if (BaseActivity.this instanceof Activity) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ViewGroup rootView = (ViewGroup) (BaseActivity.this).getWindow().getDecorView().findViewById(android.R.id.content);
                        mLoadingBar.dismiss();
                        rootView.removeView(mLoadingBar);
                    }

                });
            }
        }
    }

    //设置别名
    protected void setAlias() {
        // 调用 Handler 来异步设置别名
        aliasHandler.sendMessage(aliasHandler.obtainMessage(MSG_SET_ALIAS, AppConfig.imei));
    }

    //设置别名回调方法
    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            switch (code) {
                case 0://成功
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002://超时
                    // 延迟 60 秒来调用 Handler 设置别名
                    aliasHandler.sendMessageDelayed(aliasHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    break;
            }
        }
    };

    //处理设置别名消息
    private final Handler aliasHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAliasAndTags(getApplicationContext(),
                            (String) msg.obj,
                            null,
                            mAliasCallback);
                    break;
            }
        }
    };

    public void onEvent(OnlineExceptionEvent onlineExceptionEvent) {
        if (onlineExceptionEvent.isFlag()) {
            CommonUtils.showCustomDialogSignle(this, "", onlineExceptionEvent.getMessage(), Gravity.LEFT | Gravity.CENTER_VERTICAL, new DSingleDialogCallback() {
                @Override
                public void onPositiveButtonClick(String editText) {
                    logout();
                }
            });
        }
    }

    public void onEvent(FinishActivityEvent event) {
        if (event != null && event.isFinish() && event.getTarget() != null && event.getTarget().equals("BaseActivity")) {
            this.finish();
        }
    }

    public void logout() {
        if (AppConfig.userInfoBean != null) {
            RequestParams params = new RequestParams(HttpConstants.getLogoutUrl());
            DHttpUtils.get_String(this, true, params, new DCommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    ResponseBean bean = new Gson().fromJson(result, new TypeToken<ResponseBean>() {
                    }.getType());
                    if (bean.getCode() == 1) {
                        AppConfig.loginName = "";
                        AppConfig.password = "";
                        AppConfig.userInfoBean = null;
                        AppConfig.isExecuteVF = null;
                        SPUtils.clear(BaseActivity.this);
                        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                        startActivity(intent);
                        EventBus.getDefault().post(new FinishActivityEvent(true, "BaseActivity"));
                    } else {
                        T.showShort(BaseActivity.this, bean.getErrmsg());
                    }
                }
            });
        }
    }

}
