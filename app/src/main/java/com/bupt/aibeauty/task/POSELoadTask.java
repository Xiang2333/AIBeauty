package com.bupt.aibeauty.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.bupt.aibeauty.utils.FileUtils;
import com.bupt.aibeauty.utils.ModelUtils;
import com.bupt.aibeauty.utils.ViewUtils;

import org.pytorch.Module;

public class POSELoadTask extends AsyncTask<Void,Void,Void> {
    private Context context;
    public POSELoadTask(Context context){
        this.context=context;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        long a=System.currentTimeMillis();
        try {
            Module pose= Module.load(FileUtils.assetFilePath(context, "pose.pt"));
            ModelUtils.setPose(pose);
        } catch (Exception e) {
            Log.e("loadModel",e.toString());
        }
        long b=System.currentTimeMillis();
        Log.d("model","pose loaded takes "+(b-a)+" ms");
        //Toast.makeText(context,(b-a)+" ", Toast.LENGTH_LONG).show();
        return null;
    }
}
