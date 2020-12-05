package com.bupt.aibeauty.integrate;

import android.graphics.Bitmap;
import android.graphics.Color;

public class ProfileOperator {
    public Bitmap profile;
    public Bitmap binaryProifle;
    public byte[] byteProfile;
    public int[][] bonePoint;

    int height;
    int width;

    ProfileOperator(Bitmap profile, int[][] bonePoint){
        this.profile = profile;
        this.bonePoint = bonePoint;

        this.height = this.profile.getHeight();
        this.width = this.profile.getWidth();
        this.byteProfile = new byte[height*width];

        genBinaryBitmap();
    }

    public void genBinaryBitmap(){
        if(this.binaryProifle == null){
            this.binaryProifle = Bitmap.createBitmap(width,height,this.profile.getConfig());
            for(int y = 0 ; y < height; y++){
                for(int x = 0; x <width ; x++){
                    int col = profile.getPixel(x, y);
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
                    if(this.byteProfile != null)
                        this.byteProfile[y*this.width + x] = gray == 0? (byte) 0 :(byte)-1;

                    // 新的ARGB
                    int newColor = alpha | (gray << 16) | (gray << 8) | gray;
                    //设置新图像的当前像素值
                    this.binaryProifle.setPixel(x, y, newColor);
                }
            }

        }
    }

    public boolean isPixelBlack(int x, int y){
        if(x >= this.width || x < 0)
            return false;
        if(y >= this.height || y< 0)
            return false;
        byte color = byteProfile[y*width+x];
        if(color>=0)
            return true;
        else
            return false;
    }
    //to be delete
    public static void markBitmap(Bitmap bitmap, int x, int y){
        if(x>= bitmap.getWidth() || x <0)
            return;
        if(y>= bitmap.getHeight() || y<0)
            return;
        else{
//            for(int i = x-2; i<=x+2; i++){
//                for(int j = y - 2; y<y+2; j++){
//                    if(i>=0 && x < bitmap.getWidth() && j>=0 && j< bitmap.getHeight()){
//                        bitmap.setPixel(i,j,Color.RED);
//                    }
//                }
//            }
            bitmap.setPixel(x-1,y-1, Color.RED);
            bitmap.setPixel(x-1,y,Color.RED);
            bitmap.setPixel(x,y-1,Color.RED);
            bitmap.setPixel(x,y,Color.RED);
        }
    }
}
