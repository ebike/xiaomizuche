package com.xiaomizuche.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaomizuche.R;
import com.xiaomizuche.bean.AlarmMessageBean;
import com.xiaomizuche.utils.CommonUtils;
import com.xiaomizuche.view.ViewHolder;

import java.util.Date;

/**
 * 报警消息
 * Created by jimmy on 16/1/16.
 */
public class AlarmMessageAdapter extends TAdapter<AlarmMessageBean> {
    public AlarmMessageAdapter(Context mContext) {
        super(mContext);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_alarm_message, parent, false);
        }
        ImageView msgImageView = ViewHolder.get(convertView, R.id.iv_msg);
        TextView titleTextView = ViewHolder.get(convertView, R.id.tv_title);
        TextView contentTextView = ViewHolder.get(convertView, R.id.tv_content);
        TextView timeTextView = ViewHolder.get(convertView, R.id.tv_time);

        AlarmMessageBean bean = mList.get(position);
        if (bean != null) {
            if (bean.getStatus() == 1) {
                convertView.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                titleTextView.setTextColor(mContext.getResources().getColor(R.color.gray_3));
                contentTextView.setTextColor(mContext.getResources().getColor(R.color.gray_6));
            } else {
                convertView.setBackgroundColor(mContext.getResources().getColor(R.color.white_f5));
                titleTextView.setTextColor(mContext.getResources().getColor(R.color.gray_6));
                contentTextView.setTextColor(mContext.getResources().getColor(R.color.gray_6));
            }
            switch (bean.getEventType()) {
                case 1://震动报警
                    msgImageView.setImageResource(R.mipmap.icon_alarm_shake);
                    titleTextView.setText("震动报警");
                    break;
                case 2://电子围栏
                    msgImageView.setImageResource(R.mipmap.icon_alarm_fence);
                    titleTextView.setText("电子围栏");
                    break;
                case 3://锁车通知
                    msgImageView.setImageResource(R.mipmap.icon_notify_lock);
                    titleTextView.setText("锁车通知");
                    break;
                case 4://解锁通知
                    msgImageView.setImageResource(R.mipmap.icon_notify_unlock);
                    titleTextView.setText("解锁通知");
                    break;
                case 11://电池欠压
                    msgImageView.setImageResource(R.mipmap.icon_alarm_lowpower);
                    titleTextView.setText("电池欠压");
                    break;
                case 12://异常移动
                    msgImageView.setImageResource(R.mipmap.icon_alarm_move);
                    titleTextView.setText("异常移动");
                    break;
                case 13://主电源切断
                    msgImageView.setImageResource(R.mipmap.icon_alarm_power);
                    titleTextView.setText("主电源切断");
                    break;
                case 21://保险通知
                    msgImageView.setImageResource(R.mipmap.icon_notify_insur);
                    titleTextView.setText("保险通知");
                    break;
                case 22://注册通知
                    msgImageView.setImageResource(R.mipmap.icon_notify_register);
                    titleTextView.setText("注册通知");
                    break;
                case 23://通知消息
                    msgImageView.setImageResource(R.mipmap.icon_notify_msg);
                    titleTextView.setText("通知消息");
                    break;
            }
            contentTextView.setText(bean.getMsg());
            timeTextView.setText(CommonUtils.friendlyShowTime(new Date(bean.getCreateDate()).getTime()));
            if (bean.isExpand()) {
                contentTextView.setSingleLine(false);
            } else {
                contentTextView.setSingleLine(true);
            }
        }
        return convertView;
    }
}
