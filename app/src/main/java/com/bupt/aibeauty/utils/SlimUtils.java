package com.bupt.aibeauty.utils;

import android.graphics.Point;

import java.util.List;

public class SlimUtils {
    public static double armDefaultValue=0;
    public static double waistDefaultValue=0;
    public static double legDefaultValue=0;
    public static double armCurrentValue=armDefaultValue;
    public static double waistCurrentValue=waistDefaultValue;
    public static double legCurrentValue=legDefaultValue;

    public static List<Point> armLeftFrom;
    public static List<Point> armLeftTo;
    public static List<Point> armRightFrom;
    public static List<Point> armRightTo;

    public static List<Point> waistLeftFrom;
    public static List<Point> waistLeftTo;
    public static List<Point> waistRightFrom;
    public static List<Point> waistRightTo;

    public static List<Point> legLeftFrom;
    public static List<Point> legLeftTo;
    public static List<Point> legRightFrom;
    public static List<Point> legRightTo;

    public static void clear(){
        armCurrentValue=armDefaultValue;
        waistCurrentValue=waistDefaultValue;
        legCurrentValue=legDefaultValue;
        armLeftFrom=null;
        armLeftTo=null;
        armRightFrom=null;
        armRightTo=null;

        waistLeftFrom=null;
        waistLeftTo=null;
        waistRightFrom=null;
        waistRightTo=null;

        legLeftFrom=null;
        legLeftTo=null;
        legRightFrom=null;
        legRightTo=null;
    }
}
