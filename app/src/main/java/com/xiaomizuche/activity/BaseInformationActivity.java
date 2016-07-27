package com.xiaomizuche.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomizuche.R;
import com.xiaomizuche.base.BaseActivity;
import com.xiaomizuche.bean.LocationJson;
import com.xiaomizuche.bean.ResponseBean;
import com.xiaomizuche.bean.UserInfoBean;
import com.xiaomizuche.callback.DCommonCallback;
import com.xiaomizuche.constants.AppConfig;
import com.xiaomizuche.db.ProvinceInfoDao;
import com.xiaomizuche.http.DHttpUtils;
import com.xiaomizuche.http.DRequestParamsUtils;
import com.xiaomizuche.http.HttpConstants;
import com.xiaomizuche.utils.CommonUtils;
import com.xiaomizuche.view.RowLabelValueView;
import com.xiaomizuche.view.wheel.AddressThreeWheelViewDialog;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基本资料
 */
public class BaseInformationActivity extends BaseActivity implements RowLabelValueView.OnClickCallback {
    @ViewInject(R.id.rlvv_equipment_serial_number)
    RowLabelValueView equipmentSerialNumberRowLabelValueView;
    @ViewInject(R.id.rlvv_imei)
    RowLabelValueView imeiRowLabelValueView;
    @ViewInject(R.id.rlvv_data_card)
    RowLabelValueView dataCardRowLabelValueView;
    @ViewInject(R.id.rlvv_open_date)
    RowLabelValueView openDateRowLabelValueView;
    @ViewInject(R.id.rlvv_name)
    RowLabelValueView nameRowLabelValueView;
    @ViewInject(R.id.rlvv_sex)
    RowLabelValueView sexRowLabelValueView;
    @ViewInject(R.id.rlvv_contact_phone)
    RowLabelValueView contactPhoneRowLabelValueView;
    @ViewInject(R.id.rlvv_work_phone)
    RowLabelValueView workPhoneRowLabelValueView;
    @ViewInject(R.id.rlvv_area)
    RowLabelValueView areaRowLabelValueView;
    @ViewInject(R.id.rlvv_address)
    RowLabelValueView addressRowLabelValueView;
    @ViewInject(R.id.rlvv_belongs_to_unit)
    RowLabelValueView belongsToUnitRowLabelValueView;
    //用户信息
    private UserInfoBean userInfoBean;
    private AddressThreeWheelViewDialog dialog;
    private ProvinceInfoDao provinceDao;
    private List<LocationJson> mProvinceList;
    private int provinceId;
    private int cityId;
    private int districtId;
    private String provinceName;
    private String cityName;
    private String districtName;

    @Override
    public void loadXml() {
        setContentView(R.layout.activity_base_information);
    }

    @Override
    public void getIntentData(Bundle savedInstanceState) {

    }

    @Override
    public void init() {
        if (userInfoBean != null) {
            imeiRowLabelValueView.setValue(userInfoBean.getImei() + "");
            nameRowLabelValueView.setValue(userInfoBean.getUserName());
            if (userInfoBean.getSex() == 0) {
                sexRowLabelValueView.setValue("男");
            } else if (userInfoBean.getSex() == 1) {
                sexRowLabelValueView.setValue("女");
            }
            contactPhoneRowLabelValueView.setValue(userInfoBean.getPhone());
            areaRowLabelValueView.setValue(userInfoBean.getProvince() + "-" + userInfoBean.getCity() + "-" + userInfoBean.getArea());
            addressRowLabelValueView.setValue(userInfoBean.getAddress());
        }
        dialog = new AddressThreeWheelViewDialog(this);
        provinceDao = new ProvinceInfoDao(this);
        mProvinceList = provinceDao.queryAll();
    }

    @Override
    public void setListener() {
        nameRowLabelValueView.setOnClickCallback(this);
        sexRowLabelValueView.setOnClickCallback(this);
        contactPhoneRowLabelValueView.setOnClickCallback(this);
        workPhoneRowLabelValueView.setOnClickCallback(this);
        areaRowLabelValueView.setOnClickCallback(this);
        addressRowLabelValueView.setOnClickCallback(this);
    }

    @Override
    public void setData() {
        RequestParams params = DRequestParamsUtils.getRequestParams_Header(HttpConstants.getUserInfo());
        DHttpUtils.get_String(this, true, params, new DCommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ResponseBean<UserInfoBean> bean = new Gson().fromJson(result, new TypeToken<ResponseBean<UserInfoBean>>() {
                }.getType());
                if (bean.getCode() == 1) {
                    userInfoBean = bean.getData();
                    //更新界面
                    init();
                    //更新缓存
                    AppConfig.userInfoBean = bean.getData();
                } else {
                    showShortText(bean.getErrmsg());
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.rlvv_name:
                intent = new Intent(this, UpdateTextValueActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("fieldName_CH", getString(R.string.name));
                intent.putExtra("fieldValue", userInfoBean.getUserName());
                intent.putExtra("fieldName", "operName");
                startActivity(intent);
                break;
            case R.id.rlvv_sex:
                startActivity(new Intent(this, UpdateSexActivity.class));
                break;
            case R.id.rlvv_contact_phone:
                intent = new Intent(this, UpdateTextValueActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("fieldName_CH", getString(R.string.contact_phone));
                intent.putExtra("fieldValue", userInfoBean.getPhone());
                intent.putExtra("fieldName", "phone");
                startActivity(intent);
                break;
            case R.id.rlvv_work_phone:
                intent = new Intent(this, UpdateTextValueActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("fieldName_CH", getString(R.string.work_phone));
                intent.putExtra("fieldName", "workPhone");
                startActivity(intent);
                break;
            case R.id.rlvv_area:
                if (userInfoBean != null) {
                    dialog.setData(mProvinceList, userInfoBean.getProvince(), userInfoBean.getCity(), userInfoBean.getArea());
                } else {
                    dialog.setData(mProvinceList);
                }
                dialog.show(new AddressThreeWheelViewDialog.ConfirmAction() {
                    @Override
                    public void doAction(LocationJson root, LocationJson child, LocationJson child2) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("userId", AppConfig.userInfoBean.getUserId());
                        map.put("province", root.getName());
                        map.put("city", child.getName());
                        map.put("area", child2.getName());
                        RequestParams params = DRequestParamsUtils.getRequestParams_Header(HttpConstants.getUpdateUserUrl(), map);
                        DHttpUtils.post_String(BaseInformationActivity.this, true, params, new DCommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                ResponseBean<UserInfoBean> bean = new Gson().fromJson(result, new TypeToken<ResponseBean<UserInfoBean>>() {
                                }.getType());
                                if (bean.getCode() == 1) {
                                    onEvent(bean.getData());
                                    //更新缓存
                                    AppConfig.userInfoBean = bean.getData();
                                } else {
                                    showShortText(bean.getErrmsg());
                                }
                            }
                        });
                    }
                });
                break;
            case R.id.rlvv_address:
                intent = new Intent(this, UpdateTextValueActivity.class);
                intent.putExtra("type", 1);
                intent.putExtra("fieldName_CH", getString(R.string.detailed_address));
                intent.putExtra("fieldValue", userInfoBean.getAddress());
                intent.putExtra("fieldName", "address");
                startActivity(intent);
                break;
        }
    }

    public void onEvent(UserInfoBean user) {
        if (user != null) {
            userInfoBean = user;
            init();
        }
    }
}
