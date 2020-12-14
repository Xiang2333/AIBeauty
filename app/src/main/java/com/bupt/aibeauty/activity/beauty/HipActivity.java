package com.bupt.aibeauty.activity.beauty;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.bupt.aibeauty.R;

import java.io.IOException;

public class HipActivity extends Activity {
    private Context context;
    private ImageView imageView;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_hip);
        initView();
        String img_path=getIntent().getStringExtra("img_path");
        Uri uri=Uri.parse(img_path);
        Bitmap bitmap=null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
        } catch (IOException e) {
            Log.e("AlbumActivity","get bitmap error");
        }
        imageView.setImageBitmap(bitmap);

    }
    private void initView(){
        context=this;
        imageView=findViewById(R.id.hip_image);
    }
}
