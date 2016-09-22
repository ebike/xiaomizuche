package com.xiaomizuche.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xiaomizuche.R;
import com.xiaomizuche.base.BaseActivity;
import com.xiaomizuche.bean.PayInfoBean;
import com.xiaomizuche.bean.PayResult;
import com.xiaomizuche.bean.ResponseBean;
import com.xiaomizuche.bean.UserInfoBean;
import com.xiaomizuche.callback.DCommonCallback;
import com.xiaomizuche.constants.AppConfig;
import com.xiaomizuche.http.DHttpUtils;
import com.xiaomizuche.http.DRequestParamsUtils;
import com.xiaomizuche.http.HttpConstants;
import com.xiaomizuche.utils.T;
import com.xiaomizuche.view.PaymentDialog;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;

import java.util.HashMap;
import java.util.Map;

public class ManageCardActivity extends BaseActivity {

    private String fee;
    private PayInfoBean payInfoBean;

    @Override
    public void loadXml() {
        setContentView(R.layout.activity_manage_card);
    }

    @Override
    public void getIntentData(Bundle savedInstanceState) {

    }

    @Override
    public void init() {

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
                    T.showShort(ManageCardActivity.this, responseBean.getErrmsg());
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
                        T.showShort(ManageCardActivity.this, "暂不支持");
                        return;
                    } else if ("unionpay".equals(payMode)) {
                        T.showShort(ManageCardActivity.this, "暂不支持");
                        return;
                    }
                    Map<String, String> map = new HashMap<>();
                    map.put("userId", AppConfig.userInfoBean.getUserId());
                    map.put("payMode", payMode);
                    RequestParams params = DRequestParamsUtils.getRequestParams_Header(HttpConstants.handleCardPay(), map);
                    DHttpUtils.post_String(ManageCardActivity.this, true, params, new DCommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            ResponseBean<PayInfoBean> bean = new Gson().fromJson(result, new TypeToken<ResponseBean<PayInfoBean>>() {
                            }.getType());
                            if (bean.getCode() == 1) {
                                payInfoBean = bean.getData();
                                if ("wxpay".equals(payInfoBean.getPayMode())) {
//                                    wxpay();
                                } else if ("alipay".equals(payInfoBean.getPayMode())) {
                                    pay(payInfoBean.getOrderInfo());
                                } else if ("unionpay".equals(payInfoBean.getPayMode())) {

                                }
                            } else {
                                showShortText(bean.getErrmsg());
                            }
                        }
                    });

                }
            });
            paymentDialog.show();
        } else {
            T.showShort(ManageCardActivity.this, "请先登录，再办理租车卡");
        }
    }

    private void pay(final String orderInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(ManageCardActivity.this);
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

    private void wxpay(String content) {
        try {
            IWXAPI api = WXAPIFactory.createWXAPI(ManageCardActivity.this,  AppConfig.WX_APP_ID);
            boolean isPaySupported = api.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
            if (isPaySupported) {
                JSONObject json = new JSONObject(content);
                if (null != json && !json.has("retcode")) {
                    PayReq req = new PayReq();
                    req.appId = json.getString("appid");
                    req.partnerId = json.getString("partnerid");
                    req.prepayId = json.getString("prepayid");
                    req.nonceStr = json.getString("noncestr");
                    req.timeStamp = json.getString("timestamp");
                    req.packageValue = json.getString("package");
                    req.sign = json.getString("sign");
                    req.extData = "app data"; // optional
                    // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
                    api.sendReq(req);
                } else {
                    T.showShort(ManageCardActivity.this, "服务器参数有误");
                }
            } else {
                T.showShort(ManageCardActivity.this, "您的微信版本不支持支付功能，请更新后再操作");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                        Map<String, String> map = new HashMap<>();
                        map.put("userId", AppConfig.userInfoBean.getUserId());
                        map.put("orderId", payInfoBean.getOrderId());
                        RequestParams params = DRequestParamsUtils.getRequestParams_Header(HttpConstants.appPayNotify(), map);
                        DHttpUtils.post_String(ManageCardActivity.this, true, params, new DCommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                ResponseBean<UserInfoBean> responseBean = new Gson().fromJson(result, new TypeToken<ResponseBean<UserInfoBean>>() {
                                }.getType());
                                if (responseBean.getCode() == 1) {
                                    //保存数据信息
                                    AppConfig.userInfoBean = responseBean.getData();
                                    startActivity(new Intent(ManageCardActivity.this, MyCardActivity.class));
                                    ManageCardActivity.this.finish();
                                } else {
                                    showShortText(responseBean.getErrmsg());
                                }
                            }
                        });
                    } else {
                        T.showShort(ManageCardActivity.this, "支付失败");
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

}
