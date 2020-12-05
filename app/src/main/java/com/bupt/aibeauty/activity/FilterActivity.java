package com.bupt.aibeauty.activity;

import android.app.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.aibeauty.R;
import com.bupt.aibeauty.adapter.FilterRecyAdapter;
import com.bupt.aibeauty.utils.FilterUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import jp.co.cyberagent.android.gpuimage.GPUImageView;

public class FilterActivity extends Activity {
    private Context context;
    private GPUImageView imageView;
    private RecyclerView filter_recy;
    private FilterRecyAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        this.context=context;
        initView();
        String img_path=getIntent().getStringExtra("img_path");
        //解析出图像的uri
        Uri uri=Uri.parse(img_path);
        Bitmap bitmap=null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            imageView.setImage(bitmap);
        } catch (Exception e) {
            Log.e("filteracc",e.toString());
        }

    }
    private void initView(){
        imageView=findViewById(R.id.gpuimageview);
        filter_recy=findViewById(R.id.filter_recy);
        adapter=new FilterRecyAdapter(imageView,FilterUtils.getFilters(this),FilterUtils.getFilterImages(this),FilterUtils.getFilterNames(this));
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        filter_recy.setLayoutManager(linearLayoutManager);
        filter_recy.setAdapter(adapter);

        findViewById(R.id.filter_act_goBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.filter_act_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "AIBeauty");
                if(!mediaStorageDir.exists()){
                    mediaStorageDir.mkdir();
                }
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
                File pictureFile = new File(mediaStorageDir.getPath() + File.separator +
                        "IMG" + timeStamp + ".jpg");
                imageView.saveToPictures(mediaStorageDir.getName(), pictureFile.getName(), new GPUImageView.OnPictureSavedListener() {
                    @Override
                    public void onPictureSaved(Uri uri) {
                        Toast.makeText(context,"保存成功！",Toast.LENGTH_SHORT).show();
                        Log.d("gpuImage",mediaStorageDir.getPath()+"  "+pictureFile.getName());
                        Log.d("gpuImage",uri.getPath());
                    }
                });
            }
        });
    }
}