package com.xiaomizuche.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaomizuche.R;
import com.xiaomizuche.bean.SchoolBean;
import com.xiaomizuche.view.ViewHolder;

/**
 * 学校
 */
public class SchoolAdapter extends TAdapter<SchoolBean> {
    public SchoolAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_school, parent, false);
        }
        TextView nameView = ViewHolder.get(convertView, R.id.tv_name);

        SchoolBean bean = mList.get(position);
        if (bean != null) {
            nameView.setText(bean.getName());
        }
        return convertView;
    }
}
