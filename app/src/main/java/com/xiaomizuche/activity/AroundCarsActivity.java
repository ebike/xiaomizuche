package com.xiaomizuche.activity;

import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomizuche.R;
import com.xiaomizuche.base.BaseActivity;
import com.xiaomizuche.bean.CarLocationBean;
import com.xiaomizuche.bean.ResponseBean;
import com.xiaomizuche.callback.DCommonCallback;
import com.xiaomizuche.http.DHttpUtils;
import com.xiaomizuche.http.DRequestParamsUtils;
import com.xiaomizuche.http.HttpConstants;
import com.xiaomizuche.utils.T;

import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AroundCarsActivity extends BaseActivity
        implements LocationSource, AMapLocationListener, AMap.OnMarkerClickListener, AMap.OnInfoWindowClickListener {

    @ViewInject(R.id.map_view)
    MapView mapView;

    private AMap aMap;
    private UiSettings uiSettings;

    private List<CarLocationBean> carLocationList;

    private OnLocationChangedListener mListener;
    public AMapLocationClient mlocationClient = null;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    private boolean isExecuted;

    @Override
    public void loadXml() {
        setContentView(R.layout.activity_around_cars);
    }

    @Override
    public void getIntentData(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);
    }

    @Override
    public void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setOnMarkerClickListener(this);
            aMap.setOnInfoWindowClickListener(this);
            // 设置定位监听
            aMap.setLocationSource(this);
            // 可触发定位并显示定位层
            aMap.setMyLocationEnabled(true);
            uiSettings = aMap.getUiSettings();
            //不显示缩放按键
            uiSettings.setZoomControlsEnabled(false);
            //显示比例尺
            uiSettings.setScaleControlsEnabled(true);
            // 显示默认的定位按钮
            uiSettings.setMyLocationButtonEnabled(true);
        }

        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != mlocationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            mlocationClient.onDestroy();
            mlocationClient = null;
            mLocationOption = null;
        }
        mapView.onDestroy();
    }

    @Override
    public void setListener() {

    }

    @Override
    public void setData() {

    }

    private void initCar() {
        if (carLocationList != null && carLocationList.size() > 0) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (CarLocationBean bean : carLocationList) {
                //添加障碍物
                MarkerOptions markerOptions = new MarkerOptions();
                LatLng position = new LatLng(bean.getLat() / 1000000.0, bean.getLon() / 1000000.0);
                markerOptions.position(position);
                markerOptions.title(bean.getCarportName()).snippet(bean.getDistance());
                markerOptions.perspective(true);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_ebike_offline));
                aMap.addMarker(markerOptions);

                builder.include(position);
            }
            LatLngBounds bounds = builder.build();
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (marker != null && !marker.isInfoWindowShown()) {
            marker.showInfoWindow();
        } else {
            marker.hideInfoWindow();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker != null && !marker.isInfoWindowShown()) {
            marker.showInfoWindow();
        } else {
            marker.hideInfoWindow();
        }
        return true;
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                if (!isExecuted) {
                    isExecuted = true;
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
                    getCarInfo(amapLocation.getLongitude(), amapLocation.getLatitude());
                    LatLng curPosition = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(curPosition));
                }
                if (mListener != null) {
                    mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                }
            } else {
                T.showShort(this, "定位失败");
            }
        }
    }

    private void getCarInfo(double longitude, double latitude) {
        DecimalFormat format = new DecimalFormat("0");
        Map<String, String> map = new HashMap<>();
        map.put("lon", format.format(longitude * 1000000));
        map.put("lat", format.format(latitude * 1000000));
        RequestParams params = DRequestParamsUtils.getRequestParams(HttpConstants.arroundCar(), map);
        DHttpUtils.post_String(this, false, params, new DCommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ResponseBean<List<CarLocationBean>> responseBean = new Gson().fromJson(result, new TypeToken<ResponseBean<List<CarLocationBean>>>() {
                }.getType());
                if (responseBean.getCode() == 1
                        && responseBean.getData() != null
                        && responseBean.getData().size() > 0) {
                    carLocationList = responseBean.getData();
                    initCar();
                } else {
                    T.showShort(AroundCarsActivity.this, responseBean.getErrmsg());
                }
            }
        });
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
    }

}
