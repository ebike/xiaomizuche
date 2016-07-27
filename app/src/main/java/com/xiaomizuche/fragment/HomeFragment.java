package com.xiaomizuche.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaomizuche.R;
import com.xiaomizuche.activity.LoginActivity;
import com.xiaomizuche.activity.ManageCardActivity;
import com.xiaomizuche.base.BaseFragment;
import com.xiaomizuche.view.TopBarView;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 首页
 */
public class HomeFragment extends BaseFragment {

    @ViewInject(R.id.top_bar_view)
    TopBarView topBarView;

    private boolean isPrepared;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        x.view().inject(this, view);
        isPrepared = true;

        topBarView.setRightCallback(new TopBarView.TopBarRightCallback() {
            @Override
            public void setRightOnClickListener() {
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        });
        return view;
    }

    @Override
    public void requestDatas() {

    }

    @Event(value = R.id.tv_manage_card)
    private void manageCard(View view) {
        startActivity(new Intent(getActivity(), ManageCardActivity.class));
    }
}
