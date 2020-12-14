package com.bupt.aibeauty.activity.beauty;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bupt.aibeauty.R;
import com.bupt.aibeauty.utils.BitmapUtils;
import com.bupt.aibeauty.view.MyFreeView;
import com.bupt.aibeauty.view.MySeekBar;

import java.io.IOException;

public class FreeActivity extends Activity implements MySeekBar.OnSeekBarChangeListener {
    private Context context;
    private ImageView imageView;
    private MySeekBar seekBar;
    private MyFreeView myView;

    private Bitmap origin;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_free);
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
        origin= BitmapUtils.copyBitmap(bitmap);
        imageView.post(new Runnable() {
            @Override
            public void run() {
                myView.setBack(imageView);
                myView.setSeekBar(seekBar);
            }
        });

    }
    private void initView(){
        context=this;
        imageView=findViewById(R.id.free_image);
        seekBar=findViewById(R.id.free_seekbar);seekBar.setProgress(seekBar.getMinValue());
        seekBar.setOnSeekBarChangeListener(this);
        myView=findViewById(R.id.free_myview);
        findViewById(R.id.free_quit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.free_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
                BitmapUtils.saveBitmap(context,bitmap);
            }
        });
    }

    @Override
    public void onSeekBarValueChange(MySeekBar bar, double value) {
        double percent=1+(value-bar.getMinValue())/(bar.getMaxValue()-bar.getMinValue());
        myView.setFreeRange(percent);
    }

    @Override
    public void onSeekBarValueChangeStart(MySeekBar bar) {
        myView.startDrawFreeRange();
    }

    @Override
    public void onSeekBarValueChangeStop(MySeekBar bar) {
        myView.stopDrawFreeRange();
    }
}
