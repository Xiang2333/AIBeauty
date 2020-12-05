package com.bupt.aibeauty.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.bupt.aibeauty.integrate.ConIAfflineS;
import com.bupt.aibeauty.integrate.OperatorS;
import com.bupt.aibeauty.integrate.Scale;
import com.bupt.aibeauty.integrate.Warp;

import java.util.Arrays;


public class ImageTransformUtils {

    public static Bitmap freeChangeByRange(Bitmap origin,int startX,int startY,int toX,int toY,int range){
        Log.d("range",range+" ");
        Bitmap deform=Bitmap.createBitmap(origin);
        OperatorS operatorS=new OperatorS(origin,deform);
        Warp.testWarp(new float[]{startX,startY},new float[]{toX,toY},origin.getHeight(),origin.getWidth(),range,operatorS);
        return (Bitmap) operatorS.getDeformImg();
    }
    public static Bitmap freeChange(Bitmap origin,int startX,int startY,int toX,int toY,int range){
        Bitmap deform=Bitmap.createBitmap(origin);
        OperatorS operatorS=new OperatorS(origin,deform);
        Warp.warp(new float[]{startX,startY},new float[]{toX,toY},origin.getHeight(),origin.getWidth(),operatorS);
        return (Bitmap) operatorS.getDeformImg();
    }
    public static Bitmap changeHeight(Bitmap origin,int startY,int areaHeight,float degree){
        //Log.d("height",origin.getHeight()+" "+origin.getWidth()+" "+startY+" "+areaHeight+" "+degree);
        Scale scale=new Scale(origin);
        return scale.scaleVertical(startY,areaHeight,degree);
    }

    public static Bitmap changeByAffilne(Bitmap origin,int[][] p,int[][] q) {
        //Log.d("imagetrans", "\n"+Arrays.deepToString(p)+"\n"+Arrays.deepToString(q));
        Bitmap deform=Bitmap.createBitmap(origin.getWidth(),origin.getHeight(),origin.getConfig());
        OperatorS operatorS=new OperatorS(origin,deform);
        ConIAfflineS affline = new ConIAfflineS(origin.getHeight(),origin.getWidth(),operatorS);

        //Log.d("imagetrans",origin.getHeight()+" "+origin.getWidth());
        //IAfflineS affline=new IAfflineS(p,q,origin.getHeight(),origin.getWidth(),operatorS);
        //affline.changeImage();
        try {
            affline.changeImage(p,q);
        } catch (Exception e) {
            Log.e("imageTrans11",e.toString());
        }
        return (Bitmap) operatorS.getDeformImg();
    }
}
