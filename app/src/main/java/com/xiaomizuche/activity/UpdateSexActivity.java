package com.xiaomizuche.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomizuche.R;
import com.xiaomizuche.base.BaseActivity;
import com.xiaomizuche.bean.ResponseBean;
import com.xiaomizuche.bean.UserInfoBean;
import com.xiaomizuche.callback.DCommonCallback;
import com.xiaomizuche.constants.AppConfig;
import com.xiaomizuche.http.DHttpUtils;
import com.xiaomizuche.http.DRequestParamsUtils;
import com.xiaomizuche.http.HttpConstants;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class UpdateSexActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.relayout_man)
    RelativeLayout manRelativeLayout;
    @ViewInject(R.id.iv_man_selected)
    ImageView manSelectedImageView;
    @ViewInject(R.id.relayout_woman)
    RelativeLayout womanRelativeLayout;
    @ViewInject(R.id.iv_woman_selected)
    ImageView womanSelectedImageView;

    @Override
    public void loadXml() {
        setContentView(R.layout.activity_update_sex);
    }

    @Override
    public void getIntentData(Bundle savedInstanceState) {

    }

    @Override
    public void init() {
        if (AppConfig.userInfoBean != null) {
            if (AppConfig.userInfoBean.getSex() == 0) {
                manSelectedImageView.setVisibility(View.VISIBLE);
            } else if (AppConfig.userInfoBean.getSex() == 1) {
                womanSelectedImageView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void setListener() {
        manRelativeLayout.setOnClickListener(this);
        womanRelativeLayout.setOnClickListener(this);
    }

    @Override
    public void setData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.relayout_man:
                manSelectedImageView.setVisibility(View.VISIBLE);
                womanSelectedImageView.setVisibility(View.GONE);
                update("0");
                break;
            case R.id.relayout_woman:
                manSelectedImageView.setVisibility(View.GONE);
                womanSelectedImageView.setVisibility(View.VISIBLE);
                update("1");
                break;
        }
    }

    private void update(String type) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("carId", AppConfig.userInfoBean.getCarId() + "");
        map.put("userId", AppConfig.userInfoBean.getUserId());
        map.put("sex", type);
        RequestParams params = DRequestParamsUtils.getRequestParams_Header(HttpConstants.getUpdateUserUrl(), map);
        DHttpUtils.post_String(this, true, params, new DCommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ResponseBean<UserInfoBean> bean = new Gson().fromJson(result, new TypeToken<ResponseBean<UserInfoBean>>() {
                }.getType());
                if (bean.getCode() == 1) {
                    EventBus.getDefault().post(bean.getData());
                    //更新缓存
                    AppConfig.userInfoBean = bean.getData();
                } else {
                    showShortText(bean.getErrmsg());
                }

            }
        });
        finish();
    }
}
