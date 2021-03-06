package com.bupt.aibeauty.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bupt.aibeauty.utils.FileUtils;
import com.bupt.aibeauty.utils.ModelUtils;

import org.pytorch.Module;

public class U2NETLoadTask extends AsyncTask<Void,Void,Void> {
    private Context context;

    public U2NETLoadTask(Context context){
        this.context=context;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        long a=System.currentTimeMillis();
        try {
            Module u2net=Module.load(FileUtils.assetFilePath(context, "u2net.pt"));
            ModelUtils.setU2net(u2net);
        } catch (Exception e) {
            Log.e("loadModel",e.toString());
        }
        long b=System.currentTimeMillis();
        Log.d("model","u2net loaded takes "+(b-a)+" ms");
        //Toast.makeText(context,(b-a)+" ",Toast.LENGTH_LONG).show();
        return null;
    }
}