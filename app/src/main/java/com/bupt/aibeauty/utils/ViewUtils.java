package com.bupt.aibeauty.utils;

import android.app.AlertDialog;
import android.widget.ImageView;

import com.bupt.aibeauty.view.MySeekBar;

import java.util.ArrayList;

public class ViewUtils {
    public static int screenHeight;
    public static int screenWidth;
    public static ImageView imageView;
    public static AlertDialog dialog;
    public static ArrayList<MySeekBar> slimSeekBar=new ArrayList<>();
    public static boolean flag=false;
    public static void clearAll(){
        if(imageView!=null){
            imageView=null;
        }
        if(slimSeekBar!=null){
            slimSeekBar.clear();
            slimSeekBar=null;
        }
    }
}
