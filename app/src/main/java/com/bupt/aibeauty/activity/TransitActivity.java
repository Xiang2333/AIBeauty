package com.bupt.aibeauty.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bupt.aibeauty.R;
import com.yalantis.ucrop.UCrop;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class TransitActivity extends Activity {

    private ImageView imageView;
    private Button goSlim;
    private Button goFilter;
    private Button goCrop;

    private Button goBack;
    private Button ok;


    private Context context;
    private Uri uri;
    private boolean fromCrop=false;
    private Uri origin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transit);
        context=this;
        initView();
        String img_path=getIntent().getStringExtra("img_path");
        String from=getIntent().getStringExtra("from");
        if ("crop".equals(from)){
            fromCrop=true;
            this.origin=Uri.parse(getIntent().getStringExtra("origin_path"));
        }
        //解析出图像的uri
        uri=Uri.parse(img_path);
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            imageView.setImageBitmap(bitmap);
            imageView.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void initView(){
        imageView=findViewById(R.id.photo_taken);

        Drawable slim=getDrawable(R.drawable.ic_camera_beauty);
        slim.setBounds(0,0,50,50);
        goSlim=findViewById(R.id.goSlim);
        goSlim.setCompoundDrawables(null,slim,null,null);
        goSlim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.putExtra("img_path",uri.toString());
                intent.setClass(context,AlbumActivity.class);
                startActivity(intent);
                finish();
            }
        });


        Drawable filter=getDrawable(R.drawable.ic_camera_filter);
        filter.setBounds(0,0,50,50);
        goFilter=findViewById(R.id.goFilter);
        goFilter.setCompoundDrawables(null,filter,null,null);
        goFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.putExtra("img_path",uri.toString());
                intent.setClass(context,FilterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Drawable crop=getDrawable(com.yalantis.ucrop.R.drawable.ucrop_crop);
        crop.setBounds(0,0,50,50);
        goCrop=findViewById(R.id.goCrop);
        goCrop.setCompoundDrawables(null,crop,null,null);
        Activity thisAcc=this;
        goCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(fromCrop){
                    UCrop.Options options=new UCrop.Options();
                    options.setFreeStyleCropEnabled(true);
                    options.setCompressionQuality(100);
                    UCrop uCrop=UCrop.of(origin,uri).withOptions(options);
                    uCrop.start(thisAcc);
                }else{
                    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), "AIBeautyTemp");
                    if (!mediaStorageDir.exists()) {
                        mediaStorageDir.mkdirs();
                    }
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
                    File pictureFile = new File(mediaStorageDir.getPath() + File.separator +
                            "IMG_CROP" + timeStamp + ".jpg");

                    UCrop.Options options=new UCrop.Options();
                    options.setFreeStyleCropEnabled(true);
                    options.setCompressionQuality(100);
                    UCrop uCrop=UCrop.of(uri,Uri.fromFile(pictureFile)).withOptions(options);
                    uCrop.start(thisAcc);
                }
            }
        });

        goBack=findViewById(R.id.goBack);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "AIBeautyTemp");
                if (mediaStorageDir.exists()){
                    for(File file:mediaStorageDir.listFiles()){
                        file.delete();
                    }
                }
                ((Activity)context).finish();
            }
        });

        ok=findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    BufferedInputStream bufIn=new BufferedInputStream(new FileInputStream(new File(uri.getPath())));
                    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES), "AIBeauty");
                    if (!mediaStorageDir.exists()) {
                        mediaStorageDir.mkdirs();
                    }
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
                    File pictureFile = new File(mediaStorageDir.getPath() + File.separator +
                            "IMG" + timeStamp + ".jpg");
                    BufferedOutputStream bufOut=new BufferedOutputStream(new FileOutputStream(pictureFile));

                    byte[] buf=new byte[10*1024];
                    int len=-1;
                    while((len=bufIn.read(buf))>-1){
                        bufOut.write(buf,0,len);
                    }
                    bufOut.flush();
                    bufIn.close();
                    bufOut.close();
                } catch (Exception e) {
                    Log.e("transit",e.getMessage());
                }
                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "AIBeautyTemp");
                if (mediaStorageDir.exists()){
                    for(File file:mediaStorageDir.listFiles()){
                        file.delete();
                    }
                }
                Toast.makeText(context,"保存成功！",Toast.LENGTH_SHORT).show();
                ((Activity)context).finish();
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP&&null!=data) {
            final Uri resultUri = UCrop.getOutput(data);
            this.uri=resultUri;
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),resultUri);
                imageView.setImageBitmap(bitmap);
                imageView.invalidate();
            } catch (Exception e) {
                Log.e("transit",e.getMessage());
            }
        }
    }
}
