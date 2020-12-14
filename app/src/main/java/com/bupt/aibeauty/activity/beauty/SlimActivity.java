package com.bupt.aibeauty.activity.beauty;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bupt.aibeauty.R;
import com.bupt.aibeauty.integrate.ProfilePointCOCO;
import com.bupt.aibeauty.task.SlimTask;
import com.bupt.aibeauty.utils.BitmapUtils;
import com.bupt.aibeauty.utils.ModelUtils;
import com.bupt.aibeauty.utils.PointUtils;
import com.bupt.aibeauty.utils.SlimUtils;
import com.bupt.aibeauty.utils.ViewUtils;
import com.bupt.aibeauty.view.MyBaseView;
import com.bupt.aibeauty.view.MyHeightView;
import com.bupt.aibeauty.view.MySeekBar;
import com.bupt.aibeauty.view.MySlimView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class SlimActivity extends Activity{
    private Context context;
    private ImageView imageView;
    private MySeekBar arm;
    private MySeekBar waist;
    private MySeekBar leg;
    private MySlimView myView;

    private Point from=new Point();
    private Point to=new Point();
    private Bitmap origin;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_slim);
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
        origin=BitmapUtils.copyBitmap(bitmap);
        imageView.post(new Runnable() {
            @Override
            public void run() {
                myView.setBack(imageView);
                myView.setSeekBar(arm,waist,leg);
            }
        });
        ViewUtils.dialog=new SpotsDialog.Builder().setContext(this).setMessage("加载中").build();
        ViewUtils.dialog.show();
    }
    private void initView(){
        context=this;
        imageView=findViewById(R.id.slim_image);
        arm=findViewById(R.id.slim_seekbar1);arm.setStyle(MySeekBar.CENTER_STYLE);arm.setProgress(0);
        waist=findViewById(R.id.slim_seekbar2);waist.setStyle(MySeekBar.CENTER_STYLE);waist.setProgress(0);
        leg=findViewById(R.id.slim_seekbar3);leg.setStyle(MySeekBar.CENTER_STYLE);leg.setProgress(0);
        myView=findViewById(R.id.slim_myview);

        findViewById(R.id.slim_quit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.slim_save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
                BitmapUtils.saveBitmap(context,bitmap);
            }
        });

        arm.setOnSeekBarChangeListener(new MySeekBar.OnSeekBarChangeListener() {
            @Override
            public void onSeekBarValueChange(MySeekBar bar, double value) {
                if(((int)Math.abs(value))%10!=0){
                    return;
                }
                SlimUtils.armCurrentValue=value;
                int pointNum=6;
                ProfilePointCOCO profilePoint=ModelUtils.getProfilePoint();
                int[][][] leftArm=profilePoint.getLeftArm(3);
                int[][][] rightArm=profilePoint.getRightArm(3);
                List<Point> leftFromList=new ArrayList<>();
                List<Point> leftToList=new ArrayList<>();
                List<Point> rightFromList=new ArrayList<>();
                List<Point> rightToList=new ArrayList<>();
                fixImage(leftFromList,leftToList,rightFromList,rightToList);
                double rate=Math.abs(value)*2/(bar.getMaxValue()-bar.getMinValue());
                if(value>0){
                    for(int i=0;i<pointNum;i++){
                        if(leftArm[1][i]!=null){
                            if(leftArm[0][i]!=null){
                                from.x=leftArm[0][i][0];from.y=leftArm[0][i][1];
                                to.x=leftArm[1][i][0];to.y=leftArm[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.33);
                                leftFromList.add(new Point(from));leftToList.add(new Point(p));
                            }
                            if(leftArm[2][i]!=null){
                                from.x=leftArm[2][i][0];from.y=leftArm[2][i][1];
                                to.x=leftArm[1][i][0];to.y=leftArm[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.33);
                                leftFromList.add(new Point(from));leftToList.add(new Point(p));
                            }
                        }
                        if(rightArm[1][i]!=null){
                            if(rightArm[0][i]!=null){
                                from.x=rightArm[0][i][0];from.y=rightArm[0][i][1];
                                to.x=rightArm[1][i][0];to.y=rightArm[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.33);
                                rightFromList.add(new Point(from));rightToList.add(new Point(p));
                            }
                            if(rightArm[2][i]!=null){
                                from.x=rightArm[2][i][0];from.y=rightArm[2][i][1];
                                to.x=rightArm[1][i][0];to.y=rightArm[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.33);
                                rightFromList.add(new Point(from));rightToList.add(new Point(p));
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
                            }
                            if(leftArm[2][i]!=null){
                                from.x=leftArm[2][i][0];from.y=leftArm[2][i][1];
                                to.x=2*leftArm[2][i][0]-leftArm[1][i][0];to.y=2*leftArm[2][i][1]-leftArm[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.33);
                                leftFromList.add(new Point(from));leftToList.add(new Point(p));
                            }
                        }
                        if(rightArm[1][i]!=null){
                            if(rightArm[0][i]!=null){
                                from.x=rightArm[0][i][0];from.y=rightArm[0][i][1];
                                to.x=2*rightArm[0][i][0]-rightArm[1][i][0];to.y=2*rightArm[0][i][1]-rightArm[1][i][1];
                                Point p= PointUtils.getPointBetween(from,to,rate,0.33);
                                rightFromList.add(new Point(from));rightToList.add(new Point(p));
                            }
                            if(rightArm[2][i]!=null){
                                from.x=rightArm[2][i][0];from.y=rightArm[2][i][1];
                                to.x=2*rightArm[2][i][0]-rightArm[1][i][0];to.y=2*rightArm[2][i][1]-rightArm[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.33);
                                rightFromList.add(new Point(from));rightToList.add(new Point(p));
                            }
                        }
                    }
                }
                SlimUtils.armLeftFrom=leftFromList;SlimUtils.armLeftTo=leftToList;
                SlimUtils.armRightFrom=rightFromList;SlimUtils.armRightTo=rightToList;
                new SlimTask().execute(origin,imageView);
            }

            @Override
            public void onSeekBarValueChangeStart(MySeekBar bar) {

            }

            @Override
            public void onSeekBarValueChangeStop(MySeekBar bar) {

            }
        });

        waist.setOnSeekBarChangeListener(new MySeekBar.OnSeekBarChangeListener() {
            @Override
            public void onSeekBarValueChange(MySeekBar bar, double value) {
                if(((int)Math.abs(value))%10!=0){
                    return;
                }
                SlimUtils.waistCurrentValue=value;
                ProfilePointCOCO profilePoint=ModelUtils.getProfilePoint();
                int leftX=profilePoint.getWaistPoint()[0][0],leftY=profilePoint.getWaistPoint()[0][1];
                int rightX=profilePoint.getWaistPoint()[1][0],rightY=profilePoint.getWaistPoint()[1][1];
                //int centerX=(leftX+rightX)/2,centerY=(leftY+rightY)/2;
                List<Point> leftFromList=new ArrayList<>();
                List<Point> leftToList=new ArrayList<>();
                List<Point> rightFromList=new ArrayList<>();
                List<Point> rightToList=new ArrayList<>();
                fixImage(leftFromList,leftToList,rightFromList,rightToList);
                double rate=Math.abs(value)*2/(bar.getMaxValue()-bar.getMinValue());
                if(value>0){
                    Point fromLeft=new Point(leftX,leftY);
                    Point fromRight=new Point(rightX,rightY);
                    Point pLeft=PointUtils.getPointBetween(fromLeft,fromRight,rate,0.15);
                    leftFromList.add(new Point(fromLeft));leftToList.add(new Point(pLeft));
                    Point pRight=PointUtils.getPointBetween(fromRight,fromLeft,rate,0.15);
                    rightFromList.add(new Point(fromRight));rightToList.add(new Point(pRight));
                }else{
                    Point fromLeft=new Point(leftX,leftY);
                    Point fromRight=new Point(rightX,rightY);
                    Point mirror=new Point(2*fromLeft.x-fromRight.x,2*fromLeft.y-fromRight.y);
                    Point pLeft=PointUtils.getPointBetween(fromLeft,mirror,rate,0.15);
                    leftFromList.add(new Point(fromLeft));leftToList.add(new Point(pLeft));
                    mirror.x=2*fromRight.x-fromLeft.x;mirror.y=2*fromRight.y-fromLeft.y;
                    Point pRight=PointUtils.getPointBetween(fromRight,mirror,rate,0.15);
                    rightFromList.add(new Point(fromRight));rightToList.add(new Point(pRight));
                }
                SlimUtils.waistLeftFrom=leftFromList;SlimUtils.waistLeftTo=leftToList;
                SlimUtils.waistRightFrom=rightFromList;SlimUtils.waistRightTo=rightToList;
                new SlimTask().execute(origin,imageView);
            }

            @Override
            public void onSeekBarValueChangeStart(MySeekBar bar) {

            }

            @Override
            public void onSeekBarValueChangeStop(MySeekBar bar) {

            }
        });

        leg.setOnSeekBarChangeListener(new MySeekBar.OnSeekBarChangeListener() {
            @Override
            public void onSeekBarValueChange(MySeekBar bar, double value) {
                if(((int)Math.abs(value))%10!=0){
                    return;
                }
                SlimUtils.legCurrentValue=value;
                int pointNum=6;
                ProfilePointCOCO profilePoint=ModelUtils.getProfilePoint();
                int[][][] leftLeg=profilePoint.getLeftLeg(3);
                int[][][] rightLeg=profilePoint.getRightLeg(3);
                List<Point> leftFromList=new ArrayList<>();
                List<Point> leftToList=new ArrayList<>();
                List<Point> rightFromList=new ArrayList<>();
                List<Point> rightToList=new ArrayList<>();
                fixImage(leftFromList,leftToList,rightFromList,rightToList);
                double rate=Math.abs(value)*2/(bar.getMaxValue()-bar.getMinValue());
                if(value>0){
                    for(int i=0;i<pointNum;i++){
                        if(leftLeg[1][i]!=null){
                            if(leftLeg[0][i]!=null){
                                from.x=leftLeg[0][i][0];from.y=leftLeg[0][i][1];
                                to.x=leftLeg[1][i][0];to.y=leftLeg[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.37);
                                leftFromList.add(new Point(from));leftToList.add(new Point(p));
                                //afterChange=ImageTransformUtils.freeChange(afterChange,from.x,from.y,p.x,p.y,0);
                            }
                            if(leftLeg[2][i]!=null){
                                from.x=leftLeg[2][i][0];from.y=leftLeg[2][i][1];
                                to.x=leftLeg[1][i][0];to.y=leftLeg[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.37);
                                leftFromList.add(new Point(from));leftToList.add(new Point(p));
                                //afterChange=ImageTransformUtils.freeChange(afterChange,from.x,from.y,p.x,p.y,0);
                            }
                        }
                        if(rightLeg[1][i]!=null){
                            if(rightLeg[2][i]!=null){
                                from.x=rightLeg[2][i][0];from.y=rightLeg[2][i][1];
                                to.x=rightLeg[1][i][0];to.y=rightLeg[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.37);
                                rightFromList.add(new Point(from));rightToList.add(new Point(p));
                                //afterChange=ImageTransformUtils.freeChange(afterChange,from.x,from.y,p.x,p.y,0);
                            }
                            if(rightLeg[0][i]!=null){
                                from.x=rightLeg[0][i][0];from.y=rightLeg[0][i][1];
                                to.x=rightLeg[1][i][0];to.y=rightLeg[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.37);
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
                                Point p=PointUtils.getPointBetween(from,to,rate,0.37);
                                leftFromList.add(new Point(from));leftToList.add(new Point(p));
                                //afterChange=ImageTransformUtils.freeChange(afterChange,from.x,from.y,p.x,p.y,0);
                            }
                            if(leftLeg[2][i]!=null){
                                from.x=leftLeg[2][i][0];from.y=leftLeg[2][i][1];
                                to.x=2*leftLeg[2][i][0]-leftLeg[1][i][0];to.y=2*leftLeg[2][i][1]-leftLeg[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.37);
                                leftFromList.add(new Point(from));leftToList.add(new Point(p));
                                //afterChange=ImageTransformUtils.freeChange(afterChange,from.x,from.y,p.x,p.y,0);
                            }
                        }
                        if(rightLeg[1][i]!=null){
                            if(rightLeg[2][i]!=null){
                                from.x=rightLeg[2][i][0];from.y=rightLeg[2][i][1];
                                to.x=2*rightLeg[2][i][0]-rightLeg[1][i][0];to.y=2*rightLeg[2][i][1]-rightLeg[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.37);
                                rightFromList.add(new Point(from));rightToList.add(new Point(p));
                                //afterChange=ImageTransformUtils.freeChange(afterChange,from.x,from.y,p.x,p.y,0);
                            }
                            if(rightLeg[0][i]!=null){
                                from.x=rightLeg[0][i][0];from.y=rightLeg[0][i][1];
                                to.x=2*rightLeg[0][i][0]-rightLeg[1][i][0];to.y=2*rightLeg[0][i][1]-rightLeg[1][i][1];
                                Point p=PointUtils.getPointBetween(from,to,rate,0.37);
                                rightFromList.add(new Point(from));rightToList.add(new Point(p));
                                //afterChange=ImageTransformUtils.freeChange(afterChange,from.x,from.y,p.x,p.y,0);
                            }
                        }
                    }
                }
                SlimUtils.legLeftFrom=leftFromList;SlimUtils.legLeftTo=leftToList;
                SlimUtils.legRightFrom=rightFromList;SlimUtils.legRightTo=rightToList;
                new SlimTask().execute(origin,imageView);
            }

            @Override
            public void onSeekBarValueChangeStart(MySeekBar bar) {

            }

            @Override
            public void onSeekBarValueChangeStop(MySeekBar bar) {

            }
        });
    }
    private void fixImage(List<Point>... list){
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
}