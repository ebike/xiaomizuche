package com.xiaomizuche.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomizuche.R;
import com.xiaomizuche.base.BaseActivity;
import com.xiaomizuche.bean.CarLikeImeiBean;
import com.xiaomizuche.bean.LocationJson;
import com.xiaomizuche.bean.ResponseBean;
import com.xiaomizuche.callback.DCommonCallback;
import com.xiaomizuche.callback.DSingleDialogCallback;
import com.xiaomizuche.db.ProvinceInfoDao;
import com.xiaomizuche.http.DHttpUtils;
import com.xiaomizuche.http.DRequestParamsUtils;
import com.xiaomizuche.http.HttpConstants;
import com.xiaomizuche.utils.CommonUtils;
import com.xiaomizuche.view.RowLabelEditView;
import com.xiaomizuche.view.RowLabelValueView;
import com.xiaomizuche.view.TopBarView;
import com.xiaomizuche.view.wheel.AddressThreeWheelViewDialog;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 注册账号
 */
public class RegisterActivity extends BaseActivity {
    @ViewInject(R.id.top_bar)
    TopBarView topBarView;
    @ViewInject(R.id.rlev_imei)
    RowLabelEditView imeiRowLabelEditView;
    @ViewInject(R.id.rlvv_equipment_serial_number)
    RowLabelValueView equipmentSerialNumberRowLabelValueView;
    @ViewInject(R.id.rlev_information_id_card)
    RowLabelEditView informationIdCardRowLabelEditView;
    @ViewInject(R.id.rlev_name)
    RowLabelEditView nameRowLabelEditView;
    @ViewInject(R.id.tb_sex)
    ToggleButton sexToggleButton;
    @ViewInject(R.id.rlev_contact_phone)
    RowLabelEditView phoneRowLabelEditView;
    @ViewInject(R.id.rlvv_region)
    RowLabelValueView regionRowLabelValueView;
    @ViewInject(R.id.rlev_detailed_address)
    RowLabelEditView detailedAddressRowLabelEditView;
    @ViewInject(R.id.rlev_brand)
    RowLabelEditView brandRowLabelEditView;
    @ViewInject(R.id.rlev_models)
    RowLabelEditView modelsRowLabelEditView;
    private AddressThreeWheelViewDialog dialog;
    private ProvinceInfoDao provinceDao;
    private List<LocationJson> mProvinceList;
    private int provinceId;
    private int cityId;
    private int districtId;
    private String provinceName;
    private String cityName;
    private String districtName;
    private String sex;

    @Override
    public void loadXml() {
        setContentView(R.layout.activity_register);
        x.view().inject(this);
    }

    @Override
    public void getIntentData(Bundle savedInstanceState) {

    }

    @Override
    public void init() {
        sex = "0";
        imeiRowLabelEditView.setEditInteger();
        imeiRowLabelEditView.setEditLength(8);
        informationIdCardRowLabelEditView.setEditInteger();
        informationIdCardRowLabelEditView.setEditLength(13);
        phoneRowLabelEditView.setEditInteger();
        dialog = new AddressThreeWheelViewDialog(this);
        provinceDao = new ProvinceInfoDao(this);
        mProvinceList = provinceDao.queryAll();
    }

    @Override
    public void setListener() {
        //根据IMEI码获取设备编号
        imeiRowLabelEditView.setTextChangedCallback(new RowLabelEditView.EditTextChangedCallback() {
            @Override
            public void afterTextChanged() {
                String imei = imeiRowLabelEditView.getValue();
                if (!CommonUtils.strIsEmpty(imei) && imei.length() == 8) {
                    RequestParams params = new RequestParams(HttpConstants.getSearchCarLikeImei(imei));
                    DHttpUtils.get_String(RegisterActivity.this, true, params, new DCommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            ResponseBean<CarLikeImeiBean> bean = new Gson().fromJson(result, new TypeToken<ResponseBean<CarLikeImeiBean>>() {
                            }.getType());
                            if (bean.getCode() == 1) {
                                equipmentSerialNumberRowLabelValueView.setValue(bean.getData().getCarId() + "");
                            } else {
                                showShortText(bean.getErrmsg());
                                imeiRowLabelEditView.setValue("");
                                equipmentSerialNumberRowLabelValueView.setValue("");
                            }
                        }
                    });
                }
            }
        });
        sexToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {//选中
                    sex = "1";
                } else {//未选中
                    sex = "0";
                }
            }
        });
        //选择省市县
        regionRowLabelValueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setData(mProvinceList);
                dialog.show(new AddressThreeWheelViewDialog.ConfirmAction() {
                    @Override
                    public void doAction(LocationJson root, LocationJson child, LocationJson child2) {
                        regionRowLabelValueView.setValue(root.getName() + " " + child.getName() + " " + child2.getName());
                        regionRowLabelValueView.setValueColor(R.color.gray_6);
                        provinceId = root.getId();
                        provinceName = root.getName();
                        cityId = child.getId();
                        cityName = child.getName();
                        districtId = child2.getId();
                        districtName = child2.getName();
                    }
                });
            }
        });
        //提交
        topBarView.setRightCallback(new TopBarView.TopBarRightCallback() {
            @Override
            public void setRightOnClickListener() {
                //验证必填
                if (CommonUtils.strIsEmpty(equipmentSerialNumberRowLabelValueView.getValue())) {
                    imeiRowLabelEditView.setValue("");
                    imeiRowLabelEditView.setHintColor(R.color.main_tone);
                    return;
                }
                if (CommonUtils.strIsEmpty(informationIdCardRowLabelEditView.getValue())) {
                    informationIdCardRowLabelEditView.setHint(R.string.app_require_input);
                    informationIdCardRowLabelEditView.setHintColor(R.color.main_tone);
                    return;
                }
                if (informationIdCardRowLabelEditView.getValue().length() != 11 && informationIdCardRowLabelEditView.getValue().length() != 13) {
                    informationIdCardRowLabelEditView.setHint("数据卡号错误");
                    informationIdCardRowLabelEditView.setHintColor(R.color.main_tone);
                    return;
                }
                RequestParams params = new RequestParams(HttpConstants.getCheckTelNumUrl(informationIdCardRowLabelEditView.getValue()));
                DHttpUtils.get_String(RegisterActivity.this, true, params, new DCommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        ResponseBean<Object> bean = new Gson().fromJson(result, new TypeToken<ResponseBean<CarLikeImeiBean>>() {
                        }.getType());
                        if (bean.getCode() == 1) {
                            if (CommonUtils.strIsEmpty(nameRowLabelEditView.getValue())) {
                                nameRowLabelEditView.setHint(R.string.app_require_input);
                                nameRowLabelEditView.setHintColor(R.color.main_tone);
                                return;
                            }
                            if (CommonUtils.strIsEmpty(phoneRowLabelEditView.getValue())) {
                                phoneRowLabelEditView.setHint(R.string.app_require_input);
                                phoneRowLabelEditView.setHintColor(R.color.main_tone);
                                return;
                            }
                            if (provinceId <= 0 && cityId <= 0 && districtId <= 0) {
                                regionRowLabelValueView.setValueColor(R.color.main_tone);
                                return;
                            }
                            if (CommonUtils.strIsEmpty(detailedAddressRowLabelEditView.getValue())) {
                                detailedAddressRowLabelEditView.setHint(R.string.app_require_input);
                                detailedAddressRowLabelEditView.setHintColor(R.color.main_tone);
                                return;
                            }
                            //整理参数
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("carId", equipmentSerialNumberRowLabelValueView.getValue());
                            map.put("telNum", informationIdCardRowLabelEditView.getValue());
                            map.put("userName", nameRowLabelEditView.getValue());
                            map.put("sex", sex);
                            map.put("phone", phoneRowLabelEditView.getValue());
                            map.put("province", provinceName);
                            map.put("city", cityName);
                            map.put("area", districtName);
                            map.put("address", detailedAddressRowLabelEditView.getValue());
                            map.put("carBrand", brandRowLabelEditView.getValue());
                            map.put("carModel", modelsRowLabelEditView.getValue());
                            //提交
                            RequestParams params = DRequestParamsUtils.getRequestParams(HttpConstants.getRegDeviceUrl(), map);
                            DHttpUtils.post_String(RegisterActivity.this, true, params, new DCommonCallback<String>() {
                                @Override
                                public void onSuccess(String result) {
                                    ResponseBean<Object> bean = new Gson().fromJson(result, new TypeToken<ResponseBean<CarLikeImeiBean>>() {
                                    }.getType());
                                    if (bean.getCode() == 1) {
                                        CommonUtils.showCustomDialogSignle2(RegisterActivity.this, "恭喜您，注册成功", "\n您的账号为：" + equipmentSerialNumberRowLabelValueView.getValue() + "\n初始密码为：666666", new DSingleDialogCallback() {
                                            @Override
                                            public void onPositiveButtonClick(String editText) {
                                                RegisterActivity.this.finish();
                                            }
                                        });
                                    } else {
                                        showShortText(bean.getErrmsg());
                                    }
                                }
                            });
                        } else {
                            informationIdCardRowLabelEditView.requestFocus();
                            showShortText(bean.getErrmsg());
                        }
                    }
                });
            }
        });
    }

    @Override
    public void setData() {

    }
}