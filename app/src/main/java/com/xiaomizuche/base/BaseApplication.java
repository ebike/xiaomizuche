package com.xiaomizuche.base;

import android.app.Application;
import android.telephony.TelephonyManager;

import com.tencent.bugly.crashreport.CrashReport;
import com.xiaomizuche.constants.AppConfig;
import com.xiaomizuche.db.XUtil;
import com.xiaomizuche.utils.CommonUtils;

import org.xutils.x;

import cn.jpush.android.api.JPushInterface;

/**
 * 启动应用是执行的类
 * 主要初始化第三方SDK
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化xutils框架
        x.Ext.init(this);
        x.Ext.setDebug(true);
        //初始化DB
        XUtil.initDB(this);
        // 设置开启日志,发布时请关闭日志
        JPushInterface.setDebugMode(true);
        // 初始化 JPush
        JPushInterface.init(this);
        //获取手机imei码
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        AppConfig.imei = TelephonyMgr.getDeviceId();
        //开启bugly
        //初始化bugly crash。上报错误信息
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext()); //App的策略Bean
        strategy.setAppChannel("xiaomizuche");     //设置渠道
        strategy.setAppVersion(CommonUtils.getVersionName(getApplicationContext()));      //App的版本
        CrashReport.initCrashReport(this, "900038260", true, strategy);  //初始化bugly SDK

    }
}
