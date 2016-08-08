package com.xiaomizuche.http;

import com.xiaomizuche.constants.AppConfig;

/**
 * 接口地址
 */
public class HttpConstants {

    //接口前缀
    public static String baseUrl = "http://www.gnets.cn:8088/xmzc_api/app/";

    //登录
    public static String getLoginUrl() {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("checkLogin.do");
        return sb.toString();
    }

    //注册
    public static String getRegUser() {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("user/regUser.do");
        return sb.toString();
    }

    //完善资料
    public static String perfectUserData() {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("user/perfectUserData.do");
        return sb.toString();
    }

    //根据省市区获取学校接口
    public static String getSchools() {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("user/getSchools.do");
        return sb.toString();
    }

    //退出账号
    public static String getLogoutUrl() {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("logout.do");
        sb.append("?userId=").append(AppConfig.userInfoBean.getUserId());
        return sb.toString();
    }

    //租车接口
    public static String hireCar() {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("car/hireCar.do");
        return sb.toString();
    }

    //获取用户当前租车信息
    public static String getUserCarRecord() {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("car/getUserCarRecord.do");
        sb.append("?userId=").append(AppConfig.userInfoBean.getUserId());
        return sb.toString();
    }

    //修改用户资料接口
    public static String getUpdateUserUrl() {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("user/updateUserData.do");
        return sb.toString();
    }

    //修改用户头像接口
    public static String updateHeadPic() {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("user/updateHeadPic.do");
        return sb.toString();
    }

    //还车接口
    public static String backCar() {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("car/backCar.do");
        return sb.toString();
    }

    //远程锁车
    public static String getlockBikeUrl(String type) {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("lock/lockBike.do");
        sb.append("?carId=").append(AppConfig.userInfoBean.getCarRecord().getCarId());
        sb.append("&userId=").append(AppConfig.userInfoBean.getUserId());
        sb.append("&para=").append(type);
        return sb.toString();
    }

    //远程解锁
    public static String getUnLockBikeUrl(String type) {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("lock/unLockBike.do");
        sb.append("?carId=").append(AppConfig.userInfoBean.getCarRecord().getCarId());
        sb.append("&userId=").append(AppConfig.userInfoBean.getUserId());
        sb.append("&para=").append(type);
        return sb.toString();
    }

    //获取车辆位置信息
    public static String getLocInfoUrl() {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("map/getLocInfo.do");
        sb.append("?carId=").append(AppConfig.userInfoBean.getCarRecord().getCarId());
        sb.append("&userId=").append(AppConfig.userInfoBean.getUserId());
        return sb.toString();
    }

    //根据起止时间查询轨迹信息
    public static String getTrackInfoUrl(String startTime, String endTime) {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("map/searchTrack.do");
        sb.append("?carId=").append(AppConfig.userInfoBean.getCarRecord().getCarId());
        sb.append("&userId=").append(AppConfig.userInfoBean.getUserId());
        sb.append("&startTime=").append(startTime);
        sb.append("&endTime=").append(endTime);
        return sb.toString().replace(" ", "%20");
    }

    //开启电子围栏
    public static String getOpenVfUrl(double lon, double lat, double maxLon, double maxLat, double minLon, double minLat) {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("vf/openVf.do");
        sb.append("?carId=").append(AppConfig.userInfoBean.getCarRecord().getCarId());
        sb.append("&userId=").append(AppConfig.userInfoBean.getUserId());
        sb.append("&maxLon=").append(maxLon);
        sb.append("&maxLat=").append(maxLat);
        sb.append("&minLon=").append(minLon);
        sb.append("&minLat=").append(minLat);
        sb.append("&lon=").append(lon);
        sb.append("&lat=").append(lat);
        return sb.toString();
    }

    //关闭电子围栏
    public static String getCloseVfUrl() {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("vf/closeVf.do");
        sb.append("?carId=").append(AppConfig.userInfoBean.getCarRecord().getCarId());
        sb.append("&userId=").append(AppConfig.userInfoBean.getUserId());
        return sb.toString();
    }

    //根据手机号验证用户是否存在接口
    public static String checkUserByPhone(String phone){
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("user/checkUserByPhone.do");
        sb.append("?phone=").append(phone);
        return sb.toString();
    }

    //重置密码接口
    public static String resetPassword(){
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("user/resetPassword.do");
        return sb.toString();
    }

    //获取附近车辆接口
    public static String arroundCar(){
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("map/arroundCar.do");
        return sb.toString();
    }

    //---------------------------------以上为小米租车------------------------------------------


    //获取用户资料接口
    public static String getUserInfo() {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("user/getUserInfo.do");
        sb.append("?userId=").append(AppConfig.userInfoBean.getUserId());
        return sb.toString();
    }

    //返回车辆基本信息
    public static String getCarInfoUrl() {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("/car/getCarInfo.do");
//        sb.append("?carId=").append(AppConfig.userInfoBean.getCarId());
        return sb.toString();
    }

    //根据用户输入IMEI后八位自动补全IMEI
    public static String getSearchCarLikeImei(String imei) {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("/car/searchCarLikeImei.do");
        sb.append("?imei=").append(imei);
        return sb.toString();
    }

    //验证数据卡号
    public static String getCheckTelNumUrl(String telNum) {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("/car/checkTelNum.do");
        sb.append("?telNum=").append(telNum);
        return sb.toString();
    }

    //获取每日统计数据
    public static String getDayDataUrl(String date) {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("/chart/getDayData.do");
//        sb.append("?carId=").append(AppConfig.userInfoBean.getCarId());
        sb.append("&date=").append(date);
        return sb.toString();
    }

    //获取指定天数的统计数据
    public static String getSomeDayDataUrl() {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("/chart/getSomeDayData.do");
//        sb.append("?carId=").append(AppConfig.userInfoBean.getCarId());
        sb.append("&dayNum=").append(15);
        return sb.toString();
    }

    //查询报警消息
    public static String getNewAlarmEventInfo(int mark, int eventId) {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("/alarm/getNewAlarmEventInfo.do");
//        sb.append("?carId=").append(AppConfig.userInfoBean.getCarId());
        sb.append("&mark=").append(mark);
        sb.append("&eventId=").append(eventId);
        return sb.toString();
    }

    //查看报警消息
    public static String viewAlarmEvent(int eventId) {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("/alarm/viewAlarmEvent.do");
        sb.append("?eventId=").append(eventId);
//        sb.append("&carId=").append(AppConfig.userInfoBean.getCarId());
        return sb.toString();
    }

    //在线预订
    public static String saveOnlineBookUrl() {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("/book/saveOnlineBook.do");
        return sb.toString();
    }

    //修改车辆资料
    public static String getUpdateCarUrl() {
        StringBuffer sb = new StringBuffer(baseUrl);
        sb.append("/car/updateCar.do");
        return sb.toString();
    }

}
