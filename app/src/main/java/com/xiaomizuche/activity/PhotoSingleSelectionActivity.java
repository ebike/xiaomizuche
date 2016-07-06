package com.xiaomizuche.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.xiaomizuche.R;
import com.xiaomizuche.adapter.PhotoSingleSelectionAdapter;
import com.xiaomizuche.base.BaseActivity;
import com.xiaomizuche.bean.ImageBucket;
import com.xiaomizuche.bean.ImageItem;
import com.xiaomizuche.event.FinishActivityEvent;
import com.xiaomizuche.event.SelectPhotoEvent;
import com.xiaomizuche.view.TopBarView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import de.greenrobot.event.EventBus;

public class PhotoSingleSelectionActivity extends BaseActivity {
    @ViewInject(R.id.top_bar_view)
    TopBarView topBarView;
    @ViewInject(R.id.grid_view)
    GridView gridView;
    //相册对象
    private ImageBucket imageBucket;

    private PhotoSingleSelectionAdapter adapter;

    @Override
    public void loadXml() {
        setContentView(R.layout.activity_photo_single_selection);
        x.view().inject(this);
    }

    @Override
    public void getIntentData(Bundle savedInstanceState) {
        imageBucket = (ImageBucket) getIntent().getSerializableExtra("photoAlbum");
    }

    @Override
    public void init() {
        topBarView.setCenterTextView(imageBucket.bucketName);
        adapter = new PhotoSingleSelectionAdapter(this);
        gridView.setAdapter(adapter);
        adapter.setList(imageBucket.imageList);
    }

    @Override
    public void setListener() {
        //返回
        topBarView.setLeftCallback(new TopBarView.TopBarLeftCallback() {
            @Override
            public void setLeftOnClickListener() {
                PhotoSingleSelectionActivity.this.finish();
            }
        });
        //显示选中照片
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageItem imageItem = (ImageItem) parent.getItemAtPosition(position);
                EventBus.getDefault().post(new SelectPhotoEvent(imageItem));
                EventBus.getDefault().post(new FinishActivityEvent(true));
                PhotoSingleSelectionActivity.this.finish();
            }
        });
    }

    @Override
    public void setData() {

    }

    public void onEvent(FinishActivityEvent event) {
        if (event != null && event.isFinish()) {
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
