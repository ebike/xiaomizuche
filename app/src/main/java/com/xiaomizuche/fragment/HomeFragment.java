package com.xiaomizuche.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaomizuche.R;
import com.xiaomizuche.base.BaseFragment;

import org.xutils.x;

/**
 * 首页
 */
public class HomeFragment extends BaseFragment {

    private boolean isPrepared;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        x.view().inject(this, view);
        isPrepared = true;
        return view;
    }

    @Override
    public void requestDatas() {

    }
}
