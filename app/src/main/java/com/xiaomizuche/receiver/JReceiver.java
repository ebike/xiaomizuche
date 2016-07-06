package com.xiaomizuche.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.xiaomizuche.activity.MainActivity;
import com.xiaomizuche.bean.ReceiveExtraBean;
import com.xiaomizuche.constants.AppConfig;
import com.xiaomizuche.event.OnlineExceptionEvent;
import com.xiaomizuche.event.RemoteVFEvent;
import com.xiaomizuche.utils.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;
import de.greenrobot.event.EventBus;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
            ReceiveExtraBean receiveExtraBean = new Gson().fromJson(extra, ReceiveExtraBean.class);
            int eventType = receiveExtraBean.eventType;
            String msg = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            //如果在后台运行
            if (CommonUtils.isBackground(context)) {
                AppConfig.isDisabled = true;
                AppConfig.eventType = eventType;
                AppConfig.eventMsg = msg;
            } else {
                switch (eventType) {
                    case 8://电子围栏关闭超时
                        EventBus.getDefault().post(new RemoteVFEvent("1", msg));
                        break;
                    case 7://电子围栏关闭成功
                        EventBus.getDefault().post(new RemoteVFEvent("0", msg));
                        break;
                    case 6://电子围栏开启超时
                        EventBus.getDefault().post(new RemoteVFEvent("0", msg));
                        break;
                    case 5://电子围栏开启成功
                        EventBus.getDefault().post(new RemoteVFEvent("1", msg));
                        break;
                    case 10://账号在其他设备登录被迫下线
                        EventBus.getDefault().post(new OnlineExceptionEvent(true, msg));
                        break;
                    default:
                        break;
                }
            }

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
            ReceiveExtraBean receiveExtraBean = new Gson().fromJson(extra, ReceiveExtraBean.class);
            int eventType = receiveExtraBean.eventType;
            int fragmentPosition = 0;
            if (eventType == 9) {//报警消息
                fragmentPosition = 2;
            } else if (eventType == 11) {//您昨日的统计数据已生成，请点击查看
                fragmentPosition = 1;
            }
            Intent i = new Intent(context, MainActivity.class);
            i.putExtras(bundle);
            i.putExtra("fragmentPosition", fragmentPosition);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }
}
