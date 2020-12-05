package com.bupt.aibeauty.integrate;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Log;

import java.text.BreakIterator;

public class Scale {
    Bitmap image;
    Bitmap deformImage;
    int topHeight;
    int centerHeight;
    int downHeight;
    public Scale(Bitmap image){
        /*
        image: 源图片，要求image必须是mutable即可修改的。
        defomImage: 目标图片对象
         */
        this.image = image;
    }

    //0-2
    public Bitmap scaleVertical(int y, int areaHeight,float scaleY){
        int width = image.getWidth();
        int height = image.getHeight();
        Matrix matrix = new Matrix();
        matrix.setScale(1,scaleY);
        Bitmap top = Bitmap.createBitmap(this.image,0,0,width,y);topHeight=top.getHeight();
        Bitmap center = Bitmap.createBitmap(this.image, 0, y,width,areaHeight,matrix,false);centerHeight=center.getHeight();
        //Log.d("sacle",center.getHeight()+"");
        //Log.d("scale","height1="+y+" "+"height2="+center.getHeight()+" "+"height3="+(height-top.getHeight()-center.getHeight()));
        Bitmap down = Bitmap.createBitmap(this.image, 0, y + areaHeight,width, height-y-areaHeight);downHeight=down.getHeight();

        Rect topRect = new Rect(0,0,width,top.getHeight());
        Rect centerRect = new Rect(0,top.getHeight(),width,top.getHeight()+center.getHeight());
        Rect downRect = new Rect(0,top.getHeight()+center.getHeight(),width,top.getHeight()+center.getHeight()+down.getHeight());

        this.deformImage=Bitmap.createBitmap(width,top.getHeight()+center.getHeight()+down.getHeight(),image.getConfig());
        Canvas canvas = new Canvas(this.deformImage);
        canvas.drawBitmap(top,null,topRect,null);
        canvas.drawBitmap(center,null,centerRect,null);
        canvas.drawBitmap(down,null,downRect,null);

        return this.deformImage;
    }
}
