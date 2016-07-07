package com.xiaomizuche.utils;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaomizuche.R;
import com.xiaomizuche.bean.TabIndicator;

import java.util.ArrayList;
import java.util.List;


/**
 * ViewPager工具类, 用于提供ViewPager相关的公用方法
 */
public class ViewPagerUtils {

    // 获取ViewPager的TabIndicator列表
    public static List<TabIndicator> getTabIndicator(Integer number) {
        List<TabIndicator> list = new ArrayList<TabIndicator>();
        for (int i = 0; i < number; i++) {
            TabIndicator indicator = new TabIndicator();
            indicator.type = i;
            list.add(indicator);
        }
        return list;
    }

    /**
     * 变换底部选项卡图标和字体颜色
     *
     * @param context
     * @param index
     * @param textViews
     */
    public static void setBottomBar(Context context, int index, List<TextView> textViews, List<ImageView> imageViews) {
        for (int i = 0; i < textViews.size(); i++) {
            TextView textView = textViews.get(i);
            ImageView imageView = imageViews.get(i);
            if (index == i) {
                textView.setTextColor(context.getResources().getColor(R.color.main_tone));
                switch (i) {
                    case 0:
                        imageView.setImageResource(R.mipmap.icon_menu_home_active);
                        break;
                    case 1:
                        imageView.setImageResource(R.mipmap.icon_menu_hire_car_active);
                        break;
                    case 2:
                        imageView.setImageResource(R.mipmap.icon_menu_shop_active);
                        break;
                    case 3:
                        imageView.setImageResource(R.mipmap.icon_menu_my_active);
                        break;
                }
            } else {
                textView.setTextColor(context.getResources().getColor(R.color.font_gray));
                switch (i) {
                    case 0:
                        imageView.setImageResource(R.mipmap.icon_menu_home);
                        break;
                    case 1:
                        imageView.setImageResource(R.mipmap.icon_menu_hire_car);
                        break;
                    case 2:
                        imageView.setImageResource(R.mipmap.icon_menu_shop);
                        break;
                    case 3:
                        imageView.setImageResource(R.mipmap.icon_menu_my);
                        break;
                }
            }
        }
    }

}
