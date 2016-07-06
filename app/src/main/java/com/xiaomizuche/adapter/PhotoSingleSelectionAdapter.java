package com.xiaomizuche.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xiaomizuche.R;
import com.xiaomizuche.bean.ImageItem;
import com.xiaomizuche.utils.DensityUtil;
import com.xiaomizuche.view.ViewHolder;

import org.xutils.image.ImageOptions;
import org.xutils.x;


public class PhotoSingleSelectionAdapter extends TAdapter<ImageItem> {
    private ImageOptions imageOptions;

    public PhotoSingleSelectionAdapter(Context mContext) {
        super(mContext);
        imageOptions = new ImageOptions.Builder()
                .setAutoRotate(true)
                .setLoadingDrawableId(R.mipmap.bg_img)
                .setFailureDrawableId(R.mipmap.load_failure)
                .build();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.adapter_photo_single_selection, null);
        }

        ImageView photo = ViewHolder.get(convertView, R.id.iv_photo);
        //照片宽高为屏幕宽度减掉内外边距
        int width = (DensityUtil.screenWidth() - 32) / 3;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, width);
        photo.setLayoutParams(layoutParams);
        ImageItem item = mList.get(position);
        if (item != null) {
            x.image().bind(photo, item.sourcePath, imageOptions);
        }
        return convertView;
    }
}
