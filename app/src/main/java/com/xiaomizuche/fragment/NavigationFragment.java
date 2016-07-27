package com.xiaomizuche.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaomizuche.R;
import com.xiaomizuche.activity.AboutActivity;
import com.xiaomizuche.activity.BaseInformationActivity;
import com.xiaomizuche.activity.CarInformationActivity;
import com.xiaomizuche.activity.MainActivity;
import com.xiaomizuche.activity.WebActivity;
import com.xiaomizuche.bean.UserInfoBean;
import com.xiaomizuche.callback.DSingleDialogCallback;
import com.xiaomizuche.constants.AppConfig;
import com.xiaomizuche.utils.CommonUtils;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import de.greenrobot.event.EventBus;

/**
 * Created by jimmy on 16/3/13.
 */
public class NavigationFragment extends Fragment implements View.OnClickListener {
    @ViewInject(R.id.tv_name)
    TextView nameTV;
    @ViewInject(R.id.tv_carid)
    TextView carIdTV;
    @ViewInject(R.id.ll_user_info)
    LinearLayout userInfoLL;
    @ViewInject(R.id.ll_car_info)
    LinearLayout carInfoLL;
    @ViewInject(R.id.ll_service_terms)
    LinearLayout serviceTermsLL;
    @ViewInject(R.id.ll_contact_service)
    LinearLayout contactServiceLL;
    @ViewInject(R.id.ll_about)
    LinearLayout aboutLL;
    @ViewInject(R.id.ll_exit_user)
    LinearLayout exitUserLL;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.navigation, container, false);
        x.view().inject(this, view);
        EventBus.getDefault().register(this);
        initDatas();
        initListeners();
        return view;
    }

    private void initDatas() {
        if (AppConfig.userInfoBean != null) {
            nameTV.setText(AppConfig.userInfoBean.getUserName());
//            carIdTV.setText(AppConfig.userInfoBean.getCarId() + "");
        }
    }

    private void initListeners() {
        userInfoLL.setOnClickListener(this);
        carInfoLL.setOnClickListener(this);
        serviceTermsLL.setOnClickListener(this);
        contactServiceLL.setOnClickListener(this);
        aboutLL.setOnClickListener(this);
        exitUserLL.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_user_info:
                startActivity(new Intent(getActivity(), BaseInformationActivity.class));
                break;
            case R.id.ll_car_info:
                startActivity(new Intent(getActivity(), CarInformationActivity.class));
                break;
            case R.id.ll_service_terms:
                Intent intent = new Intent(getActivity(), WebActivity.class);
                intent.putExtra("title", getResources().getString(R.string.service_item));
                intent.putExtra("url", "http://api.car.gnets.cn/app/h5/service_terms.html");
                startActivity(intent);
                break;
            case R.id.ll_contact_service:
                ((MainActivity) getActivity()).setMenuToggle();
                CommonUtils.showCustomDialog3(getActivity(), "呼叫", "取消", "", "0531-67805000", new DSingleDialogCallback() {
                    @Override
                    public void onPositiveButtonClick(String editText) {
                        // 用intent启动拨打电话
                        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:053167805000"));
                        try {
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
            case R.id.ll_about:
                startActivity(new Intent(getActivity(), AboutActivity.class));
                break;
            case R.id.ll_exit_user:
                ((MainActivity) getActivity()).setMenuToggle();
                CommonUtils.showCustomDialog0(getActivity(), "", "您确定要退出登录吗？", new DSingleDialogCallback() {
                    @Override
                    public void onPositiveButtonClick(String editText) {
                        ((MainActivity) getActivity()).logout();
                    }
                });
                break;
        }
    }

    public void onEvent(UserInfoBean user) {
        if (user != null) {
            initDatas();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
