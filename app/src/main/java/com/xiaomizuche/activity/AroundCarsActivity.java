package com.xiaomizuche.activity;

import android.location.Location;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
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
import com.xiaomizuche.map.JCLocationManager;
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
            aMap.setLocationSource(this);// 设置定位监听
            aMap.setMyLocationEnabled(true);// 可触发定位并显示定位层
            uiSettings = aMap.getUiSettings();
            //不显示缩放按键
            uiSettings.setZoomControlsEnabled(false);
            //显示比例尺
            uiSettings.setScaleControlsEnabled(true);
            uiSettings.setMyLocationButtonEnabled(true); // 显示默认的定位按钮
        }

        // 自定义系统定位小蓝点
//        MyLocationStyle myLocationStyle = new MyLocationStyle();
//        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
//                .fromResource(R.drawable.location_marker));// 设置小蓝点的图标
//
//        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
//        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
//        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
//        aMap.setMyLocationStyle(myLocationStyle);

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

        JCLocationManager.instance().init(this);
        JCLocationManager.instance().start();
        Location location = JCLocationManager.instance().getCurrentLocation();
//        LatLng curPosition = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng curPosition = new LatLng(36.686678, 117.131284);
        CameraPosition cp = new CameraPosition(curPosition, 17, 0, 0);
        CameraUpdate center = CameraUpdateFactory.newCameraPosition(cp);
        aMap.moveCamera(center);
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
        mapView.onDestroy();
    }

    @Override
    public void setListener() {

    }

    @Override
    public void setData() {
        DecimalFormat format = new DecimalFormat("0");
        Location location = JCLocationManager.instance().getCurrentLocation();
        Map<String, String> map = new HashMap<>();
//        map.put("lon", format.format(location.getLongitude() * 1000000));
//        map.put("lat", format.format(location.getLatitude() * 1000000));
        map.put("lon", format.format(117131284));
        map.put("lat", format.format(36686678));
        RequestParams params = DRequestParamsUtils.getRequestParams(HttpConstants.arroundCar(), map);
        DHttpUtils.post_String(this, true, params, new DCommonCallback<String>() {
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

    private void initCar() {
        if (carLocationList != null && carLocationList.size() > 0) {
            for (CarLocationBean bean : carLocationList) {
                //添加障碍物
                MarkerOptions markerOptions = new MarkerOptions();
                LatLng position = new LatLng(bean.getLat() / 1000000.0, bean.getLon() / 1000000.0);
                markerOptions.position(position);
                markerOptions.title(bean.getCarportName()).snippet(bean.getDistance());
                markerOptions.perspective(true);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_ebike_offline));
                aMap.addMarker(markerOptions);
            }
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
            if (mListener != null) {
                amapLocation.setLatitude(36.686678);
                amapLocation.setLongitude(117.131284);
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
            }
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
            }
        }
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
