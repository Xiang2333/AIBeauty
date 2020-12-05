package com.bupt.aibeauty.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.aibeauty.R;

import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;


public class FilterRecyAdapter extends RecyclerView.Adapter<FilterRecyAdapter.FilterHolder>{
    private List<GPUImageFilter> filters;
    private List<Drawable> filterImages;
    private List<String> filterNames;
    private GPUImageView imageView;

    public FilterRecyAdapter(GPUImageView imageView,List<GPUImageFilter> filters,List<Drawable> filterImages,List<String> filterNames){
        this.imageView=imageView;
        this.filters=filters;
        this.filterImages=filterImages;
        this.filterNames=filterNames;
    }

    @NonNull
    @Override
    public FilterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_item,parent,false);
        FilterHolder myHolder=new FilterHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FilterHolder holder, int position) {
        holder.textView.setText(filterNames.get(position));
        holder.imageView.setBackground(filterImages.get(position));
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView.setFilter(filters.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return filters.size();
    }

    class FilterHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        public FilterHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.filter_img);
            textView=itemView.findViewById(R.id.filter_text);
        }
    }
}
