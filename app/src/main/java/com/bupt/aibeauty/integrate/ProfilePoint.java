package com.bupt.aibeauty.integrate;

import android.graphics.Bitmap;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ProfilePoint {

    /*常量*/
    //左上臂
    public static int LUARM = 1;
    //左下臂
    public static int LFARM = 2;
    //右上臂
    public static int RUARM = 5;
    //右下臂
    public static int RFARM = 6;
    //左大腿
    public static int LTHIGH = 9;
    //左小腿
    public static int LSHANK =10;
    //右大腿
    public static int RTHIGH =11;
    //右小腿
    public static int RSHANK =12;

    //左臂
    public static int LARM =1;
    //右臂
    public static int RARM =5;
    //左腿
    public static int LLEG =9;
    //右腿
    public static int RLEG =11;

    /*构造边缘点的默认给定间距*/
    //public static int gap = 3;
    public static int gap = 3;
    public static float alpha = (float) 1.2;

    int[][] bonePoint;  //骨骼点数据
    int[][][] pointSet;

    ProfileOperator operator;

    int slimRange;  //瘦身范围

    public int [][] waist;



    /*点位说明
     * [一侧/骨骼线/一侧][numpoint][2]
     * 某一点可能会为Null,原因是属于异常点
     * */
    private int[][][] leftArm;
    private int[][][] rightArm;
    private int[][][] leftLeg;
    private int[][][] rightLeg;




    public float waistScale = (float)0.7;//腰部从胸口到档部在的位置

    public ProfilePoint(Bitmap profileImage,int[][] bonePoint){
        this.bonePoint = bonePoint;
        this.pointSet = genPointSet(bonePoint);

        this.operator = new ProfileOperator(profileImage,bonePoint);
        this.slimRange = Math.abs(bonePoint[2][0] - bonePoint[1][0])/2;

        this.waist = new int[2][];


        this.leftArm = new int[3][][];
        this.rightArm = new int[3][][];
        this.leftLeg = new int[3][][];
        this.rightLeg = new int[3][][];
    }

    //app中对于腰部的检测比较简单，并没有在意与手部重叠的问题，所以直接向外延伸，取点即可
    // 暂未将人体站立姿态纳入考虑，默认认识站直的。
    //返回两个点，第一个表示身体腰部左侧的点，第二个表示身体腰部右侧的点
    public int[][] getWaistPoint(){
        if(this.waist[0] == null && this.waist[1] == null) {
            waist[0] = new int[2];
            waist[1] = new int[2];
            //1是胸口，8是档部
            int m_0 = (bonePoint[1][0] + bonePoint[8][0]) / 2;
            int m_1 = bonePoint[1][1] + (int) (waistScale * (bonePoint[8][1] - bonePoint[1][1]));

            //查找图像左侧
            for (int i = m_0; i >= 0; i = i - 1) {
                //byte color = this.operator.byteProfile[m_1*this.operator.width+i];
                if (this.operator.isPixelBlack(i, m_1)) {
                    waist[0][0] = i;
                    waist[0][1] = m_1;
                    break;
                }
            }
            //查找图像右侧
            for (int i = m_0; i < this.operator.width; i += 1) {
                //profileImage.get(m_1,i,pixelColor);
                //byte color = this.operator.byteProfile[m_1*this.operator.width+i];
                //这里颜色值都是有符号数，所以先这样写了
                if (this.operator.isPixelBlack(i, m_1)) {
                    waist[1][0] = i;
                    waist[1][1] = m_1;
                    break;
                }
            }

        }
        return this.waist;
    }


    private void genLimbPoint(int limbNum, int pointNum){
        int[][][] tar = null;
        if(limbNum ==  LARM){
            tar = this.leftArm;
        }
        if(limbNum == RARM){
            tar = this.rightArm;
        }
        if(limbNum == LLEG){
            tar = this.leftLeg;
        }
        if(limbNum == RLEG){
            tar = this.rightLeg;
        }

        tar[0] = new int[pointNum*2][];
        tar[1] = new int[pointNum*2][];
        tar[2] = new int[pointNum*2][];
        ArrayList<int[]>[] result;
        ArrayList<int[]> d;
        ArrayList<int[]> m;
        result = this.getIntersection(pointSet[limbNum][0],pointSet[limbNum][1],true,true,pointNum,gap,alpha);
        d = result[1];
        m = result[2];

        for(int i = 0 ; i< m.size(); i++){
            tar[0][i] = d.get(2*i);
            tar[1][i] = m.get(i);
            tar[2][i] = d.get(2*i+1);
        }
        /*聚类*/
        boolean[] lBool = clusterPoint(tar[0],this.pointSet,limbNum);
        boolean[] rBool = clusterPoint(tar[2],this.pointSet,limbNum);
        for(int i = 0; i < m.size(); i++){
            if(!lBool[i])
                tar[0][i] = null;
            if(!rBool[i])
                tar[2][i] = null;
        }


        result = this.getIntersection(pointSet[limbNum+1][0],pointSet[limbNum+1][1],true,true,pointNum,gap,alpha);
        d.addAll(result[1]);
        m.addAll(result[2]);
        for(int i = pointNum;i < 2*pointNum; i++){
            tar[0][i] = d.get(2*i);
            tar[1][i] = m.get(i);
            tar[2][i] = d.get(2*i+1);
        }

        /*聚类*/
        lBool = clusterPoint(tar[0],this.pointSet,limbNum+1);
        rBool = clusterPoint(tar[2],this.pointSet,limbNum+1);
        for(int i = pointNum; i < m.size(); i++){
            if(!lBool[i])
                tar[0][i] = null;
            if(!rBool[i])
                tar[2][i] = null;
        }

        return;
    }

    /*
    @description: 返回一组肢体上的的点
    @return:[一侧/骨骼线/一侧][index][x/y]
     */
    public int[][][] getLeftArm(int pointNum){
        if(this.leftArm[0] == null && this.leftArm[1] == null && this.leftArm[2]==null)
            this.genLimbPoint(LARM,pointNum);
        return this.leftArm;
    }

    public int[][][] getRightArm(int pointNum){
        if(this.rightArm[0] == null && this.rightArm[1] == null && this.rightArm[2]==null)
            this.genLimbPoint(RARM,pointNum);
        return this.rightArm;
    }

    public int[][][] getLeftLeg(int pointNum){
        if(this.leftLeg[0]== null && this.leftLeg[1]== null && this.leftLeg[2]== null)
            this.genLimbPoint(LLEG,pointNum);
        return this.leftLeg;
    }

    public int[][][] getRightLeg(int pointNum){
        if(this.rightLeg[0] == null && this.rightLeg[1] == null && this.rightLeg[2]==null)
            this.genLimbPoint(RLEG,pointNum);

        return this.rightLeg;
    }

    /*
    @description: 沿着指定点p,q 构成的直线，作垂线，确定轮廓边缘与垂线的焦点的集合
    @param: p，q: 起始点和终止点的坐标
            leftFlag： 是否在左侧采取点
            rightFlag: 是否在右侧采取点
            pointNum: 待采取点的数量
            sacle:外围点与内测点位置的比例关系
            gapDistance: 采集的边缘点应与边缘的距离
            alpha: 内测点和外侧点之间的比例距离。
    @note: 需要用到ProfileOperator 完成相关操作
    @return: result[0] 点s, 数量：2*numPoint
             result[1] 点d, 数量：2*numPoint
             result[2] 骨骼点, 数量:numPoint
    * */
    private ArrayList [] getIntersection(int[] p , int[] q,boolean leftFlag, boolean rightFlag, int pointNum,int gapDistance,float alpha){
        int[] direct = {q[0] - p[0], q[1] - p[1]};
        int sec_0 = direct[0]/(pointNum+1);
        int sec_1 = direct[1]/(pointNum+1);
        int[][] inerPoint = null;


        ArrayList<int[]> s = new ArrayList<int[]>();
        ArrayList<int[]> d = new ArrayList<int[]>();
        ArrayList<int[]> m = new ArrayList<int[]>();

        ArrayList[] result = null;
        result = new ArrayList[3];
        result[0] = s;
        result[1] = d;
        result[2] = m;


        //斜率不存在
        if(direct[0] == 0){
            if(direct[1] < pointNum)
                inerPoint = new int[direct[1]][2];
            else
                inerPoint = new int[pointNum][2];
            sec_1 = direct[1]/inerPoint.length;
            for(int i=0; i< inerPoint.length; i++){
                inerPoint[i][0] = p[0];
                inerPoint[i][1] = p[1] + (i+1) * sec_1;
            }
            for(int i = 0; i < inerPoint.length; i++){
                m.add(new int[]{inerPoint[i][0],inerPoint[i][1]});
                if(leftFlag){
                    int x = inerPoint[i][0];
                    int y = inerPoint[i][1];
                    while( x >0 && !operator.isPixelBlack(x,y))
                        x--;
                    //还需要考虑重合的情况，不过这种比较特殊，比较少见，暂不处理
                    x -= gapDistance;
                    s.add(new int[]{Math.max(0,x),y});
                    int distance = Math.abs(inerPoint[i][0] - x);
                    d.add(new int[]{Math.max(0, (int)(inerPoint[i][0]- distance*alpha)),y});
                }
                if(rightFlag){
                    int x = inerPoint[i][0];
                    int y = inerPoint[i][1];
                    while( x < operator.width && !operator.isPixelBlack(x,y))
                        x++;
                    x += gapDistance;
                    s.add(new int[]{Math.min(operator.width-1, x),y});
                    int distance = Math.abs(inerPoint[i][0] - x);
                    d.add(new int[]{Math.min(operator.width-1, (int)(inerPoint[i][0]+ distance*alpha)),y});
                }
            }
            return result;
        }
        //斜率为 0 , left 转为下，right 转为上
        if(direct[1] == 0){
            if(direct[0] < pointNum)
                inerPoint = new int[direct[0]][2];
            else
                inerPoint = new int[pointNum][2];
            sec_0 = direct[0] /  inerPoint.length;
            for(int i=0; i <inerPoint.length;i++){
                inerPoint[i][0]= p[0] + (i+1)*sec_0;
                inerPoint[i][1]= p[1];
            }

            for(int i = 0; i < inerPoint.length; i++){
                m.add(new int[]{inerPoint[i][0],inerPoint[i][1]});
                if(leftFlag){
                    int x = inerPoint[i][0];
                    int y = inerPoint[i][1];
                    while (y > 0 && !operator.isPixelBlack(x,y))
                        y--;
                    y -= gapDistance;
                    s.add(new int[]{x,Math.max(0, y)});
                    int distance = Math.abs(inerPoint[i][1] - y);
                    d.add(new int[]{x,Math.max(0, (int)(y - distance*alpha))});
                }
                if(rightFlag){
                    int x = inerPoint[i][0];
                    int y = inerPoint[i][1];
                    while (y > 0 && !operator.isPixelBlack(x,y))
                        y++;
                    y += gapDistance;
                    s.add(new int[]{x,Math.min(y, operator.height-1)});
                    int distance = Math.abs(inerPoint[i][1] - y);
                    d.add(new int[]{x,Math.min((int)(y+distance*alpha),operator.height-1)});

                }
            }
            return result;
        }
        //斜率存在
        if(direct[0] != 0 && direct[1] != 0){
            float k1 = (float) direct[1]/(float) direct[0];
            int b = p[1] - (int) (p[0] * k1);
            float k2 = (-1)/k1;
            if(Math.abs(k1) >= 1){
                sec_1 = direct[1]/(pointNum + 1);
                inerPoint = new int[pointNum][2];
                for(int i =0; i < inerPoint.length; i++){
                    inerPoint[i][1] = p[1] + (i+1)*sec_1;
                    inerPoint[i][0] = p[0] + (int) ((i+1)*sec_1/k1);

                }

            }
            else{
                sec_0 = direct[0] / (pointNum + 1);
                inerPoint = new int[pointNum][2];
                for(int i=0; i < inerPoint.length; i++){
                    inerPoint[i][0] = p[0] + (i+1) * sec_0;
                    inerPoint[i][1] = p[1] + (int)((i+1)*sec_0*k1);
                }

            }

            if(Math.abs(k2)>1){
                //先减后加
                float delta = -k1;
                for(int i=0; i < inerPoint.length; i++){
                    m.add(new int[]{inerPoint[i][0],inerPoint[i][1]});
                    if(leftFlag){
                        int y = inerPoint[i][1];
                        float x = inerPoint[i][0];

                        while (x>=0 && x<operator.width
                                && y>=0 && y< operator.height
                                &&!operator.isPixelBlack((int) x, y)){
                            y--;
                            x-= delta;
                        }
                        y -= gapDistance;
                        x -= gapDistance*delta;

                        s.add(new int[]{(int) x, y});
                        int distance_y = Math.abs(inerPoint[i][1] - y);
                        float distance_x = Math.abs((float) (inerPoint[i][0] - x));

                        y = Math.max(0,(int) (inerPoint[i][1] - alpha*distance_y));
                        x = Math.max(0,(int) (inerPoint[i][0] - alpha*distance_x*delta));
                        x = Math.min(operator.height -1, x);

                        d.add(new int[]{(int) x,y});

                    }
                    if(rightFlag){
                        int y = inerPoint[i][1];
                        float x = inerPoint[i][0];

                        while (x>=0 && x<operator.width
                                && y>=0 && y< operator.height
                                &&!operator.isPixelBlack((int) x, y)) {
                            y++;
                            x += delta;
                        }
                        y += gapDistance;
                        x += gapDistance*delta;
                        s.add(new int[]{(int) x, y});
                        int distance_y = Math.abs(inerPoint[i][1] - y);
                        float distance_x = Math.abs((float) (inerPoint[i][0] - x));

                        y = Math.min(operator.height-1,(int) (inerPoint[i][1] + alpha*distance_y));
                        x = Math.min(operator.width-1,(int) (inerPoint[i][0] + alpha*distance_x*delta));
                        x = Math.max(0, x);

                        d.add(new int[]{(int) x,y});

                    }
                }
                return result;
            }
            else{
                float delta = k2;
                for (int i =0 ; i < inerPoint.length ; i++ ){
                    m.add(new int[]{inerPoint[i][0],inerPoint[i][1]});
                    if(leftFlag){
                        int x = inerPoint[i][0];
                        float y = inerPoint[i][1];
                        while (x>=0 && x<operator.width
                                && y>=0 && y< operator.height
                                &&!operator.isPixelBlack( x, (int) y)){
                            x --;
                            y -= delta;
                        }
                        x -= gapDistance;
                        y -= gapDistance*delta;
                        s.add(new int[]{x, (int)y});

                        int distance_x = Math.abs(inerPoint[i][0] - x);
                        float distance_y= Math.abs(inerPoint[i][1] - y);

                        x = Math.max(0, inerPoint[i][0] - (int)(distance_x*alpha));
                        y = Math.max(0, inerPoint[i][1] - (distance_x*alpha)*delta);
                        y = Math.min(operator.height-1, y);
                        d.add(new int[]{x,(int) y});
                    }
                    if(rightFlag){
                        int x = inerPoint[i][0];
                        float y = inerPoint[i][1];
                        while (x>=0 && x<operator.width
                                && y>=0 && y< operator.height
                                &&!operator.isPixelBlack( x, (int) y)){
                            x ++;
                            y += delta;
                        }
                        x += gapDistance;
                        y += gapDistance*delta;
                        s.add(new int[]{x, (int)y});

                        int distance_x = Math.abs(inerPoint[i][0] - x);
                        float distance_y= Math.abs(inerPoint[i][1] - y);

                        x = Math.min(operator.width -1, inerPoint[i][0] + (int) (distance_x*alpha));
                        y = Math.min(operator.height -1, inerPoint[i][1] + (distance_x*alpha)*delta);
                        y = Math.max(0, y);
                        d.add(new int[]{x,(int) y});
                    }

                }
                return result;
            }
        }
        return null;
    }

    /*
    @descirption: 对顶点进行聚类，挑出其中异常的关键点
    @param:     points 待处理的点
                pointSet 一组点对即 n *2 *2
                category 期望点归属的点对的索引
    @renturn:   boolean[]  对应的点是与期望类别一致，一致则为true，否则为false
     */
    public static boolean[] clusterPoint(int[][] points, int[][][] pointSet, int category){
        /*distanceMatrix 记录到各个点对的情况*/
        float[][] distanceMatrix = new float[points.length][pointSet.length];
        boolean [] realIndex = new boolean[points.length];

        for(int i = 0; i < points.length; i++){
            float minDistance = -1;
            int minIndex = -1;
            if(points[i] == null){
                realIndex[i] = false;
                continue;
            }
            for(int j =0; j < pointSet.length; j++){
                float d1 = distance(points[i],pointSet[j][0]);
                float d2 = distance(points[i],pointSet[j][1]);
                distanceMatrix[i][j] = d1 + d2;
                if(minDistance == -1){
                    minDistance = distanceMatrix[i][j];
                    minIndex = j;
                }
                else{
                    if(distanceMatrix[i][j] < minDistance){
                        minDistance = distanceMatrix[i][j];
                        minIndex = j;
                    }
                }

            }
            if(minIndex == category)
                realIndex[i] = true;
            else
                realIndex[i] = false;
        }

        return realIndex;
    }

    public static float distance(int[] p , int[] q){
        float sum = 0;
        sum += ((p[0] - q[0])*(p[0] - q[0]));
        sum += ((p[1] - q[1])*(p[1] - q[1]));
        sum = (float) Math.sqrt((double) sum);
        return sum;
    }

    public static int[][][] genPointSet(int[][] bonePoint){
        int[][][] result = new int[13][2][2];
        result[0] = new int[][]{bonePoint[0],bonePoint[1]};
        result[1] = new int[][]{bonePoint[2],bonePoint[3]};
        result[2] = new int[][]{bonePoint[3],bonePoint[4]};
        result[3] = new int[][]{bonePoint[2],bonePoint[1]};
        result[4] = new int[][]{bonePoint[1],bonePoint[5]};
        result[5] = new int[][]{bonePoint[5],bonePoint[6]};
        result[6] = new int[][]{bonePoint[6],bonePoint[7]};
        result[7] = new int[][]{bonePoint[2],bonePoint[9]};
        result[8] = new int[][]{bonePoint[5],bonePoint[12]};
        result[9] = new int[][]{bonePoint[9],bonePoint[10]};
        result[10] = new int[][]{bonePoint[10],bonePoint[11]};
        result[11] = new int[][]{bonePoint[12],bonePoint[13]};
        result[12] = new int[][]{bonePoint[13],bonePoint[14]};
        return result;
    }
}