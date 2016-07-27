package com.xiaomizuche.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomizuche.R;
import com.xiaomizuche.base.BaseActivity;
import com.xiaomizuche.bean.ResponseBean;
import com.xiaomizuche.bean.UserInfoBean;
import com.xiaomizuche.callback.DCommonCallback;
import com.xiaomizuche.callback.DSingleDialogCallback;
import com.xiaomizuche.constants.AppConfig;
import com.xiaomizuche.http.DHttpUtils;
import com.xiaomizuche.http.HttpConstants;
import com.xiaomizuche.utils.CommonUtils;
import com.xiaomizuche.utils.SPUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

public class LoginActivity extends BaseActivity implements View.OnClickListener, TextWatcher {
    @ViewInject(R.id.rl_rootView)
    RelativeLayout rootViewRL;
    @ViewInject(R.id.iv_loginName)
    ImageView loginNameIV;
    @ViewInject(R.id.et_loginName)
    EditText loginNameET;
    @ViewInject(R.id.v_line_loginName)
    View loginNameLine;
    @ViewInject(R.id.iv_password)
    ImageView passwordIV;
    @ViewInject(R.id.et_password)
    EditText passwordET;
    @ViewInject(R.id.v_line_password)
    View passwordLine;
    @ViewInject(R.id.btn_login)
    Button loginButton;
    @ViewInject(R.id.tv_forget_password)
    TextView forgetPasswordTV;
    @ViewInject(R.id.tv_register)
    TextView registerTV;
    private String loginName;
    private String password;
    private boolean isUpdate;

    @Override
    public void loadXml() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_login);
    }

    @Override
    public void getIntentData(Bundle savedInstanceState) {

    }

    @Override
    public void init() {
        controlKeyboardLayout(rootViewRL, loginButton);
        if (CommonUtils.strIsEmpty(AppConfig.loginName)) {
            AppConfig.loginName = preferencesUtil.getPrefString(LoginActivity.this, AppConfig.LOGIN_NAME, "");
        }
        loginNameET.setText(AppConfig.loginName);
    }

    @Override
    public void setListener() {
        loginButton.setOnClickListener(this);
        registerTV.setOnClickListener(this);
        forgetPasswordTV.setOnClickListener(this);
        loginNameET.addTextChangedListener(this);
        passwordET.addTextChangedListener(this);
    }

    @Override
    public void setData() {

    }

    /**
     * 处理软键盘遮挡登陆按钮
     *
     * @param root         最外层布局，需要调整的布局
     * @param scrollToView 被键盘遮挡的scrollToView，滚动root,使scrollToView在root可视区域的底部
     */
    private void controlKeyboardLayout(final View root, final View scrollToView) {
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (isUpdate) {
                    isUpdate = false;
                } else {
                    Rect rect = new Rect();
                    //获取root在窗体的可视区域
                    root.getWindowVisibleDisplayFrame(rect);
                    //获取root在窗体的不可视区域高度(被其他View遮挡的区域高度)
                    int rootInvisibleHeight = root.getRootView().getHeight() - rect.bottom;
                    //若不可视区域高度大于100，则键盘显示
                    if (rootInvisibleHeight > 100) {
                        int[] location = new int[2];
                        //获取scrollToView在窗体的坐标
                        scrollToView.getLocationInWindow(location);
                        //计算root滚动高度，使scrollToView在可见区域
                        int srollHeight = (location[1] + scrollToView.getHeight()) - rect.bottom;
                        root.scrollTo(0, srollHeight);
                    } else {
                        //键盘隐藏
                        root.scrollTo(0, 0);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login://登录
                loginName = loginNameET.getText().toString().trim();
                password = passwordET.getText().toString().trim();
                if (CommonUtils.strIsEmpty(loginName)) {
                    showShortText("请输入手机号码/租车卡号");
                    return;
                } else if (CommonUtils.strIsEmpty(password)) {
                    showShortText("请输入密码");
                    return;
                }
                RequestParams params = new RequestParams(HttpConstants.getLoginUrl(loginName, CommonUtils.MD5(password)));
                DHttpUtils.post_String(LoginActivity.this, false, params, new DCommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        ResponseBean<UserInfoBean> responseBean = new Gson().fromJson(result, new TypeToken<ResponseBean<UserInfoBean>>() {
                        }.getType());
                        if (responseBean.getCode() == 1) {
                            //保存数据信息
                            AppConfig.userInfoBean = responseBean.getData();
                            SPUtils.put(LoginActivity.this, AppConfig.LOGIN_NAME, loginName);
                            SPUtils.put(LoginActivity.this, AppConfig.PASSWORD, CommonUtils.MD5(password));
                            //注册极光推送别名
                            setAlias();
                            LoginActivity.this.finish();
                        } else {
                            showShortText(responseBean.getErrmsg());
                        }
                    }
                });
                break;
            case R.id.tv_register://注册帐号
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                break;
            case R.id.tv_forget_password://忘记密码
                CommonUtils.showCustomDialog3(this, "呼叫", "取消", "", "0531-67805000", new DSingleDialogCallback() {
                    @Override
                    public void onPositiveButtonClick(String editText) {
                        // 用intent启动拨打电话
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:053167805000"));
                        try {
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        isUpdate = true;
        String loginName = loginNameET.getText().toString().trim();
        String password = passwordET.getText().toString().trim();
        if (!CommonUtils.strIsEmpty(loginName)) {
            loginNameIV.setImageResource(R.mipmap.icon_id_active);
            loginNameLine.setBackgroundColor(getResources().getColor(R.color.main_tone));
            loginNameET.setTextColor(getResources().getColor(R.color.main_tone));
        } else {
            loginNameIV.setImageResource(R.mipmap.icon_id);
            loginNameLine.setBackgroundColor(getResources().getColor(R.color.font_gray));
        }
        if (!CommonUtils.strIsEmpty(password)) {
            passwordIV.setImageResource(R.mipmap.icon_pass_active);
            passwordLine.setBackgroundColor(getResources().getColor(R.color.main_tone));
            passwordET.setTextColor(getResources().getColor(R.color.main_tone));
        } else {
            passwordIV.setImageResource(R.mipmap.icon_pass);
            passwordLine.setBackgroundColor(getResources().getColor(R.color.font_gray));
        }
    }

    @Event(value = R.id.iv_back)
    private void back(View v) {
        finish();
    }
}
