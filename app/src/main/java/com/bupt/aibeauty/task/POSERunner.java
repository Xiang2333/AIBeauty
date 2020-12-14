package com.bupt.aibeauty.task;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.bupt.aibeauty.utils.ModelUtils;

import java.util.List;

public class POSERunner extends AsyncTask<Bitmap,Void,Void> {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected Void doInBackground(Bitmap... bitmaps) {
        while(!ModelUtils.modelLoadFin()){
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                Log.e("poserunner",e.toString());
            }
        }
        long a=System.currentTimeMillis();
        ModelUtils.reRunPOSE();
        List<Point> res=ModelUtils.runPose(bitmaps[0]);
        ModelUtils.setPointList(res);
        long b=System.currentTimeMillis();
        Log.d("model","openpose run finish takes "+(b-a)+" ms");
        //Toast.makeText(context,(b-a)+" ",Toast.LENGTH_LONG).show();
        return null;
    }
}
