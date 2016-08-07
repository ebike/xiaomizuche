package com.xiaomizuche.http;

import android.content.Context;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomizuche.base.BaseActivity;
import com.xiaomizuche.bean.ResponseBean;
import com.xiaomizuche.callback.DCommonCallback;
import com.xiaomizuche.callback.DSingleDialogCallback;
import com.xiaomizuche.constants.AppConfig;
import com.xiaomizuche.utils.CommonUtils;
import com.xiaomizuche.utils.T;

import org.xutils.http.RequestParams;

import java.util.HashMap;
import java.util.Map;

public class DMethods {

    public static void backCar(final Context context, String force, final View.OnClickListener callback) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", AppConfig.userInfoBean.getUserId());
        map.put("id", AppConfig.userInfoBean.getCarRecord().getId());
        map.put("force", force);
        RequestParams params = DRequestParamsUtils.getRequestParams_Header(HttpConstants.backCar(), map);
        DHttpUtils.post_String((BaseActivity) context, true, params, new DCommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ResponseBean responseBean = new Gson().fromJson(result, new TypeToken<ResponseBean>() {
                }.getType());
                if (responseBean.getCode() == 1) {
                    AppConfig.userInfoBean.setCarRecord(null);
                    callback.onClick(null);
                } else if (responseBean.getCode() == 2) {
                    CommonUtils.showCustomDialog0(context, "", "是否强制还车", new DSingleDialogCallback() {
                        @Override
                        public void onPositiveButtonClick(String editText) {
                            backCar(context, "2", callback);
                        }
                    });
                } else {
                    T.showShort(context, responseBean.getErrmsg());
                }
            }
        });
    }
}
