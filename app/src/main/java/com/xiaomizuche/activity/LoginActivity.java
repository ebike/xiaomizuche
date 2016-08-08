package com.xiaomizuche.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomizuche.R;
import com.xiaomizuche.base.BaseActivity;
import com.xiaomizuche.bean.ResponseBean;
import com.xiaomizuche.bean.UserInfoBean;
import com.xiaomizuche.callback.DCommonCallback;
import com.xiaomizuche.constants.AppConfig;
import com.xiaomizuche.event.FinishActivityEvent;
import com.xiaomizuche.http.DHttpUtils;
import com.xiaomizuche.http.DRequestParamsUtils;
import com.xiaomizuche.http.HttpConstants;
import com.xiaomizuche.utils.CommonUtils;
import com.xiaomizuche.utils.SPUtils;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class LoginActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

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
    private boolean goToHome;

    @Override
    public void loadXml() {
        setContentView(R.layout.activity_login);
    }

    @Override
    public void getIntentData(Bundle savedInstanceState) {
        goToHome = getIntent().getBooleanExtra("goToHome", false);
    }

    @Override
    public void init() {
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
                Map<String, String> map = new HashMap<>();
                map.put("loginName", loginName);
                map.put("password", CommonUtils.MD5(password));
                map.put("clientId", AppConfig.imei);
                map.put("platform", "android:" + android.os.Build.VERSION.RELEASE);
                RequestParams params = DRequestParamsUtils.getRequestParams(HttpConstants.getLoginUrl(), map);
                DHttpUtils.post_String(LoginActivity.this, true, params, new DCommonCallback<String>() {
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
                            EventBus.getDefault().post(AppConfig.userInfoBean);
                            if (goToHome) {
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            }
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
                startActivity(new Intent(LoginActivity.this, FindPasswordActivity.class));
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

    public void onEvent(FinishActivityEvent event) {
        if (event.isFinish() && event.getTarget().equals(this.getClass().getSimpleName())) {
            finish();
        }
    }
}
