package com.xiaomizuche.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiaomizuche.R;
import com.xiaomizuche.activity.AboutActivity;
import com.xiaomizuche.activity.BaseInformationActivity;
import com.xiaomizuche.activity.LoginActivity;
import com.xiaomizuche.base.BaseActivity;
import com.xiaomizuche.base.BaseFragment;
import com.xiaomizuche.bean.UserInfoBean;
import com.xiaomizuche.constants.AppConfig;
import com.xiaomizuche.utils.CommonUtils;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import de.greenrobot.event.EventBus;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * 我的
 */
public class MyFragment extends BaseFragment {

    @ViewInject(R.id.iv_header)
    ImageView headerView;
    @ViewInject(R.id.tv_no_login)
    TextView noLoginView;
    @ViewInject(R.id.ll_name)
    LinearLayout nameLayout;
    @ViewInject(R.id.tv_name)
    TextView nameView;
    @ViewInject(R.id.tv_phone)
    TextView phoneView;
    @ViewInject(R.id.ll_date)
    LinearLayout dateLayout;
    @ViewInject(R.id.tv_date)
    TextView dateView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        x.view().inject(this, view);
        isPrepared = true;
        EventBus.getDefault().register(this);

        return view;
    }

    @Override
    public void requestDatas() {
        if (!isPrepared || !isVisible || hasLoadedOnce || !isAdded()) {
            return;
        }
        if (AppConfig.userInfoBean != null) {
            noLoginView.setVisibility(View.GONE);
            nameLayout.setVisibility(View.VISIBLE);
            dateLayout.setVisibility(View.VISIBLE);
            Glide.with(getActivity())
                    .load(AppConfig.userInfoBean.getHeadPic())
                    .bitmapTransform(new CropCircleTransformation(getActivity()))
                    .into(headerView);
            if (!CommonUtils.strIsEmpty(AppConfig.userInfoBean.getUserName())) {
                nameView.setText(AppConfig.userInfoBean.getUserName());
            }
            phoneView.setText(AppConfig.userInfoBean.getPhone());
            dateView.setText(AppConfig.userInfoBean.getRegTime());
        } else {
            noLoginView.setVisibility(View.VISIBLE);
            nameLayout.setVisibility(View.GONE);
            dateLayout.setVisibility(View.GONE);
        }
        hasLoadedOnce = true;
    }

    @Event(value = R.id.ll_user_info)
    private void userInfo(View view) {
        if (AppConfig.userInfoBean != null) {
            startActivity(new Intent(getActivity(), BaseInformationActivity.class));
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }
    }

    @Event(value = R.id.rev_car_card)
    private void carCard(View view) {

    }

    @Event(value = R.id.rev_service)
    private void service(View view) {

    }

    @Event(value = R.id.rev_contact_customer_service)
    private void contactCustomerService(View view) {

    }

    @Event(value = R.id.rev_about)
    private void about(View view) {
        startActivity(new Intent(getActivity(), AboutActivity.class));
    }

    @Event(value = R.id.ll_logout)
    private void logout(View view) {
        ((BaseActivity) getActivity()).logout();
    }

    public void onEvent(UserInfoBean bean) {
        hasLoadedOnce = false;
        requestDatas();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
