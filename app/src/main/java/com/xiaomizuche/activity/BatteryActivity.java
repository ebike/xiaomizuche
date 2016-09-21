package com.xiaomizuche.activity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaomizuche.R;
import com.xiaomizuche.base.BaseActivity;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

public class BatteryActivity extends BaseActivity {

    @ViewInject(R.id.tv_voltage)
    TextView voltageView;
    @ViewInject(R.id.iv_battery)
    ImageView batteryImageView;
    @ViewInject(R.id.tv_battery)
    TextView batteryView;

    private String remainBattery;
    private String currVoltage;

    @Override
    public void loadXml() {
        setContentView(R.layout.activity_battery);
        x.view().inject(this);
    }

    @Override
    public void getIntentData(Bundle savedInstanceState) {
        remainBattery = getIntent().getStringExtra("RemainBattery");
        currVoltage = getIntent().getStringExtra("CurrVoltage");
    }

    @Override
    public void init() {
        voltageView.setText("您的电动车规格为：" + currVoltage + "V");
        batteryView.setText("当前剩余电量：" + remainBattery + "%");
        if (remainBattery.equals("0") || remainBattery.equals("10")) {
            batteryView.setTextColor(getResources().getColor(R.color.red));
        } else {
            batteryView.setTextColor(getResources().getColor(R.color.green));
        }
        if (remainBattery.equals("100")) {
            batteryImageView.setImageResource(R.mipmap.icon_battery_percent_100);
        } else if (remainBattery.equals("90")) {
            batteryImageView.setImageResource(R.mipmap.icon_battery_percent_90);
        } else if (remainBattery.equals("80")) {
            batteryImageView.setImageResource(R.mipmap.icon_battery_percent_80);
        } else if (remainBattery.equals("70")) {
            batteryImageView.setImageResource(R.mipmap.icon_battery_percent_70);
        } else if (remainBattery.equals("60")) {
            batteryImageView.setImageResource(R.mipmap.icon_battery_percent_60);
        } else if (remainBattery.equals("50")) {
            batteryImageView.setImageResource(R.mipmap.icon_battery_percent_50);
        } else if (remainBattery.equals("40")) {
            batteryImageView.setImageResource(R.mipmap.icon_battery_percent_40);
        } else if (remainBattery.equals("30")) {
            batteryImageView.setImageResource(R.mipmap.icon_battery_percent_30);
        } else if (remainBattery.equals("20")) {
            batteryImageView.setImageResource(R.mipmap.icon_battery_percent_20);
        } else if (remainBattery.equals("10")) {
            batteryImageView.setImageResource(R.mipmap.icon_battery_percent_10);
        } else {
            batteryImageView.setImageResource(R.mipmap.icon_battery_percent_0);
        }
    }

    @Override
    public void setListener() {

    }

    @Override
    public void setData() {

    }
}
