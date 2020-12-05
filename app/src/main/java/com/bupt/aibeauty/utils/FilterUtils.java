package com.bupt.aibeauty.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.bupt.aibeauty.R;
import com.bupt.aibeauty.filters.IF1977Filter;
import com.bupt.aibeauty.filters.IFAmaroFilter;
import com.bupt.aibeauty.filters.IFBrannanFilter;
import com.bupt.aibeauty.filters.IFEarlybirdFilter;
import com.bupt.aibeauty.filters.IFHefeFilter;
import com.bupt.aibeauty.filters.IFHudsonFilter;
import com.bupt.aibeauty.filters.IFInkwellFilter;
import com.bupt.aibeauty.filters.IFLomoFilter;
import com.bupt.aibeauty.filters.IFLordKelvinFilter;
import com.bupt.aibeauty.filters.IFNashvilleFilter;
import com.bupt.aibeauty.filters.IFRiseFilter;
import com.bupt.aibeauty.filters.IFSierraFilter;
import com.bupt.aibeauty.filters.IFSutroFilter;
import com.bupt.aibeauty.filters.IFToasterFilter;
import com.bupt.aibeauty.filters.IFValenciaFilter;
import com.bupt.aibeauty.filters.IFWaldenFilter;
import com.bupt.aibeauty.filters.IFXprollFilter;

import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;

public class FilterUtils {

    private static List<GPUImageFilter> filters=null;
    private static List<Drawable> filterImages=null;
    private static List<String> filterNames=null;

    public static List<GPUImageFilter> getFilters(Context context){
        if(filters==null){
            filters=new ArrayList<>();
            filters.add(new IF1977Filter(context));
            filters.add(new IFAmaroFilter(context));
            filters.add(new IFBrannanFilter(context));
            filters.add(new IFEarlybirdFilter(context));
            filters.add(new IFHefeFilter(context));
            filters.add(new IFHudsonFilter(context));
            filters.add(new IFInkwellFilter(context));
            filters.add(new IFLomoFilter(context));
            filters.add(new IFLordKelvinFilter(context));
            filters.add(new IFNashvilleFilter(context));
            filters.add(new IFRiseFilter(context));
            filters.add(new IFSierraFilter(context));
            filters.add(new IFSutroFilter(context));
            filters.add(new IFToasterFilter(context));
            filters.add(new IFValenciaFilter(context));
            filters.add(new IFWaldenFilter(context));
            filters.add(new IFXprollFilter(context));
        }
        return filters;
    }
    public static List<Drawable> getFilterImages(Context context){
        if(filterImages==null){
            filterImages=new ArrayList<>();
            filterImages.add(context.getDrawable(R.drawable.filter_thumb_1977));
            filterImages.add(context.getDrawable(R.drawable.filter_thumb_amoro));
            filterImages.add(context.getDrawable(R.drawable.filter_thumb_brannan));
            filterImages.add(context.getDrawable(R.drawable.filter_thumb_earlybird));
            filterImages.add(context.getDrawable(R.drawable.filter_thumb_hefe));
            filterImages.add(context.getDrawable(R.drawable.filter_thumb_hudson));
            filterImages.add(context.getDrawable(R.drawable.filter_thumb_inkwell));
            filterImages.add(context.getDrawable(R.drawable.filter_thumb_lomo));
            filterImages.add(context.getDrawable(R.drawable.filter_thumb_kevin));
            filterImages.add(context.getDrawable(R.drawable.filter_thumb_nashville));
            filterImages.add(context.getDrawable(R.drawable.filter_thumb_rise));
            filterImages.add(context.getDrawable(R.drawable.filter_thumb_sierra));
            filterImages.add(context.getDrawable(R.drawable.filter_thumb_sutro));
            filterImages.add(context.getDrawable(R.drawable.filter_thumb_toastero));
            filterImages.add(context.getDrawable(R.drawable.filter_thumb_valencia));
            filterImages.add(context.getDrawable(R.drawable.filter_thumb_walden));
            filterImages.add(context.getDrawable(R.drawable.filter_thumb_xpro));
        }
        return filterImages;
    }
    public static List<String> getFilterNames(Context context){
        if(filterNames==null){
            filterNames=new ArrayList<>();
            filterNames.add(context.getString(R.string.filter_n1977));
            filterNames.add(context.getString(R.string.filter_amaro));
            filterNames.add(context.getString(R.string.filter_brannan));
            filterNames.add(context.getString(R.string.filter_Earlybird));
            filterNames.add(context.getString(R.string.filter_hefe));
            filterNames.add(context.getString(R.string.filter_hudson));
            filterNames.add(context.getString(R.string.filter_inkwell));
            filterNames.add(context.getString(R.string.filter_lomo));
            filterNames.add(context.getString(R.string.filter_kevin));
            filterNames.add(context.getString(R.string.filter_nashville));
            filterNames.add(context.getString(R.string.filter_rise));
            filterNames.add(context.getString(R.string.filter_sierra));
            filterNames.add(context.getString(R.string.filter_sutro));
            filterNames.add(context.getString(R.string.filter_toastero));
            filterNames.add(context.getString(R.string.filter_valencia));
            filterNames.add(context.getString(R.string.filter_walden));
            filterNames.add(context.getString(R.string.filter_xproii));
        }
        return filterNames;
    }
}