package com.xiaomizuche.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.xiaomizuche.R;
import com.xiaomizuche.base.BaseActivity;
import com.xiaomizuche.constants.AppConfig;

import org.xutils.view.annotation.ViewInject;

public class MyCardActivity extends BaseActivity {

    @ViewInject(R.id.tv_number)
    TextView numberView;
    @ViewInject(R.id.tv_due_time)
    TextView dueTimeView;

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

    }
}
