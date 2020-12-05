package com.bupt.aibeauty.utils;

import android.graphics.Point;

public class PointUtils {

    public static Point getPointBetween(Point from,Point to,double rate,double p){
        rate*=p;
        Point point=new Point(from);
        point.x+=(to.x-from.x)*rate;
        point.y+=(to.y-from.y)*rate;
        return point;
    }

    public static double getDistance(Point from,Point to){
        return Math.sqrt((from.x-to.x)*(from.x-to.x)+(from.y-to.y)*(from.y-to.y));
    }
}

