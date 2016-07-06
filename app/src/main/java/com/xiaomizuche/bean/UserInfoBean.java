package com.xiaomizuche.bean;

import java.io.Serializable;

/**
 * Created by jimmy on 16/3/4.
 */
public class UserInfoBean implements Serializable{
    private int carId;
    private long imei;
    private String userId;
    private String telNum;
    private String userName;
    private int sex;
    private String province;
    private String city;
    private String area;
    private String address;
    private String phone;
    private String workPhone;
    private String activeDate;
    private String expireDate;
    private String userToken;
    private String orgName;

    public UserInfoBean() {
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public void setImei(long imei) {
        this.imei = imei;
    }

    public void setTelNum(String telNum) {
        this.telNum = telNum;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public void setActiveDate(String activeDate) {
        this.activeDate = activeDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public int getCarId() {
        return carId;
    }

    public long getImei() {
        return imei;
    }

    public String getTelNum() {
        return telNum;
    }

    public String getUserName() {
        return userName;
    }

    public int getSex() {
        return sex;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getArea() {
        return area;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public String getActiveDate() {
        return activeDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public String getUserToken() {
        return userToken;
    }

    public String getOrgName() {
        return orgName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
