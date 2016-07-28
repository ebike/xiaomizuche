package com.xiaomizuche.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xiaomizuche.R;
import com.xiaomizuche.base.BaseActivity;
import com.xiaomizuche.utils.CommonUtils;
import com.xiaomizuche.utils.T;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 注册会员
 */
public class RegisterActivity extends BaseActivity {

    @ViewInject(R.id.et_phone)
    EditText phoneText;
    @ViewInject(R.id.et_validatecode)
    EditText validateCodeText;
    @ViewInject(R.id.et_password)
    EditText passwordText;
    @ViewInject(R.id.tv_send_validatecode)
    TextView sendValidatecodeView;

    private EventHandler eh;
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
        eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) { //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
                        dealHandler.sendEmptyMessageDelayed(SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE, 0);
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {//获取验证码成功
                        dealHandler.sendEmptyMessageDelayed(SMSSDK.EVENT_GET_VERIFICATION_CODE, 0);
                    }
                } else {
                    ((Throwable) data).printStackTrace();
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调

        dealHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
                        T.showShort(RegisterActivity.this, "验证码验证成功");
                        break;
                    case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                        T.showShort(RegisterActivity.this, "验证码已发送至手机");
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
                        break;
                    default:
                        break;
                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void setListener() {

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
        SMSSDK.getVerificationCode("86", phone);
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
        SMSSDK.submitVerificationCode("86", phone, validateCode);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }
}