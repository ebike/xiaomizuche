package com.xiaomizuche.bean;

/**
 * 周围车辆位置信息
 */
public class CarLocationBean {

    private String carId;
    private int lon;
    private int lat;
    private String carportName;
    private String distance;

    public CarLocationBean() {
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public void setLon(int lon) {
        this.lon = lon;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public void setCarportName(String carportName) {
        this.carportName = carportName;
    }

    public int getLon() {
        return lon;
    }

    public int getLat() {
        return lat;
    }

    public String getCarportName() {
        return carportName;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
