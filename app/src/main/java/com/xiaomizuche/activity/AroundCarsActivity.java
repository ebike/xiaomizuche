package com.xiaomizuche.activity;

import android.location.Location;
import android.os.Bundle;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AroundCarsActivity extends BaseActivity {

    @ViewInject(R.id.map_view)
    MapView mapView;

    private AMap aMap;
    private UiSettings uiSettings;

    private List<CarLocationBean> carLocationList;

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
            uiSettings = aMap.getUiSettings();
            //不显示缩放按键
            uiSettings.setZoomControlsEnabled(false);
            //显示比例尺
            uiSettings.setScaleControlsEnabled(true);
        }
        JCLocationManager.instance().init(this);
        JCLocationManager.instance().start();
        Location location = JCLocationManager.instance().getCurrentLocation();
        LatLng curPosition = new LatLng(location.getLatitude(), location.getLongitude());
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
        map.put("lon", format.format(location.getLongitude() * 1000000));
        map.put("lat", format.format(location.getLatitude() * 1000000));
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
                markerOptions.perspective(true);
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_ebike_offline));
                aMap.addMarker(markerOptions);
            }
        }
    }

}
