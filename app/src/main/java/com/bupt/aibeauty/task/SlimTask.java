package com.bupt.aibeauty.task;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.bupt.aibeauty.adapter.RecycleViewAdapter;
import com.bupt.aibeauty.integrate.ConIAfflineS;
import com.bupt.aibeauty.utils.ImageTransformUtils;
import com.bupt.aibeauty.utils.SlimUtils;

import java.util.ArrayList;
import java.util.List;

public class SlimTask extends AsyncTask<Object,Void,Void> {
    @Override
    protected Void doInBackground(Object... objects) {
        //手 脚一起变化 腰单独变化
        ConIAfflineS.defaultSize = 60;
        Bitmap afterChange = (Bitmap) objects[0];
        ImageView view = (ImageView) objects[1];
        List<Point> from = new ArrayList<>();
        List<Point> to = new ArrayList<>();
        List<Point> from2 = new ArrayList<>();
        List<Point> to2 = new ArrayList<>();
        boolean flag = false;
        long a = System.currentTimeMillis();

        if (SlimUtils.armCurrentValue != SlimUtils.armDefaultValue && SlimUtils.armLeftFrom != null && SlimUtils.armLeftTo != null && SlimUtils.armRightFrom != null && SlimUtils.armRightTo != null) {
            if (flag) {
                for (int i = 8; i < SlimUtils.armLeftFrom.size(); i++) {
                    from.add(SlimUtils.armLeftFrom.get(i));
                    to.add(SlimUtils.armLeftTo.get(i));
                }
                for (int i = 8; i < SlimUtils.armRightFrom.size(); i++) {
                    from.add(SlimUtils.armRightFrom.get(i));
                    to.add(SlimUtils.armRightTo.get(i));
                }
            } else {
                for (int i = 0; i < SlimUtils.armLeftFrom.size(); i++) {
                    from.add(SlimUtils.armLeftFrom.get(i));
                    to.add(SlimUtils.armLeftTo.get(i));
                }
                flag = true;
                for (int i = 8; i < SlimUtils.armRightFrom.size(); i++) {
                    from.add(SlimUtils.armRightFrom.get(i));
                    to.add(SlimUtils.armRightTo.get(i));
                }
            }
        }
        if (SlimUtils.legCurrentValue != SlimUtils.legDefaultValue && SlimUtils.legLeftFrom != null && SlimUtils.legLeftTo != null && SlimUtils.legRightFrom != null && SlimUtils.legRightTo != null) {
            if (flag) {
                for (int i = 8; i < SlimUtils.legLeftFrom.size(); i++) {
                    from.add(SlimUtils.legLeftFrom.get(i));
                    to.add(SlimUtils.legLeftTo.get(i));
                }
                for (int i = 8; i < SlimUtils.legRightFrom.size(); i++) {
                    from.add(SlimUtils.legRightFrom.get(i));
                    to.add(SlimUtils.legRightTo.get(i));
                }
            } else {
                for (int i = 0; i < SlimUtils.legLeftFrom.size(); i++) {
                    from.add(SlimUtils.legLeftFrom.get(i));
                    to.add(SlimUtils.legLeftTo.get(i));
                }
                flag = true;
                for (int i = 8; i < SlimUtils.legRightFrom.size(); i++) {
                    from.add(SlimUtils.legRightFrom.get(i));
                    to.add(SlimUtils.legRightTo.get(i));
                }
            }
        }
        flag = false;
        if (SlimUtils.waistCurrentValue != SlimUtils.waistDefaultValue && SlimUtils.waistLeftFrom != null && SlimUtils.waistLeftTo != null && SlimUtils.waistRightFrom != null && SlimUtils.waistRightTo != null) {
            //Log.d("recy",SlimUtils.waistLeftFrom.toString());
            //Log.d("recy",SlimUtils.waistLeftTo.toString());
            //Log.d("recy",SlimUtils.waistRightFrom.toString());
            //Log.d("recy",SlimUtils.waistRightTo.toString());
            if (flag) {
                for (int i = 8; i < SlimUtils.waistLeftFrom.size(); i++) {
                    from2.add(SlimUtils.waistLeftFrom.get(i));
                    to2.add(SlimUtils.waistLeftTo.get(i));
                }
                for (int i = 8; i < SlimUtils.waistRightFrom.size(); i++) {
                    from2.add(SlimUtils.waistRightFrom.get(i));
                    to2.add(SlimUtils.waistRightTo.get(i));
                }
            } else {
                for (int i = 0; i < SlimUtils.waistLeftFrom.size(); i++) {
                    from2.add(SlimUtils.waistLeftFrom.get(i));
                    to2.add(SlimUtils.waistLeftTo.get(i));
                }
                flag = true;
                for (int i = 8; i < SlimUtils.waistRightFrom.size(); i++) {
                    from2.add(SlimUtils.waistRightFrom.get(i));
                    to2.add(SlimUtils.waistRightTo.get(i));
                }
            }
        }
        int[][] p;
        int[][] q;
        if (from2.size() > 0) {
            p = new int[from2.size()][2];
            q = new int[to2.size()][2];
            for (int i = 0; i < p.length; i++) {
                p[i][0] = from2.get(i).x;
                p[i][1] = from2.get(i).y;
                q[i][0] = to2.get(i).x;
                q[i][1] = to2.get(i).y;
            }
            afterChange = ImageTransformUtils.changeByAffilne(afterChange, p, q);
        }
        if (from.size() > 0) {
            p = new int[from.size()][2];
            q = new int[to.size()][2];
            for (int i = 0; i < p.length; i++) {
                p[i][0] = from.get(i).x;
                p[i][1] = from.get(i).y;
                q[i][0] = to.get(i).x;
                q[i][1] = to.get(i).y;
            }
            afterChange = ImageTransformUtils.changeByAffilne(afterChange, p, q);
        }

        long b = System.currentTimeMillis();
        Log.d("task", "imgtrans takes " + ((b - a)) + " ms");

        view.setImageBitmap(afterChange);
        return null;
    }
}
