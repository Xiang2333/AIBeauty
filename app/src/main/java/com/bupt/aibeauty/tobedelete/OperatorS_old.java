package com.bupt.aibeauty.tobedelete;
import android.graphics.Bitmap;

import com.bupt.aibeauty.integrate.ImageOperator;

import java.nio.ByteBuffer;

public class OperatorS_old implements ImageOperator {
    Bitmap bitmap;
    Bitmap deformBitmap;
    byte[] orgin_byte;
    byte[] defom_byte;
    int width;
    int height;
    //int a=99999999,b=0,c=99999999,d=0;
    public OperatorS_old(Bitmap bitmapIn, Bitmap bitmapOut){
        this.bitmap = bitmapIn;
        this.height = bitmapIn.getHeight();
        this.width = bitmapIn.getWidth();
        int bytes = bitmapIn.getByteCount();
        ByteBuffer buf = ByteBuffer.allocate(bytes);
        bitmapIn.copyPixelsToBuffer(buf);
        this.orgin_byte = buf.array();
        this.defom_byte = this.orgin_byte.clone();
        this.deformBitmap  = bitmapOut;
    }

    @Override
    public void setPixelColor(int x, int y, int sourceX, int sourceY) {
        //Log.d("operatorS",x+","+y+"  "+sourceX+","+sourceY);
        /*
        if(y<a) a=y;
        if(y>b) b=y;
        if(x<c) c=x;
        if(x>d) d=x;
        Log.d("operatorS",a+" "+b+" "+c+" "+d);
         */
        this.defom_byte[y*width*4+x*4+0]=this.orgin_byte[sourceY*width*4+sourceX*4+0];
        this.defom_byte[y*width*4+x*4+1]=this.orgin_byte[sourceY*width*4+sourceX*4+1];
        this.defom_byte[y*width*4+x*4+2]=this.orgin_byte[sourceY*width*4+sourceX*4+2];
        this.defom_byte[y*width*4+x*4+3]=this.orgin_byte[sourceY*width*4+sourceX*4+3];
    }

    @Override
    public Object getDeformImg() {
        this.saveChange();
        return this.deformBitmap;
    }

    @Override
    public void saveChange() {
        this.deformBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(this.defom_byte));
    }
}