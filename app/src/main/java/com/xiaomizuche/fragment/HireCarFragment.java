package com.xiaomizuche.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomizuche.R;
import com.xiaomizuche.base.BaseActivity;
import com.xiaomizuche.base.BaseFragment;
import com.xiaomizuche.bean.ResponseBean;
import com.xiaomizuche.callback.DCommonCallback;
import com.xiaomizuche.constants.AppConfig;
import com.xiaomizuche.http.DHttpUtils;
import com.xiaomizuche.http.DRequestParamsUtils;
import com.xiaomizuche.http.HttpConstants;
import com.xiaomizuche.utils.T;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;

/**
 * 租车
 */
public class HireCarFragment extends BaseFragment {

    @ViewInject(R.id.fl_location)
    FrameLayout locationLayout;
    @ViewInject(R.id.ll_hire_car)
    LinearLayout hireCarLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hire_car, container, false);
        x.view().inject(this, view);
        isPrepared = true;

        hireCarLayout.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void requestDatas() {

    }

    @Event(value = R.id.btn_apply)
    private void apply(View view) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", AppConfig.userInfoBean.getUserId());
        RequestParams params = DRequestParamsUtils.getRequestParams_Header(HttpConstants.hireCar(), map);
        DHttpUtils.post_String((BaseActivity) getActivity(), true, params, new DCommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ResponseBean responseBean = new Gson().fromJson(result, new TypeToken<ResponseBean>() {
                }.getType());
                if (responseBean.getCode() == 1) {

                } else {
                    T.showShort(getActivity(), responseBean.getErrmsg());
                }
            }
        });
    }
}
