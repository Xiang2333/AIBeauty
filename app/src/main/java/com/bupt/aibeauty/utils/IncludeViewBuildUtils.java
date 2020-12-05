package com.bupt.aibeauty.utils;

import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.bupt.aibeauty.R;
import com.bupt.aibeauty.view.MySeekBar;

public class IncludeViewBuildUtils {
    public static LinearLayout buildCenterSeekBarWithName(String name, MySeekBar.OnSeekBarChangeListener listener, RelativeLayout parent, int width){
        return IncludeViewBuildUtils.buildSeekBarWithName(name,listener,parent,width,MySeekBar.CENTER_STYLE);
    }
    public static LinearLayout buildDefaultSeekBarWithName(String name, MySeekBar.OnSeekBarChangeListener listener, RelativeLayout parent, int width){
        return IncludeViewBuildUtils.buildSeekBarWithName(name,listener,parent,width,MySeekBar.DEFAULT_STYLE);
    }
    //构建SeekBar时最终调用的方法
    private static LinearLayout buildSeekBarWithName(String name, MySeekBar.OnSeekBarChangeListener listener, RelativeLayout parent, int width,boolean style){
        //seeker为一个 增高字符串+seekBar
        LinearLayout seeker=new LinearLayout(parent.getContext());
        seeker.setOrientation(LinearLayout.HORIZONTAL);
        seeker.setGravity(Gravity.CENTER);
        //显示功能名 比如瘦身
        TextView showType=new TextView(parent.getContext());
        showType.setTextColor(Color.BLACK);
        showType.setGravity(Gravity.LEFT);
        //showType.setEms(4);
        showType.append(name);
        showType.setEnabled(false);
        showType.setWidth(120);

        LinearLayout.LayoutParams textParams=new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        seeker.addView(showType,textParams);

        MySeekBar seekBar=new MySeekBar(parent.getContext());
        if(ViewUtils.flag){
            ViewUtils.flag=false;
            ViewUtils.slimSeekBar.add(seekBar);
        }
        if(style==MySeekBar.CENTER_STYLE){
            seekBar.setStyle(MySeekBar.CENTER_STYLE);
        }else{
            seekBar.setStyle(MySeekBar.DEFAULT_STYLE);
        }
        //SeekBar seekBar=new SeekBar(parent.getContext());
        //设置seekbar在屏幕上的宽度
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(width, -1);
        lp.gravity=Gravity.CENTER_HORIZONTAL;
        seekBar.setLayoutParams(lp);

        //设定初始进度为50%
        seekBar.setProgress(0.5);
        seekBar.setOnSeekBarChangeListener(listener);
        //Log.d("includeutils",ViewUtils.screenHeight+" -- "+ViewUtils.imageView.getBottom());
        int remain=ViewUtils.screenHeight-ViewUtils.imageView.getBottom();
        int padding=remain/40;
        //Log.d("includeutils",ViewUtils.screenHeight+" -- "+ViewUtils.imageView.getBottom()+"   "+padding);
        seeker.addView(seekBar);
        seeker.setPadding(0,padding,0,padding);
        seeker.invalidate();
        return seeker;
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static RelativeLayout buildButtons(String name, Button.OnClickListener quitListener, Button.OnClickListener saveListener, View parent){
        RelativeLayout buttons=new RelativeLayout(parent.getContext());
        buttons.setGravity(Gravity.CENTER_HORIZONTAL);
        //buttons.setOrientation(LinearLayout.HORIZONTAL);
        //buttons.setGravity(Gravity.CENTER_HORIZONTAL);

        ImageButton quit=new ImageButton(parent.getContext());
        RelativeLayout.LayoutParams params1=new RelativeLayout.LayoutParams(
            80,80
        );
        params1.leftMargin=50;
        params1.bottomMargin=30;
        params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);

        quit.setLayoutParams(params1);
        quit.setBackground(parent.getContext().getDrawable(R.drawable.quit));
        quit.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        //quit.setText("退出");
        quit.setOnClickListener(quitListener);
        buttons.addView(quit);

        TextView funcName=new TextView(parent.getContext());
        funcName.setTextSize(18);
        RelativeLayout.LayoutParams params2=new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        params2.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        params2.bottomMargin=40;
        funcName.setLayoutParams(params2);
        funcName.setTextColor(Color.BLACK);
        funcName.append(name);
        funcName.setEnabled(false);
        funcName.setPadding(20,0,20,0);
        buttons.addView(funcName);

        ImageButton save=new ImageButton(parent.getContext());
        RelativeLayout.LayoutParams params3=new RelativeLayout.LayoutParams(
                80,80
        );
        params3.rightMargin=50;
        params3.bottomMargin=30;
        params3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
        save.setLayoutParams(params3);
        save.setBackground(parent.getContext().getDrawable(R.drawable.save));
        save.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        //save.setText("保存");
        save.setOnClickListener(saveListener);
        buttons.addView(save);

        return buttons;
    }
}