package com.xiaomizuche.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaomizuche.R;
import com.xiaomizuche.view.ZoomImageView;

import org.xutils.common.Callback;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.IOException;

/**
 * Created by jimmy on 15/12/8.
 */
public class ImageUtils {

    /**
     * xutils异步加载ZoomImageView图片
     *
     * @param context
     * @param zoomImageView
     * @param url
     */
    public static Callback.Cancelable setZoomImageView(final Context context, final ZoomImageView zoomImageView, String url) {
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setAutoRotate(true)
                .setLoadingDrawableId(R.mipmap.bg_img)
                .setFailureDrawableId(R.mipmap.load_failure)
                .build();
        Callback.Cancelable cancelable = x.image().loadDrawable(url, imageOptions, new Callback.CommonCallback<Drawable>() {
            @Override
            public void onSuccess(Drawable drawable) {
                zoomImageView.setImageDrawable(drawable);
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                zoomImageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.load_failure));
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
        return cancelable;
    }

    /* 旋转图片
   * @param angle
   * @param bitmap
   * @return Bitmap
   */
    public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 读取图片属性：旋转的角度
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree  = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 设置圆形头像
     *
     * @param headerImageView
     */
    public static void setCircularHeader(String url, ImageView headerImageView) {
        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions optionsHeadIcon = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.icon_default_head)         //加载开始默认的图片
                .showImageForEmptyUri(R.mipmap.icon_default_head)     //url爲空會显示该图片，自己放在drawable里面的
                .showImageOnFail(R.mipmap.icon_default_head)                //加载图片出现问题，会显示该图片
                .cacheOnDisc()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)//缓存用
                //.displayer(new RoundedBitmapDisplayer(90))       //图片圆角显示，值为整数
                .build();
        imageLoader.displayImage(url, headerImageView, optionsHeadIcon);
    }
}
