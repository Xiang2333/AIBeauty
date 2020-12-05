package com.bupt.aibeauty.adapter;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.aibeauty.R;
import com.bupt.aibeauty.activity.MainActivity;
import com.bupt.aibeauty.activity.SlimActivity;
import com.bupt.aibeauty.activity.TransitActivity;
import com.bupt.aibeauty.integrate.ConIAfflineS;
import com.bupt.aibeauty.integrate.ProfilePoint;
import com.bupt.aibeauty.utils.BitmapUtils;
import com.bupt.aibeauty.utils.ImageTransformUtils;
import com.bupt.aibeauty.utils.IncludeViewBuildUtils;
import com.bupt.aibeauty.utils.ModelUtils;
import com.bupt.aibeauty.utils.PointUtils;
import com.bupt.aibeauty.utils.SlimUtils;
import com.bupt.aibeauty.utils.ViewUtils;
import com.bupt.aibeauty.view.MySeekBar;
import com.bupt.aibeauty.view.MyView;

import java.util.ArrayList;
import java.util.List;


public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyHolder> {
    private Context context;
    private List<String> btn_func;
    private List<Integer> btn_id;
    private RelativeLayout includeView;
    private ImageView imageView;
    private RecyclerView recyclerView;
    private MyView viewBefore;
    private Uri uri;

    private Bitmap origin;
    private Bitmap deform;
    private int y1;
    private int y2;

    private static long a=0,b=0,t=0;

    public RecycleViewAdapter(Context context, List<String> btn_func,List<Integer> btn_id,RelativeLayout includeView,ImageView imageView,RecyclerView recyclerView,MyView viewBefore,Uri uri) {
        this.context = context;
        this.btn_func = btn_func;
        this.includeView=includeView;
        this.imageView=imageView;
        this.recyclerView=recyclerView;
        this.viewBefore=viewBefore;
        this.btn_id=btn_id;
        this.uri=uri;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.button_item,viewGroup,false);
        MyHolder myHolder=new MyHolder(view);
        return myHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        final String btn_text=btn_func.get(i);
        final Integer index=i;
        myHolder.button.setText(btn_text);
        Drawable drawable=null;
        switch (btn_id.get(i)){
            case R.id.heightButton:
                drawable=context.getDrawable(R.drawable.height);
                break;
            case R.id.slimButton:
                drawable=context.getDrawable(R.drawable.slim);
                break;
            case R.id.shoulderButton:
                drawable=context.getDrawable(R.drawable.shoulder);
                break;
            case R.id.hipButton:
                drawable=context.getDrawable(R.drawable.hip);
                break;
            case R.id.chestButton:
                drawable=context.getDrawable(R.drawable.chest);
                break;
            case R.id.headButton:
                drawable=context.getDrawable(R.drawable.head);
                break;
            case R.id.freeButton:
                drawable=context.getDrawable(R.drawable.free);
                break;
            default:
                break;
        }
        if(drawable!=null){
            drawable.setBounds(0, 0, 80, 80);
        }
        myHolder.button.setCompoundDrawables(null,drawable,null,null);
        //点击功能按钮，显示功能窗口
        myHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("RecycleViewAdapter",btn_text);
                /*
                switch (btn_id.get(i)){
                    case R.id.heightButton:

                        break;
                    case R.id.slimButton:
                        Intent intent=new Intent();
                        intent.setClass(context, SlimActivity.class);
                        intent.putExtra("img_path",uri.toString());
                        context.startActivity(intent);
                        break;
                    case R.id.shoulderButton:

                        break;
                    case R.id.hipButton:

                        break;
                    case R.id.chestButton:

                        break;
                    case R.id.headButton:

                        break;
                    case R.id.freeButton:

                        break;
                    default:
                        break;
                }
                */

                //直接改变是否可视无法立即生效，用一个动画过度
                ObjectAnimator animator = ObjectAnimator.ofFloat(includeView, "translationY", includeView.getHeight(), 0);
                animator.setDuration(200);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        initIncludeView(btn_id.get(index),btn_text);
                        includeView.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                    }
                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }
                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                });
                animator.start();

            }
        });
    }
    //根据传入的type即功能，动态构建includeView
    private void initIncludeView(final Integer id, final String btn_text){
        switch (id){
            case R.id.heightButton:
                prepareChangeHeight();
                height(btn_text);

                break;
            case R.id.slimButton:
                prepareButtons();
                slim(btn_text);

                break;
            case R.id.shoulderButton:
                shoulder(btn_text);

                break;
            case R.id.hipButton:
                hip(btn_text);

                break;
            case R.id.chestButton:
                chest(btn_text);

                break;
            case R.id.headButton:
                head(btn_text);

                break;
            case R.id.freeButton:
                prepareFreeChange();
                free(btn_text);

                break;
            default:
                break;
        }
        prepareModel();
    }
    private void prepareModel(){
        try {
            while(!ModelUtils.modelPrepareFin()){
                Thread.sleep(10);
            }
        }catch (Exception e){
            Log.e("recy",e.toString());
        }
    }
    private void prepareButtons(){
        viewBefore.setBack(imageView);
        viewBefore.setMode(MyView.BUTTONMODE);
        Bitmap bitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
        this.origin=BitmapUtils.copyBitmap(bitmap);
    }
    //准备增高功能需要的两条线
    private void prepareChangeHeight(){
        viewBefore.setBack(imageView);
        viewBefore.setMode(MyView.HEIGHTMODE);
        Bitmap bitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
        this.origin=BitmapUtils.copyBitmap(bitmap);
    }
    //增高功能的控制面板
    private void height(final String type){
        MySeekBar.OnSeekBarChangeListener seekerListener=new MySeekBar.OnSeekBarChangeListener() {
            @Override
            public void onSeekBarValueChange(MySeekBar bar, double value) {

                //Log.d("changeHeightMinMax",bar.getMinValue()+" "+bar.getMaxValue());
                //Log.d("changeHeightLineLoc",viewBefore.getLine1Location()+"  "+viewBefore.getLine2Location());
                //Log.d("changeHeightDegree",value+"");

                //Bitmap mutableOrigin=Bitmap.createScaledBitmap(origin,origin.getWidth(),origin.getHeight(),true);
                double line1Location=viewBefore.getLine1Location();
                double line2Location=viewBefore.getLine2Location();
                int y= (int) (origin.getHeight()*Math.min(line1Location,line2Location));
                int areaHeight= (int) (origin.getHeight()*Math.abs(line2Location-line1Location));
                //0-1 -> 0.5-1.5
                float mapMin=0.7f,mapMax=1.3f;
                float mapDegree= (float) (mapMin+(mapMax-mapMin)*(value-bar.getMinValue())/(bar.getMaxValue()-bar.getMinValue()));
                //Log.d("height",origin.getHeight()+" "+origin.getWidth()+" "+y+" "+areaHeight+" "+mapDegree);
                deform=ImageTransformUtils.changeHeight(origin,y,areaHeight,mapDegree);
                imageView.setImageBitmap(deform);

                //ViewGroup.LayoutParams params=imageView.getLayoutParams();
                //params.width= ViewGroup.LayoutParams.WRAP_CONTENT;
                //params.height= ViewGroup.LayoutParams.MATCH_PARENT;
                //Log.d("height",params.height+" "+params.width);
                //int oriWidth=origin.getWidth(),oriHeight=origin.getHeight();
                //int defWidth=deform.getWidth(),defHeight=deform.getHeight();

                //double ratio=1.0*defHeight/oriHeight;
                //Log.d("height",defHeight+" "+oriHeight);
                //params.height= (int) (imageView.getHeight()*ratio);
                //Log.d("height",ratio+" "+params.height);
                //imageView.setLayoutParams(params);
                imageView.invalidate();

            }

            @Override
            public void onSeekBarValueChangeStart(MySeekBar bar) {
                double line1Location=viewBefore.getLine1Location();
                double line2Location=viewBefore.getLine2Location();
                Bitmap current=((BitmapDrawable)(imageView.getDrawable())).getBitmap();
                y1= (int) (current.getHeight()*Math.min(line1Location,line2Location));
                y2= (int) ((1-Math.max(line1Location,line2Location))*current.getHeight());
                viewBefore.startChangeHeight();
            }

            @Override
            public void onSeekBarValueChangeStop(MySeekBar bar) {
                viewBefore.endChangeHeight();
                viewBefore.setLine1Location(1.0*y1/deform.getHeight());
                viewBefore.setLine2Location(1-1.0*y2/deform.getHeight());
            }
        };
        int width=imageView.getWidth()-200;
        LinearLayout seeker= IncludeViewBuildUtils.buildCenterSeekBarWithName(context.getString(R.string.seekbar_height),seekerListener,includeView,width);
        RelativeLayout.LayoutParams seekerParams=new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        seekerParams.addRule(RelativeLayout.ABOVE,R.id.heightButtons);
        seekerParams.bottomMargin=50;
        includeView.addView(seeker,seekerParams);


        Button.OnClickListener quitListener=new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                //移除添加的布局，否则布局不会消失，下一次还在
                includeView.removeAllViewsInLayout();
                viewBefore.resetMode();
                includeView.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                origin=null;
            }
        };
        Button.OnClickListener saveListener=new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Bitmap bitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
                BitmapUtils.saveBitmap(context,bitmap);
            }
        };


        RelativeLayout buttons=IncludeViewBuildUtils.buildButtons(type,quitListener,saveListener,includeView);
        buttons.setId(R.id.heightButtons);
        RelativeLayout.LayoutParams buttonsParams=new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        buttonsParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
        includeView.addView(buttons,buttonsParams);
    }
    //瘦身功能的控制面板
    private void slim(final String type){
        //Bitmap mask=((BitmapDrawable)context.getDrawable(R.drawable.mask_5)).getBitmap();
        if(BitmapUtils.originBitmap==null){
            BitmapUtils.originBitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
        }

        ProfilePoint profilePoint= ModelUtils.getProfilePoint(BitmapUtils.originBitmap);
        Point from=new Point(),to=new Point();
        //手臂功能的SeekBar
        MySeekBar.OnSeekBarChangeListener armListener=new MySeekBar.OnSeekBarChangeListener() {
            @Override
            public void onSeekBarValueChange(MySeekBar bar, double value) {
                if(((int)Math.abs(value))%10!=0){
                    return;
                }
                SlimUtils.armCurrentValue=value;
                //Log.d("recy",value+"");
                int pointNum=6;
                int[][][] leftArm=profilePoint.getLeftArm(3);
                int[][][] rightArm=profilePoint.getRightArm(3);
                List<Point> leftFromList=new ArrayList<>();
                List<Point> leftToList=new ArrayList<>();
                List<Point> rightFromList=new ArrayList<>();
                List<Point> rightToList=new ArrayList<>();
                fixImage(leftFromList,leftToList,rightFromList,rightToList);
                double rate=Math.abs(value)*2/(bar.getMaxValue()-bar.getMinValue());
                Bitmap afterChange=BitmapUtils.copyBitmap(BitmapUtils.originBitmap);
                if(value>0){
                    for(int i=0;i<pointNum;i++){
                        if(leftArm[1][i]!=null){
                            if(leftArm[0][i]!=null){
                                from.x=leftArm[0][i][0];from.y=leftArm[0][i][1];
                                to.x=leftArm[1][i][0];to.y=leftArm[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.33);
                                leftFromList.add(new Point(from));leftToList.add(new Point(p));
                                //afterChange=ImageTransformUtils.freeChange(afterChange,from.x,from.y,p.x,p.y,0);
                                //afterChange=ImageTransformUtils.changeByAffilne(afterChange,from.x,from.y,p.x,p.y);
                            }
                            if(leftArm[2][i]!=null){
                                from.x=leftArm[2][i][0];from.y=leftArm[2][i][1];
                                to.x=leftArm[1][i][0];to.y=leftArm[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.33);
                                leftFromList.add(new Point(from));leftToList.add(new Point(p));
                                //afterChange=ImageTransformUtils.freeChange(afterChange,from.x,from.y,p.x,p.y,0);
                                //afterChange=ImageTransformUtils.changeByAffilne(afterChange,from.x,from.y,p.x,p.y);
                            }
                        }
                        if(rightArm[1][i]!=null){
                            if(rightArm[0][i]!=null){
                                from.x=rightArm[0][i][0];from.y=rightArm[0][i][1];
                                to.x=rightArm[1][i][0];to.y=rightArm[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.33);
                                rightFromList.add(new Point(from));rightToList.add(new Point(p));
                                //afterChange=ImageTransformUtils.freeChange(afterChange,from.x,from.y,p.x,p.y,0);
                                //afterChange=ImageTransformUtils.changeByAffilne(afterChange,from.x,from.y,p.x,p.y);
                            }
                            if(rightArm[2][i]!=null){
                                from.x=rightArm[2][i][0];from.y=rightArm[2][i][1];
                                to.x=rightArm[1][i][0];to.y=rightArm[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.33);
                                rightFromList.add(new Point(from));rightToList.add(new Point(p));
                                //afterChange=ImageTransformUtils.freeChange(afterChange,from.x,from.y,p.x,p.y,0);
                                //afterChange=ImageTransformUtils.changeByAffilne(afterChange,from.x,from.y,p.x,p.y);
                            }
                        }
                    }
                }else{
                    for(int i=0;i<pointNum;i++){
                        if(leftArm[1][i]!=null){
                            if(leftArm[0][i]!=null){
                                from.x=leftArm[0][i][0];from.y=leftArm[0][i][1];
                                to.x=2*leftArm[0][i][0]-leftArm[1][i][0];to.y=2*leftArm[0][i][1]-leftArm[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.33);
                                leftFromList.add(new Point(from));leftToList.add(new Point(p));
                                //afterChange=ImageTransformUtils.freeChange(afterChange,from.x,from.y,p.x,p.y,0);
                                //afterChange=ImageTransformUtils.changeByAffilne(afterChange,from.x,from.y,p.x,p.y);
                            }
                            if(leftArm[2][i]!=null){
                                from.x=leftArm[2][i][0];from.y=leftArm[2][i][1];
                                to.x=2*leftArm[2][i][0]-leftArm[1][i][0];to.y=2*leftArm[2][i][1]-leftArm[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.33);
                                leftFromList.add(new Point(from));leftToList.add(new Point(p));
                                //afterChange=ImageTransformUtils.freeChange(afterChange,from.x,from.y,p.x,p.y,0);
                                //afterChange=ImageTransformUtils.changeByAffilne(afterChange,from.x,from.y,p.x,p.y);
                            }
                        }
                        if(rightArm[1][i]!=null){
                            if(rightArm[0][i]!=null){
                                from.x=rightArm[0][i][0];from.y=rightArm[0][i][1];
                                to.x=2*rightArm[0][i][0]-rightArm[1][i][0];to.y=2*rightArm[0][i][1]-rightArm[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.33);
                                rightFromList.add(new Point(from));rightToList.add(new Point(p));
                                //afterChange=ImageTransformUtils.freeChange(afterChange,from.x,from.y,p.x,p.y,0);
                                //afterChange=ImageTransformUtils.changeByAffilne(afterChange,from.x,from.y,p.x,p.y);
                            }
                            if(rightArm[2][i]!=null){
                                from.x=rightArm[2][i][0];from.y=rightArm[2][i][1];
                                to.x=2*rightArm[2][i][0]-rightArm[1][i][0];to.y=2*rightArm[2][i][1]-rightArm[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.33);
                                rightFromList.add(new Point(from));rightToList.add(new Point(p));
                                //afterChange=ImageTransformUtils.freeChange(afterChange,from.x,from.y,p.x,p.y,0);
                                //afterChange=ImageTransformUtils.changeByAffilne(afterChange,from.x,from.y,p.x,p.y);
                            }
                        }
                    }
                }
                SlimUtils.armLeftFrom=leftFromList;SlimUtils.armLeftTo=leftToList;
                SlimUtils.armRightFrom=rightFromList;SlimUtils.armRightTo=rightToList;
                AsyncTask<Object,Void,Bitmap> task=new ImageTransTask().execute(afterChange,leftFromList,leftToList,rightFromList,rightToList,imageView);
            }
            @Override
            public void onSeekBarValueChangeStart(MySeekBar bar) {

            }
            @Override
            public void onSeekBarValueChangeStop(MySeekBar bar) {
                //BitmapUtils.deformBitmap=BitmapUtils.copyBitmap(((BitmapDrawable)imageView.getDrawable()).getBitmap());
                if(t==0) return;
                //Toast.makeText(context,"imgtrans takes "+((b-a)/t)+" ms",Toast.LENGTH_LONG).show();
                Log.d("task","imgtrans takes "+((b-a)/t)+" ms");
            }
        };
        //腰部功能的SeekBar
        MySeekBar.OnSeekBarChangeListener waistListener=new MySeekBar.OnSeekBarChangeListener() {
            @Override
            public void onSeekBarValueChange(MySeekBar bar, double value) {
                if(((int)Math.abs(value))%10!=0){
                    return;
                }
                SlimUtils.waistCurrentValue=value;
                int leftX=profilePoint.getWaistPoint()[0][0],leftY=profilePoint.getWaistPoint()[0][1];
                int rightX=profilePoint.getWaistPoint()[1][0],rightY=profilePoint.getWaistPoint()[1][1];
                //int centerX=(leftX+rightX)/2,centerY=(leftY+rightY)/2;
                List<Point> leftFromList=new ArrayList<>();
                List<Point> leftToList=new ArrayList<>();
                List<Point> rightFromList=new ArrayList<>();
                List<Point> rightToList=new ArrayList<>();
                fixImage(leftFromList,leftToList,rightFromList,rightToList);
                double rate=Math.abs(value)*2/(bar.getMaxValue()-bar.getMinValue());
                Bitmap afterChange=BitmapUtils.copyBitmap(BitmapUtils.originBitmap);

                if(value>0){
                    Point fromLeft=new Point(leftX,leftY);
                    Point fromRight=new Point(rightX,rightY);
                    Point pLeft=PointUtils.getPointBetween(fromLeft,fromRight,rate,0.15);
                    //afterChange=ImageTransformUtils.freeChange(afterChange,fromLeft.x,fromLeft.y,pLeft.x,pLeft.y,0);
                    //afterChange=ImageTransformUtils.changeByAffilne(afterChange,new int[][]{{fromLeft.x,fromLeft.y}},new int[][]{{pLeft.x,pLeft.y}});
                    leftFromList.add(new Point(fromLeft));leftToList.add(new Point(pLeft));
                    Point pRight=PointUtils.getPointBetween(fromRight,fromLeft,rate,0.15);
                    rightFromList.add(new Point(fromRight));rightToList.add(new Point(pRight));
                    //afterChange=ImageTransformUtils.freeChange(afterChange,fromRight.x,fromRight.y,pRight.x,pRight.y,0);
                    //afterChange=ImageTransformUtils.changeByAffilne(afterChange,new int[][]{{fromRight.x,fromRight.y}},new int[][]{{pRight.x,pRight.y}});
                }else{
                    Point fromLeft=new Point(leftX,leftY);
                    Point fromRight=new Point(rightX,rightY);
                    Point mirror=new Point(2*fromLeft.x-fromRight.x,2*fromLeft.y-fromRight.y);
                    Point pLeft=PointUtils.getPointBetween(fromLeft,mirror,rate,0.15);
                    leftFromList.add(new Point(fromLeft));leftToList.add(new Point(pLeft));
                    //afterChange=ImageTransformUtils.freeChange(afterChange,fromLeft.x,fromLeft.y,pLeft.x,pLeft.y,0);
                    mirror.x=2*fromRight.x-fromLeft.x;mirror.y=2*fromRight.y-fromLeft.y;
                    Point pRight=PointUtils.getPointBetween(fromRight,mirror,rate,0.15);
                    rightFromList.add(new Point(fromRight));rightToList.add(new Point(pRight));
                    //afterChange=ImageTransformUtils.freeChange(afterChange,fromRight.x,fromRight.y,pRight.x,pRight.y,0);
                }
                SlimUtils.waistLeftFrom=leftFromList;SlimUtils.waistLeftTo=leftToList;
                SlimUtils.waistRightFrom=rightFromList;SlimUtils.waistRightTo=rightToList;
                AsyncTask<Object,Void,Bitmap> task=new ImageTransTask().execute(afterChange,leftFromList,leftToList,rightFromList,rightToList,imageView);
            }

            @Override
            public void onSeekBarValueChangeStart(MySeekBar bar) {

            }

            @Override
            public void onSeekBarValueChangeStop(MySeekBar bar) {
                //BitmapUtils.deformBitmap=BitmapUtils.copyBitmap(((BitmapDrawable)imageView.getDrawable()).getBitmap());
            }
        };
        //腿部功能的SeekBar
        MySeekBar.OnSeekBarChangeListener lagListener=new MySeekBar.OnSeekBarChangeListener() {
            @Override
            public void onSeekBarValueChange(MySeekBar bar, double value) {
                if(((int)Math.abs(value))%10!=0){
                    return;
                }
                SlimUtils.legCurrentValue=value;
                int pointNum=6;
                int[][][] leftLeg=profilePoint.getLeftLeg(3);
                int[][][] rightLeg=profilePoint.getRightLeg(3);
                List<Point> leftFromList=new ArrayList<>();
                List<Point> leftToList=new ArrayList<>();
                List<Point> rightFromList=new ArrayList<>();
                List<Point> rightToList=new ArrayList<>();
                fixImage(leftFromList,leftToList,rightFromList,rightToList);
                double rate=Math.abs(value)*2/(bar.getMaxValue()-bar.getMinValue());
                Bitmap afterChange=BitmapUtils.copyBitmap(BitmapUtils.originBitmap);

                if(value>0){
                    for(int i=0;i<pointNum;i++){
                        if(leftLeg[1][i]!=null){
                            if(leftLeg[0][i]!=null){
                                from.x=leftLeg[0][i][0];from.y=leftLeg[0][i][1];
                                to.x=leftLeg[1][i][0];to.y=leftLeg[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.33);
                                leftFromList.add(new Point(from));leftToList.add(new Point(p));
                                //afterChange=ImageTransformUtils.freeChange(afterChange,from.x,from.y,p.x,p.y,0);
                            }
                            if(leftLeg[2][i]!=null){
                                from.x=leftLeg[2][i][0];from.y=leftLeg[2][i][1];
                                to.x=leftLeg[1][i][0];to.y=leftLeg[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.33);
                                leftFromList.add(new Point(from));leftToList.add(new Point(p));
                                //afterChange=ImageTransformUtils.freeChange(afterChange,from.x,from.y,p.x,p.y,0);
                            }
                        }
                        if(rightLeg[1][i]!=null){
                            if(rightLeg[2][i]!=null){
                                from.x=rightLeg[2][i][0];from.y=rightLeg[2][i][1];
                                to.x=rightLeg[1][i][0];to.y=rightLeg[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.33);
                                rightFromList.add(new Point(from));rightToList.add(new Point(p));
                                //afterChange=ImageTransformUtils.freeChange(afterChange,from.x,from.y,p.x,p.y,0);
                            }
                            if(rightLeg[0][i]!=null){
                                from.x=rightLeg[0][i][0];from.y=rightLeg[0][i][1];
                                to.x=rightLeg[1][i][0];to.y=rightLeg[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.33);
                                rightFromList.add(new Point(from));rightToList.add(new Point(p));
                                //afterChange=ImageTransformUtils.freeChange(afterChange,from.x,from.y,p.x,p.y,0);
                            }
                        }
                    }
                }else{
                    for(int i=0;i<pointNum;i++){
                        if(leftLeg[1][i]!=null){
                            if(leftLeg[0][i]!=null){
                                from.x=leftLeg[0][i][0];from.y=leftLeg[0][i][1];
                                to.x=2*leftLeg[0][i][0]-leftLeg[1][i][0];to.y=2*leftLeg[0][i][1]-leftLeg[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.33);
                                leftFromList.add(new Point(from));leftToList.add(new Point(p));
                                //afterChange=ImageTransformUtils.freeChange(afterChange,from.x,from.y,p.x,p.y,0);
                            }
                            if(leftLeg[2][i]!=null){
                                from.x=leftLeg[2][i][0];from.y=leftLeg[2][i][1];
                                to.x=2*leftLeg[2][i][0]-leftLeg[1][i][0];to.y=2*leftLeg[2][i][1]-leftLeg[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.33);
                                leftFromList.add(new Point(from));leftToList.add(new Point(p));
                                //afterChange=ImageTransformUtils.freeChange(afterChange,from.x,from.y,p.x,p.y,0);
                            }
                        }
                        if(rightLeg[1][i]!=null){
                            if(rightLeg[2][i]!=null){
                                from.x=rightLeg[2][i][0];from.y=rightLeg[2][i][1];
                                to.x=2*rightLeg[2][i][0]-rightLeg[1][i][0];to.y=2*rightLeg[2][i][1]-rightLeg[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.33);
                                rightFromList.add(new Point(from));rightToList.add(new Point(p));
                                //afterChange=ImageTransformUtils.freeChange(afterChange,from.x,from.y,p.x,p.y,0);
                            }
                            if(rightLeg[0][i]!=null){
                                from.x=rightLeg[0][i][0];from.y=rightLeg[0][i][1];
                                to.x=2*rightLeg[0][i][0]-rightLeg[1][i][0];to.y=2*rightLeg[0][i][1]-rightLeg[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.33);
                                rightFromList.add(new Point(from));rightToList.add(new Point(p));
                                //afterChange=ImageTransformUtils.freeChange(afterChange,from.x,from.y,p.x,p.y,0);
                            }
                        }
                    }
                }
                SlimUtils.legLeftFrom=leftFromList;SlimUtils.legLeftTo=leftToList;
                SlimUtils.legRightFrom=rightFromList;SlimUtils.legRightTo=rightToList;
                AsyncTask<Object,Void,Bitmap> task=new ImageTransTask().execute(afterChange,leftFromList,leftToList,rightFromList,rightToList,imageView);
            }

            @Override
            public void onSeekBarValueChangeStart(MySeekBar bar) {

            }

            @Override
            public void onSeekBarValueChangeStop(MySeekBar bar) {
                //BitmapUtils.deformBitmap=BitmapUtils.copyBitmap(((BitmapDrawable)imageView.getDrawable()).getBitmap());
            }
        };
        int width=imageView.getWidth()-200;
        ViewUtils.flag=true;
        LinearLayout armSeeker=IncludeViewBuildUtils.buildCenterSeekBarWithName(context.getString(R.string.seekbar_arm),armListener,includeView,width);
        ViewUtils.flag=true;
        LinearLayout waistSeeker=IncludeViewBuildUtils.buildCenterSeekBarWithName(context.getString(R.string.seekbar_waist),waistListener,includeView,width);
        ViewUtils.flag=true;
        LinearLayout lagSeeker=IncludeViewBuildUtils.buildCenterSeekBarWithName(context.getString(R.string.seekbar_lag),lagListener,includeView,width);
        armSeeker.setId(R.id.armBar);
        waistSeeker.setId(R.id.waistBar);
        lagSeeker.setId(R.id.lagBar);
        RelativeLayout.LayoutParams armParams=new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        armParams.addRule(RelativeLayout.ABOVE,R.id.waistBar);
        RelativeLayout.LayoutParams waistParams=new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        waistParams.addRule(RelativeLayout.ABOVE,R.id.lagBar);
        RelativeLayout.LayoutParams lagParams=new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        lagParams.addRule(RelativeLayout.ABOVE,R.id.silmButtons);


        includeView.addView(armSeeker,armParams);
        includeView.addView(waistSeeker,waistParams);
        includeView.addView(lagSeeker,lagParams);
        Button.OnClickListener quitListener=new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                includeView.removeAllViewsInLayout();
                viewBefore.resetMode();
                includeView.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                BitmapUtils.deformBitmap=null;
                BitmapUtils.originBitmap=null;
                if(BitmapUtils.originBitmap!=null){
                    imageView.setImageBitmap(BitmapUtils.copyBitmap(BitmapUtils.originBitmap));
                }
            }
        };
        Button.OnClickListener saveListener=new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Bitmap bitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
                BitmapUtils.saveBitmap(context,bitmap);
            }
        };

        RelativeLayout buttons=IncludeViewBuildUtils.buildButtons(type,quitListener,saveListener,includeView);
        buttons.setId(R.id.silmButtons);
        RelativeLayout.LayoutParams buttonsParams=new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        buttonsParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
        includeView.addView(buttons,buttonsParams);

    }
    //美肩功能的面板
    private void shoulder(final String type){
        //肩部宽窄的SeekBar
        MySeekBar.OnSeekBarChangeListener widthListener=new MySeekBar.OnSeekBarChangeListener() {

            @Override
            public void onSeekBarValueChange(MySeekBar bar, double value) {

            }

            @Override
            public void onSeekBarValueChangeStart(MySeekBar bar) {

            }

            @Override
            public void onSeekBarValueChangeStop(MySeekBar bar) {

            }
        };
        //直角肩SeekBar
        MySeekBar.OnSeekBarChangeListener rightAngleListener=new MySeekBar.OnSeekBarChangeListener() {
            @Override
            public void onSeekBarValueChange(MySeekBar bar, double value) {

            }

            @Override
            public void onSeekBarValueChangeStart(MySeekBar bar) {

            }

            @Override
            public void onSeekBarValueChangeStop(MySeekBar bar) {

            }
        };
        //天鹅颈SeekBar
        MySeekBar.OnSeekBarChangeListener swanListener=new MySeekBar.OnSeekBarChangeListener() {
            @Override
            public void onSeekBarValueChange(MySeekBar bar, double value) {

            }

            @Override
            public void onSeekBarValueChangeStart(MySeekBar bar) {

            }

            @Override
            public void onSeekBarValueChangeStop(MySeekBar bar) {

            }
        };
        int width=imageView.getWidth()-200;
        LinearLayout widthSeeker=IncludeViewBuildUtils.buildCenterSeekBarWithName(context.getString(R.string.seekbar_width),widthListener,includeView,width);
        LinearLayout rightAngleSeeker=IncludeViewBuildUtils.buildDefaultSeekBarWithName(context.getString(R.string.seekbar_rightAngle),rightAngleListener,includeView,width);
        LinearLayout swanSeeker=IncludeViewBuildUtils.buildDefaultSeekBarWithName(context.getString(R.string.seekbar_swan),swanListener,includeView,width);
        widthSeeker.setId(R.id.widthBar);
        rightAngleSeeker.setId(R.id.rightAngleBar);
        swanSeeker.setId(R.id.swanBar);
        RelativeLayout.LayoutParams widthParams=new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        widthParams.addRule(RelativeLayout.ABOVE,R.id.rightAngleBar);
        RelativeLayout.LayoutParams rightAngleParams=new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        rightAngleParams.addRule(RelativeLayout.ABOVE,R.id.swanBar);
        RelativeLayout.LayoutParams swanParams=new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        swanParams.addRule(RelativeLayout.ABOVE,R.id.shoulderButtons);


        includeView.addView(widthSeeker,widthParams);
        includeView.addView(rightAngleSeeker,rightAngleParams);
        includeView.addView(swanSeeker,swanParams);
        Button.OnClickListener quitListener=new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                includeView.removeAllViewsInLayout();
                viewBefore.resetMode();
                includeView.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        };
        Button.OnClickListener saveListener=new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Bitmap bitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
                BitmapUtils.saveBitmap(context,bitmap);
            }
        };

        RelativeLayout buttons=IncludeViewBuildUtils.buildButtons(type,quitListener,saveListener,includeView);
        buttons.setId(R.id.shoulderButtons);
        RelativeLayout.LayoutParams buttonsParams=new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        buttonsParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
        includeView.addView(buttons,buttonsParams);

    }
    //美跨功能面板
    private void hip(final String type){
        MySeekBar.OnSeekBarChangeListener seekerListener=new MySeekBar.OnSeekBarChangeListener() {
            @Override
            public void onSeekBarValueChange(MySeekBar bar, double value) {

            }

            @Override
            public void onSeekBarValueChangeStart(MySeekBar bar) {

            }

            @Override
            public void onSeekBarValueChangeStop(MySeekBar bar) {

            }
        };
        int width=imageView.getWidth()-200;
        LinearLayout seeker= IncludeViewBuildUtils.buildCenterSeekBarWithName(context.getString(R.string.seekbar_hip),seekerListener,includeView,width);
        RelativeLayout.LayoutParams seekerParams=new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        seekerParams.addRule(RelativeLayout.ABOVE,R.id.hipButtons);
        seekerParams.bottomMargin=50;
        includeView.addView(seeker,seekerParams);


        Button.OnClickListener quitListener=new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                //移除添加的布局，否则布局不会消失，下一次还在
                includeView.removeAllViewsInLayout();
                viewBefore.resetMode();
                includeView.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        };

        Button.OnClickListener saveListener=new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Bitmap bitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
                BitmapUtils.saveBitmap(context,bitmap);
            }
        };
        RelativeLayout buttons=IncludeViewBuildUtils.buildButtons(type,quitListener,saveListener,includeView);
        buttons.setId(R.id.hipButtons);
        RelativeLayout.LayoutParams buttonsParams=new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        buttonsParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
        includeView.addView(buttons,buttonsParams);
    }
    //丰胸功能
    private void chest(final String type){
        MySeekBar.OnSeekBarChangeListener seekerListener=new MySeekBar.OnSeekBarChangeListener() {
            @Override
            public void onSeekBarValueChange(MySeekBar bar, double value) {

            }

            @Override
            public void onSeekBarValueChangeStart(MySeekBar bar) {

            }

            @Override
            public void onSeekBarValueChangeStop(MySeekBar bar) {

            }
        };
        int width=imageView.getWidth()-200;
        LinearLayout seeker= IncludeViewBuildUtils.buildCenterSeekBarWithName(context.getString(R.string.seekbar_chest),seekerListener,includeView,width);
        RelativeLayout.LayoutParams seekerParams=new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        seekerParams.addRule(RelativeLayout.ABOVE,R.id.chestButtons);
        seekerParams.bottomMargin=50;
        includeView.addView(seeker,seekerParams);


        Button.OnClickListener quitListener=new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                //移除添加的布局，否则布局不会消失，下一次还在
                includeView.removeAllViewsInLayout();
                viewBefore.resetMode();
                includeView.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        };
        Button.OnClickListener saveListener=new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Bitmap bitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
                BitmapUtils.saveBitmap(context,bitmap);
            }
        };

        RelativeLayout buttons=IncludeViewBuildUtils.buildButtons(type,quitListener,saveListener,includeView);
        buttons.setId(R.id.chestButtons);
        RelativeLayout.LayoutParams buttonsParams=new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        buttonsParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
        includeView.addView(buttons,buttonsParams);
    }
    //小头功能
    private void head(final String type){
        MySeekBar.OnSeekBarChangeListener seekerListener=new MySeekBar.OnSeekBarChangeListener() {
            @Override
            public void onSeekBarValueChange(MySeekBar bar, double value) {

            }

            @Override
            public void onSeekBarValueChangeStart(MySeekBar bar) {

            }

            @Override
            public void onSeekBarValueChangeStop(MySeekBar bar) {

            }
        };
        int width=imageView.getWidth()-200;
        LinearLayout seeker= IncludeViewBuildUtils.buildCenterSeekBarWithName(context.getString(R.string.seekbar_head),seekerListener,includeView,width);
        RelativeLayout.LayoutParams seekerParams=new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        seekerParams.addRule(RelativeLayout.ABOVE,R.id.headButtons);
        seekerParams.bottomMargin=50;
        includeView.addView(seeker,seekerParams);


        Button.OnClickListener quitListener=new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                //移除添加的布局，否则布局不会消失，下一次还在
                includeView.removeAllViewsInLayout();
                viewBefore.resetMode();
                includeView.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        };

        Button.OnClickListener saveListener=new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Bitmap bitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
                BitmapUtils.saveBitmap(context,bitmap);
            }
        };
        RelativeLayout buttons=IncludeViewBuildUtils.buildButtons(type,quitListener,saveListener,includeView);
        buttons.setId(R.id.headButtons);
        RelativeLayout.LayoutParams buttonsParams=new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        buttonsParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
        includeView.addView(buttons,buttonsParams);
    }

    //准备自由瘦身需要的圆
    private void prepareFreeChange(){
        viewBefore.setBack(imageView);
        viewBefore.setMode(MyView.FREEMODE);
    }
    //自由瘦身功能的控制面板
    private void free(final String type){
        MySeekBar.OnSeekBarChangeListener seekerListener=new MySeekBar.OnSeekBarChangeListener() {
            @Override
            public void onSeekBarValueChange(MySeekBar bar, double value) {
                //value -100~100

                double percent=1+(value-bar.getMinValue())/(bar.getMaxValue()-bar.getMinValue());
                Log.d("barValue"," "+percent);
                viewBefore.setFreeRange(percent);
            }

            @Override
            public void onSeekBarValueChangeStart(MySeekBar bar) {
                viewBefore.startDrawFreeRange();
            }

            @Override
            public void onSeekBarValueChangeStop(MySeekBar bar) {
                viewBefore.stopDrawFreeRange();
            }
        };
        int width=imageView.getWidth()-200;
        LinearLayout seeker= IncludeViewBuildUtils.buildDefaultSeekBarWithName(context.getString(R.string.seekbar_free),seekerListener,includeView,width);
        RelativeLayout.LayoutParams seekerParams=new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        seekerParams.addRule(RelativeLayout.ABOVE,R.id.freeChangeButtons);
        seekerParams.bottomMargin=50;
        includeView.addView(seeker,seekerParams);


        Button.OnClickListener quitListener=new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                //移除添加的布局，否则布局不会消失，下一次还在
                includeView.removeAllViewsInLayout();
                viewBefore.resetMode();
                includeView.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        };

        Button.OnClickListener saveListener=new Button.OnClickListener(){
            @Override
            public void onClick(View view) {
                Bitmap bitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
                BitmapUtils.saveBitmap(context,bitmap);
            }
        };
        RelativeLayout buttons=IncludeViewBuildUtils.buildButtons(type,quitListener,saveListener,includeView);
        buttons.setId(R.id.freeChangeButtons);
        RelativeLayout.LayoutParams buttonsParams=new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        buttonsParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,RelativeLayout.TRUE);
        includeView.addView(buttons,buttonsParams);
    }

    public void clean(){
        includeView.removeAllViewsInLayout();
        viewBefore.resetMode();
        includeView.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }
    private void fixImage(List<Point>... list){
        Bitmap bitmap=origin;
        for(List<Point> l:list){
            l.add(new Point(0,0));
            l.add(new Point(0,origin.getHeight()/2));
            l.add(new Point(0,origin.getHeight()-1));
            l.add(new Point(origin.getWidth()/2,origin.getHeight()-1));
            l.add(new Point(origin.getWidth()-1,origin.getHeight()-1));
            l.add(new Point(origin.getWidth()-1,origin.getHeight()/2));
            l.add(new Point(origin.getWidth()-1,0));
            l.add(new Point(origin.getWidth()/2,0));
        }
    }

    @Override
    public int getItemCount() {
        return btn_func.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{
        Button button;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            button=itemView.findViewById(R.id.recy_btn);
            button.setGravity(Gravity.CENTER);
        }
    }

    public static class ImageTransTask extends AsyncTask<Object,Void,Bitmap>{
        @Override
        protected Bitmap doInBackground(Object... objects) {
            /*
            long a=System.currentTimeMillis();
            Bitmap afterChange= (Bitmap) objects[0];
            List<Point> leftFromList= (List<Point>) objects[1];
            List<Point> leftToList= (List<Point>) objects[2];
            List<Point> rightFromList= (List<Point>) objects[3];
            List<Point> rightToList= (List<Point>) objects[4];
            ImageView view= (ImageView) objects[5];
            int[][] p=new int[leftFromList.size()][2];int[][] q=new int[leftToList.size()][2];
            for(int i=0;i<p.length;i++){
                p[i][0]=leftFromList.get(i).x;
                p[i][1]=leftFromList.get(i).y;
                q[i][0]=leftToList.get(i).x;
                q[i][1]=leftToList.get(i).y;
            }
            afterChange=ImageTransformUtils.changeByAffilne(afterChange,p,q);
            p=new int[rightFromList.size()][2];q=new int[rightToList.size()][2];
            for(int i=0;i<p.length;i++){
                p[i][0]=rightFromList.get(i).x;
                p[i][1]=rightFromList.get(i).y;
                q[i][0]=rightToList.get(i).x;
                q[i][1]=rightToList.get(i).y;
            }
            afterChange=ImageTransformUtils.changeByAffilne(afterChange,p,q);
            long b=System.currentTimeMillis();
            RecycleViewAdapter.a+=a;
            RecycleViewAdapter.b+=b;
            RecycleViewAdapter.t+=1;
            view.setImageBitmap(afterChange);
            return afterChange;
             */

            /*
            //分三次变化 模糊较大 但形变正常
            ConIAfflineS.defaultSize=33;
            Bitmap afterChange= (Bitmap) objects[0];
            ImageView view= (ImageView) objects[5];
            long a=System.currentTimeMillis();
            if(SlimUtils.armCurrentValue!=SlimUtils.armDefaultValue&&SlimUtils.armLeftFrom!=null&&SlimUtils.armLeftTo!=null&&SlimUtils.armRightFrom!=null&&SlimUtils.armRightTo!=null){
                int[][] p=new int[SlimUtils.armLeftFrom.size()+SlimUtils.armRightFrom.size()-8][2];int[][] q=new int[SlimUtils.armLeftTo.size()+SlimUtils.armRightTo.size()-8][2];
                int index=0;
                for(int i=0;i<SlimUtils.armLeftFrom.size();i++){
                    p[index][0]=SlimUtils.armLeftFrom.get(i).x;
                    p[index][1]=SlimUtils.armLeftFrom.get(i).y;
                    q[index][0]=SlimUtils.armLeftTo.get(i).x;
                    q[index][1]=SlimUtils.armLeftTo.get(i).y;
                    index+=1;
                }
                //afterChange=ImageTransformUtils.changeByAffilne(afterChange,p,q);
                //p=new int[SlimUtils.armRightFrom.size()][2];q=new int[SlimUtils.armRightTo.size()][2];
                for(int i=8;i<SlimUtils.armRightFrom.size();i++){
                    p[index][0]=SlimUtils.armRightFrom.get(i).x;
                    p[index][1]=SlimUtils.armRightFrom.get(i).y;
                    q[index][0]=SlimUtils.armRightTo.get(i).x;
                    q[index][1]=SlimUtils.armRightTo.get(i).y;
                    index+=1;
                }
                afterChange=ImageTransformUtils.changeByAffilne(afterChange,p,q);
            }
            if(SlimUtils.waistCurrentValue!=SlimUtils.waistDefaultValue&&SlimUtils.waistLeftFrom!=null&&SlimUtils.waistLeftTo!=null&&SlimUtils.waistRightFrom!=null&&SlimUtils.waistRightTo!=null){
                int[][] p=new int[SlimUtils.waistLeftFrom.size()+SlimUtils.waistRightFrom.size()-8][2];int[][] q=new int[SlimUtils.waistLeftTo.size()+SlimUtils.waistRightTo.size()-8][2];
                int index=0;
                for(int i=0;i<SlimUtils.waistLeftFrom.size();i++){
                    p[index][0]=SlimUtils.waistLeftFrom.get(i).x;
                    p[index][1]=SlimUtils.waistLeftFrom.get(i).y;
                    q[index][0]=SlimUtils.waistLeftTo.get(i).x;
                    q[index][1]=SlimUtils.waistLeftTo.get(i).y;
                    index+=1;
                }
                //afterChange=ImageTransformUtils.changeByAffilne(afterChange,p,q);
                //p=new int[SlimUtils.armRightFrom.size()][2];q=new int[SlimUtils.armRightTo.size()][2];
                for(int i=8;i<SlimUtils.waistRightFrom.size();i++){
                    p[index][0]=SlimUtils.waistRightFrom.get(i).x;
                    p[index][1]=SlimUtils.waistRightFrom.get(i).y;
                    q[index][0]=SlimUtils.waistRightTo.get(i).x;
                    q[index][1]=SlimUtils.waistRightTo.get(i).y;
                    index+=1;
                }
                afterChange=ImageTransformUtils.changeByAffilne(afterChange,p,q);
            }
            if(SlimUtils.legCurrentValue!=SlimUtils.legDefaultValue&&SlimUtils.legLeftFrom!=null&&SlimUtils.legLeftTo!=null&&SlimUtils.legRightFrom!=null&&SlimUtils.legRightTo!=null){
                int[][] p=new int[SlimUtils.legLeftFrom.size()+SlimUtils.legRightFrom.size()-8][2];int[][] q=new int[SlimUtils.legLeftTo.size()+SlimUtils.legRightTo.size()-8][2];
                int index=0;
                for(int i=0;i<SlimUtils.legLeftFrom.size();i++){
                    p[index][0]=SlimUtils.legLeftFrom.get(i).x;
                    p[index][1]=SlimUtils.legLeftFrom.get(i).y;
                    q[index][0]=SlimUtils.legLeftTo.get(i).x;
                    q[index][1]=SlimUtils.legLeftTo.get(i).y;
                    index+=1;
                }
                //afterChange=ImageTransformUtils.changeByAffilne(afterChange,p,q);
                //p=new int[SlimUtils.armRightFrom.size()][2];q=new int[SlimUtils.armRightTo.size()][2];
                for(int i=8;i<SlimUtils.legRightFrom.size();i++){
                    p[index][0]=SlimUtils.legRightFrom.get(i).x;
                    p[index][1]=SlimUtils.legRightFrom.get(i).y;
                    q[index][0]=SlimUtils.legRightTo.get(i).x;
                    q[index][1]=SlimUtils.legRightTo.get(i).y;
                    index+=1;
                }
                afterChange=ImageTransformUtils.changeByAffilne(afterChange,p,q);
            }
            long b=System.currentTimeMillis();
            RecycleViewAdapter.a+=a;
            RecycleViewAdapter.b+=b;
            RecycleViewAdapter.t+=1;
            view.setImageBitmap(afterChange);
            return afterChange;
            */

            /*
            //做一次变化 不模糊 但腰部跳变
            ConIAfflineS.defaultSize=80;
            Bitmap afterChange= (Bitmap) objects[0];
            ImageView view= (ImageView) objects[5];
            List<Point> from=new ArrayList<>();
            List<Point> to=new ArrayList<>();
            boolean flag=false;
            long a=System.currentTimeMillis();

            if(SlimUtils.armCurrentValue!=SlimUtils.armDefaultValue&&SlimUtils.armLeftFrom!=null&&SlimUtils.armLeftTo!=null&&SlimUtils.armRightFrom!=null&&SlimUtils.armRightTo!=null){
                if(flag){
                    for(int i=8;i<SlimUtils.armLeftFrom.size();i++){
                        from.add(SlimUtils.armLeftFrom.get(i));
                        to.add(SlimUtils.armLeftTo.get(i));
                    }
                    for(int i=8;i<SlimUtils.armRightFrom.size();i++){
                        from.add(SlimUtils.armRightFrom.get(i));
                        to.add(SlimUtils.armRightTo.get(i));
                    }
                }else{
                    for(int i=0;i<SlimUtils.armLeftFrom.size();i++){
                        from.add(SlimUtils.armLeftFrom.get(i));
                        to.add(SlimUtils.armLeftTo.get(i));
                    }
                    flag=true;
                    for(int i=8;i<SlimUtils.armRightFrom.size();i++){
                        from.add(SlimUtils.armRightFrom.get(i));
                        to.add(SlimUtils.armRightTo.get(i));
                    }
                }
            }
            if(SlimUtils.legCurrentValue!=SlimUtils.legDefaultValue&&SlimUtils.legLeftFrom!=null&&SlimUtils.legLeftTo!=null&&SlimUtils.legRightFrom!=null&&SlimUtils.legRightTo!=null){
                if(flag){
                    for(int i=8;i<SlimUtils.legLeftFrom.size();i++) {
                        from.add(SlimUtils.legLeftFrom.get(i));
                        to.add(SlimUtils.legLeftTo.get(i));
                    }
                    for(int i=8;i<SlimUtils.legRightFrom.size();i++){
                        from.add(SlimUtils.legRightFrom.get(i));
                        to.add(SlimUtils.legRightTo.get(i));
                    }
                }else{
                    for(int i=0;i<SlimUtils.legLeftFrom.size();i++) {
                        from.add(SlimUtils.legLeftFrom.get(i));
                        to.add(SlimUtils.legLeftTo.get(i));
                    }
                    flag=true;
                    for(int i=8;i<SlimUtils.legRightFrom.size();i++){
                        from.add(SlimUtils.legRightFrom.get(i));
                        to.add(SlimUtils.legRightTo.get(i));
                    }
                }
            }
            if(SlimUtils.waistCurrentValue!=SlimUtils.waistDefaultValue&&SlimUtils.waistLeftFrom!=null&&SlimUtils.waistLeftTo!=null&&SlimUtils.waistRightFrom!=null&&SlimUtils.waistRightTo!=null){
                //Log.d("recy",SlimUtils.waistLeftFrom.toString());
                //Log.d("recy",SlimUtils.waistLeftTo.toString());
                //Log.d("recy",SlimUtils.waistRightFrom.toString());
                //Log.d("recy",SlimUtils.waistRightTo.toString());
                if(flag){
                    for(int i=8;i<SlimUtils.waistLeftFrom.size();i++) {
                        from.add(SlimUtils.waistLeftFrom.get(i));
                        to.add(SlimUtils.waistLeftTo.get(i));
                    }
                    for(int i=8;i<SlimUtils.waistRightFrom.size();i++){
                        from.add(SlimUtils.waistRightFrom.get(i));
                        to.add(SlimUtils.waistRightTo.get(i));
                    }
                }else{
                    for(int i=0;i<SlimUtils.waistLeftFrom.size();i++) {
                        from.add(SlimUtils.waistLeftFrom.get(i));
                        to.add(SlimUtils.waistLeftTo.get(i));
                    }
                    flag=true;
                    for(int i=8;i<SlimUtils.waistRightFrom.size();i++){
                        from.add(SlimUtils.waistRightFrom.get(i));
                        to.add(SlimUtils.waistRightTo.get(i));
                    }
                }
            }

            int[][] p=new int[from.size()][2];
            int[][] q=new int[to.size()][2];
            for(int i=0;i<p.length;i++){
                p[i][0]=from.get(i).x;
                p[i][1]=from.get(i).y;
                q[i][0]=to.get(i).x;
                q[i][1]=to.get(i).y;
            }
            afterChange=ImageTransformUtils.changeByAffilne(afterChange,p,q);
            long b=System.currentTimeMillis();
            RecycleViewAdapter.a+=a;
            RecycleViewAdapter.b+=b;
            RecycleViewAdapter.t+=1;
            view.setImageBitmap(afterChange);
            return afterChange;
            */


            //手 脚一起变化 腰单独变化
            ConIAfflineS.defaultSize=60;
            Bitmap afterChange= (Bitmap) objects[0];
            ImageView view= (ImageView) objects[5];
            List<Point> from=new ArrayList<>();
            List<Point> to=new ArrayList<>();
            List<Point> from2=new ArrayList<>();
            List<Point> to2=new ArrayList<>();
            boolean flag=false;
            long a=System.currentTimeMillis();

            if(SlimUtils.armCurrentValue!=SlimUtils.armDefaultValue&&SlimUtils.armLeftFrom!=null&&SlimUtils.armLeftTo!=null&&SlimUtils.armRightFrom!=null&&SlimUtils.armRightTo!=null){
                if(flag){
                    for(int i=8;i<SlimUtils.armLeftFrom.size();i++){
                        from.add(SlimUtils.armLeftFrom.get(i));
                        to.add(SlimUtils.armLeftTo.get(i));
                    }
                    for(int i=8;i<SlimUtils.armRightFrom.size();i++){
                        from.add(SlimUtils.armRightFrom.get(i));
                        to.add(SlimUtils.armRightTo.get(i));
                    }
                }else{
                    for(int i=0;i<SlimUtils.armLeftFrom.size();i++){
                        from.add(SlimUtils.armLeftFrom.get(i));
                        to.add(SlimUtils.armLeftTo.get(i));
                    }
                    flag=true;
                    for(int i=8;i<SlimUtils.armRightFrom.size();i++){
                        from.add(SlimUtils.armRightFrom.get(i));
                        to.add(SlimUtils.armRightTo.get(i));
                    }
                }
            }
            if(SlimUtils.legCurrentValue!=SlimUtils.legDefaultValue&&SlimUtils.legLeftFrom!=null&&SlimUtils.legLeftTo!=null&&SlimUtils.legRightFrom!=null&&SlimUtils.legRightTo!=null){
                if(flag){
                    for(int i=8;i<SlimUtils.legLeftFrom.size();i++) {
                        from.add(SlimUtils.legLeftFrom.get(i));
                        to.add(SlimUtils.legLeftTo.get(i));
                    }
                    for(int i=8;i<SlimUtils.legRightFrom.size();i++){
                        from.add(SlimUtils.legRightFrom.get(i));
                        to.add(SlimUtils.legRightTo.get(i));
                    }
                }else{
                    for(int i=0;i<SlimUtils.legLeftFrom.size();i++) {
                        from.add(SlimUtils.legLeftFrom.get(i));
                        to.add(SlimUtils.legLeftTo.get(i));
                    }
                    flag=true;
                    for(int i=8;i<SlimUtils.legRightFrom.size();i++){
                        from.add(SlimUtils.legRightFrom.get(i));
                        to.add(SlimUtils.legRightTo.get(i));
                    }
                }
            }
            flag=false;
            if(SlimUtils.waistCurrentValue!=SlimUtils.waistDefaultValue&&SlimUtils.waistLeftFrom!=null&&SlimUtils.waistLeftTo!=null&&SlimUtils.waistRightFrom!=null&&SlimUtils.waistRightTo!=null){
                //Log.d("recy",SlimUtils.waistLeftFrom.toString());
                //Log.d("recy",SlimUtils.waistLeftTo.toString());
                //Log.d("recy",SlimUtils.waistRightFrom.toString());
                //Log.d("recy",SlimUtils.waistRightTo.toString());
                if(flag){
                    for(int i=8;i<SlimUtils.waistLeftFrom.size();i++) {
                        from2.add(SlimUtils.waistLeftFrom.get(i));
                        to2.add(SlimUtils.waistLeftTo.get(i));
                    }
                    for(int i=8;i<SlimUtils.waistRightFrom.size();i++){
                        from2.add(SlimUtils.waistRightFrom.get(i));
                        to2.add(SlimUtils.waistRightTo.get(i));
                    }
                }else{
                    for(int i=0;i<SlimUtils.waistLeftFrom.size();i++) {
                        from2.add(SlimUtils.waistLeftFrom.get(i));
                        to2.add(SlimUtils.waistLeftTo.get(i));
                    }
                    flag=true;
                    for(int i=8;i<SlimUtils.waistRightFrom.size();i++){
                        from2.add(SlimUtils.waistRightFrom.get(i));
                        to2.add(SlimUtils.waistRightTo.get(i));
                    }
                }
            }
            int[][] p;
            int[][] q;
            if(from2.size()>0){
                p=new int[from2.size()][2];
                q=new int[to2.size()][2];
                for(int i=0;i<p.length;i++){
                    p[i][0]=from2.get(i).x;
                    p[i][1]=from2.get(i).y;
                    q[i][0]=to2.get(i).x;
                    q[i][1]=to2.get(i).y;
                }
                afterChange=ImageTransformUtils.changeByAffilne(afterChange,p,q);
            }
            if(from.size()>0){
                p=new int[from.size()][2];
                q=new int[to.size()][2];
                for(int i=0;i<p.length;i++){
                    p[i][0]=from.get(i).x;
                    p[i][1]=from.get(i).y;
                    q[i][0]=to.get(i).x;
                    q[i][1]=to.get(i).y;
                }
                afterChange=ImageTransformUtils.changeByAffilne(afterChange,p,q);
            }

            long b=System.currentTimeMillis();
            RecycleViewAdapter.a+=a;
            RecycleViewAdapter.b+=b;
            RecycleViewAdapter.t+=1;
            view.setImageBitmap(afterChange);
            return afterChange;
        }
    }
}