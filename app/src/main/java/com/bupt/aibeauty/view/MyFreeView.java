package com.bupt.aibeauty.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bupt.aibeauty.utils.BitmapUtils;
import com.bupt.aibeauty.utils.ImageTransformUtils;

import java.util.LinkedList;

public class MyFreeView extends MyBaseView implements View.OnTouchListener {
    private View back;
    private MySeekBar seekBar;
    private boolean backPrepareOk=false;
    private boolean isDrag=false;
    private final int defaultFreeModeCircleR=50;
    //显示自由瘦的作用范围大小
    private int freeModeCircleR=defaultFreeModeCircleR;
    //判断当前是否正在移动自由瘦的SeekBar
    private boolean isMoveFreeChangeSeekBar=false;
    //自由瘦的拖动的原点与终点
    private int fromX;
    private int fromY;
    private int toX;
    private int toY;

    private Bitmap origin,current;
    private int operatorMemoSize=6;
    private LinkedList<Bitmap> operatorList=new LinkedList<>();

    public MyFreeView(Context context) {
        super(context);
    }

    public MyFreeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        backPrepareOk=false;
        this.setOnTouchListener(this);
    }
    public void setBack(View back){
        backPrepareOk=true;
        this.back=back;
        ImageView imageView=(ImageView)back;
        Bitmap source=((BitmapDrawable)imageView.getDrawable()).getBitmap();
        this.origin= BitmapUtils.copyBitmap(source);
    }
    public void setSeekBar(MySeekBar seekBar){
        this.seekBar=seekBar;
    }
    private void resetSeekbar(){
        if(seekBar.style==MySeekBar.CENTER_STYLE){
            seekBar.setProgress(0);
        }else{
            seekBar.setProgress(seekBar.getMinValue());
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                isDrag=true;
                fromX=(int)motionEvent.getX();
                fromY=(int)motionEvent.getY();
                if(clickDifBtn(fromX,fromY)){
                    showDif=true;
                    current=Bitmap.createBitmap(((BitmapDrawable)(((ImageView)(back)).getDrawable())).getBitmap());
                    ((ImageView)back).setImageBitmap(origin);
                    invalidate();
                }
                if(clickUndoBtn(fromX,fromY)){
                    doUndo=true;
                    undo();
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                toX=(int)motionEvent.getX();
                toY=(int)motionEvent.getY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                isDrag=false;
                if(showDif){
                    showDif=false;
                    ((ImageView)back).setImageBitmap(current);
                    invalidate();
                    return true;
                }
                toX=(int)motionEvent.getX();
                toY=(int)motionEvent.getY();
                invalidate();
                if (Math.abs(toX-fromX)<=5&&Math.abs(toY=fromY)<=5) return true;
                ImageView imageView=(ImageView)back;
                Bitmap origin=((BitmapDrawable)imageView.getDrawable()).getBitmap();
                int mapFromX=(int) (1.0*fromX/back.getWidth()*origin.getWidth());
                int mapFromY= (int) (1.0*(fromY-back.getTop())/back.getHeight()*origin.getHeight());
                int mapToX= (int) (1.0*toX/back.getWidth()*origin.getWidth());
                int mapToY= (int) (1.0*(toY-back.getTop())/back.getHeight()*origin.getHeight());
                double factor=Math.sqrt((mapFromX-mapToX)*(mapFromX-mapToX)+(mapFromY-mapToY)*(mapFromY-mapToY))/Math.sqrt((origin.getWidth())*(origin.getWidth())+(origin.getHeight())*(origin.getHeight()));
                int mapR= (int) (1.0*freeModeCircleR/back.getHeight()*origin.getHeight()*(1+factor)*2);
                //double MapDistance= PointUtils.getDistance(new Point(mapFromX,mapFromY),new Point(mapToX,mapToY))/3*freeModeCircleR/defaultFreeModeCircleR;
                //Bitmap deform=ImageTransformUtils.freeChange(origin,mapFromX,mapFromY,mapToX,mapToY, (int) MapDistance);
                Bitmap deform= ImageTransformUtils.freeChangeByRange(origin,mapFromX,mapFromY,mapToX,mapToY,mapR);
                //Log.d("myview",fromX+","+fromY+"-->"+toX+","+toY);
                ((ImageView) back).setImageBitmap(deform);
                if(doUndo){
                    doUndo=false;
                    invalidate();
                }else{
                    addToMemo(deform);
                }
                break;
        }
        return true;
    }
    private void addToMemo(Bitmap bitmap){
        if(operatorList.size()>=operatorMemoSize){
            operatorList.removeFirst();
        }
        operatorList.add(BitmapUtils.copyBitmap(bitmap));
    }
    private void undo(){
        if(operatorList.size()<=0){
            ((ImageView)back).setImageBitmap(origin);
            resetSeekbar();
        }else{
            ((ImageView)back).setImageBitmap(operatorList.removeLast());
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!backPrepareOk) return;
        if(isMoveFreeChangeSeekBar){
            Log.d("freeview","move");
            float centerX= (float) (1.0*getWidth()/2);
            float centerY=(float) (1.0*getHeight()/2);
            drawRangeCircle(canvas,centerX,centerY,freeModeCircleR);
        }
        if(isDrag){
            //当拖动点在图像内时，才画圆
            if(checkCircle(fromX,fromY)&&checkCircle(toX,toY)){
                drawRangeCircle(canvas,fromX,fromY,freeModeCircleR);
                Paint linkTheCircle=new Paint();
                linkTheCircle.setColor(Color.WHITE);
                linkTheCircle.setStrokeWidth(10);
                linkTheCircle.setAlpha(150);
                canvas.drawLine(fromX,fromY,toX,toY,linkTheCircle);
                drawRangeCircle(canvas,toX,toY,freeModeCircleR);
            }
        }
    }

    private boolean checkCircle(float x, float y){
        boolean flag1=(x-freeModeCircleR)>back.getLeft()&&(x+freeModeCircleR)<back.getRight();
        boolean flag2=(y-freeModeCircleR)>back.getTop()&&(y+freeModeCircleR)<back.getBottom();
        return flag1&&flag2;
    }
    //画出标识自由瘦范围的圆
    private void drawRangeCircle(Canvas canvas, float centerX, float centerY, float r){
        Paint outline=new Paint();
        outline.setStyle(Paint.Style.STROKE);
        //设置粗细
        outline.setStrokeWidth(5);
        outline.setColor(Color.WHITE);
        outline.setAlpha(150);

        //画出轮廓，上下左右四根线条
        canvas.drawLine(centerX,centerY-r/3*2,centerX,centerY-r/3,outline);
        canvas.drawLine(centerX,centerY+r/3*2,centerX,centerY+r/3,outline);
        canvas.drawLine(centerX-r/3*2,centerY,centerX-r/3,centerY,outline);
        canvas.drawLine(centerX+r/3*2,centerY,centerX+r/3,centerY,outline);
        canvas.drawCircle(centerX,centerY,freeModeCircleR,outline);
        //内部填充
        Paint fill=new Paint();
        fill.setStyle(Paint.Style.FILL);
        fill.setColor(Color.BLACK);
        fill.setAlpha(100);
        canvas.drawCircle(centerX,centerY,freeModeCircleR,fill);
    }



    public void startDrawFreeRange(){
        this.isMoveFreeChangeSeekBar=true;
        invalidate();
    }
    public void stopDrawFreeRange(){
        this.isMoveFreeChangeSeekBar=false;
        invalidate();
    }
    public void setFreeRange(double percent){
        this.freeModeCircleR=(int)(this.defaultFreeModeCircleR*percent);
        this.invalidate();
    }
}
