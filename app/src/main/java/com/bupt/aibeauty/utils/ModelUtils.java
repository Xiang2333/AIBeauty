package com.bupt.aibeauty.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.bupt.aibeauty.R;
import com.bupt.aibeauty.integrate.BonePoint;
import com.bupt.aibeauty.integrate.ProfilePoint;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.bupt.aibeauty.utils.BitmapUtils.scaleBitmap;

public class ModelUtils {
    public static Context context=null;
    private static Module u2net=null;
    private static Module pose=null;
    private static boolean u2netLoadFin=false;
    private static boolean openposeLoadFin=false;
    private static boolean u2netRunFin=false;
    private static boolean openposeRunFin=false;
    private static int keyPointsNum = 18;
    private static int[] POSE_COCO_PAIRS = { 1, 2, 1, 5, 2, 3, 3, 4, 5, 6, 6, 7, 1, 8, 8, 9, 9, 10, 1, 11, 11, 12, 12, 13, 1, 0, 0, 14, 14, 16, 0, 15, 15, 17, 2, 16, 5, 17};
    private static int[] POSE_COCO_MAP_IDX = { 31, 32, 39, 40, 33, 34, 35, 36, 41, 42, 43, 44, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 47, 48, 49, 50, 53, 54, 51, 52, 55, 56, 37, 38, 45, 46};

    public static ProfilePoint getProfilePoint(Bitmap bitmap){
        //Bitmap mask=runU2net(bitmap);
        return new ProfilePoint(getMask(bitmap),getBonePoint(bitmap));
    }

    public static Bitmap getMask(Bitmap bitmap){
        return ((BitmapDrawable)context.getDrawable(R.drawable.mask_5)).getBitmap();
    }
    public static int[][] getBonePoint(Bitmap bitmap){
        return BonePoint.example_5;
    }

    public static Bitmap runU2net(Bitmap input){
        if(u2net==null){
            new U2NETLoadTask().execute();
        }
        if(input==null){
            return null;
        }
        int w = input.getWidth();
        int h = input.getHeight();
        input = BitmapUtils.scaleBitmap(input, 320, 320);

        // preparing input tensor
        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(input,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB);

        // running the model
        long startTime = System.currentTimeMillis();
        Tensor outputTuple = u2net.forward(IValue.from(inputTensor)).toTuple()[0].toTensor();
        long endTime = System.currentTimeMillis();
        //Log.i("forward", String.valueOf((endTime-startTime)));
        //Log.i("forward", String.valueOf((outputTuple.shape())));

        // getting tensor content as java array of floats
        final float[] confidenceMap = outputTuple.getDataAsFloatArray();


        // AfterProcess
        // normalize confidenceMap
        float maxScore = -Float.MAX_VALUE;
        float minScore = Float.MAX_VALUE;
        for (int i = 0; i < confidenceMap.length; i++) {
            if (confidenceMap[i] > maxScore) {
                maxScore = confidenceMap[i];
            }
            else if (confidenceMap[i] < minScore) {
                minScore = confidenceMap[i];
            }
        }
        float temp = maxScore - minScore;
        int[] grayImage = new int[confidenceMap.length];
        for (int i=0; i < confidenceMap.length; i++) {
            grayImage[i] = (int)((confidenceMap[i] - minScore)/temp*255);
        }

        Bitmap newBitmap = Bitmap.createBitmap(320, 320,  Bitmap.Config.ARGB_8888);
        for (int i=0; i<320; i++) {
            for (int j=0; j<320; j++) {
                if (grayImage[i*320+j] > 127) newBitmap.setPixel(j, i, android.graphics.Color.argb(255, 255,255, 255));
                else newBitmap.setPixel(j, i, android.graphics.Color.argb(255, 0,0, 0));
            }
        }


        newBitmap = scaleBitmap(newBitmap, w, h);

        return newBitmap;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static List<Point> runPose(Bitmap input){
        if(pose==null){
            new POSELoadTask().execute();
        }
        if(input==null){
            return null;
        }
        ArrayList<ArrayList<Float>> keypoints=new ArrayList<>();
        // prepare input data
        float scale = 0.5f;
        int bboxsize = 368;
        int stride = 8;
        int padvalue = (255<<24) + (128<<16) + (128<<8) + 128;
        float thresh_1 = 0.001f;
        float thresh_2 = 0.05f;
        float multipliers = scale * bboxsize / input.getHeight();

        //resize
        int width = input.getWidth();
        int height = input.getHeight();
        Bitmap image = scaleBitmap(input, Math.round(width*multipliers), Math.round(height*multipliers));

        //pad
        int rightPad = image.getWidth()%stride == 0 ? 0 : stride - (image.getWidth()%stride);
        int downPad = image.getHeight()%stride == 0 ? 0 : stride - (image.getHeight()%stride);
        if (rightPad!=0 || downPad!=0) {
            Bitmap tempImage = Bitmap.createBitmap(image.getWidth()+rightPad, image.getHeight()+downPad, Bitmap.Config.ARGB_8888);

            for (int i=0; i<tempImage.getHeight(); ++i) {
                for (int j=0; j<tempImage.getWidth(); ++j) {
                    if (i<image.getHeight() && j<image.getWidth()) {
                        tempImage.setPixel(j, i, image.getPixel(j, i));
                    }
                    else tempImage.setPixel(j, i, padvalue);
                }
            }

            image = tempImage;
        }

        // preparing input tensor
        float[] mean = {0.5f, 0.5f, 0.5f};
        float[] std = {1, 1, 1};

        final Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(image, mean, std);

        // running the model
        IValue[] outputTuple = pose.forward(IValue.from(inputTensor)).toTuple();
        Tensor heatmapTensor = outputTuple[1].toTensor();
        Tensor pafTensor = outputTuple[0].toTensor();

        // afterprocess
        long[] heatmapShape = heatmapTensor.shape();
        long[] pafShape = pafTensor.shape();

        float[] array1 = heatmapTensor.getDataAsFloatArray();
        int ch = (int)heatmapShape[1];
        int ro = (int)heatmapShape[2];
        int cl = (int)heatmapShape[3];
        int idx = 0;
        Mat h1 = new Mat(ro, cl, CvType.CV_32FC(ch));
        for (int i=0; i<ro; i++) {
            for (int j=0; j<cl; j++) {
                int k=i*cl+j;
                float[] temp = new float[ch];
                for(int l=0; l<ch; l++) {
                    temp[l] = array1[k];
                    k += ro*cl;
                }
                h1.put(i, j, temp);
            }
        }

        float[] array2 = pafTensor.getDataAsFloatArray();
        ch = (int)pafShape[1];
        ro = (int)pafShape[2];
        cl = (int)pafShape[3];
        idx = 0;
        Mat p1 = new Mat(ro, cl, CvType.CV_32FC(ch));
        for (int i=0; i<ro; i++) {
            for (int j=0; j<cl; j++) {
                int k=i*cl+j;
                float[] temp = new float[ch];
                for(int l=0; l<ch; l++) {
                    temp[l] = array2[k];
                    k += ro*cl;
                }
                p1.put(i, j, temp);
            }
        }

        // resize, remove pad, resize
        Mat h2 = new Mat();
        Mat p2 = new Mat();
        Imgproc.resize(h1, h2, new Size(), stride, stride, Imgproc.INTER_CUBIC);
        Imgproc.resize(p1, p2, new Size(), stride, stride, Imgproc.INTER_CUBIC);

        if (downPad!=0 || rightPad!=0){
            Rect crop = new Rect(0, 0, h2.cols()-rightPad, h2.rows()-downPad);
            h2 = new Mat(h2, crop);
            p2 = new Mat(p2, crop);
        }

        Mat h3 = new Mat();
        Mat p3 = new Mat();
        Imgproc.resize(h2, h3, new Size(width, height), 0, 0, Imgproc.INTER_CUBIC);
        Imgproc.resize(p2, p3, new Size(width, height), 0, 0, Imgproc.INTER_CUBIC);

        ArrayList<ArrayList<Float>> nmsOut = new ArrayList<>(keyPointsNum);
        int peakNum = nms(h3, nmsOut, thresh_2);
        ArrayList<ArrayList<Float>> peoples = new ArrayList<>();
        connectPoseKeypoints(nmsOut, p3, peoples, thresh_1);

        // construct result
        for (int i=0; i<peoples.size(); i++) {
            ArrayList<Float> res = new ArrayList<>(38); // 18个关键点的坐标值 + score + number
            for(int j=0; j<38; j++) res.add(j, -1f);
            res.set(36, peoples.get(i).get(18)); // score
            res.set(37, peoples.get(i).get(19)); // number
            for (int j=0; j<18; j++) {
                if (peoples.get(i).get(j).floatValue() != -1f) {
                    int temp = peoples.get(i).get(j).intValue();
                    res.set(2*j, nmsOut.get(j).get(3*temp+1));
                    res.set(2*j+1, nmsOut.get(j).get(3*temp+2));
                }
            }
            keypoints.add(res);
        }
        if(keypoints.size()>1){
            Log.e("pose","to many people");
        }
        if(keypoints.size()<=0){
            Log.e("pose","pose result error");
        }
        List<Point> bonePoints=new ArrayList<>();
        for(int i=0;i<keyPointsNum;i++){
            float x=(keypoints.get(0).get(2*i));
            float y=(keypoints.get(0).get(2*i+1));
            bonePoints.add(new Point((int)x,(int)y));
        }
        return bonePoints;
    }
    private static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }
        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }
    private static void loadU2NET(){
        try {
            u2net=Module.load(assetFilePath(context, "u2net.pt"));
        } catch (Exception e) {
            Log.e("loadModel",e.toString());
        }
    }
    private static void loadPOSE(){
        try {
            pose=Module.load(assetFilePath(context, "pose.pt"));
        } catch (Exception e) {
            Log.e("loadModel",e.toString());
        }
    }
    public static class POSELoadTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            long a=System.currentTimeMillis();
            loadPOSE();openposeLoadFin=true;
            if(modelPrepareFin()&&ViewUtils.dialog!=null){
                ViewUtils.dialog.dismiss();
                ViewUtils.dialog=null;
            }
            long b=System.currentTimeMillis();
            Log.d("model","pose loaded takes "+(b-a)+" ms");
            //Toast.makeText(context,(b-a)+" ", Toast.LENGTH_LONG).show();
            return null;
        }
    }
    public static class U2NETLoadTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            long a=System.currentTimeMillis();
            loadU2NET();u2netLoadFin=true;
            if(modelPrepareFin()&&ViewUtils.dialog!=null){
                ViewUtils.dialog.dismiss();
                ViewUtils.dialog=null;
            }
            long b=System.currentTimeMillis();
            Log.d("model","u2net loaded takes "+(b-a)+" ms");
            //Toast.makeText(context,(b-a)+" ",Toast.LENGTH_LONG).show();
            return null;
        }
    }

    public static class U2NETRunner extends AsyncTask<Bitmap,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            long a=System.currentTimeMillis();
            u2netRunFin=false;
            Bitmap res=runU2net(bitmaps[0]);
            u2netRunFin=true;
            if(modelPrepareFin()&&ViewUtils.dialog!=null){
                ViewUtils.dialog.dismiss();
                ViewUtils.dialog=null;
            }
            long b=System.currentTimeMillis();
            Log.d("model","u2net run finish takes "+(b-a)+" ms");
            //Toast.makeText(context,(b-a)+" ",Toast.LENGTH_LONG).show();
            return res;
        }
    }
    public static class POSERunner extends AsyncTask<Bitmap,Void,List<Point>>{

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected List<Point> doInBackground(Bitmap... bitmaps) {
            long a=System.currentTimeMillis();
            openposeRunFin=false;
            List<Point> res=runPose(bitmaps[0]);
            openposeRunFin=true;
            if(modelPrepareFin()&&ViewUtils.dialog!=null){
                ViewUtils.dialog.dismiss();
                ViewUtils.dialog=null;
            }
            long b=System.currentTimeMillis();
            Log.d("model","openpose run finish takes "+(b-a)+" ms");
            //Toast.makeText(context,(b-a)+" ",Toast.LENGTH_LONG).show();
            return res;
        }
    }

    // 与周围八个像素比较，得出局部极值点
    // nmsOut中的元素为arrayList, 该元素中第一个元素为peak的数目，其余为peak三元组(col, row, score)
    private static int nms(Mat heatmap, ArrayList<ArrayList<Float>> nmsOut, float threshold){
        Imgproc.GaussianBlur(heatmap, heatmap, new Size(3, 3), 3);
        int peakCount = 0;
        int maxPeakNum = heatmap.height()-1; // threshold of number of peak
        for (int i=0; i<heatmap.channels()-1; ++i) { // 最后一个channel类别为backgroud，故不进行计算
            int count = 0;
            ArrayList<Float> temp = new ArrayList<>();
            temp.add(0f); // Number of peak

            for (int j=1; j<heatmap.width()-1 && peakCount != maxPeakNum; ++j) {
                for(int k=1; k<heatmap.height()-1 && peakCount != maxPeakNum; ++k) {
                    double value = heatmap.get(k, j)[i];
                    if (value > threshold) {
                        double topLeft = heatmap.get(k-1, j-1)[i];
                        double top = heatmap.get(k-1, j)[i];
                        double topRight = heatmap.get(k-1, j+1)[i];
                        double left = heatmap.get(k, j-1)[i];
                        double right = heatmap.get(k, j+1)[i];
                        double bottomLeft = heatmap.get(k+1, j-1)[i];
                        double bottom = heatmap.get(k+1, j)[i];
                        double bottomRight = heatmap.get(k+1, j+1)[i];

                        if (value>topLeft && value>top && value>topRight && value>left && value>right && value>bottomLeft && value>bottom && value>bottomRight) {
                            temp.add((float)j);
                            temp.add((float)k);
                            temp.add((float)value);
                            count++;
                        }
                    }
                }
            }
            temp.set(0, (float) count);
            nmsOut.add(temp);
            peakCount += count;
        }

        return peakCount;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void connectPoseKeypoints(ArrayList<ArrayList<Float>> candidate, Mat paf, ArrayList<ArrayList<Float>> peoples, float threshold){
//    int POSE_MAX_PEOPLE = 10;
        int bodyPartNums = POSE_COCO_PAIRS.length/2;

        ArrayList<ArrayList<Float>> connectionAll = new ArrayList<ArrayList<Float>>(bodyPartNums); //每一个bodypart的连接情况，(A在candidate中的索引， B在candidate中的索引， 分值)
        boolean[] specialIndex = new boolean[bodyPartNums]; // 默认初始化为false ?
        int samplePointNum = 10;

        // 计算各个pair对连接的得分，按得分从高到低筛选
        for (int partIndex=0; partIndex<bodyPartNums; partIndex++) {
            int candidateAIndex = POSE_COCO_PAIRS[partIndex*2];
            int candidateBIndex = POSE_COCO_PAIRS[partIndex*2+1];
            ArrayList<Float> candidateA = candidate.get(candidateAIndex);
            ArrayList<Float> candidateB = candidate.get(candidateBIndex);
            float nA = candidateA.get(0);
            float nB = candidateB.get(0);

            if (nA!=0f && nB!=0f) {
                ArrayList<ArrayList<Float>> connectionTemp = new ArrayList<ArrayList<Float>>();
                // 以下求取各个bodypart的paf值是依据公式来的
                for (int i=0; i<nA; i++) {
                    for (int j=0; j<nB; j++) {
                        float[] vec = {candidateB.get(3*j+1) - candidateA.get(i*3+1), candidateB.get(3*j+2) - candidateA.get(i*3+2)};
                        double norm = Math.sqrt(vec[0]*vec[0] + vec[1]*vec[1]);

                        if (norm < 1e-6) continue;

                        vec[0] = (float) (vec[0]/norm);
                        vec[1] = (float) (vec[1]/norm);

                        float t = Math.min(0f, (float) (0.5*paf.height()/norm - 1));
                        float scoreSum = 0;
                        int scoreBThresholdNum = 0;
                        float thetaX = (candidateA.get(i*3+1) - candidateB.get(3*j+1)) / (samplePointNum+1);
                        float thetaY = (candidateA.get(i*3+2) - candidateB.get(3*j+2)) / (samplePointNum+1);
                        float tempX = candidateB.get(j*3+1);
                        float tempY = candidateB.get(j*3+2);
                        for(int k=0; k<samplePointNum; k++) { // sample
                            tempX += thetaX;
                            tempY += thetaY;
                            float vecX = (float) paf.get((int)Math.floor(Math.min(tempY, paf.height()-1)), (int)Math.floor(Math.min(tempX, paf.width()-1)))[POSE_COCO_MAP_IDX[partIndex*2] - 19];
                            float vecY = (float) paf.get((int)Math.floor(Math.min(tempY, paf.height()-1)), (int)Math.floor(Math.min(tempX, paf.width()-1)))[POSE_COCO_MAP_IDX[partIndex*2+1] - 19];
                            float s = vecX*vec[0] + vecY*vec[1];
                            if (s > threshold) scoreBThresholdNum++;
                            scoreSum += s;
                        }

                        float scoreWithDistPrior = scoreSum / samplePointNum + t;
                        if (scoreBThresholdNum > 0.8*samplePointNum && scoreWithDistPrior > 0) {
                            ArrayList<Float> ct = new ArrayList<Float>();
                            ct.add((float) i);
                            ct.add((float) j);
                            ct.add(scoreWithDistPrior);
                            ct.add(scoreWithDistPrior + candidateA.get(i*3 + 3) + candidateB.get(j*3 + 3)); //似乎没有用到这个分值
                            connectionTemp.add(ct);
                        }

                    }
                }

                connectionTemp.sort(new Comparator<ArrayList<Float>>() {
                    @Override
                    public int compare(ArrayList<Float> floats, ArrayList<Float> t1) { // 依据scoreWithDistPrior降序排序
                        if (t1.get(2) > floats.get(2)) {
                            return 1;
                        }
                        else return -1;
                    }
                });


                boolean[] nASet = new boolean[(int) nA]; //默认初始化为false ?
                boolean[] nBSet = new boolean[(int) nB];
                ArrayList<Float> connection = new ArrayList<Float>();

                for (int k=0; k<connectionTemp.size(); k++) {
                    ArrayList<Float> temp = connectionTemp.get(k);
                    int a = temp.get(0).intValue();
                    int b = temp.get(1).intValue();
                    if (nASet[a]==false && nBSet[b]==false) {
                        connection.add(temp.get(0)); //
                        connection.add(temp.get(1)); //
                        connection.add(temp.get(2)); // score
                        if (connection.size() >= Math.min(nA, nB)) break;
                        nASet[a] = true;
                        nBSet[b] = true;
                    }
                }
                connectionAll.add(connection);
            }
            else {
                specialIndex[partIndex] = true;
                connectionAll.add(new ArrayList<Float>());
            }
        }


        //依据connectionAll拼接各个bodypart
        ArrayList<ArrayList<Float>> subSet = new ArrayList<ArrayList<Float>>(); // len=20, last number in each row is the total parts number of that person, the second last number in each row is the score of the overall configuration

        for (int partIndex=0; partIndex<bodyPartNums; partIndex++) {
            if (specialIndex[partIndex] == false) {
                int indexA = POSE_COCO_PAIRS[partIndex*2];
                int indexB = POSE_COCO_PAIRS[partIndex*2 +1];
                ArrayList<Float> connection = connectionAll.get(partIndex);

                for (int i=0; i<connection.size()/3; i++) {
                    int found = 0;
                    int[] subsetIndex = {-1, -1};
                    for (int j=0; j<subSet.size(); j++) {
                        if (subSet.get(j).get(indexA).intValue() == connection.get(3*i).intValue() || subSet.get(j).get(indexB).intValue() == connection.get(3*i+1).intValue()) {
                            subsetIndex[found] = j;
                            found++;
                        }
                    }

                    if (found == 1) {
                        int j = subsetIndex[0];
                        if (subSet.get(j).get(indexB).intValue() != connection.get(3*i+1).intValue()) { // 只发现A，则把B加入subset
                            subSet.get(j).set(indexB, connection.get(3*i+1));
                            Float num = subSet.get(j).get(19) + 1;
                            Float score = subSet.get(j).get(18) + connection.get(3*i+2) +
                                    candidate.get(indexB).get(connection.get(3*i+1).intValue()*3+2);
                            subSet.get(j).set(19, num);
                            subSet.get(j).set(18, score);
                        }
                    }
                    else if(found == 2) {
                        int j1 = subsetIndex[0], j2 = subsetIndex[1];
                        int count = 0;
                        for(int l=0; l<18; l++) {
                            if (subSet.get(j1).get(l).floatValue() != -1f && subSet.get(j2).get(l).floatValue() != -1f) {
                                count++;
                            }
                        }

                        if (count == 0) { //merge
                            for(int l=0; l<18; l++) {
                                if (subSet.get(j2).get(l).floatValue() != -1f) {
                                    subSet.get(j1).set(l, subSet.get(j2).get(l));
                                }
                            }
                            Float num = subSet.get(j1).get(19);
                            Float score = subSet.get(j1).get(18);
                            subSet.get(j1).set(19, num + subSet.get(j2).get(19));
                            subSet.get(j1).set(18, score + subSet.get(j2).get(18) + connection.get(3*i+2));
                        }
                        else { // as like found=1
                            subSet.get(j1).set(indexB, connection.get(3*i+1));
                            Float num = subSet.get(j1).get(19) + 1;
                            Float score = subSet.get(j1).get(18) + connection.get(3*i+2) +
                                    candidate.get(indexB).get(connection.get(3*i+1).intValue()*3+2);
                            subSet.get(j1).set(19, num);
                            subSet.get(j1).set(18, score);
                        }
                    }
                    else if (found == 0 && partIndex < 17) {
                        ArrayList<Float> row = new ArrayList<>(20);
                        for(int l=0; l<20; ++l) row.add(-1f);
                        row.set(indexA, connection.get(3*i));
                        row.set(indexB, connection.get(3*i+1));
                        row.set(19, 2f);
                        row.set(18, connection.get(3*i+2) + candidate.get(indexB).get(connection.get(3*i+1).intValue()*3+2) +
                                candidate.get(indexA).get(connection.get(3*i).intValue()*3+2));
                        subSet.add(row);
                    }
                }
            }
        }

        // delete some rows of subset which has few parts occur
        for(int i=0; i<subSet.size(); i++) {
            if (subSet.get(i).get(19) > 4 && (subSet.get(i).get(18) / subSet.get(i).get(19) > 0.4)) {
                peoples.add(subSet.get(i));
            }
        }
    }
    private static boolean modelLoadFin(){
        return u2netLoadFin&&openposeLoadFin;
    }
    private static boolean modelRunFin(){
        return u2netRunFin&&openposeRunFin;
    }

    public static boolean modelPrepareFin(){
        boolean flag=modelLoadFin()&&modelRunFin();
        return flag;
    }
    public static void reRun(){
        u2netRunFin=false;
        openposeRunFin=false;
    }
}