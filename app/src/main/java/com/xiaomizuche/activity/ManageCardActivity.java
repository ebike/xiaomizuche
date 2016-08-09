package com.xiaomizuche.activity;

import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomizuche.R;
import com.xiaomizuche.base.BaseActivity;
import com.xiaomizuche.bean.ResponseBean;
import com.xiaomizuche.callback.DCommonCallback;
import com.xiaomizuche.http.DHttpUtils;
import com.xiaomizuche.http.HttpConstants;
import com.xiaomizuche.utils.T;
import com.xiaomizuche.view.PaymentDialog;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;

public class ManageCardActivity extends BaseActivity {

    private String fee;

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
        PaymentDialog paymentDialog = new PaymentDialog(this).builder();
        paymentDialog.setFee(fee);
        paymentDialog.setCancelable(true).setCanceledOnTouchOutside(true).show();
    }

}
