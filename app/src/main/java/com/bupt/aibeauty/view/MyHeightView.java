package com.bupt.aibeauty.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bupt.aibeauty.utils.BitmapUtils;

public class MyHeightView extends MyBaseView implements View.OnTouchListener {
    private static String TAG="myHeightView";
    private View back;
    private MySeekBar seekBar;
    //直线的点击域大小
    private int halfReactArea=40;
    //直线距离边界的最小距离
    private int lineMargin=10;
    //表示选中哪条直线，用来解决重合问题
    private int choose=-1;
    //判断是否在拉动直线，用于画出红色的增高区域
    private boolean isDrag=false;
    private boolean isChangeHeight=false;
    //直线1的位置
    private int y1=100;
    //直线2的位置
    private int y2=200;
    //直线的粗细 用一个长条矩形表示
    private int lineHeight=20;
    //增高线两边原点的半径大小
    private int r=20;

    private Bitmap current,origin;

    private boolean backPrepareOk=false;

    public MyHeightView(Context context) {
        super(context);
    }

    public MyHeightView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        backPrepareOk=false;
        this.setOnTouchListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(!backPrepareOk) return;
        if(!isChangeHeight){
            Paint paint1=new Paint();
            paint1.setColor(Color.BLACK);
            //0-255
            paint1.setAlpha(150);
            paint1.setStyle(Paint.Style.FILL_AND_STROKE);
            //以自身的边界为基准
            //left 左边框离左边的距离  right 右边框离左边的距离 top 上边框离上面的距离 bottom 下边框离上面的距离
            //画直线1
            canvas.drawRect((getWidth()-back.getWidth())/2, y1, back.getWidth()-(getWidth()-back.getWidth())/2, y1+lineHeight, paint1);
            canvas.drawCircle(0,y1+lineHeight/2,r,paint1);
            canvas.drawCircle(back.getWidth(),y1+lineHeight/2,r,paint1);
            //画直线2
            canvas.drawRect((getWidth()-back.getWidth())/2, y2, back.getWidth()-(getWidth()-back.getWidth())/2, y2+lineHeight, paint1);
            canvas.drawCircle(0,y2+lineHeight/2,r,paint1);
            canvas.drawCircle(back.getWidth(),y2+lineHeight/2,r,paint1);
        }
        //拖动中画出红色的增高区域
        if(isDrag){
            Paint paint2=new Paint();
            paint2.setColor(Color.RED);
            paint2.setAlpha(100);
            paint2.setStyle(Paint.Style.FILL_AND_STROKE);
            int left=100,right=back.getWidth()-100;
            int biggerY=y1>y2?y1:y2;
            int smallerY=y1<y2?y1:y2;
            int top=smallerY+lineHeight;
            int bot=biggerY;
            canvas.drawRect(left,top,right,bot,paint2);
            if(Math.abs(y1-y2)>125){
                Paint paint3=new Paint();
                //计算该字体文本的长度
                paint3.setTextSize(100);
                paint3.setColor(Color.BLACK);
                float len=paint3.measureText("增高区域");
                Paint.FontMetrics fm=paint3.getFontMetrics();
                //字符串在矩形中间显示
                float y=(y1-lineHeight+y2)/2-(paint3.getFontMetricsInt().bottom+paint3.getFontMetricsInt().top)/2;
                float x=(right-left)/2-len/2+left;
                canvas.drawText("增高区域",x,y,paint3);
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(!backPrepareOk) return false;
        float y=0;
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                isDrag=true;
                y=motionEvent.getY();
                if(y>y1-halfReactArea&&y<y1+halfReactArea) {
                    choose=1;
                }
                if(y>y2-halfReactArea&&y<y2+halfReactArea){
                    choose=2;
                }
                int fromX=(int)motionEvent.getX();
                int fromY=(int)motionEvent.getY();

                if(clickDifBtn(fromX,fromY)){
                    isDrag=false;
                    startChangeHeight();
                    showDif=true;
                    current=Bitmap.createBitmap(((BitmapDrawable)(((ImageView)(back)).getDrawable())).getBitmap());
                    ((ImageView)back).setImageBitmap(origin);
                    invalidate();
                }
                if(clickUndoBtn(fromX,fromY)) {
                    isDrag = false;
                    startChangeHeight();
                    doUndo = true;
                    current = Bitmap.createBitmap(((BitmapDrawable) (((ImageView) (back)).getDrawable())).getBitmap());
                    ((ImageView) back).setImageBitmap(origin);
                    resetSeekbar();
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                y=motionEvent.getY();
                //判断是否y1区域被点击
                if(choose==1&&y>y1-halfReactArea&&y<y1+halfReactArea){
                    //需要相对于back.getTop来说
                    if(y<back.getTop()+lineMargin){
                        y1=back.getTop()+lineMargin;
                    }else if(y>back.getBottom()-lineHeight-lineMargin){
                        y1=back.getBottom()-lineHeight-lineMargin;
                    }else{
                        y1=(int)y;
                    }
                    invalidate();
                    return true;
                }
                //判断是否y2区域被点击
                if(choose==2&&y>y2-halfReactArea&&y<y2+halfReactArea) {
                    if(y<back.getTop()+lineMargin){
                        y2=back.getTop()+lineMargin;
                    }else if(y>back.getBottom()-lineHeight-lineMargin){
                        y2=back.getBottom()-lineHeight-lineMargin;
                    }else{
                        y2=(int)y;
                    }
                    invalidate();
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                isDrag=false;
                choose=-1;
                if(showDif) {
                    showDif = false;
                    endChangeHeight();
                    ((ImageView) back).setImageBitmap(current);
                    invalidate();
                    return true;
                }
                if(doUndo){
                    endChangeHeight();
                    doUndo=false;
                    invalidate();
                }
                invalidate();
                break;
        }
        return true;
    }
    public void startChangeHeight(){
        isChangeHeight=true;
        invalidate();
    }
    public void endChangeHeight(){
        isChangeHeight=false;
        invalidate();
    }
    public void setBack(View back){
        backPrepareOk=true;
        this.back=back;
        ImageView imageView=(ImageView)back;
        Bitmap source=((BitmapDrawable)imageView.getDrawable()).getBitmap();
        this.origin= BitmapUtils.copyBitmap(source);
        initLine();
    }
    public void setSeekBar(MySeekBar seekBar){
        this.seekBar=seekBar;
    }
    private void initLine(){
        y1=back.getTop()+(int)((back.getBottom()-back.getTop())*0.3);
        y2=back.getTop()+(int)((back.getBottom()-back.getTop())*0.7);
        invalidate();
    }
    private void resetSeekbar(){
        if(seekBar.style==MySeekBar.CENTER_STYLE){
            seekBar.setProgress(0);
        }else{
            seekBar.setProgress(seekBar.getMinValue());
        }
    }
    public void setLine1Location(double location){
        y1= (int) (back.getTop()+back.getHeight()*location);
    }
    public void setLine2Location(double location){
        y2=(int) (back.getTop()+back.getHeight()*location);
    }
    public double getLine1Location(){
        return Math.abs(1.0*(y1-back.getTop())/back.getHeight());
    }
    public double getLine2Location(){
        return Math.abs(1.0*(y2-back.getTop())/back.getHeight());
    }
}