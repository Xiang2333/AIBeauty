package com.bupt.aibeauty.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.bupt.aibeauty.R;


public class MyBaseView extends View {
    protected boolean showBtns=true;
    protected boolean showDif=false;
    protected boolean doUndo=false;
    protected Rect dif_btn_area;
    protected Rect undo_btn_area;


    public MyBaseView(Context context) {
        super(context);
    }
    public MyBaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(showBtns){
            drawButtons(canvas);
        }
    }
    private void drawButtons(Canvas canvas){
        Rect src=null;Rect des=null;
        int leftMag=0,topMag=0;
        int bigger=20;
        Bitmap dif=((BitmapDrawable)getResources().getDrawable(R.drawable.btn_show_change)).getBitmap();
        Bitmap dif2=null;
        if(showDif){
            dif2=Bitmap.createScaledBitmap(dif,dif.getWidth()*2-bigger,dif.getHeight()*2-bigger,true);
            src=new Rect(0,0,dif2.getWidth(),dif2.getHeight());
            leftMag=getWidth()-dif2.getWidth()-20-bigger/2;
            topMag=20;
            des=new Rect(0+leftMag,0+topMag,dif2.getWidth()+leftMag,dif2.getHeight()+topMag);
            canvas.drawBitmap(dif2,src,des,null);
        }else{
            dif2= Bitmap.createScaledBitmap(dif,dif.getWidth()*2,dif.getHeight()*2,true);
            src=new Rect(0,0,dif2.getWidth(),dif2.getHeight());
            leftMag=getWidth()-dif2.getWidth()-20;
            topMag=20;
            des=new Rect(0+leftMag,0+topMag,dif2.getWidth()+leftMag,dif2.getHeight()+topMag);
            dif_btn_area=des;
            canvas.drawBitmap(dif2,src,des,null);
        }
        Bitmap undo=((BitmapDrawable)getResources().getDrawable(R.drawable.btn_undo)).getBitmap();
        Bitmap undo2=null;
        if(doUndo){
            undo2=Bitmap.createScaledBitmap(undo,undo.getWidth()*2-bigger,undo.getHeight()*2-bigger,true);
            src=new Rect(0,0,undo2.getWidth(),undo2.getHeight());
            leftMag=getWidth()-dif.getWidth()*2-20-undo2.getWidth()-20-bigger/2;
            topMag=20;
            des=new Rect(0+leftMag,0+topMag,undo2.getWidth()+leftMag,undo2.getHeight()+topMag);
            undo_btn_area=des;
            canvas.drawBitmap(undo2,src,des,null);
        }else{
            undo2=Bitmap.createScaledBitmap(undo,undo.getWidth()*2,undo.getHeight()*2,true);
            src=new Rect(0,0,undo2.getWidth(),undo2.getHeight());
            leftMag=getWidth()-dif.getWidth()*2-20-undo2.getWidth()-20;
            topMag=20;
            des=new Rect(0+leftMag,0+topMag,undo2.getWidth()+leftMag,undo2.getHeight()+topMag);
            undo_btn_area=des;
            canvas.drawBitmap(undo2,src,des,null);
        }
    }
    protected boolean clickDifBtn(int x,int y){
        if(x>dif_btn_area.left&&x<dif_btn_area.right&&y>dif_btn_area.top&&y<dif_btn_area.bottom){
            return true;
        }
        return false;
    }
    protected boolean clickUndoBtn(int x,int y){
        if(x>undo_btn_area.left&&x<undo_btn_area.right&&y>undo_btn_area.top&&y<undo_btn_area.bottom){
            return true;
        }
        return false;
    }
}
