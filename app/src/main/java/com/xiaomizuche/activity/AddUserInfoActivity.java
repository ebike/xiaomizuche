package com.xiaomizuche.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomizuche.R;
import com.xiaomizuche.base.BaseActivity;
import com.xiaomizuche.bean.LocationJson;
import com.xiaomizuche.bean.ResponseBean;
import com.xiaomizuche.callback.DCommonCallback;
import com.xiaomizuche.db.ProvinceInfoDao;
import com.xiaomizuche.http.DHttpUtils;
import com.xiaomizuche.http.DRequestParamsUtils;
import com.xiaomizuche.http.HttpConstants;
import com.xiaomizuche.utils.CommonUtils;
import com.xiaomizuche.utils.IDCard;
import com.xiaomizuche.utils.T;
import com.xiaomizuche.view.CustomDialog;
import com.xiaomizuche.view.wheel.AddressThreeWheelViewDialog;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddUserInfoActivity extends BaseActivity implements TextWatcher {

    @ViewInject(R.id.et_name)
    EditText nameText;
    @ViewInject(R.id.v_line_name)
    View nameLine;
    @ViewInject(R.id.ll_sex)
    LinearLayout sexLayout;
    @ViewInject(R.id.tv_sex)
    TextView sexView;
    @ViewInject(R.id.et_id_card)
    EditText idCardText;
    @ViewInject(R.id.v_line_id_card)
    View idCardLine;
    @ViewInject(R.id.tv_area)
    TextView areaView;
    @ViewInject(R.id.tv_user_type)
    TextView userTypeView;
    @ViewInject(R.id.ll_school)
    LinearLayout schoolLayout;
    @ViewInject(R.id.tv_school)
    TextView schoolView;
    @ViewInject(R.id.v_line_school)
    View schoolLine;
    @ViewInject(R.id.ll_address)
    LinearLayout addressLayout;
    @ViewInject(R.id.et_address)
    EditText addressText;
    @ViewInject(R.id.v_line_address)
    View addressLine;
    @ViewInject(R.id.btn_save)
    Button saveButton;

    private String name;
    private String sex;
    private String idCard;
    private String userType;
    private String school;
    private String address;

    private AddressThreeWheelViewDialog dialog;
    private List<LocationJson> mProvinceList;
    private ProvinceInfoDao provinceDao;
    private String province;
    private String city;
    private String county;

    @Override
    public void loadXml() {
        setContentView(R.layout.activity_add_user_info);
    }

    @Override
    public void getIntentData(Bundle savedInstanceState) {

    }

    @Override
    public void init() {
        dialog = new AddressThreeWheelViewDialog(this);
        provinceDao = new ProvinceInfoDao(this);
        mProvinceList = provinceDao.queryAll();
    }

    @Override
    public void setListener() {
        nameText.addTextChangedListener(this);
        idCardText.addTextChangedListener(this);
        addressText.addTextChangedListener(this);
    }

    @Event(value = R.id.ll_sex)
    private void sex(View view) {
        chooseSex();
    }

    @Event(value = R.id.ll_area)
    private void area(View view) {
        dialog.setData(mProvinceList);
        dialog.show(new AddressThreeWheelViewDialog.ConfirmAction() {
            @Override
            public void doAction(LocationJson root, LocationJson child, LocationJson child2) {
                province = root.getName();
                city = child.getName();
                county = child2.getName();
                areaView.setText(province + "-" + city + "-" + county);
            }
        });
    }

    @Event(value = R.id.ll_user_type)
    private void userType(View view) {
        chooseUserType();
    }

    @Event(value = R.id.btn_save)
    private void save(View view) {
        if (CommonUtils.strIsEmpty(nameText.getText().toString().trim())) {
            T.showShort(this, "请输入姓名");
            return;
        }
        if (CommonUtils.strIsEmpty(idCardText.getText().toString().trim())) {
            T.showShort(this, "请输入身份证号码");
            return;
        }
        IDCard idCard = new IDCard();
        String errorInfo = idCard.IDCardValidate(idCardText.getText().toString().trim().toLowerCase());
        if (!CommonUtils.strIsEmpty(errorInfo)) {
            T.showShort(this, "身份证号码不合法");
            return;
        }
        if (CommonUtils.strIsEmpty(areaView.getText().toString())) {
            T.showShort(this, "请选择省市区");
            return;
        }
        if (CommonUtils.strIsEmpty(userType)) {
            T.showShort(this, "请选择用户类型");
            return;
        }
        if (userType.equals("2") && CommonUtils.strIsEmpty(addressText.getText().toString().trim())) {
            T.showShort(this, "请输入详细地址");
            return;
        }
        if (userType.equals("1") && CommonUtils.strIsEmpty(schoolView.getText().toString())) {
            T.showShort(this, "请选择学校");
            return;
        }
        Map<String, String> map = new HashMap<>();
//        map.put("userId",);
        RequestParams params = DRequestParamsUtils.getRequestParams_Header(HttpConstants.perfectUserData(), map);
        DHttpUtils.post_String(this, true, params, new DCommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ResponseBean bean = new Gson().fromJson(result, new TypeToken<ResponseBean>() {
                }.getType());
                if (bean.getCode() == 1) {
                    AddUserInfoActivity.this.finish();
                } else {
                    showShortText(bean.getErrmsg());
                }
            }
        });
    }

    @Override
    public void setData() {

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        name = nameText.getText().toString().trim();
        idCard = idCardText.getText().toString().trim();
        address = addressText.getText().toString().trim();
        if (!CommonUtils.strIsEmpty(name)) {
            nameLine.setBackgroundColor(getResources().getColor(R.color.main_tone));
            nameText.setTextColor(getResources().getColor(R.color.main_tone));
        } else {
            nameLine.setBackgroundColor(getResources().getColor(R.color.font_gray));
        }
        if (!CommonUtils.strIsEmpty(idCard)) {
            idCardLine.setBackgroundColor(getResources().getColor(R.color.main_tone));
            idCardText.setTextColor(getResources().getColor(R.color.main_tone));
        } else {
            idCardLine.setBackgroundColor(getResources().getColor(R.color.font_gray));
        }
        if (!CommonUtils.strIsEmpty(address)) {
            addressLine.setBackgroundColor(getResources().getColor(R.color.main_tone));
            addressText.setTextColor(getResources().getColor(R.color.main_tone));
        } else {
            addressLine.setBackgroundColor(getResources().getColor(R.color.font_gray));
        }
    }

    private void chooseSex() {
        View view = LayoutInflater.from(this).inflate(R.layout.view_sex, null, false);
        TextView manView = (TextView) view.findViewById(R.id.tv_man);
        TextView womanView = (TextView) view.findViewById(R.id.tv_woman);
        final CustomDialog dialog = CommonUtils.showCustomDialog1(this, "选择性别", view);
        manView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                sex = "1";
                sexView.setText("男");
            }
        });
        womanView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                sex = "2";
                sexView.setText("女");
            }
        });
    }

    private void chooseUserType() {
        View view = LayoutInflater.from(this).inflate(R.layout.view_user_type, null, false);
        TextView schoolUserView = (TextView) view.findViewById(R.id.tv_school_user);
        TextView commonUserView = (TextView) view.findViewById(R.id.tv_common_user);
        final CustomDialog dialog = CommonUtils.showCustomDialog1(this, "选择用户类型", view);
        schoolUserView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                userType = "1";
                userTypeView.setText("学校用户");
                schoolLayout.setVisibility(View.VISIBLE);
                addressLayout.setVisibility(View.GONE);
            }
        });
        commonUserView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                userType = "2";
                userTypeView.setText("普通用户");
                schoolLayout.setVisibility(View.GONE);
                addressLayout.setVisibility(View.VISIBLE);
            }
        });
    }
}
