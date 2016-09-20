package com.xiaomizuche.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomizuche.R;
import com.xiaomizuche.base.BaseActivity;
import com.xiaomizuche.bean.ResponseBean;
import com.xiaomizuche.bean.ValidateCodeBean;
import com.xiaomizuche.callback.DCommonCallback;
import com.xiaomizuche.http.DHttpUtils;
import com.xiaomizuche.http.DRequestParamsUtils;
import com.xiaomizuche.http.HttpConstants;
import com.xiaomizuche.utils.CommonUtils;
import com.xiaomizuche.utils.T;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FindPasswordActivity extends BaseActivity implements TextWatcher {

    @ViewInject(R.id.iv_phone)
    ImageView phoneView;
    @ViewInject(R.id.et_phone)
    EditText phoneText;
    @ViewInject(R.id.v_line_phone)
    View phoneLine;
    @ViewInject(R.id.iv_validatecode)
    ImageView validatecodeView;
    @ViewInject(R.id.et_validatecode)
    EditText validateCodeText;
    @ViewInject(R.id.v_line_validatecode)
    View validatecodeLine;
    @ViewInject(R.id.tv_send_validatecode)
    TextView sendValidatecodeView;

    private Handler handler;
    private Runnable runnable;
    private int minute = 60;
    private String phone;
    private String validateCode;
    private String validatePhone;
    private ValidateCodeBean validateCodeBean;

    @Override
    public void loadXml() {
        setContentView(R.layout.activity_find_password);
    }

    @Override
    public void getIntentData(Bundle savedInstanceState) {

    }

    @Override
    public void init() {

    }

    @Override
    public void setListener() {
        phoneText.addTextChangedListener(this);
        validateCodeText.addTextChangedListener(this);
    }

    @Override
    public void setData() {

    }

    @Event(value = R.id.tv_send_validatecode)
    private void sendValidatecode(View v) {
        phone = phoneText.getText().toString().trim();
        if (CommonUtils.strIsEmpty(phone) || !CommonUtils.isPhoneNumber(phone)) {
            T.showShort(this, "手机号码格式不正确");
            return;
        }
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("phone", phone);
        RequestParams params = DRequestParamsUtils.getRequestParams(HttpConstants.sendUpdatePassCode(), paramsMap);
        DHttpUtils.post_String(FindPasswordActivity.this, true, params, new DCommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ResponseBean<ValidateCodeBean> responseBean = new Gson().fromJson(result, new TypeToken<ResponseBean<ValidateCodeBean>>() {
                }.getType());
                if (responseBean.getCode() == 1) {
                    validateCodeBean = responseBean.getData();
                    validatePhone = phone;
                    T.showShort(FindPasswordActivity.this, "验证码已发送至手机");
                    handler = new Handler();
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (minute > 0) {
                                sendValidatecodeView.setEnabled(false);
                                sendValidatecodeView.setText(minute + "s后可重发");
                                minute--;
                                handler.postDelayed(this, 1000);
                            } else {
                                sendValidatecodeView.setText("获取验证码");
                                minute = 60;
                                sendValidatecodeView.setEnabled(true);
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

    @Event(value = R.id.btn_next)
    private void next(View v) {
        phone = phoneText.getText().toString().trim();
        validateCode = validateCodeText.getText().toString().trim();
        if (CommonUtils.strIsEmpty(phone) || !CommonUtils.isPhoneNumber(phone)) {
            T.showShort(this, "手机号码格式不正确");
            return;
        }
        if (CommonUtils.strIsEmpty(validateCode)) {
            T.showShort(this, "请输入验证码");
            return;
        }
        if (validateCodeBean != null
                && new Date().getTime() < validateCodeBean.expireTime
                && phone.equals(validatePhone)
                && validateCodeBean.code.equals(validateCode)) {
            RequestParams params = new RequestParams(HttpConstants.checkUserByPhone(phone));
            DHttpUtils.get_String(FindPasswordActivity.this, true, params, new DCommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    ResponseBean<String> responseBean = new Gson().fromJson(result, new TypeToken<ResponseBean<String>>() {
                    }.getType());
                    if (responseBean.getCode() == 1) {
                        Intent intent = new Intent(FindPasswordActivity.this, UpdatePasswordActivity.class);
                        intent.putExtra("userId", responseBean.getData());
                        startActivity(intent);
                        FindPasswordActivity.this.finish();
                    } else {
                        showShortText(responseBean.getErrmsg());
                    }
                }
            });
        } else {
            T.showShort(this, "验证码不正确");
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        phone = phoneText.getText().toString().trim();
        validateCode = validateCodeText.getText().toString().trim();
        if (!CommonUtils.strIsEmpty(phone)) {
            phoneView.setImageResource(R.mipmap.icon_phone_active);
            phoneLine.setBackgroundColor(getResources().getColor(R.color.main_tone));
            phoneText.setTextColor(getResources().getColor(R.color.main_tone));
        } else {
            phoneView.setImageResource(R.mipmap.icon_phone);
            phoneLine.setBackgroundColor(getResources().getColor(R.color.font_gray));
        }
        if (!CommonUtils.strIsEmpty(validateCode)) {
            validatecodeView.setImageResource(R.mipmap.icon_code_active);
            validatecodeLine.setBackgroundColor(getResources().getColor(R.color.main_tone));
            validateCodeText.setTextColor(getResources().getColor(R.color.main_tone));
        } else {
            validatecodeView.setImageResource(R.mipmap.icon_code);
            validatecodeLine.setBackgroundColor(getResources().getColor(R.color.font_gray));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }
}
