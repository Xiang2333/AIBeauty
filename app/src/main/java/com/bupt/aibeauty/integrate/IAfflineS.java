package com.bupt.aibeauty.integrate;

import android.util.Log;

import java.util.Arrays;

public class IAfflineS {
    protected float w[];
    protected float pstar[];
    protected float mul_left[][][];
    protected float vpstar[];
    protected float mul_right[][];
    protected float qstar[];
    protected float qhat[][];
    protected float[][] qhat_A;
    protected float[] qhat_A_0;
    protected float [][] A ;
    protected int ctrl;

    public float[][] p;
    public float[][] q;
    public int height;
    public int width;

    static  double nan = 1/0.;
    private byte [] color;
    private int [] color11;
    private int [] color12;
    private int [] color21;
    private int [] color22;
    protected int pixelColor = 4;
    OperatorS  operator;

    public IAfflineS(int[][] p, int[][] q, int height, int width, OperatorS operator) {
        this.width =width;
        this.height = height;
        this.operator = operator;

        //翻转
        if(q.length != p.length)
            return;
        else{
            this.p = new float[p.length][2];
            this.q = new float[q.length][2];
            for(int i = 0;i < p.length;i++ ){
                this.p[i][0]= q[i][1];
                this.p[i][1]= q[i][0];
                this.q[i][0]= p[i][1];
                this.q[i][1]= p[i][0];
            }
        }

        ctrl = p.length;
        w = new float[20];
        pstar = new float[2];
        mul_left = new float[20][2][2];
        vpstar = new float[2];
        mul_right = new float[2][2];
        qstar  = new float[2];
        qhat = new float[20][2];
        qhat_A =new float[20][2];
        qhat_A_0 = new float[2];
        A = new float[2][2];


        pixelColor = operator.pixelColor;
        this.operator = operator;
        this.color = new byte[pixelColor];
        this.color11 = new int[pixelColor];
        this.color12 = new int[pixelColor];
        this.color21 = new int[pixelColor];
        this.color22 = new int[pixelColor];
    }
    public IAfflineS(int height, int width, OperatorS operator) {
        this.width =width;
        this.height = height;
        this.operator = operator;

        w = new float[20];
        pstar = new float[2];
        mul_left = new float[20][2][2];
        vpstar = new float[2];
        mul_right = new float[2][2];
        qstar  = new float[2];
        qhat = new float[20][2];
        qhat_A =new float[20][2];
        qhat_A_0 = new float[2];
        A = new float[2][2];


        pixelColor = operator.pixelColor;
        this.operator = operator;
        this.color = new byte[pixelColor];
        this.color11 = new int[pixelColor];
        this.color12 = new int[pixelColor];
        this.color21 = new int[pixelColor];
        this.color22 = new int[pixelColor];
    }
    public void setP(int [][]p){
        this.q = new float[p.length][2];
        for(int i = 0;i < p.length;i++ ){
            this.q[i][0]= p[i][1];
            this.q[i][1]= p[i][0];
        }
    }
    public void setQ(int [][]q){
        this.p = new float[q.length][2];
        for(int i = 0;i < q.length;i++ ){
            this.p[i][0]= q[i][1];
            this.p[i][1]= q[i][0];
        }
    }
    public void setPQ(int [][]p, int[][] q){
        //Log.d("affline", Arrays.deepToString(p)+"\n"+Arrays.deepToString(q));
        setP(p);
        setQ(q);
        if(p.length != ctrl)
            ctrl = p.length;
    }
    public float[] afflinePointf(int y, int x) {

        //前处理，应当放置于矩阵之外
        //计算w
        //计算p_start

        pstar[0] =0; pstar[1]=0;
        qhat_A_0[0] =0;
        qhat_A_0[1] =0;


        //qhat_A_0 = new float[2];


        float sum_w =0;//[2]
        {
            for(int i =0; i<ctrl; i++){
                w[i]  =(float) (1/Math.sqrt((double) ((this.p[i][0] - x)*(this.p[i][0] - x)+(this.p[i][1] - y)*(this.p[i][1] - y))));
                pstar[0] += p[i][0]*w[i];
                pstar[1] += p[i][1]*w[i];
                sum_w+=w[i];
            }
            pstar[0] =pstar[0]/sum_w;
            pstar[1] =pstar[1]/sum_w;

        }
        //[ctrl][2][2]
        for(int i=0; i< ctrl;i++){
            mul_left[i][0][0] = p[i][0] - pstar[0] ;
            mul_left[i][0][1] = p[i][1] - pstar[1];
            mul_left[i][1][0] = p[i][1] - pstar[1];
            mul_left[i][1][1] = -p[i][0] + pstar[0];
        }

        //v-p*

        //因为太短这里就不写循环了
        vpstar[0] = x-pstar[0];
        vpstar[1] = y-pstar[1];

        {
            //因为元素比较少，所以直接选择赋值了
            mul_right[0][0]=vpstar[0];
            mul_right[0][1]=vpstar[1];
            mul_right[1][0]=vpstar[1];
            mul_right[1][1]=-vpstar[0];
        }
        //q
        //[ctrl,2]
        {
            qstar[0] =0;
            qstar[1] =0;
            for(int i =0; i < ctrl; i++){
                qstar[0] +=w[i]*q[i][0];
                qstar[1] +=w[i]*q[i][1];
            }
            qstar[0] = qstar[0]/sum_w;
            qstar[1] = qstar[1]/sum_w;
            for(int i=0; i<ctrl;i++){
                qhat[i][0]=q[i][0] - qstar[0];
                qhat[i][1]=q[i][1] - qstar[1];
            }
        }



        //qhat与A作矩阵运算[是多组矩阵叠加，不知如何优化]

        for(int i=0; i<ctrl;i++){
            A[0][0]=w[i]*(mul_left[i][0][0]*mul_right[0][0]+mul_left[i][0][1]*mul_right[1][0]) ;
            A[0][1]=w[i]*(mul_left[i][0][0]*mul_right[0][1]+mul_left[i][0][1]*mul_right[1][1])  ;
            A[1][0]=w[i]*(mul_left[i][1][0]*mul_right[0][0]+mul_left[i][1][1]*mul_right[1][0])  ;
            A[1][1]=w[i]*(mul_left[i][1][0]*mul_right[0][1]+mul_left[i][1][1]*mul_right[1][1])  ;
            qhat_A[i][0] = qhat[i][0]*A[0][0] +qhat[i][1]*A[1][0];
            qhat_A[i][1] = qhat[i][0]*A[0][1] +qhat[i][1]*A[1][1];
            qhat_A_0[0] +=qhat_A[i][0];
            qhat_A_0[1] +=qhat_A[i][1];
        }
        //对qhat_A_0求范数
        float norm_qhat_A = (float)Math.sqrt(qhat_A_0[0]*qhat_A_0[0] + qhat_A_0[1]*qhat_A_0[1]);
        float nor_vpstar = (float)Math.sqrt(vpstar[0]*vpstar[0]+vpstar[1]*vpstar[1]);
        float[] transfer = new float[2];
        transfer[0] = (qhat_A_0[0]*nor_vpstar/norm_qhat_A+qstar[0]);
        transfer[1] = (qhat_A_0[1]*nor_vpstar/norm_qhat_A+qstar[1]);

        //阶段
        if(transfer[0]<0 || transfer[0] > height-1)
            transfer[0] =0;
        if(transfer[1]<0 || transfer[1] >width-1)
            transfer[1] =0;

        return transfer;
    }
    public float[] afflinePointf(int y, int x, float[] point) {

        //前处理，应当放置于矩阵之外
        //计算w
        //计算p_start

        pstar[0] =0; pstar[1]=0;
        qhat_A_0[0] =0;
        qhat_A_0[1] =0;


        //qhat_A_0 = new float[2];


        float sum_w =0;//[2]
        {
            for(int i =0; i<ctrl; i++){
                if(p[i][0] == x && p[i][1] == y){
                    point[1] = p[i][0];
                    point[0] = p[i][1];
                    return point;
                }

                w[i]  =(float) (1/Math.sqrt((double) ((this.p[i][0] - x)*(this.p[i][0] - x)+(this.p[i][1] - y)*(this.p[i][1] - y))));
                pstar[0] += p[i][0]*w[i];
                pstar[1] += p[i][1]*w[i];
                sum_w+=w[i];
            }
            pstar[0] =pstar[0]/sum_w;
            pstar[1] =pstar[1]/sum_w;

        }
        //[ctrl][2][2]
        for(int i=0; i< ctrl;i++){
            mul_left[i][0][0] = p[i][0] - pstar[0] ;
            mul_left[i][0][1] = p[i][1] - pstar[1];
            mul_left[i][1][0] = p[i][1] - pstar[1];
            mul_left[i][1][1] = -p[i][0] + pstar[0];
        }

        //v-p*

        //因为太短这里就不写循环了
        vpstar[0] = x-pstar[0];
        vpstar[1] = y-pstar[1];

        {
            //因为元素比较少，所以直接选择赋值了
            mul_right[0][0]=vpstar[0];
            mul_right[0][1]=vpstar[1];
            mul_right[1][0]=vpstar[1];
            mul_right[1][1]=-vpstar[0];
        }
        //q
        //[ctrl,2]
        {
            qstar[0] =0;
            qstar[1] =0;
            for(int i =0; i < ctrl; i++){
                qstar[0] +=w[i]*q[i][0];
                qstar[1] +=w[i]*q[i][1];
            }
            qstar[0] = qstar[0]/sum_w;
            qstar[1] = qstar[1]/sum_w;
            for(int i=0; i<ctrl;i++){
                qhat[i][0]=q[i][0] - qstar[0];
                qhat[i][1]=q[i][1] - qstar[1];
            }
        }



        //qhat与A作矩阵运算[是多组矩阵叠加，不知如何优化]

        for(int i=0; i<ctrl;i++){
            A[0][0]=w[i]*(mul_left[i][0][0]*mul_right[0][0]+mul_left[i][0][1]*mul_right[1][0]) ;
            A[0][1]=w[i]*(mul_left[i][0][0]*mul_right[0][1]+mul_left[i][0][1]*mul_right[1][1])  ;
            A[1][0]=w[i]*(mul_left[i][1][0]*mul_right[0][0]+mul_left[i][1][1]*mul_right[1][0])  ;
            A[1][1]=w[i]*(mul_left[i][1][0]*mul_right[0][1]+mul_left[i][1][1]*mul_right[1][1])  ;
            qhat_A[i][0] = qhat[i][0]*A[0][0] +qhat[i][1]*A[1][0];
            qhat_A[i][1] = qhat[i][0]*A[0][1] +qhat[i][1]*A[1][1];
            qhat_A_0[0] +=qhat_A[i][0];
            qhat_A_0[1] +=qhat_A[i][1];
        }
        //对qhat_A_0求范数
        float norm_qhat_A = (float)Math.sqrt(qhat_A_0[0]*qhat_A_0[0] + qhat_A_0[1]*qhat_A_0[1]);
        float nor_vpstar = (float)Math.sqrt(vpstar[0]*vpstar[0]+vpstar[1]*vpstar[1]);

        point[0] = (qhat_A_0[0]*nor_vpstar/norm_qhat_A+qstar[0]);
        point[1] = (qhat_A_0[1]*nor_vpstar/norm_qhat_A+qstar[1]);

        //截断

        if(point[0]<0 )
            point[0] =0;
        if(point[0]>=height-1)
            point[0] =height-2;
        if(point[1]<0 )
            point[1] =0;
        if(point[1]>=width-1)
            point[1] = width -2;

        return point;
    }
    public void changeImage(int[][] p,int[][] q){
        this.setPQ(p,q);
        this.changeImage();
    }
    public void changeImage(){
        float[] point = new float[2];
        for(int y=0; y<this.height;y++){
            for(int x=0; x<this.width;x++){
                point = this.afflinePointf(x,y,point);
                //作双线性差值变换
                this.insertValue(point[1],point[0],color);
                for(int t =0; t < pixelColor; t++ ){
                    operator.setDeformValue(x,y,t,color[t]);
                }
            }
        }
    }
    public void insertValue(double x, double y, byte[] color){
        if(color == null) return;

        double x1 = Math.floor(x);
        double y1 = Math.floor(y);

        double x2 = x1 +1;
        double y2 = y1 +1;

        if(y1 == 770||y2==770)
            Log.i("aaa","aaa");


        double k11 = (x2-x)/(x2-x1);
        double k12 = (x-x1)/(x2-x1);
        double k31 = (y2-y)/(y2-y1);
        double k32 = (y-y1)/(y2-y1);
        color11 = operator.getOriginValue((int) x1,(int) y1,this.color11);
        color12 = operator.getOriginValue((int) x1,(int) y2,this.color12);
        color21 = operator.getOriginValue((int) x2,(int) y1,this.color21);
        color22 = operator.getOriginValue((int) x2,(int) y2,this.color22);

        for(int i=0; i < pixelColor;i++){
            double r1,r2;
            if(x == x1){
                r1 = color11[i];
                r2 = color12[i];
            }
            else{
                r1 = (color11[i]*k11 + color21[i]*k12);
                r2 = (color12[i]*k11 + color22[i]*k12);
            }
            if(y == y1){
                color[i] = (byte) r1;
            }else{
                color[i] = (byte)(k31*r1 + k32* r2);
            }
        }
    }
}