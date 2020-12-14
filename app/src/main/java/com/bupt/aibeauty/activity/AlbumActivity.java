package com.bupt.aibeauty.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.aibeauty.R;
import com.bupt.aibeauty.adapter.RecycleViewAdapter;
import com.bupt.aibeauty.utils.BitmapUtils;
import com.bupt.aibeauty.utils.ModelUtils;
import com.bupt.aibeauty.utils.ViewUtils;
import com.bupt.aibeauty.tobedelete.MyView;

import org.opencv.android.OpenCVLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AlbumActivity extends Activity{
    private ImageView imageView;
    private RecyclerView recyclerView;
    private RecycleViewAdapter adapter;
    private RelativeLayout includeView;
    private MyView myView;
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, null);
        }

        String img_path=getIntent().getStringExtra("img_path");
        //解析出图像的uri
        Uri uri=Uri.parse(img_path);
        this.uri=uri;
        initView();
        Log.d("AlbumActivity",img_path);
        Bitmap bitmap=null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
        } catch (IOException e) {
            Log.e("AlbumActivity","get bitmap error");
        }
        if(bitmap!=null){
            imageView.setImageBitmap(bitmap);
            BitmapUtils.originBitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
            ModelUtils.reset();
            ModelUtils.runModels(bitmap);
            /*
            ViewUtils.dialog.dismiss();
            Bitmap mask=null;
            List<Point> list=null;
            try {
                mask=u2netResult.get();
                list=openposeResult.get();
            } catch (Exception e) {
                Log.e("album",e.toString());
            }
            int[][] p=new int[list.size()][2];
            for(int i=0;i<p.length;i++){
                p[i][0]=list.get(i).x;
                p[i][1]=list.get(i).y;
            }
            Log.d("album", Arrays.deepToString(p));
            ProfilePointCOCO profile=new ProfilePointCOCO(mask,p);
            Bitmap bone=BitmapUtils.markArm(mask,profile.getLeftArm(3));
            bone=BitmapUtils.markArm(bone,profile.getRightArm(3));
            bone=BitmapUtils.markWaist(bone,profile.getWaistPoint());
            bone=BitmapUtils.markLeg(bone,profile.getLeftLeg(3));
            bone=BitmapUtils.markLeg(bone,profile.getRightLeg(3));
            imageView.setImageBitmap(bone);
            */

            ViewUtils.imageView=imageView;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean defaultReact=super.onKeyDown(keyCode, event);
        ModelUtils.reset();
        return defaultReact;
    }

    public void initView(){

        imageView=findViewById(R.id.show_img_from_album);
        recyclerView=findViewById(R.id.recy_view);
        includeView=findViewById(R.id.include_view);
        myView=findViewById(R.id.myview);
        List<String> btn_func=new ArrayList<>();
        List<Integer> btn_id=new ArrayList<>();
        btn_func.add(getString(R.string.album_height));btn_id.add(R.id.heightButton);
        btn_func.add(getString(R.string.album_slim));btn_id.add(R.id.slimButton);
        btn_func.add(getString(R.string.album_shoulder));btn_id.add(R.id.shoulderButton);
        btn_func.add(getString(R.string.album_hip));btn_id.add(R.id.hipButton);
        btn_func.add(getString(R.string.album_chest));btn_id.add(R.id.chestButton);
        btn_func.add(getString(R.string.album_head));btn_id.add(R.id.headButton);
        btn_func.add(getString(R.string.album_free));btn_id.add(R.id.freeButton);
        adapter=new RecycleViewAdapter(this,btn_func,btn_id,uri);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }
}
