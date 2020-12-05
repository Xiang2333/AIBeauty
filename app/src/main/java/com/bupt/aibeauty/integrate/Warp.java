package com.bupt.aibeauty.integrate;


import com.bupt.aibeauty.integrate.ImageOperator;

import static java.lang.Math.*;

public class Warp {
    //全部为x,y形式
    public static void warp(float[] center, float[] orient,float height, float width, ImageOperator operator){
        float down,top,left,right;
        float radius2,radius;
        float direct0,direct1;
        //数据检查
        {
            if(center.length != 2) return;
            if(orient.length != 2) return;
        }
        direct0 = orient[0] - center[0];
        direct1 = orient[1] - center[1];
        radius2 = direct0 * direct0 + direct1 * direct1;
        radius = (float) sqrt((double) radius2);
        //计算radius
        top = Math.max(0, center[1]-radius);
        down= Math.min(height - 1, center[1] + radius);
        left= Math.max(0,center[0]-radius);
        right = Math.min(width-1, center[0]+radius);

        for(int i = (int) top; i <= down; i++){//y
            for (int j = (int) left; j<= right; j++){//x
                float len2 = (j-center[0])*(j-center[0]) + (i-center[1])*(i-center[1]);
                if(len2 <= radius2) {//圈内
                    float e = 1 - (radius2/(2*radius2 - len2));
                    e = e*e;
                    float source_0 = j - e * direct0;
                    float source_1 = i - e * direct1;

                    if( source_0>=0 && source_0 <= width -1 && source_1 >=0 && source_1 <= height -1){
                        operator.setPixelColor(j,i,(int)source_0,(int) source_1);
                    }

                }
            }
        }
    }
    //全部为x,y形式
    public static void warpByRange(float[] center, float[] orient,float height, float width, int range,ImageOperator operator){
        float direct0 = orient[0] - center[0];
        float direct1 = orient[1] - center[1];
        float distance=(float) Math.sqrt(direct0 * direct0 + direct1 * direct1);
        float circleX=range*direct0/distance+center[0];
        float circleY=range*direct1/distance+center[1];

        direct0=circleX-center[0];
        direct1=circleY-center[1];

        float down,top,left,right;
        float radius2,radius;

        radius2 = direct0 * direct0 + direct1 * direct1;
        radius = (float) sqrt(radius2);
        //Log.d("free",radius+" ");
        //计算radius

        top = Math.max(0, center[1]-radius);
        down= Math.min(height - 1, center[1] + radius);
        left= Math.max(0,center[0]-radius);
        right = Math.min(width-1, center[0]+radius);
        //Log.d("warp",top+" "+down+" "+left+" "+right);

        for(int i = (int) top; i <= down; i++){//y
            for (int j = (int) left; j<= right; j++){//x
                float len2 = (j-center[0])*(j-center[0]) + (i-center[1])*(i-center[1]);
                if(len2 <= radius2) {//圈内
                    float e = 1 - (radius2/(2*radius2 - len2));
                    e = e*e;
                    float source_0 = j - e * direct0;
                    float source_1 = i - e * direct1;

                    if( source_0>=0 && source_0 <= width -1 && source_1 >=0 && source_1 <= height -1){
                        operator.setPixelColor(j,i,(int)source_0,(int) source_1);
                    }

                }
            }
        }

    }
    public static void testWarp(float[] center, float[] orient,float height, float width, int range,ImageOperator operator){
        float down,top,left,right;
        float radius2,radius;
        float direct0,direct1;
        //数据检查
        {
            if(center.length != 2) return;
            if(orient.length != 2) return;
        }

        top = Math.max(0, center[1]-range);
        down= Math.min(height - 1, center[1] + range);
        left= Math.max(0,center[0]-range);
        right = Math.min(width-1, center[0]+range);

        direct0 = orient[0] - center[0];
        direct1 = orient[1] - center[1];
        radius2 = direct0 * direct0 + direct1 * direct1;
        radius = (float) sqrt((double) radius2);

        //计算radius


        for(int i = (int) top; i <= down; i++){//y
            for (int j = (int) left; j<= right; j++){//x
                int destX=j,destY=i;
                float temp1=(destX-center[0])*(destX-center[0])+(destY-center[1])*(destY-center[1]);
                float temp2=(orient[0]-center[0])*(orient[0]-center[0])+(orient[1]-center[1])*(orient[1]-center[1]);
                float temp3=range*range;
                if(temp1>temp3) continue;
                if(temp2<4*temp3){
                    temp2= (float) (6.25*temp3);
                }

                float len2 = (j-center[0])*(j-center[0]) + (i-center[1])*(i-center[1]);
                if(len2 <= range*range) {//圈内
                    float e = (float) (1 - (1.0*temp2/(temp3-temp1+temp2)));
                    e = e*e;
                    float source_0 = j - e * direct0;
                    float source_1 = i - e * direct1;

                    if( source_0>=0 && source_0 <= width -1 && source_1 >=0 && source_1 <= height -1){
                        operator.setPixelColor(j,i,(int)source_0,(int) source_1);
                    }

                }
            }
        }

    }
}