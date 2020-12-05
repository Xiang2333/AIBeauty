package com.bupt.aibeauty.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.bupt.aibeauty.R;
import com.bupt.aibeauty.utils.BitmapUtils;
import com.bupt.aibeauty.utils.ImageTransformUtils;
import com.bupt.aibeauty.utils.SlimUtils;
import com.bupt.aibeauty.utils.ViewUtils;

import java.util.LinkedList;

public class MyView extends View {
    public static final int DEFAULTMODE=0;
    public static final int HEIGHTMODE=1;
    public static final int FREEMODE=2;
    public static final int BUTTONMODE=3;
    private int MODE=DEFAULTMODE;

    private View back;
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

    private boolean doUndo=false;
    private boolean showDif=false;
    private Bitmap origin;
    private Bitmap current;
    private Rect dif_btn_area;
    private Rect undo_btn_area;
    private int operatorMemoSize=6;
    private LinkedList<Bitmap> operatorList=new LinkedList<>();


    public MyView(Context context) {
        super(context);
    }
    public MyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float y=0;
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        isDrag=true;
                        if(MODE==HEIGHTMODE){
                            //getY相对于getTop来说
                            y=motionEvent.getY();
                            if(y>y1-halfReactArea&&y<y1+halfReactArea) {
                                choose=1;
                            }
                            if(y>y2-halfReactArea&&y<y2+halfReactArea){
                                choose=2;
                            }
                            fromX=(int)motionEvent.getX();
                            fromY=(int)motionEvent.getY();
                            if(fromX>dif_btn_area.left&&fromX<dif_btn_area.right&&fromY>dif_btn_area.top&&fromY<dif_btn_area.bottom){
                                isDrag=false;
                                //Log.d("showDif","down");
                                startChangeHeight();
                                showDif=true;
                                current=Bitmap.createBitmap(((BitmapDrawable)(((ImageView)(back)).getDrawable())).getBitmap());
                                ((ImageView)back).setImageBitmap(origin);
                                invalidate();
                            }
                            if(fromX>undo_btn_area.left&&fromX<undo_btn_area.right&&fromY>undo_btn_area.top&&fromY<undo_btn_area.bottom){
                                isDrag=false;
                                startChangeHeight();
                                doUndo=true;
                                //Log.d("undo","down");
                                current=Bitmap.createBitmap(((BitmapDrawable)(((ImageView)(back)).getDrawable())).getBitmap());
                                ((ImageView)back).setImageBitmap(origin);
                                invalidate();
                            }
                        }else if(MODE==FREEMODE){
                            fromX=(int)motionEvent.getX();
                            fromY=(int)motionEvent.getY();
                            if(fromX>dif_btn_area.left&&fromX<dif_btn_area.right&&fromY>dif_btn_area.top&&fromY<dif_btn_area.bottom){
                                //Log.d("showDif","down");
                                showDif=true;
                                current=Bitmap.createBitmap(((BitmapDrawable)(((ImageView)(back)).getDrawable())).getBitmap());
                                ((ImageView)back).setImageBitmap(origin);
                                invalidate();
                            }
                            if(fromX>undo_btn_area.left&&fromX<undo_btn_area.right&&fromY>undo_btn_area.top&&fromY<undo_btn_area.bottom){
                                doUndo=true;
                                //Log.d("myview","undo down");
                                undo();
                                invalidate();
                            }
                            Log.d("myview down",fromX+","+fromY);
                        }else if(MODE==BUTTONMODE){
                            fromX=(int)motionEvent.getX();
                            fromY=(int)motionEvent.getY();
                            if(fromX>dif_btn_area.left&&fromX<dif_btn_area.right&&fromY>dif_btn_area.top&&fromY<dif_btn_area.bottom){
                                //Log.d("showDif","down");
                                showDif=true;
                                current=Bitmap.createBitmap(((BitmapDrawable)(((ImageView)(back)).getDrawable())).getBitmap());
                                ((ImageView)back).setImageBitmap(origin);
                                invalidate();
                            }
                            if(fromX>undo_btn_area.left&&fromX<undo_btn_area.right&&fromY>undo_btn_area.top&&fromY<undo_btn_area.bottom){
                                doUndo=true;
                                //Log.d("myview","undo down");
                                undo();
                                invalidate();
                            }
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(MODE==HEIGHTMODE){
                            //折合到本组件的边界，从0开始
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

                        }else if(MODE==FREEMODE){
                            toX=(int)motionEvent.getX();
                            toY=(int)motionEvent.getY();
                            invalidate();
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        isDrag=false;
                        if(MODE==HEIGHTMODE){
                            choose=-1;
                            if(showDif){
                                showDif=false;
                                endChangeHeight();
                                //Log.d("showDif","up");
                                ((ImageView)back).setImageBitmap(current);
                                invalidate();
                                return true;
                            }
                            if(doUndo){
                                endChangeHeight();
                                doUndo=false;
                                invalidate();
                            }
                        }else if(MODE==FREEMODE){
                            if(showDif){
                                showDif=false;
                                //Log.d("showDif","up");
                                ((ImageView)back).setImageBitmap(current);
                                invalidate();
                                return true;
                            }
                            toX=(int)motionEvent.getX();
                            toY=(int)motionEvent.getY();
                            if (Math.abs(toX-fromX)<=5&&Math.abs(toY=fromY)<=5) return true;
                            //Log.d("backSize",back.getHeight()+" "+back.getWidth());
                            ImageView imageView=(ImageView)back;
                            Bitmap origin=((BitmapDrawable)imageView.getDrawable()).getBitmap();
                            //Log.d("bitmapSize",origin.getHeight()+" "+origin.getWidth());
                            int mapFromX=(int) (1.0*fromX/back.getWidth()*origin.getWidth());
                            int mapFromY= (int) (1.0*(fromY-back.getTop())/back.getHeight()*origin.getHeight());
                            int mapToX= (int) (1.0*toX/back.getWidth()*origin.getWidth());
                            int mapToY= (int) (1.0*(toY-back.getTop())/back.getHeight()*origin.getHeight());
                            double factor=Math.sqrt((mapFromX-mapToX)*(mapFromX-mapToX)+(mapFromY-mapToY)*(mapFromY-mapToY))/Math.sqrt((origin.getWidth())*(origin.getWidth())+(origin.getHeight())*(origin.getHeight()));
                            int mapR= (int) (1.0*freeModeCircleR/back.getHeight()*origin.getHeight()*(1+factor)*2);
                            //double MapDistance= PointUtils.getDistance(new Point(mapFromX,mapFromY),new Point(mapToX,mapToY))/3*freeModeCircleR/defaultFreeModeCircleR;


                            //Bitmap deform=ImageTransformUtils.freeChange(origin,mapFromX,mapFromY,mapToX,mapToY, (int) MapDistance);
                            Bitmap deform=ImageTransformUtils.freeChangeByRange(origin,mapFromX,mapFromY,mapToX,mapToY,mapR);
                            //Log.d("myview",fromX+","+fromY+"-->"+toX+","+toY);
                            ((ImageView) back).setImageBitmap(deform);
                            if(doUndo){
                                doUndo=false;
                                invalidate();
                            }else{
                                addToMemo(deform);
                            }
                        }else if(MODE==BUTTONMODE){
                            if(showDif){
                                showDif=false;
                                //Log.d("showDif","up");
                                ((ImageView)back).setImageBitmap(current);
                                invalidate();
                                return true;
                            }
                            if(doUndo){
                                doUndo=false;
                                invalidate();
                            }
                        }
                        invalidate();
                        break;
                }
                return true;
            }
        });
    }
    private void resetOperateMemo(){
        operatorList.clear();
    }
    public void setBack(View back){
        Log.d("myview","setBack");
        this.back=back;
        ImageView imageView=(ImageView)back;
        Bitmap source=((BitmapDrawable)imageView.getDrawable()).getBitmap();
        this.origin= BitmapUtils.copyBitmap(source);
    }
    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
    public void setMode(int mode){
        switch (mode){
            case HEIGHTMODE:
                y1=back.getTop()+(int)((back.getBottom()-back.getTop())*0.3);
                y2=back.getTop()+(int)((back.getBottom()-back.getTop())*0.7);
                freeModeCircleR=defaultFreeModeCircleR;
                break;
            case FREEMODE:
                break;
            case BUTTONMODE:
                break;
            case DEFAULTMODE:
                break;
        }
        MODE=mode;
        resetOperateMemo();
        this.invalidate();
    }
    public void resetMode(){
        setMode(DEFAULTMODE);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        switch (MODE){
            case DEFAULTMODE:
                break;
            case BUTTONMODE:
                drawButtons(canvas);
                break;
            case HEIGHTMODE:
                drawButtons(canvas);
                //拖动seekbar进行增高时隐藏两条黑线
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
                break;
            case FREEMODE:
                drawButtons(canvas);

                if(isMoveFreeChangeSeekBar){
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

                break;
            default:
                break;
        }
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
            if(MODE==BUTTONMODE){
                BitmapUtils.deformBitmap=null;
                for(MySeekBar seekBar:ViewUtils.slimSeekBar){
                    if(seekBar.style==MySeekBar.DEFAULT_STYLE){
                        seekBar.setProgress(seekBar.getMinValue());
                    }else{
                        seekBar.setProgress(0);
                    }
                }
                SlimUtils.clear();
            }

        }else{
            ((ImageView)back).setImageBitmap(operatorList.removeLast());
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
            dif2=Bitmap.createScaledBitmap(dif,dif.getWidth()*2,dif.getHeight()*2,true);
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

    private boolean checkCircle(float x,float y){
        boolean flag1=(x-freeModeCircleR)>back.getLeft()&&(x+freeModeCircleR)<back.getRight();
        boolean flag2=(y-freeModeCircleR)>back.getTop()&&(y+freeModeCircleR)<back.getBottom();
        return flag1&&flag2;
    }
    //画出标识自由瘦范围的圆
    private void drawRangeCircle(Canvas canvas,float centerX,float centerY,float r){
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
    public void startChangeHeight(){
        this.isChangeHeight=true;
        invalidate();
    }
    public void endChangeHeight(){
        this.isChangeHeight=false;
        invalidate();
    }
    public void startDrawFreeRange(){
        this.isMoveFreeChangeSeekBar=true;
    }
    public void stopDrawFreeRange(){
        this.isMoveFreeChangeSeekBar=false;
    }
    public void setFreeRange(double percent){
        this.freeModeCircleR=(int)(this.defaultFreeModeCircleR*percent);
        this.invalidate();
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