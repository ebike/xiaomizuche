package com.xiaomizuche.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaomizuche.R;
import com.xiaomizuche.activity.HomeActivity;
import com.xiaomizuche.activity.LoginActivity;
import com.xiaomizuche.activity.ManageCardActivity;
import com.xiaomizuche.base.BaseFragment;
import com.xiaomizuche.bean.UserInfoBean;
import com.xiaomizuche.constants.AppConfig;
import com.xiaomizuche.utils.CommonUtils;
import com.xiaomizuche.utils.T;
import com.xiaomizuche.view.TopBarView;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import de.greenrobot.event.EventBus;

/**
 * 首页
 */
public class HomeFragment extends BaseFragment {

    @ViewInject(R.id.top_bar_view)
    TopBarView topBarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        x.view().inject(this, view);
        isPrepared = true;
        EventBus.getDefault().register(this);

        if (AppConfig.userInfoBean != null) {
            topBarView.setRightTextEnabled(false);
        } else {
            topBarView.setRightCallback(new TopBarView.TopBarRightCallback() {
                @Override
                public void setRightOnClickListener() {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
            });
        }

        return view;
    }

    @Override
    public void requestDatas() {

    }

    @Event(value = R.id.tv_manage_card)
    private void manageCard(View view) {
        startActivity(new Intent(getActivity(), ManageCardActivity.class));
    }

    @Event(value = R.id.tv_hire_car)
    private void hireCar(View view) {
        ((HomeActivity) getActivity()).changeToHireCar();
    }

    @Event(value = R.id.tv_back_car)
    private void backCar(View view){
        //判断是否登录状态
        if(AppConfig.userInfoBean != null){
            if(AppConfig.userInfoBean.getCarRecord() != null){
                CommonUtils.backCar(getActivity(), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        T.showShort(getActivity(),"还车成功");
                    }
                });
            }else{
                T.showShort(getActivity(),"您没有租车");
            }
        }else{
            T.showShort(getActivity(),"请您先登录账号，再进行还车");
        }
    }

    public void onEvent(UserInfoBean bean) {
        topBarView.setRightTextEnabled(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
