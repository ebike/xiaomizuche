package com.xiaomizuche.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xiaomizuche.R;
import com.xiaomizuche.base.BaseActivity;
import com.xiaomizuche.bean.LocationJson;
import com.xiaomizuche.bean.UserInfoBean;
import com.xiaomizuche.constants.AppConfig;
import com.xiaomizuche.db.ProvinceInfoDao;
import com.xiaomizuche.utils.CommonUtils;
import com.xiaomizuche.view.RowLabelValueView;
import com.xiaomizuche.view.wheel.AddressThreeWheelViewDialog;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * 基本资料
 */
public class BaseInformationActivity extends BaseActivity implements RowLabelValueView.OnClickCallback {

    @ViewInject(R.id.iv_header)
    ImageView headerView;
    @ViewInject(R.id.rlvv_name)
    RowLabelValueView nameView;
    @ViewInject(R.id.rlvv_sex)
    RowLabelValueView sexView;
    @ViewInject(R.id.rlvv_phone)
    RowLabelValueView phoneView;
    @ViewInject(R.id.rlvv_id_card)
    RowLabelValueView idCardView;
    @ViewInject(R.id.rlvv_user_type)
    RowLabelValueView userTypeView;
    @ViewInject(R.id.rlvv_area)
    RowLabelValueView areaView;
    @ViewInject(R.id.rlvv_address)
    RowLabelValueView addressView;

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
        if (AppConfig.userInfoBean != null) {
            Glide.with(this)
                    .load(AppConfig.userInfoBean.getHeadPic())
                    .bitmapTransform(new CropCircleTransformation(this))
                    .into(headerView);
            if (!CommonUtils.strIsEmpty(AppConfig.userInfoBean.getUserId())) {
                nameView.setValue(AppConfig.userInfoBean.getUserId());
            }
            if (AppConfig.userInfoBean.getSex() == 0) {
                sexView.setValue("男");
            } else if (AppConfig.userInfoBean.getSex() == 1) {
                sexView.setValue("女");
            }
            phoneView.setValue(AppConfig.userInfoBean.getPhone());
            if (!CommonUtils.strIsEmpty(AppConfig.userInfoBean.getIdNum())) {
                idCardView.setValue(AppConfig.userInfoBean.getIdNum());
            }
            if (AppConfig.userInfoBean.getUserType() == 1) {
                userTypeView.setValue("学校用户");
            } else {
                userTypeView.setValue("普通用户");
            }
            areaView.setValue(AppConfig.userInfoBean.getProvince() + "-" + AppConfig.userInfoBean.getCity() + "-" + AppConfig.userInfoBean.getArea());
            addressView.setValue(AppConfig.userInfoBean.getAddress());
        }
        dialog = new AddressThreeWheelViewDialog(this);
        provinceDao = new ProvinceInfoDao(this);
        mProvinceList = provinceDao.queryAll();
    }

    @Override
    public void setListener() {
        nameView.setOnClickCallback(this);
        sexView.setOnClickCallback(this);
        phoneView.setOnClickCallback(this);
        idCardView.setOnClickCallback(this);
        userTypeView.setOnClickCallback(this);
        areaView.setOnClickCallback(this);
        addressView.setOnClickCallback(this);
    }

    @Event(value = R.id.rl_header)
    private void header() {

    }

    @Override
    public void setData() {

    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
//        switch (view.getId()) {
//            case R.id.rlvv_name:
//                intent = new Intent(this, UpdateTextValueActivity.class);
//                intent.putExtra("type", 1);
//                intent.putExtra("fieldName_CH", getString(R.string.name));
//                intent.putExtra("fieldValue", userInfoBean.getUserName());
//                intent.putExtra("fieldName", "operName");
//                startActivity(intent);
//                break;
//            case R.id.rlvv_sex:
//                startActivity(new Intent(this, UpdateSexActivity.class));
//                break;
//            case R.id.rlvv_contact_phone:
//                intent = new Intent(this, UpdateTextValueActivity.class);
//                intent.putExtra("type", 1);
//                intent.putExtra("fieldName_CH", getString(R.string.contact_phone));
//                intent.putExtra("fieldValue", userInfoBean.getPhone());
//                intent.putExtra("fieldName", "phone");
//                startActivity(intent);
//                break;
//            case R.id.rlvv_work_phone:
//                intent = new Intent(this, UpdateTextValueActivity.class);
//                intent.putExtra("type", 1);
//                intent.putExtra("fieldName_CH", getString(R.string.work_phone));
//                intent.putExtra("fieldName", "workPhone");
//                startActivity(intent);
//                break;
//            case R.id.rlvv_area:
//                if (userInfoBean != null) {
//                    dialog.setData(mProvinceList, userInfoBean.getProvince(), userInfoBean.getCity(), userInfoBean.getArea());
//                } else {
//                    dialog.setData(mProvinceList);
//                }
//                dialog.show(new AddressThreeWheelViewDialog.ConfirmAction() {
//                    @Override
//                    public void doAction(LocationJson root, LocationJson child, LocationJson child2) {
//                        Map<String, String> map = new HashMap<String, String>();
//                        map.put("userId", AppConfig.userInfoBean.getUserId());
//                        map.put("province", root.getName());
//                        map.put("city", child.getName());
//                        map.put("area", child2.getName());
//                        RequestParams params = DRequestParamsUtils.getRequestParams_Header(HttpConstants.getUpdateUserUrl(), map);
//                        DHttpUtils.post_String(BaseInformationActivity.this, true, params, new DCommonCallback<String>() {
//                            @Override
//                            public void onSuccess(String result) {
//                                ResponseBean<UserInfoBean> bean = new Gson().fromJson(result, new TypeToken<ResponseBean<UserInfoBean>>() {
//                                }.getType());
//                                if (bean.getCode() == 1) {
//                                    onEvent(bean.getData());
//                                    //更新缓存
//                                    AppConfig.userInfoBean = bean.getData();
//                                } else {
//                                    showShortText(bean.getErrmsg());
//                                }
//                            }
//                        });
//                    }
//                });
//                break;
//            case R.id.rlvv_address:
//                intent = new Intent(this, UpdateTextValueActivity.class);
//                intent.putExtra("type", 1);
//                intent.putExtra("fieldName_CH", getString(R.string.detailed_address));
//                intent.putExtra("fieldValue", userInfoBean.getAddress());
//                intent.putExtra("fieldName", "address");
//                startActivity(intent);
//                break;
//        }
    }

    public void onEvent(UserInfoBean user) {
        if (user != null) {
            init();
        }
    }
}
