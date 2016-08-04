package com.xiaomizuche.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiaomizuche.R;
import com.xiaomizuche.bean.SchoolBean;
import com.xiaomizuche.utils.CommonUtils;
import com.xiaomizuche.view.ViewHolder;

/**
 * 学校
 */
public class SchoolAdapter extends TAdapter<SchoolBean> {

    private String schoolId;

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
            if (!CommonUtils.strIsEmpty(schoolId) && schoolId.equals(bean.getId())) {
                Drawable drawable = mContext.getResources().getDrawable(R.mipmap.icon_sel);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                nameView.setCompoundDrawables(null, null, drawable, null);
            } else {
                nameView.setCompoundDrawables(null, null, null, null);
            }
        }
        return convertView;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }
}
