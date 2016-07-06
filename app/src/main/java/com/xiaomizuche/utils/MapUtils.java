package com.xiaomizuche.utils;

/**
 * Created by jimmy on 16/1/15.
 */
public class MapUtils {

    /**
     * 根据航向返回反向
     *
     * @param heading
     * @return
     */
    public static String directionStr(int heading) {
        String d = "未知";
        if (heading > 0 && heading < 90) {
            d = "东北方向";
        }
        if (heading == 90) {
            d = "正东方向";
        }
        if (heading > 90 && heading < 180) {
            d = "东南方向";
        }
        if (heading == 180) {
            d = "正南方向";
        }
        if (heading > 180 && heading < 270) {
            d = "西南方向";
        }
        if (heading == 270) {
            d = "正西方向";
        }
        if (heading > 270 && heading < 360) {
            d = "西北方向";
        }
        if (heading == 0 || heading == 360) {
            d = "正北方向";
        }
        return d;
    }
}
