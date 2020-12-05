package com.bupt.aibeauty.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bupt.aibeauty.R;
import com.bupt.aibeauty.utils.BitmapUtils;
import com.bupt.aibeauty.utils.ModelUtils;
import com.bupt.aibeauty.utils.ViewUtils;
import com.yalantis.ucrop.UCrop;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;
import com.zhouwei.mzbanner.holder.MZViewHolder;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends Activity {
    private static final int ALBUM=100;
    private static final int FILTER=200;
    private static final int SYSTEMCAMERA=300;
    private static final int CUT=400;
    private MZBannerView mzBannerView;
    private List<Integer> list;

    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ModelUtils.context=this;
        loadModel();
        Point screenSize=new Point();
        getWindowManager().getDefaultDisplay().getRealSize(screenSize);
        ViewUtils.screenHeight=screenSize.y;ViewUtils.screenWidth=screenSize.x;

        mzBannerView=findViewById(R.id.banner);
        list=initBannerImage();
        mzBannerView.setPages(list, new MZHolderCreator<BannerViewHolder>() {
            @Override
            public BannerViewHolder createViewHolder() {
                return new BannerViewHolder();
            }
        });
        //给相机绑定点击事件
        findViewById(R.id.index_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取摄像头权限，没权限就申请
                if (PermissionChecker.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                        == PermissionChecker.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[] { Manifest.permission.CAMERA },
                            v.getId());
                } else {
                    //获得权限后开启CameraActivity
                    startActivity(v.getId());
                }
            }
        });
        //给相册绑定点击事件
        findViewById(R.id.index_album).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(PermissionChecker.checkCallingOrSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        ==PermissionChecker.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, view.getId());
                }else{
                    startActivity(view.getId());
                }
            }
        });
        findViewById(R.id.index_filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PermissionChecker.checkCallingOrSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        ==PermissionChecker.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, view.getId());
                }else{
                    startActivity(view.getId());
                }
            }
        });
        findViewById(R.id.index_cut).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(PermissionChecker.checkCallingOrSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        ==PermissionChecker.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, view.getId());
                }else{
                    startActivity(view.getId());
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("MainActivity","onActivityResult in");
        //接受到了图片的data，则开启AlbumActivity
        if (requestCode == MainActivity.ALBUM && resultCode == RESULT_OK && null != data) {
            Intent intent=new Intent();
            intent.setClass(MainActivity.this, AlbumActivity.class);
            //data.getDate()获取图片uri，保存其字符串形式
            intent.putExtra("img_path",data.getData().toString());
            startActivity(intent);
        }else if(requestCode == MainActivity.FILTER && resultCode == RESULT_OK && null != data){
            Intent intent=new Intent();
            intent.setClass(MainActivity.this, FilterActivity.class);
            //data.getDate()获取图片uri，保存其字符串形式
            intent.putExtra("img_path",data.getData().toString());
            startActivity(intent);
        }else if(requestCode==MainActivity.SYSTEMCAMERA&& resultCode == RESULT_OK&&null!=data){
            Log.d("main","take photo over");
            Log.d("main",uri.toString());
            Intent intent=new Intent();
            intent.setClass(MainActivity.this, TransitActivity.class);
            intent.putExtra("from","camera");
            intent.putExtra("img_path",uri.toString());
            startActivity(intent);
        }else if(requestCode==MainActivity.CUT&&resultCode==RESULT_OK&&null!=data){
            Uri uri=data.getData();
            try {
                InputStream source = this.getContentResolver().openInputStream(uri);
                uri=BitmapUtils.convertToJPG(source);
            } catch (Exception e) {
                Log.e("main",e.getMessage());
            }
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "AIBeautyTemp");
            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdirs();
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINESE).format(new Date());
            File pictureFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_CROP_" + timeStamp + ".jpg");
            Log.d("Ucrop",pictureFile.exists()+"");
            UCrop.Options options=new UCrop.Options();
            options.setFreeStyleCropEnabled(true);
            options.setCompressionQuality(100);
            this.uri=uri;
            Log.d("Ucrop",uri.toString()+"\n"+Uri.fromFile(pictureFile).toString());
            UCrop uCrop=UCrop.of(uri,Uri.fromFile(pictureFile)).withOptions(options);
            uCrop.start(this);
        }else if(resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP&&null!=data) {
            final Uri resultUri = UCrop.getOutput(data);
            //Toast.makeText(this,"裁减完成！",Toast.LENGTH_SHORT).show();
            Intent intent=new Intent();
            intent.setClass(MainActivity.this, TransitActivity.class);
            intent.putExtra("img_path",resultUri.toString());
            intent.putExtra("from","crop");
            intent.putExtra("origin_path",this.uri.toString());
            Log.d("Ucrop","finish "+resultUri.toString());
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                     int[] grantResults) {
        if (grantResults.length != 1 || grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startActivity(requestCode);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    private void startActivity(int id) {
        switch (id) {
            case R.id.index_camera:
                //startActivity(new Intent(this, CameraActivity.class));
                Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //系统常量， 启动相机的关键
                String f = System.currentTimeMillis()+".jpg";
                File dir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "AIBeautyTemp");
                if(!dir.exists()){
                    dir.mkdir();
                }
                Uri fileUri = Uri.fromFile(new File(dir.getPath()+"/"+f));
                this.uri=fileUri;
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); //指定图片存放位置，指定后，在onActivityResult里得到的Data将为null
                startActivityForResult(openCameraIntent, SYSTEMCAMERA);
                break;
            case R.id.index_album:
                Intent intent = new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                startActivityForResult(intent,MainActivity.ALBUM);
                break;
            case R.id.index_filter:
                Intent intent2 = new Intent("android.intent.action.GET_CONTENT");
                intent2.setType("image/*");
                startActivityForResult(intent2,MainActivity.FILTER);
                break;
            case R.id.index_cut:
                Intent intent3 = new Intent("android.intent.action.GET_CONTENT");
                intent3.setType("image/*");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    String[] mimeTypes = {"image/jpeg", "image/png"};
                    intent3.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
                }
                startActivityForResult(intent3,MainActivity.CUT);
            default:
                break;
        }
    }
    private List<Integer> initBannerImage(){
        List<Integer> list=new ArrayList<>();
        list.add(R.drawable.index);
        list.add(R.drawable.index2);
        list.add(R.drawable.index3);
        list.add(R.drawable.index4);
        return list;
    }
    public static class BannerViewHolder implements MZViewHolder<Integer> {
        private ImageView mImageView;
        @Override
        public View createView(Context context) {
            // 返回页面布局
            View view = LayoutInflater.from(context).inflate(R.layout.banner_item,null);
            mImageView = (ImageView) view.findViewById(R.id.banner_image);
            return view;
        }

        @Override
        public void onBind(Context context, int position, Integer data) {
            // 数据绑定
            mImageView.setImageResource(data);
        }
    }
    private static void loadModel(){
        long a=System.currentTimeMillis(),b=0,c=0;
        AsyncTask<Void,Void,Void> task1=new ModelUtils.U2NETLoadTask().execute();
        AsyncTask<Void,Void,Void> task2=new ModelUtils.POSELoadTask().execute();
        /*
        try {
            task1.get();
            b=System.currentTimeMillis();
            task2.get();
            c=System.currentTimeMillis();
        } catch (Exception e) {
            Log.e("main",e.toString());
        }
        Toast.makeText(ModelUtils.context,"model load takes "+(Math.max(b,c)-a)+" ms",Toast.LENGTH_LONG).show();
         */
    }

}