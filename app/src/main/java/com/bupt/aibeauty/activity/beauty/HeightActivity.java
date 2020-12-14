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
import com.bupt.aibeauty.utils.ImageTransformUtils;
import com.bupt.aibeauty.view.MyHeightView;
import com.bupt.aibeauty.view.MySeekBar;

import java.io.IOException;

public class HeightActivity extends Activity implements MySeekBar.OnSeekBarChangeListener{
    private Context context;
    private ImageView imageView;
    private MySeekBar seekBar;
    private MyHeightView myView;

    private Bitmap origin,deform;
    private int y1,y2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_height);
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
        imageView=findViewById(R.id.height_image);
        seekBar=findViewById(R.id.height_seekbar);
        seekBar.setStyle(MySeekBar.CENTER_STYLE);
        seekBar.setProgress(0);
        seekBar.setOnSeekBarChangeListener(this);
        myView=findViewById(R.id.height_myview);
        findViewById(R.id.height_quit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.height_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
                BitmapUtils.saveBitmap(context,bitmap);
            }
        });
    }

    @Override
    public void onSeekBarValueChange(MySeekBar bar, double value) {
        double line1Location=myView.getLine1Location();
        double line2Location=myView.getLine2Location();
        int y= (int) (origin.getHeight()*Math.min(line1Location,line2Location));
        int areaHeight= (int) (origin.getHeight()*Math.abs(line2Location-line1Location));
        //0-1 -> 0.5-1.5
        float mapMin=0.7f,mapMax=1.3f;
        float mapDegree= (float) (mapMin+(mapMax-mapMin)*(value-bar.getMinValue())/(bar.getMaxValue()-bar.getMinValue()));

        deform= ImageTransformUtils.changeHeight(origin,y,areaHeight,mapDegree);
        imageView.setImageBitmap(deform);
    }

    @Override
    public void onSeekBarValueChangeStart(MySeekBar bar) {
        double line1Location=myView.getLine1Location();
        double line2Location=myView.getLine2Location();
        Bitmap current=((BitmapDrawable)(imageView.getDrawable())).getBitmap();
        y1= (int) (current.getHeight()*Math.min(line1Location,line2Location));
        y2= (int) ((1-Math.max(line1Location,line2Location))*current.getHeight());
        myView.startChangeHeight();
    }

    @Override
    public void onSeekBarValueChangeStop(MySeekBar bar) {
        myView.endChangeHeight();
        myView.setLine1Location(1.0*y1/deform.getHeight());
        myView.setLine2Location(1-1.0*y2/deform.getHeight());
    }
}