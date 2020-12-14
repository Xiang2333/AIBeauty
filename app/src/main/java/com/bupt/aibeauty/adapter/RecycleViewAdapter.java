package com.bupt.aibeauty.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.aibeauty.R;
import com.bupt.aibeauty.activity.beauty.ChestActivity;
import com.bupt.aibeauty.activity.beauty.FreeActivity;
import com.bupt.aibeauty.activity.beauty.HeadActivity;
import com.bupt.aibeauty.activity.beauty.HeightActivity;
import com.bupt.aibeauty.activity.beauty.HipActivity;
import com.bupt.aibeauty.activity.beauty.ShoulderActivity;
import com.bupt.aibeauty.activity.beauty.SlimActivity;

import java.util.List;


public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyHolder> {
    private Context context;
    private List<String> btn_func;
    private List<Integer> btn_id;
    private Uri uri;

    public RecycleViewAdapter(Context context, List<String> btn_func,List<Integer> btn_id,Uri uri) {
        this.context = context;
        this.btn_func = btn_func;
        this.btn_id=btn_id;
        this.uri=uri;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.button_item,viewGroup,false);
        MyHolder myHolder=new MyHolder(view);
        return myHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        final String btn_text=btn_func.get(i);
        myHolder.button.setText(btn_text);
        Drawable drawable=null;
        switch (btn_id.get(i)){
            case R.id.heightButton:
                drawable=context.getDrawable(R.drawable.height);
                break;
            case R.id.slimButton:
                drawable=context.getDrawable(R.drawable.slim);
                break;
            case R.id.shoulderButton:
                drawable=context.getDrawable(R.drawable.shoulder);
                break;
            case R.id.hipButton:
                drawable=context.getDrawable(R.drawable.hip);
                break;
            case R.id.chestButton:
                drawable=context.getDrawable(R.drawable.chest);
                break;
            case R.id.headButton:
                drawable=context.getDrawable(R.drawable.head);
                break;
            case R.id.freeButton:
                drawable=context.getDrawable(R.drawable.free);
                break;
            default:
                break;
        }
        if(drawable!=null){
            drawable.setBounds(0, 0, 80, 80);
        }
        myHolder.button.setCompoundDrawables(null,drawable,null,null);
        //点击功能按钮，显示功能窗口
        myHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                Log.d("RecycleViewAdapter",btn_text);
                switch (btn_id.get(i)){
                    case R.id.heightButton:
                        intent.setClass(context, HeightActivity.class);
                        break;
                    case R.id.slimButton:
                        intent.setClass(context, SlimActivity.class);
                        break;
                    case R.id.shoulderButton:
                        intent.setClass(context, ShoulderActivity.class);
                        break;
                    case R.id.hipButton:
                        intent.setClass(context, HipActivity.class);
                        break;
                    case R.id.chestButton:
                        intent.setClass(context, ChestActivity.class);
                        break;
                    case R.id.headButton:
                        intent.setClass(context, HeadActivity.class);
                        break;
                    case R.id.freeButton:
                        intent.setClass(context, FreeActivity.class);
                        break;
                    default:
                        break;
                }
                intent.putExtra("img_path",uri.toString());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return btn_func.size();
    }
    class MyHolder extends RecyclerView.ViewHolder{
        Button button;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            button=itemView.findViewById(R.id.recy_btn);
            button.setGravity(Gravity.CENTER);
        }
    }
}