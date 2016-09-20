package com.xiaomizuche.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomizuche.R;
import com.xiaomizuche.base.BaseActivity;
import com.xiaomizuche.bean.PayInfoBean;
import com.xiaomizuche.bean.PayResult;
import com.xiaomizuche.bean.ResponseBean;
import com.xiaomizuche.callback.DCommonCallback;
import com.xiaomizuche.constants.AppConfig;
import com.xiaomizuche.http.DHttpUtils;
import com.xiaomizuche.http.DRequestParamsUtils;
import com.xiaomizuche.http.HttpConstants;
import com.xiaomizuche.utils.T;
import com.xiaomizuche.view.PaymentDialog;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

public class MyCardActivity extends BaseActivity {

    @ViewInject(R.id.tv_number)
    TextView numberView;
    @ViewInject(R.id.tv_due_time)
    TextView dueTimeView;

    private String fee;

    @Override
    public void loadXml() {
        setContentView(R.layout.activity_my_card);
    }

    @Override
    public void getIntentData(Bundle savedInstanceState) {

    }

    @Override
    public void init() {
        if (AppConfig.userInfoBean != null) {
            numberView.setText("NO." + AppConfig.userInfoBean.getUserId());
            dueTimeView.setText("到期时间：" + AppConfig.userInfoBean.getExpireTime());
        }
    }

    @Override
    public void setListener() {

    }

    @Override
    public void setData() {
        RequestParams params = new RequestParams(HttpConstants.getVipYearPrice());
        DHttpUtils.get_String(this, true, params, new DCommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ResponseBean<String> responseBean = new Gson().fromJson(result, new TypeToken<ResponseBean<String>>() {
                }.getType());
                if (responseBean.getCode() == 1) {
                    fee = responseBean.getData();
                } else {
                    T.showShort(MyCardActivity.this, responseBean.getErrmsg());
                }
            }
        });
    }

    @Event(value = R.id.btn_deal)
    private void deal(View v) {
        if (AppConfig.userInfoBean != null) {
            PaymentDialog paymentDialog = new PaymentDialog(this).builder();
            paymentDialog.setFee(fee);
            paymentDialog.setCancelable(true);
            paymentDialog.setCanceledOnTouchOutside(true);
            paymentDialog.setPayCallBack(new PaymentDialog.PayCallBack() {
                @Override
                public void onPay(String payMode) {
                    if ("wxpay".equals(payMode)) {
                        T.showShort(MyCardActivity.this, "暂不支持");
                        return;
                    } else if ("unionpay".equals(payMode)) {
                        T.showShort(MyCardActivity.this, "暂不支持");
                        return;
                    }
                    Map<String, String> map = new HashMap<>();
                    map.put("userId", AppConfig.userInfoBean.getUserId());
                    map.put("payMode", payMode);
                    RequestParams params = DRequestParamsUtils.getRequestParams_Header(HttpConstants.renewCardPay(), map);
                    DHttpUtils.post_String(MyCardActivity.this, true, params, new DCommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            ResponseBean<PayInfoBean> bean = new Gson().fromJson(result, new TypeToken<ResponseBean<PayInfoBean>>() {
                            }.getType());
                            if (bean.getCode() == 1) {
                                PayInfoBean payInfoBean = bean.getData();
                                pay(payInfoBean.getOrderInfo());
                            } else {
                                showShortText(bean.getErrmsg());
                            }
                        }
                    });

                }
            });
            paymentDialog.show();
        } else {
            T.showShort(MyCardActivity.this, "请先登录，再办理租车卡");
        }
    }

    private void pay(final String orderInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(MyCardActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);

                Message msg = new Message();
                msg.what = 1;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case AppConfig.SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {

                    } else {
                        T.showShort(MyCardActivity.this, "支付失败");
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };
}
