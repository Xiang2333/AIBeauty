package com.bupt.aibeauty.integrate;

import android.graphics.Bitmap;
import android.graphics.Color;
import java.nio.ByteBuffer;

public class OperatorS implements ImageOperator {
    Bitmap bitmap;
    Bitmap deformBitmap;
    Bitmap profileBitmap;
    Bitmap binaryBitmap;
    byte[] orgin_byte;
    byte[] defom_byte;

    int width;
    int height;
    int pixelColor =4;
    public OperatorS(Bitmap bitmapIn,Bitmap bitmapOut){
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
        this.defom_byte[y*width*pixelColor+x*pixelColor+0]=this.orgin_byte[sourceY*width*pixelColor+sourceX*pixelColor+0];
        this.defom_byte[y*width*pixelColor+x*pixelColor+1]=this.orgin_byte[sourceY*width*pixelColor+sourceX*pixelColor+1];
        this.defom_byte[y*width*pixelColor+x*pixelColor+2]=this.orgin_byte[sourceY*width*pixelColor+sourceX*pixelColor+2];
        this.defom_byte[y*width*pixelColor+x*pixelColor+3]=this.orgin_byte[sourceY*width*pixelColor+sourceX*pixelColor+3];
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

    public void setProfile(Bitmap profileBitmap){
        this.profileBitmap = profileBitmap;
    }

    public Bitmap getBinaryBitmap(){
        if(this.binaryBitmap == null){
            this.binaryBitmap = Bitmap.createBitmap(width,height,this.profileBitmap.getConfig());
            for(int y = 0 ; y < height; y++){
                for(int x = 0; x <width ; x++){
                    int col = profileBitmap.getPixel(x, y);
                    //得到alpha通道的值
                    int alpha = col & 0xFF000000;
                    //得到图像的像素RGB的值
                    int red = (col & 0x00FF0000) >> 16;
                    int green = (col & 0x0000FF00) >> 8;
                    int blue = (col & 0x000000FF);
                    // 用公式X = 0.3×R+0.59×G+0.11×B计算出X代替原来的RGB
                    int gray = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                    //对图像进行二值化处理
                    if (gray <= 95) {
                        gray = 0;
                    } else {
                        gray = 255;
                    }
                    // 新的ARGB
                    int newColor = alpha | (gray << 16) | (gray << 8) | gray;
                    //设置新图像的当前像素值
                    this.binaryBitmap.setPixel(x, y, newColor);
                }
            }

            //骨骼点上色
            for(int i = 0; i < BonePoint.example_5.length; i++){
                binaryBitmap.setPixel(BonePoint.example_5[i][0],BonePoint.example_5[i][1], Color.RED);
                binaryBitmap.setPixel(BonePoint.example_5[i][0]-1,BonePoint.example_5[i][1]-1, Color.RED);
                binaryBitmap.setPixel(BonePoint.example_5[i][0]-1,BonePoint.example_5[i][1], Color.RED);
                binaryBitmap.setPixel(BonePoint.example_5[i][0],BonePoint.example_5[i][1]-1, Color.RED);

            }

        }
        return  this.binaryBitmap;
    }

    public int[] getOriginValue(int x, int y, int[] colorInt){
        /*
        if(y*width*pixelColor  + x*pixelColor + pixelColor >= this.orgin_byte.length){
            Log.i("aaa",String.format("%d %d ", x, y));
        }
        */
        for(int i =0; i< this.pixelColor; i++){
            colorInt[i] = this.orgin_byte[y*width*pixelColor  + x*pixelColor +i] & 0xff;
        }
        return colorInt;
    }

    public byte getOriginValue(int x, int y, int i){
        return this.orgin_byte[y*width*pixelColor+ x*pixelColor +i];
    }
    public void setDeformValue(int x, int y, int i, byte value){
        this.defom_byte[y*width*pixelColor + x*pixelColor +i] = value;
    }


}

