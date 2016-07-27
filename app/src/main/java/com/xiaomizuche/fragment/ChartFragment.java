package com.xiaomizuche.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomizuche.R;
import com.xiaomizuche.activity.MainActivity;
import com.xiaomizuche.base.BaseFragment;
import com.xiaomizuche.bean.DayDataBean;
import com.xiaomizuche.bean.ResponseBean;
import com.xiaomizuche.callback.DCommonCallback;
import com.xiaomizuche.constants.AppConfig;
import com.xiaomizuche.db.XUtil;
import com.xiaomizuche.http.DHttpUtils;
import com.xiaomizuche.http.DRequestParamsUtils;
import com.xiaomizuche.http.HttpConstants;
import com.xiaomizuche.utils.CommonUtils;
import com.xiaomizuche.utils.PreferencesUtil;
import com.xiaomizuche.utils.ReportUtils;
import com.xiaomizuche.view.TopBarView;

import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by jimmy on 15/12/28.
 */
public class ChartFragment extends BaseFragment implements View.OnClickListener {
    @ViewInject(R.id.top_bar)
    TopBarView topBarView;
    @ViewInject(R.id.hsv_chart)
    HorizontalScrollView chartHorizontalScrollView;
    @ViewInject(R.id.bar_chart)
    BarChart barChart;
    @ViewInject(R.id.line_chart)
    LineChart lineChart;
    @ViewInject(R.id.rl_today_mileage)
    RelativeLayout todayMileageRelativeLayout;
    @ViewInject(R.id.rl_average_speed)
    RelativeLayout averageSpeedRelativeLayout;
    @ViewInject(R.id.rl_max_speed)
    RelativeLayout maxSpeedRelativeLayout;
    @ViewInject(R.id.rl_min_speed)
    RelativeLayout minSpeedRelativeLayout;
    @ViewInject(R.id.tv_today_mileage)
    TextView todayMileageTextView;
    @ViewInject(R.id.tv_average_speed)
    TextView averageSpeedTextView;
    @ViewInject(R.id.tv_max_speed)
    TextView maxSpeedTextView;
    @ViewInject(R.id.tv_min_speed)
    TextView minSpeedTextView;
    @ViewInject(R.id.iv_today_mileage)
    ImageView todayMileageImageView;
    @ViewInject(R.id.iv_average_speed)
    ImageView averageSpeedImageView;
    @ViewInject(R.id.iv_max_speed)
    ImageView maxSpeedImageView;
    @ViewInject(R.id.iv_min_speed)
    ImageView minSpeedImageView;
    //标志位，标志已经初始化完成
    private boolean isPrepared;
    //统计数据
    private List<DayDataBean> dayDataBeans;
    //选中的统计(0：里程；1：平均速度；2：最大速度；3：最小速度)
    private int which;
    private Handler handler;
    //初始打开的页面是该页面时，需要在初始化时加载数据
    private int initPosition;

    public ChartFragment() {
    }

    @SuppressLint("ValidFragment")
    public ChartFragment(int initPosition) {
        this.initPosition = initPosition;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        x.view().inject(this, view);
        isPrepared = true;
        if (initPosition == 1) {
            requestDatas();
        }
        initListener();
        return view;
    }

    private void initListener() {
        todayMileageRelativeLayout.setOnClickListener(this);
        averageSpeedRelativeLayout.setOnClickListener(this);
        maxSpeedRelativeLayout.setOnClickListener(this);
        minSpeedRelativeLayout.setOnClickListener(this);
        topBarView.setLeftCallback(new TopBarView.TopBarLeftCallback() {
            @Override
            public void setLeftOnClickListener() {
                ((MainActivity) getActivity()).setMenuToggle();
            }
        });
    }

    @Override
    public void requestDatas() {
        if (!isPrepared || !isVisible || hasLoadedOnce || !isAdded()) {
            return;
        }
        //每天调一次半月的统计数据
        String isUsedDate = PreferencesUtil.getPrefString(getActivity(), AppConfig.IS_USED_DATE, "");
        String isUsedCarId = PreferencesUtil.getPrefString(getActivity(), AppConfig.IS_USED_CARID, "");
//        if (!(isUsedCarId.equals(AppConfig.userInfoBean.getCarId() + "") && isUsedDate.equals(CommonUtils.getCurrentDateString(null)))) {
//            getSomeDayData();
//        }
//        //查询数据库中统计数据
//        try {
//            if (dayDataBeans == null || dayDataBeans.size() == 0) {
//                dayDataBeans = XUtil.db.selector(DayDataBean.class).where("carId", "=", AppConfig.userInfoBean.getCarId()).orderBy("date", false).findAll();
//            }
//        } catch (DbException e) {
//            e.printStackTrace();
//        }
        //请求今日统计数据
        RequestParams params = DRequestParamsUtils.getRequestParams_Header(HttpConstants.getDayDataUrl(CommonUtils.DateToString(new Date(), "yyyy-MM-dd")));
        DHttpUtils.get_String((MainActivity) getActivity(), true, params, new DCommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ResponseBean<DayDataBean> bean = new Gson().fromJson(result, new TypeToken<ResponseBean<DayDataBean>>() {
                }.getType());
                if (bean.getCode() == 1) {
                    try {
                        boolean isUpdate = false;
                        if (dayDataBeans == null || dayDataBeans.size() == 0) {
                            dayDataBeans = new ArrayList<DayDataBean>();
                        } else {
                            if (dayDataBeans.get(dayDataBeans.size() - 1).getDate().equals(bean.getData().getDate())) {
                                isUpdate = true;
                            }
                        }
                        //数据入库
                        if (isUpdate) {
                            dayDataBeans.remove(dayDataBeans.size() - 1);
                            dayDataBeans.add(bean.getData());
                            KeyValue[] keyValues = new KeyValue[4];
                            keyValues[0] = new KeyValue("maxSpeed", bean.getData().getMaxSpeed());
                            keyValues[1] = new KeyValue("minSpeed", bean.getData().getMinSpeed());
                            keyValues[2] = new KeyValue("avgSpeed", bean.getData().getAvgSpeed());
                            keyValues[3] = new KeyValue("mileage", bean.getData().getMileage());
                            XUtil.db.update(bean.getData().getClass(), WhereBuilder.b("date", "=", bean.getData().getDate()), keyValues);
                        } else {
                            dayDataBeans.add(bean.getData());
                            XUtil.db.save(bean.getData());
                        }
                        //初始报表
                        initChart();
                        //赋值今日统计信息
                        initViews(dayDataBeans.size() - 1);
                    } catch (DbException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    showShortText(bean.getErrmsg());
                }
            }
        });

    }

    //每天调一次半月的统计数据
    private void getSomeDayData() {
        RequestParams params = DRequestParamsUtils.getRequestParams_Header(HttpConstants.getSomeDayDataUrl());
        DHttpUtils.get_String((MainActivity) getActivity(), true, params, new DCommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    ResponseBean<List<DayDataBean>> bean = new Gson().fromJson(result, new TypeToken<ResponseBean<List<DayDataBean>>>() {
                    }.getType());
                    if (bean.getCode() == 1) {
                        dayDataBeans = bean.getData();
                        //删除表中数据
                        XUtil.db.delete(DayDataBean.class);
                        //插入新数据
                        for (DayDataBean dataBean : dayDataBeans) {
                            XUtil.db.save(dataBean);
                        }
//                        PreferencesUtil.setPrefString(getActivity(), AppConfig.IS_USED_CARID, AppConfig.userInfoBean.getCarId() + "");
                        PreferencesUtil.setPrefString(getActivity(), AppConfig.IS_USED_DATE, CommonUtils.getCurrentDateString(null));
                    } else {
                        showShortText(bean.getErrmsg());
                    }
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void initChart() throws ParseException {
        initLineChart();
        initBarChart();
        if (which == 0) {
            showLineChart(getLineData());
        } else {
            showBarChart(getBarData());
        }
    }

    private void initViews(int index) {
        try {
            topBarView.setCenterTextView(CommonUtils.changeDateFormat2(dayDataBeans.get(index).getDate()) + "行车记录");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        todayMileageTextView.setText(dayDataBeans.get(index).getMileage() + "公里");
        averageSpeedTextView.setText(dayDataBeans.get(index).getAvgSpeed() + "km/h");
        maxSpeedTextView.setText(dayDataBeans.get(index).getMaxSpeed() + "km/h");
        minSpeedTextView.setText(dayDataBeans.get(index).getMinSpeed() + "km/h");
    }

    private void initLineChart() {
        ReportUtils.setChartLayout(getActivity(), lineChart, dayDataBeans.size());
        // 如果没有数据的时候，会显示这个
        lineChart.setNoDataTextDescription("");
        lineChart.setNoDataText("");
        lineChart.setBackgroundColor(getResources().getColor(R.color.blue));
        lineChart.setDrawBorders(false);  ////是否在折线图上添加边框
        lineChart.setDescription("");// 数据描述
        lineChart.setDrawGridBackground(false); // 是否显示表格颜色
        lineChart.setTouchEnabled(false); // 设置是否可以触摸
        lineChart.setDragEnabled(false);// 是否可以拖拽
        lineChart.setScaleEnabled(false);// 是否可以缩放
        lineChart.setPinchZoom(false);//
        lineChart.setViewPortOffsets(88f, 0, 88f, 80f);
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(0);
        xAxis.setLabelsToSkip(0);
        xAxis.setTextSize(14f);
        xAxis.setYOffset(8f);
        xAxis.setTextColor(getResources().getColor(R.color.white));
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setEnabled(false);
        YAxis rightAxis = lineChart.getAxisRight();
        rightAxis.setEnabled(false);
        Legend mLegend = lineChart.getLegend(); // 设置比例图标示
        mLegend.setEnabled(false);
        lineChart.fitScreen();
    }

    private void showLineChart(LineData lineData) {
        barChart.setVisibility(View.GONE);
        lineChart.setVisibility(View.VISIBLE);
        lineChart.setData(lineData); // 设置数据
        lineChart.animateX(1000); // 立即执行的动画
        scrollToRight(lineChart);
    }

    private void initBarChart() {
        ReportUtils.setChartLayout(getActivity(), barChart, dayDataBeans.size());
        // 如果没有数据的时候，会显示这个
        barChart.setNoDataTextDescription("");
        barChart.setNoDataText("");
        barChart.setBackgroundColor(getResources().getColor(R.color.blue));
        barChart.setDrawBorders(false);  ////是否在折线图上添加边框
        barChart.setDescription("");// 数据描述
        barChart.setDrawGridBackground(false); // 是否显示表格颜色
        barChart.setTouchEnabled(true); // 设置是否可以触摸
        barChart.setDragEnabled(false);// 是否可以拖拽
        barChart.setScaleEnabled(false);// 是否可以缩放
        barChart.setPinchZoom(false);//
        barChart.setViewPortOffsets(0, 0, 0, 80f);
        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int dataSetIndex, Highlight highlight) {
                initViews(entry.getXIndex());
            }

            @Override
            public void onNothingSelected() {

            }
        });
        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawAxisLine(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(0);
        xAxis.setLabelsToSkip(0);
        xAxis.setTextSize(14f);
        xAxis.setYOffset(8f);
        xAxis.setTextColor(getResources().getColor(R.color.white));
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setEnabled(false);
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(false);
        Legend mLegend = barChart.getLegend(); // 设置比例图标示
        mLegend.setEnabled(false);
        barChart.fitScreen();
    }

    private void showBarChart(BarData barData) {
        barChart.setVisibility(View.VISIBLE);
        lineChart.setVisibility(View.GONE);
        barChart.setData(barData); // 设置数据
        barChart.animateXY(1000, 1000); // 立即执行的动画
        scrollToRight(barChart);
    }

    private void scrollToRight(final Chart chart) {
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                chartHorizontalScrollView.smoothScrollTo(chart.getWidth(), 0);
            }
        });
    }

    private LineData getLineData() throws ParseException {
        ArrayList<String> xValues = new ArrayList<String>();
        for (int i = 0; i < dayDataBeans.size(); i++) {
            if (dayDataBeans.get(i).getDate().equals(CommonUtils.getCurrentDateString(null))) {
                xValues.add("今天");
            } else {
                xValues.add(CommonUtils.changeDateFormat1(dayDataBeans.get(i).getDate()));
            }
        }

        ArrayList<Entry> yValues = new ArrayList<Entry>();
        for (int i = 0; i < dayDataBeans.size(); i++) {
            yValues.add(new Entry((float) dayDataBeans.get(i).getMileage(), i));
        }
        LineDataSet lineDataSet = new LineDataSet(yValues, "");
        lineDataSet.setColor(getResources().getColor(R.color.chart_data));// 显示颜色
        lineDataSet.setDrawValues(true);
        lineDataSet.setValueTextColor(getResources().getColor(R.color.white));
        lineDataSet.setValueTextSize(14f);
        lineDataSet.setValueFormatter(ReportUtils.formatterOnePoint);
        lineDataSet.setLineWidth(2f); // 线宽
        lineDataSet.setCircleColorHole(getResources().getColor(R.color.chart_data));
        lineDataSet.setCircleSize(3f);// 显示的圆形大小
        lineDataSet.setCircleColor(getResources().getColor(R.color.chart_data));// 圆形的颜色
        ArrayList<LineDataSet> lineDataSets = new ArrayList<LineDataSet>();
        lineDataSets.add(lineDataSet);
        LineData lineData = new LineData(xValues, lineDataSets);
        return lineData;
    }

    private BarData getBarData() throws ParseException {
        ArrayList<String> xValues = new ArrayList<String>();
        for (int i = 0; i < dayDataBeans.size(); i++) {
            if (dayDataBeans.get(i).getDate().equals(CommonUtils.getCurrentDateString(null))) {
                xValues.add("今天");
            } else {
                xValues.add(CommonUtils.changeDateFormat1(dayDataBeans.get(i).getDate()));
            }
        }
        ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
        for (int i = 0; i < dayDataBeans.size(); i++) {
            switch (which) {
                case 1:
                    yValues.add(new BarEntry((float) dayDataBeans.get(i).getAvgSpeed(), i));
                    break;
                case 2:
                    yValues.add(new BarEntry((float) dayDataBeans.get(i).getMaxSpeed(), i));
                    break;
                case 3:
                    yValues.add(new BarEntry((float) dayDataBeans.get(i).getMinSpeed(), i));
                    break;
            }
        }
        // y轴的数据集合
        BarDataSet barDataSet = new BarDataSet(yValues, "");
        barDataSet.setColor(getResources().getColor(R.color.chart_data));// 显示颜色
        barDataSet.setDrawValues(true);
        barDataSet.setValueTextColor(getResources().getColor(R.color.white));
        barDataSet.setValueTextSize(14f);
        barDataSet.setValueFormatter(ReportUtils.formatterOnePoint);
        barDataSet.setHighLightColor(getResources().getColor(R.color.chart_data));
        ArrayList<BarDataSet> barDataSets = new ArrayList<BarDataSet>();
        barDataSets.add(barDataSet);
        barDataSet.setBarSpacePercent(30);
        BarData barData = new BarData(xValues, barDataSets);
        return barData;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_today_mileage:
                todayMileageRelativeLayout.setBackgroundColor(getResources().getColor(R.color.chart_select));
                averageSpeedRelativeLayout.setBackgroundColor(getResources().getColor(R.color.chart_unselect));
                maxSpeedRelativeLayout.setBackgroundColor(getResources().getColor(R.color.chart_unselect));
                minSpeedRelativeLayout.setBackgroundColor(getResources().getColor(R.color.chart_unselect));
                todayMileageImageView.setVisibility(View.VISIBLE);
                averageSpeedImageView.setVisibility(View.GONE);
                maxSpeedImageView.setVisibility(View.GONE);
                minSpeedImageView.setVisibility(View.GONE);
                which = 0;
                //初始报表
                try {
                    showLineChart(getLineData());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.rl_average_speed:
                todayMileageRelativeLayout.setBackgroundColor(getResources().getColor(R.color.chart_unselect));
                averageSpeedRelativeLayout.setBackgroundColor(getResources().getColor(R.color.chart_select));
                maxSpeedRelativeLayout.setBackgroundColor(getResources().getColor(R.color.chart_unselect));
                minSpeedRelativeLayout.setBackgroundColor(getResources().getColor(R.color.chart_unselect));
                todayMileageImageView.setVisibility(View.GONE);
                averageSpeedImageView.setVisibility(View.VISIBLE);
                maxSpeedImageView.setVisibility(View.GONE);
                minSpeedImageView.setVisibility(View.GONE);
                which = 1;
                //初始报表
                try {
                    showBarChart(getBarData());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.rl_max_speed:
                todayMileageRelativeLayout.setBackgroundColor(getResources().getColor(R.color.chart_unselect));
                averageSpeedRelativeLayout.setBackgroundColor(getResources().getColor(R.color.chart_unselect));
                maxSpeedRelativeLayout.setBackgroundColor(getResources().getColor(R.color.chart_select));
                minSpeedRelativeLayout.setBackgroundColor(getResources().getColor(R.color.chart_unselect));
                todayMileageImageView.setVisibility(View.GONE);
                averageSpeedImageView.setVisibility(View.GONE);
                maxSpeedImageView.setVisibility(View.VISIBLE);
                minSpeedImageView.setVisibility(View.GONE);
                which = 2;
                //初始报表
                try {
                    showBarChart(getBarData());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.rl_min_speed:
                todayMileageRelativeLayout.setBackgroundColor(getResources().getColor(R.color.chart_unselect));
                averageSpeedRelativeLayout.setBackgroundColor(getResources().getColor(R.color.chart_unselect));
                maxSpeedRelativeLayout.setBackgroundColor(getResources().getColor(R.color.chart_unselect));
                minSpeedRelativeLayout.setBackgroundColor(getResources().getColor(R.color.chart_select));
                todayMileageImageView.setVisibility(View.GONE);
                averageSpeedImageView.setVisibility(View.GONE);
                maxSpeedImageView.setVisibility(View.GONE);
                minSpeedImageView.setVisibility(View.VISIBLE);
                which = 3;
                //初始报表
                try {
                    showBarChart(getBarData());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
