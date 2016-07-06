package com.xiaomizuche.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.xiaomizuche.R;
import com.xiaomizuche.base.BaseActivity;
import com.xiaomizuche.utils.CommonUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 关于
 */
public class AboutActivity extends BaseActivity {
    @ViewInject(R.id.tv_logo)
    TextView logoTextView;

    @Override
    public void loadXml() {
        setContentView(R.layout.activity_about);
        x.view().inject(this);
    }

    @Override
    public void getIntentData(Bundle savedInstanceState) {

    }

    @Override
    public void init() {
        String versionName = CommonUtils.getVersionName(this);
        logoTextView.setText("For Android V" + versionName);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void setData() {

    }
}
