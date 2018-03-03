package com.example.gongju;

import android.graphics.RectF;
import android.util.Log;

/**
 * Created by 李旭 on 2018/2/27  14:24
 */

public class xuanzekuang_gongju {
    //选择框的x坐标
    private float x = 0;
    //选择框的y坐标
    private float y = 0;
    //选择框的宽度
    private float width;
    //选择框的高度
    private float height;
    //可视
    private boolean keshi = false;

    public boolean getKeshi(){
        return keshi;
    }

    public void kai(float x1,float y1,float x2,float y2){
        keshi = true;
        if (x1 > x2){
            x = x2;
            width = Math.abs(x1 - x2);
        }else {
            x = x1;
            width = Math.abs(x2 - x1);
        }
        if (y1 < y2){
            y = y1;
            height = Math.abs(y2 - y1);
        }else {
            y = y2;
            height = Math.abs(y1-y2);
        }
        Log.e("选择框：",x1+"   "+y1+"   "+x2+"   "+y2);
    }

    public float[] yidong(float x,float y){
        this.x = this.x - x;
        this.y = this.y - y;

        float[] wz = {this.x,this.y,this.x+width,this.y+height};
        Log.e("位置",this.x+"   "+this.y);
        return wz;
    }

    public RectF getRectF(){
        return new RectF(x,y,x+width,y+height);
    }

    public float[] getwz(){
        return new float[]{this.x,this.y,this.x+width,this.y+height};
    }

    public float[] getwz2(){
        return new float[]{this.x,this.y,this.width,this.height};
    }

    public void clear(){
        x = 0;
        y = 0;
        width = 0;
        height = 0;
        keshi = false;
    }

    public boolean cunzai(){
        if (width > 0 && height > 0){
            return true;
        }
        return false;
    }
}
