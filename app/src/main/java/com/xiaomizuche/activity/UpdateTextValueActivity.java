package com.xiaomizuche.activity;

import android.os.Bundle;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomizuche.R;
import com.xiaomizuche.base.BaseActivity;
import com.xiaomizuche.bean.CarInfoBean;
import com.xiaomizuche.bean.ResponseBean;
import com.xiaomizuche.bean.UserInfoBean;
import com.xiaomizuche.callback.DCommonCallback;
import com.xiaomizuche.constants.AppConfig;
import com.xiaomizuche.http.DHttpUtils;
import com.xiaomizuche.http.DRequestParamsUtils;
import com.xiaomizuche.http.HttpConstants;
import com.xiaomizuche.view.TopBarView;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;

public class UpdateTextValueActivity extends BaseActivity {
    @ViewInject(R.id.top_bar_view)
    TopBarView topBarView;
    @ViewInject(R.id.et_field)
    EditText fieldEditText;
    private int type;
    private String fieldName;
    private String fieldValue;
    private String fieldName_CH;

    @Override
    public void loadXml() {
        setContentView(R.layout.activity_update_text_value);
    }

    @Override
    public void getIntentData(Bundle savedInstanceState) {
        type = getIntent().getIntExtra("type", -1);
        fieldName_CH = getIntent().getStringExtra("fieldName_CH");
        fieldValue = getIntent().getStringExtra("fieldValue");
        fieldName = getIntent().getStringExtra("fieldName");
    }

    @Override
    public void init() {
        topBarView.setCenterTextView(fieldName_CH);
        fieldEditText.setText(fieldValue);
        fieldEditText.requestFocus();
    }

    @Override
    public void setListener() {
        topBarView.setRightCallback(new TopBarView.TopBarRightCallback() {
            @Override
            public void setRightOnClickListener() {
                String newValue = fieldEditText.getText().toString();
                //老值和新值不同是进行保存
                if (!fieldValue.equals(newValue)) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("carId", AppConfig.userInfoBean.getCarId() + "");
                    map.put(fieldName, newValue);
                    String url = "";
                    if (type == 1) {
                        url = HttpConstants.getUpdateUserUrl();
                        map.put("userId", AppConfig.userInfoBean.getUserId());
                    } else if (type == 2) {
                        url = HttpConstants.getUpdateCarUrl();
                    }
                    RequestParams params = DRequestParamsUtils.getRequestParams_Header(url, map);
                    DHttpUtils.post_String(UpdateTextValueActivity.this, true, params, new DCommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            if (type == 1) {
                                ResponseBean<UserInfoBean> bean = new Gson().fromJson(result, new TypeToken<ResponseBean<UserInfoBean>>() {
                                }.getType());
                                if (bean.getCode() == 1) {
                                    AppConfig.userInfoBean = bean.getData();
                                    EventBus.getDefault().post(bean.getData());
                                } else {
                                    showShortText(bean.getErrmsg());
                                }
                            } else if (type == 2) {
                                ResponseBean<CarInfoBean> bean = new Gson().fromJson(result, new TypeToken<ResponseBean<CarInfoBean>>() {
                                }.getType());
                                if (bean.getCode() == 1) {
                                    EventBus.getDefault().post(bean.getData());
                                } else {
                                    showShortText(bean.getErrmsg());
                                }
                            }

                        }
                    });
                }
                UpdateTextValueActivity.this.finish();
            }
        });
    }

    @Override
    public void setData() {

    }
}
