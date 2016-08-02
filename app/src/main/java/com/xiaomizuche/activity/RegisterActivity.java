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
import com.xiaomizuche.bean.UserInfoBean;
import com.xiaomizuche.callback.DCommonCallback;
import com.xiaomizuche.constants.AppConfig;
import com.xiaomizuche.event.FinishActivityEvent;
import com.xiaomizuche.http.DHttpUtils;
import com.xiaomizuche.http.DRequestParamsUtils;
import com.xiaomizuche.http.HttpConstants;
import com.xiaomizuche.utils.CommonUtils;
import com.xiaomizuche.utils.SPUtils;
import com.xiaomizuche.utils.T;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

/**
 * 注册会员
 */
public class RegisterActivity extends BaseActivity implements TextWatcher {

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
    @ViewInject(R.id.iv_password)
    ImageView passwordView;
    @ViewInject(R.id.et_password)
    EditText passwordText;
    @ViewInject(R.id.v_line_password)
    View passwordLine;
    @ViewInject(R.id.tv_send_validatecode)
    TextView sendValidatecodeView;

    private Handler dealHandler;
    private Handler handler;
    private Runnable runnable;
    private int minute = 60;
    private String phone;
    private String password;
    private String validateCode;

    @Override
    public void loadXml() {
        setContentView(R.layout.activity_register);
    }

    @Override
    public void getIntentData(Bundle savedInstanceState) {

    }

    @Override
    public void init() {
//        eh = new EventHandler() {
//            @Override
//            public void afterEvent(int event, int result, Object data) {
//                if (result == SMSSDK.RESULT_COMPLETE) { //回调完成
//                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
//                        dealHandler.sendEmptyMessageDelayed(SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE, 0);
//                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {//获取验证码成功
//                        dealHandler.sendEmptyMessageDelayed(SMSSDK.EVENT_GET_VERIFICATION_CODE, 0);
//                    }
//                } else {
//                    ((Throwable) data).printStackTrace();
//                    Message message = new Message();
//                    message.what = SMSSDK.RESULT_ERROR;
//                    message.obj = ((Throwable) data).getMessage();
//                    dealHandler.sendMessageDelayed(message, 0);
//                }
//            }
//        };
//        SMSSDK.registerEventHandler(eh); //注册短信回调
//
//        dealHandler = new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                switch (msg.what) {
//                    case SMSSDK.RESULT_ERROR:
//                        try {
//                            SMSResult result = new Gson().fromJson(msg.obj + "", SMSResult.class);
//                            if (result.getStatus() == 467 || result.getStatus() == 468) {
//                                T.showShort(RegisterActivity.this, "验证码不正确");
//                            } else {
//                                T.showShort(RegisterActivity.this, result.getDetail());
//                            }
//                        } catch (IllegalStateException ex) {
//                        }
//                        break;
//                    case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:

//                        break;
//                    case SMSSDK.EVENT_GET_VERIFICATION_CODE:
//                        T.showShort(RegisterActivity.this, "验证码已发送至手机");
//                        handler = new Handler();
//                        runnable = new Runnable() {
//                            @Override
//                            public void run() {
//                                if (minute > 0) {
//                                    sendValidatecodeView.setEnabled(false);
//                                    sendValidatecodeView.setText(minute + "s后可重发");
//                                    minute--;
//                                    handler.postDelayed(this, 1000);
//                                } else {
//                                    sendValidatecodeView.setText("获取验证码");
//                                    minute = 60;
//                                    sendValidatecodeView.setEnabled(true);
//                                }
//                            }
//                        };
//
//                        handler.postDelayed(runnable, 1000);
//                        break;
//                    default:
//                        break;
//                }
//                super.handleMessage(msg);
//            }
//        };
    }

    @Override
    public void setListener() {
        phoneText.addTextChangedListener(this);
        validateCodeText.addTextChangedListener(this);
        passwordText.addTextChangedListener(this);
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
//        SMSSDK.getVerificationCode("86", phone);
    }

    @Event(value = R.id.btn_next)
    private void next(View v) {
        phone = phoneText.getText().toString().trim();
        validateCode = validateCodeText.getText().toString().trim();
        password = passwordText.getText().toString().trim();
        if (CommonUtils.strIsEmpty(phone) || !CommonUtils.isPhoneNumber(phone)) {
            T.showShort(this, "手机号码格式不正确");
            return;
        }
        if (CommonUtils.strIsEmpty(validateCode)) {
            T.showShort(this, "请输入验证码");
            return;
        }
        if (CommonUtils.strIsEmpty(password) || password.length() < 6 || password.length() > 16) {
            T.showShort(this, "密码为6-16位字母或数字");
            return;
        }
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("phone", phone);
        paramsMap.put("password", CommonUtils.MD5(password));
        paramsMap.put("clientId", AppConfig.imei);
        paramsMap.put("platform", "android:" + android.os.Build.VERSION.RELEASE);
        RequestParams params = DRequestParamsUtils.getRequestParams(HttpConstants.getRegUser(), paramsMap);
        DHttpUtils.post_String(RegisterActivity.this, true, params, new DCommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ResponseBean<UserInfoBean> responseBean = new Gson().fromJson(result, new TypeToken<ResponseBean<UserInfoBean>>() {
                }.getType());
                if (responseBean.getCode() == 1) {
                    //保存数据信息
                    AppConfig.userInfoBean = responseBean.getData();
                    SPUtils.put(RegisterActivity.this, AppConfig.LOGIN_NAME, phone);
                    SPUtils.put(RegisterActivity.this, AppConfig.PASSWORD, CommonUtils.MD5(password));
                    //注册极光推送别名
                    setAlias();
                    EventBus.getDefault().post(new FinishActivityEvent(true, LoginActivity.class.getSimpleName()));
                    EventBus.getDefault().post(AppConfig.userInfoBean);
                    startActivity(new Intent(RegisterActivity.this, AddUserInfoActivity.class));
                    RegisterActivity.this.finish();
                } else {
                    showShortText(responseBean.getErrmsg());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacks(runnable);
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
        phone = phoneText.getText().toString().trim();
        validateCode = validateCodeText.getText().toString().trim();
        password = passwordText.getText().toString().trim();
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
        if (!CommonUtils.strIsEmpty(password)) {
            passwordView.setImageResource(R.mipmap.icon_pass_active);
            passwordLine.setBackgroundColor(getResources().getColor(R.color.main_tone));
            passwordText.setTextColor(getResources().getColor(R.color.main_tone));
        } else {
            passwordView.setImageResource(R.mipmap.icon_pass);
            passwordLine.setBackgroundColor(getResources().getColor(R.color.font_gray));
        }
    }
}