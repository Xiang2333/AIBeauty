package com.bupt.aibeauty.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class BitmapUtils {
    public static Bitmap originBitmap=null;
    public static Bitmap deformBitmap=null;
    public static Bitmap tempBitmap=null;
    private static String appDirName="Beauty";
    //获取像素数组
    public static int[] getPicturePixels(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        /*
            void setPixels (int[] pixels,  // 设置像素数组，对应点的像素被放在数组中的对应位置，像素的argb值全包含在该位置中
            int offset,    // 设置偏移量，我们截图的位置就靠此参数的设置
            int stride,    // 设置一行打多少像素，通常一行设置为bitmap的宽度，
            int x,         // 设置开始绘图的x坐标
            int y,         // 设置开始绘图的y坐标
            int width,     // 设置绘制出图片的宽度
            int height)    // 设置绘制出图片的高度
         */
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        return pixels;
    }
    //从像素矩阵中分离处 透明度 红色通道 绿色通道 蓝色通道
    public static int[][] parseARGBFromPixels(int[] pixels){
        int[][] ARGB=new int[4][pixels.length];
        for(int i=0;i<pixels.length;i++){
            int pixel=pixels[i];
            int a=(pixel&0xff000000)>>>24;//取透明度
            int r=(pixel&0x00ff0000)>>>16;//取红色
            int g=(pixel&0x0000ff00)>>>8;//取绿色
            int b=(pixel&0x000000ff);//取蓝色
            ARGB[0][i]=a;
            ARGB[1][i]=r;
            ARGB[2][i]=g;
            ARGB[3][i]=b;
        }
        return ARGB;
    }
    //将新的ARGB值放入pixels数组
    public static void setARGBIntoPixels(int[][] ARGB,int[] pixels){
        for(int i=0;i<pixels.length;i++){
            ARGB[1][i]=ARGB[2][i]=0;
            int pixel=(ARGB[0][i]<<24)+(ARGB[1][i]<<16)+(ARGB[2][i]<<8)+ARGB[3][i];
            pixels[i]=pixel;
        }
    }

    public static void saveBitmap(Context context,Bitmap bitmap){
        File pictureFile=FileUtils.createFile();
        try {
            FileOutputStream out = new FileOutputStream(pictureFile);
            //90表图像品质  0-100 100表示不压缩
            boolean suc=bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Toast.makeText(context,"保存成功!",Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("save",e.toString());

        }

        // 最后通知图库更新
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, pictureFile.getAbsolutePath());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(uri);
        context.sendBroadcast(intent);//这个广播的目的就是更新图库，发了这个广播进入相册就可以找到你保存的图片
    }

    public static Bitmap copyBitmap(Bitmap source){
        int bytes = source.getByteCount();
        ByteBuffer buf = ByteBuffer.allocate(bytes);
        source.copyPixelsToBuffer(buf);
        Bitmap bitmap=Bitmap.createBitmap(source.getWidth(),source.getHeight(),source.getConfig());
        bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(buf.array()));
        return bitmap;
    }

    public static Uri convertToJPG(InputStream source){
        Log.d("bitmaputil","convertToJPG");
        Bitmap bitmap=BitmapFactory.decodeStream(source);
        Uri res=null;
        BufferedOutputStream out;
        try {
            File convertFile = FileUtils.createTempFile();
            out = new BufferedOutputStream(new FileOutputStream(convertFile));
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)){
                out.flush();
                out.close();
            }
            res=Uri.fromFile(convertFile);
        } catch (Exception e) {
            Log.e("bitmaputil",e.getMessage());
        }
        return res;
    }

    public static Bitmap bindWithBonePoint(Bitmap bitmap,int[][] bonePoint){
        Bitmap scaledBitmap=Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
        scaledBitmap=scaledBitmap.copy(bitmap.getConfig(),true);
        Canvas canvas = new Canvas(scaledBitmap);
        Rect rect=new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
        canvas.drawBitmap(bitmap,null,rect,null);
        Paint paint=new Paint();
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL);

        int r=5;
        for(int i=0;i<bonePoint.length;i++){
            int x=bonePoint[i][0];
            int y=bonePoint[i][1];
            canvas.drawCircle(x,y,r,paint);
        }
        return scaledBitmap;
    }
    public static Bitmap scaleBitmap(Bitmap origin, int newWidth, int newHeight) {
        if (origin == null) {
            return null;
        }
        int height = origin.getHeight();
        int width = origin.getWidth();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);// 使用后乘
        Bitmap newBM = Bitmap.createBitmap(origin, 0, 0, width, height, matrix, false);

        return newBM;
    }

    public static Bitmap markArm(Bitmap bitmap,int[][][]Arm){
        Bitmap scaledBitmap=Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
        scaledBitmap=scaledBitmap.copy(bitmap.getConfig(),true);
        Canvas canvas = new Canvas(scaledBitmap);
        Rect rect=new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
        canvas.drawBitmap(bitmap,null,rect,null);
        Paint paint=new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);

        int r=5;


        for(int i =0; i < 3; i++){
            for(int j=0; j< Arm[i].length ;j++)
                if(Arm[i][j]!=null)canvas.drawCircle(Arm[i][j][0],Arm[i][j][1],r,paint);
        }







        //if(Arm[0][0]!=null)canvas.drawCircle(Arm[0][0][0],Arm[0][0][1],r,paint);
        //if(Arm[0][1]!=null)canvas.drawCircle(Arm[0][1][0],Arm[0][1][1],r,paint);
        //if(Arm[0][2]!=null)canvas.drawCircle(Arm[0][2][0],Arm[0][2][1],r,paint);

        ///if(Arm[1][0]!=null)canvas.drawCircle(Arm[1][0][0],Arm[1][0][1],r,paint);
        //if(Arm[1][1]!=null)canvas.drawCircle(Arm[1][1][0],Arm[1][1][1],r,paint);
        //if(Arm[1][2]!=null)canvas.drawCircle(Arm[1][2][0],Arm[1][2][1],r,paint);

        //if(Arm[2][0]!=null)canvas.drawCircle(Arm[2][0][0],Arm[2][0][1],r,paint);
        //if(Arm[2][1]!=null)canvas.drawCircle(Arm[2][1][0],Arm[2][1][1],r,paint);
        //if(Arm[2][2]!=null)canvas.drawCircle(Arm[2][2][0],Arm[2][2][1],r,paint);

        //if(Arm[0][3]!=null)canvas.drawCircle(Arm[0][3][0],Arm[0][3][1],r,paint);
        //if(Arm[0][4]!=null)canvas.drawCircle(Arm[0][4][0],Arm[0][4][1],r,paint);
        //if(Arm[0][5]!=null)canvas.drawCircle(Arm[0][5][0],Arm[0][5][1],r,paint);

        //if(Arm[1][3]!=null)canvas.drawCircle(Arm[1][3][0],Arm[1][3][1],r,paint);
        //if(Arm[1][4]!=null)canvas.drawCircle(Arm[1][4][0],Arm[1][4][1],r,paint);
        //if(Arm[1][5]!=null)canvas.drawCircle(Arm[1][5][0],Arm[1][5][1],r,paint);

        //if(Arm[2][3]!=null)canvas.drawCircle(Arm[2][3][0],Arm[2][3][1],r,paint);
        //if(Arm[2][4]!=null)canvas.drawCircle(Arm[2][4][0],Arm[2][4][1],r,paint);
        //if(Arm[2][5]!=null)canvas.drawCircle(Arm[2][5][0],Arm[2][5][1],r,paint);

        return scaledBitmap;
    }
    public static Bitmap markWaist(Bitmap bitmap,int[][]waist){
        Bitmap scaledBitmap=Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
        scaledBitmap=scaledBitmap.copy(bitmap.getConfig(),true);
        Canvas canvas = new Canvas(scaledBitmap);
        Rect rect=new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
        canvas.drawBitmap(bitmap,null,rect,null);
        Paint paint=new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);

        int r=5;
        canvas.drawCircle(waist[0][0],waist[0][1],r,paint);
        canvas.drawCircle(waist[1][0],waist[1][1],r,paint);

        return scaledBitmap;
    }
    public static Bitmap markLeg(Bitmap bitmap,int[][][]leg){
        Bitmap scaledBitmap=Bitmap.createScaledBitmap(bitmap,bitmap.getWidth(),bitmap.getHeight(),true);
        scaledBitmap=scaledBitmap.copy(bitmap.getConfig(),true);
        Canvas canvas = new Canvas(scaledBitmap);
        Rect rect=new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
        canvas.drawBitmap(bitmap,null,rect,null);
        Paint paint=new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);

        int r=5;
        Log.d("leg", Arrays.deepToString(leg));

        for(int i =0; i < 3; i++){
            for(int j=0; j< leg[i].length ;j++)
                if(leg[i][j]!=null)canvas.drawCircle(leg[i][j][0],leg[i][j][1],r,paint);
        }

        //if(leg[0][0]!=null)canvas.drawCircle(leg[0][0][0],leg[0][0][1],r,paint);
        //if(leg[0][1]!=null)canvas.drawCircle(leg[0][1][0],leg[0][1][1],r,paint);
        //if(leg[0][2]!=null)canvas.drawCircle(leg[0][2][0],leg[0][2][1],r,paint);

        //if(leg[1][0]!=null)canvas.drawCircle(leg[1][0][0],leg[1][0][1],r,paint);
        //if(leg[1][1]!=null)canvas.drawCircle(leg[1][1][0],leg[1][1][1],r,paint);
        //if(leg[1][2]!=null)canvas.drawCircle(leg[1][2][0],leg[1][2][1],r,paint);

        //if(leg[2][0]!=null)canvas.drawCircle(leg[2][0][0],leg[2][0][1],r,paint);
        //if(leg[2][1]!=null)canvas.drawCircle(leg[2][1][0],leg[2][1][1],r,paint);
        //if(leg[2][2]!=null)canvas.drawCircle(leg[2][2][0],leg[2][2][1],r,paint);


        return scaledBitmap;

    }
}