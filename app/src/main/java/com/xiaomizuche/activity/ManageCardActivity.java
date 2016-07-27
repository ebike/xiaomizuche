package com.xiaomizuche.activity;

import android.os.Bundle;
import android.view.View;

import com.xiaomizuche.R;
import com.xiaomizuche.base.BaseActivity;
import com.xiaomizuche.view.PaymentDialog;

import org.xutils.view.annotation.Event;

public class ManageCardActivity extends BaseActivity {

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

    }

    @Event(value = R.id.btn_deal)
    private void deal(View v) {
        PaymentDialog paymentDialog = new PaymentDialog(this);
        paymentDialog.builder().setCancelable(true).setCanceledOnTouchOutside(true).show();
    }

}
