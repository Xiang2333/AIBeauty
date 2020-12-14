package com.bupt.aibeauty.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.bupt.aibeauty.utils.ModelUtils;

public class U2NETRunner extends AsyncTask<Bitmap,Void,Void> {
    @Override
    protected Void doInBackground(Bitmap... bitmaps) {
        while(!ModelUtils.modelLoadFin()){
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                Log.e("u2netrunner",e.toString());
            }
        }
        long a=System.currentTimeMillis();
        ModelUtils.reRunU2NET();
        Bitmap res=ModelUtils.runU2net(bitmaps[0]);
        ModelUtils.setMask(res);
        long b=System.currentTimeMillis();
        Log.d("model","u2net run finish takes "+(b-a)+" ms");
        //Toast.makeText(context,(b-a)+" ",Toast.LENGTH_LONG).show();
        return null;
    }
}
