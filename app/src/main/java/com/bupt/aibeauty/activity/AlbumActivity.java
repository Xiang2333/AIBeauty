package com.bupt.aibeauty.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bupt.aibeauty.R;
import com.bupt.aibeauty.adapter.RecycleViewAdapter;
import com.bupt.aibeauty.integrate.ConIAfflineS;
import com.bupt.aibeauty.integrate.OperatorS;
import com.bupt.aibeauty.integrate.ProfilePoint;
import com.bupt.aibeauty.utils.BitmapUtils;
import com.bupt.aibeauty.utils.ImageTransformUtils;
import com.bupt.aibeauty.utils.ModelUtils;
import com.bupt.aibeauty.utils.ViewUtils;
import com.bupt.aibeauty.view.MyView;

import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;


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
        ModelUtils.context=this;
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
            /*
            int[] pixels=getImagePixels(bitmap);
            int[] newPixels= Arrays.copyOfRange(pixels,0,bitmap.getHeight()/2*bitmap.getWidth());
            int[][] ARGB=parseARGBFromPixels(newPixels);
            setARGBIntoPixels(ARGB,newPixels);
            Bitmap mutableBitmap=Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight()/2, Bitmap.Config.ARGB_8888);
            mutableBitmap.setPixels(newPixels,0,bitmap.getWidth(),0,0,bitmap.getWidth(),bitmap.getHeight()/2);
             */
            //ProfilePoint profilePoint=new ProfilePoint(bitmap, BonePoint.example_2);
            //Bitmap bitmapWithPoint= BitmapUtils.bindWithBonePoint(bitmap,BonePoint.example_2);

            //Bitmap bitmapWithPoint=bitmap;
            //bitmapWithPoint=BitmapUtils.markWaist(bitmapWithPoint,profilePoint.getWaistPoint());
            //bitmapWithPoint=BitmapUtils.markArm(bitmapWithPoint,profilePoint.getLeftArm(3));
            //bitmapWithPoint=BitmapUtils.markArm(bitmapWithPoint,profilePoint.getRightArm(3));
            //bitmapWithPoint=BitmapUtils.markLeg(bitmapWithPoint,profilePoint.getLeftLeg(3));
            //bitmapWithPoint=BitmapUtils.markLeg(bitmapWithPoint,profilePoint.getRightLeg(3));

            /*
            ProfilePoint profilePoint=ModelUtils.getProfilePoint(bitmap);
            Bitmap bitmapWithPoint= ModelUtils.getMask(bitmap);
            bitmapWithPoint=BitmapUtils.markWaist(bitmapWithPoint,profilePoint.getWaistPoint());
            bitmapWithPoint=BitmapUtils.markArm(bitmapWithPoint,profilePoint.getLeftArm(3));
            bitmapWithPoint=BitmapUtils.markArm(bitmapWithPoint,profilePoint.getRightArm(3));
            bitmapWithPoint=BitmapUtils.markLeg(bitmapWithPoint,profilePoint.getLeftLeg(3));
            bitmapWithPoint=BitmapUtils.markLeg(bitmapWithPoint,profilePoint.getRightLeg(3));
            imageView.setImageBitmap(bitmapWithPoint);
            */

            //Bitmap mask=ModelUtils.runU2net(bitmap);
            //imageView.setImageBitmap(mask);

            //Bitmap mask=((BitmapDrawable)this.getDrawable(R.drawable.mask_5)).getBitmap();
            //ProfilePoint profilePoint=new ProfilePoint(mask,BonePoint.example_5);
            //Log.d("album",Arrays.deepToString(profilePoint.getWaistPoint()));

            /*  work
            int[][] p=new int[][]{{277,420},{365,420}};
            int[][] q=new int[][]{{300,420},{350,420}};
             */
            /* work
            int[][] p=new int[][]{{277,420},{365,420}};
            int[][] q=new int[][]{{250,420},{380,420}};
             */
            //Bitmap deform= ImageTransformUtils.changeByAffilne(bitmap,p,q);

            /*
            long timeStart=System.currentTimeMillis();
            Bitmap mask=ModelUtils.runU2net(bitmap);
            long timeEnd=System.currentTimeMillis();
            Log.d("album u2net time test","img size :"+mask.getHeight()+"*"+mask.getWidth());
            Log.d("album u2net time test","takes "+(timeEnd-timeStart));
            Toast.makeText(this,(timeEnd-timeStart)+"",Toast.LENGTH_LONG).show();
            imageView.setImageBitmap(mask);
            */
            imageView.setImageBitmap(bitmap);
            BitmapUtils.originBitmap=((BitmapDrawable)imageView.getDrawable()).getBitmap();
            ModelUtils.reRun();
            AsyncTask<Bitmap, Void, Bitmap> u2netResult=new ModelUtils.U2NETRunner().execute(bitmap);
            AsyncTask<Bitmap,Void,List<Point>> openposeResult=new ModelUtils.POSERunner().execute(bitmap);
            ViewUtils.dialog=new SpotsDialog.Builder().setContext(this).setMessage("加载中").build();
            ViewUtils.dialog.show();
            /*
            try {
                u2netResult.get();
                b=System.currentTimeMillis();
            } catch (Exception e) {
                Log.e("album",e.toString());
            }
            Toast.makeText(this,(b-a)+" ",Toast.LENGTH_LONG).show();
            */

            //imageView.setImageBitmap(after);

            ViewUtils.imageView=imageView;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, null);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean defaultReact=super.onKeyDown(keyCode, event);
        if(keyCode==KeyEvent.KEYCODE_BACK&&includeView.getVisibility()==View.VISIBLE){
            adapter.clean();
            return false;
        }
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
        adapter=new RecycleViewAdapter(this,btn_func,btn_id,includeView,imageView,recyclerView,myView,uri);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }
}
