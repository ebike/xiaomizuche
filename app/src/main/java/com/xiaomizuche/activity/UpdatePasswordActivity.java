package com.xiaomizuche.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomizuche.R;
import com.xiaomizuche.base.BaseActivity;
import com.xiaomizuche.bean.ResponseBean;
import com.xiaomizuche.callback.DCommonCallback;
import com.xiaomizuche.http.DHttpUtils;
import com.xiaomizuche.http.DRequestParamsUtils;
import com.xiaomizuche.http.HttpConstants;
import com.xiaomizuche.utils.CommonUtils;
import com.xiaomizuche.utils.T;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

public class UpdatePasswordActivity extends BaseActivity implements TextWatcher {

    @ViewInject(R.id.iv_password)
    ImageView passwordView;
    @ViewInject(R.id.et_password)
    EditText passwordText;
    @ViewInject(R.id.v_line_password)
    View passwordLine;
    @ViewInject(R.id.iv_password1)
    ImageView passwordView1;
    @ViewInject(R.id.et_password1)
    EditText passwordText1;
    @ViewInject(R.id.v_line_password1)
    View passwordLine1;

    private String password;
    private String password1;
    private String userId;

    @Override
    public void loadXml() {
        setContentView(R.layout.activity_update_password);
    }

    @Override
    public void getIntentData(Bundle savedInstanceState) {
        userId = getIntent().getStringExtra("userId");
    }

    @Override
    public void init() {

    }

    @Override
    public void setListener() {

    }

    @Override
    public void setData() {
        passwordText.addTextChangedListener(this);
        passwordText1.addTextChangedListener(this);
    }

    @Event(value = R.id.btn_next)
    private void next(View v) {
        password = passwordText.getText().toString().trim();
        password1 = passwordText1.getText().toString().trim();
        if (CommonUtils.strIsEmpty(password)) {
            T.showShort(this, "请输入新密码");
            return;
        }
        if (CommonUtils.strIsEmpty(password1)) {
            T.showShort(this, "请再次输入新密码");
            return;
        }
        if (!password.equals(password1)) {
            T.showShort(this, "两次密码不一致");
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("userId", userId);
        map.put("password", CommonUtils.MD5(password));
        RequestParams params = DRequestParamsUtils.getRequestParams(HttpConstants.resetPassword(), map);
        DHttpUtils.post_String(UpdatePasswordActivity.this, true, params, new DCommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ResponseBean<String> responseBean = new Gson().fromJson(result, new TypeToken<ResponseBean<String>>() {
                }.getType());
                showShortText(responseBean.getErrmsg());
                if (responseBean.getCode() == 1) {
                    UpdatePasswordActivity.this.finish();
                }
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        password = passwordText.getText().toString().trim();
        password1 = passwordText1.getText().toString().trim();
        if (!CommonUtils.strIsEmpty(password)) {
            passwordView.setImageResource(R.mipmap.icon_pass_active);
            passwordLine.setBackgroundColor(getResources().getColor(R.color.main_tone));
            passwordText.setTextColor(getResources().getColor(R.color.main_tone));
        } else {
            passwordView.setImageResource(R.mipmap.icon_pass);
            passwordLine.setBackgroundColor(getResources().getColor(R.color.font_gray));
        }
        if (!CommonUtils.strIsEmpty(password1)) {
            passwordView1.setImageResource(R.mipmap.icon_pass_active);
            passwordLine1.setBackgroundColor(getResources().getColor(R.color.main_tone));
            passwordText1.setTextColor(getResources().getColor(R.color.main_tone));
        } else {
            passwordView1.setImageResource(R.mipmap.icon_pass);
            passwordLine1.setBackgroundColor(getResources().getColor(R.color.font_gray));
        }
    }
}
