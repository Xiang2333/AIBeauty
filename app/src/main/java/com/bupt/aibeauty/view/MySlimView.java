package com.bupt.aibeauty.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bupt.aibeauty.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

public class MySlimView extends MyBaseView implements View.OnTouchListener {
    private View back;
    private Bitmap origin,current;
    private boolean backPrepareOk=false;
    private List<MySeekBar> seekBars;

    public MySlimView(Context context) {
        super(context);
    }

    public MySlimView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
    }
    public void setBack(View back){
        backPrepareOk=true;
        this.back=back;
        ImageView imageView=(ImageView)back;
        Bitmap source=((BitmapDrawable)imageView.getDrawable()).getBitmap();
        this.origin= BitmapUtils.copyBitmap(source);
    }
    public void setSeekBar(MySeekBar... mySeekBars){
        seekBars=new ArrayList<>();
        for(MySeekBar seekBar:mySeekBars){
               seekBars.add(seekBar);
        }
    }
    private void resetSeekbar(){
        for(MySeekBar seekBar:seekBars){
            if(seekBar.style==MySeekBar.CENTER_STYLE){
                seekBar.setProgress(0);
            }else{
                seekBar.setProgress(seekBar.getMinValue());
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                int fromX=(int)motionEvent.getX();
                int fromY=(int)motionEvent.getY();
                if(clickDifBtn(fromX,fromY)){
                    showDif=true;
                    current= Bitmap.createBitmap(((BitmapDrawable)(((ImageView)(back)).getDrawable())).getBitmap());
                    ((ImageView)back).setImageBitmap(origin);
                    invalidate();
                }
                if(clickUndoBtn(fromX,fromY)) {
                    doUndo = true;
                    current = Bitmap.createBitmap(((BitmapDrawable) (((ImageView) (back)).getDrawable())).getBitmap());
                    ((ImageView) back).setImageBitmap(origin);
                    resetSeekbar();
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if(showDif) {
                    showDif = false;
                    ((ImageView) back).setImageBitmap(current);
                    invalidate();
                    return true;
                }
                if(doUndo){
                    doUndo=false;
                    invalidate();
                }
                invalidate();
                break;
        }
        return true;
    }
}
