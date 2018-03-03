package com.example;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;

import com.example.gongju.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李旭 on 2018/2/27  12:07
 */

public class My_huabu extends SurfaceView implements SurfaceHolder.Callback{
    //图片的宽
    private int tupian_width = 0;
    //图片的高
    private int tupian_height = 0;
    //图片的位图变量
    private Bitmap tupian;
    //画布
    private Canvas huabu;
    //图片的倍率
    private float beilv = 1;
    //图片移动的x距离
    private float x_juli = 0;
    //图片移动的y距离
    private float y_juli = 0;
    //选择框工具
    private xuanzekuang_gongju xuanzekuang = new xuanzekuang_gongju();
    //记录带位置的图片识别返回的位置信息集合
    List<List<Integer>> wenzi_wz = new ArrayList<>();

    public My_huabu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setTupian(String dizhi){
        BitmapFactory.Options yasuo = new BitmapFactory.Options();
        yasuo.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeFile(dizhi);
        tupian = bitmap.copy(Bitmap.Config.RGB_565,true);
        if (tupian != bitmap){
            if (bitmap!= null&& !bitmap.isRecycled()){
                bitmap.recycle();
                bitmap = null;
            }
        }
        getHolder().addCallback(this);
    }

    public int[] getjuli(int tupian_width,int tupian_height){
        this.tupian_height = tupian_height;
        this.tupian_width = tupian_width;
        int x_juli = 0;
        int y_juli = 0;
        if (tupian_width<getWidth()){
            x_juli = getWidth() / 2 - tupian_width/2;
        }else if (tupian_width>getWidth()){
            x_juli = x_juli - (tupian_width - getWidth())/2;
        }
        if (tupian_height < getHeight()){
            y_juli = getHeight() / 2 - tupian_height /2;
        }else if (tupian_height>getHeight()){
            y_juli = y_juli - (tupian_height - getHeight())/2;
        }
        return new int[]{x_juli,y_juli};
    }

    public void fuwei(){
        int[] juli = getjuli(tupian.getWidth(),tupian.getHeight());
        x_juli = juli[0];
        y_juli = juli[1];
        beilv = 1;
        shuaxin();
    }

    public Bitmap getTupian(){
        int width = tupian.getWidth();
        int height = tupian.getHeight();
        int x1 = 0;
        int y1 = 0;
        int x2 = width;
        int y2 = height;
        if (xuanzekuang.getKeshi()){
            float[] wz = xuanzekuang.getwz2();
            x1 = (int)wz[0];
            y1 = (int)wz[1];
            x2 = (int)wz[2];
            y2 = (int)wz[3];
        }
        if (x1<0){
            x2 = x1 + x2;
            x1 = 0;
        }else if (x1 + x2>width){
            x2 = width - x1;
        }
        if (y1<0){
            y2 = y1 + y2;
            y1 = 0;
        }else if (y1 + y2 > height){
            y2 = height - y1;
        }

        if (x2<1){
            x2=1;
        }else if (x1 >= width){
            x1 = width - 1;
            x2 = 1;
        }
        if (y2<1){
            y2=1;
        }
        Log.e("位置2：",x1+"   "+x2+"   "+y1+"   "+y2);
        Log.e("位置2：",tupian.getWidth()+"   "+tupian.getHeight());
        Matrix matrix = new Matrix();

        int wd = x1 == width - 1 ? 1 : x2;
        int he = y1 == height - 1 ? 1 : y2;
        int max = wd > he ? wd : he;
        if (max> 2048){
            matrix.setScale(2048f/(float)max,2048f/(float)max);
        }
        Bitmap bitmap = Bitmap.createBitmap(tupian,x1 >= width ? width - 1 : x1,y1 >= height ? height -1 : y1,wd,he,matrix,true);
        return bitmap;
    }

    public void setWenzi_wz(List<List<Integer>> wenzi_wz){
        if (this.wenzi_wz.size()>1){
            this.wenzi_wz.clear();
        }
        this.wenzi_wz = wenzi_wz;
        shuaxin();
    }

    public boolean getWenzi_wz_kesih(){
        if (wenzi_wz.size()>1){
            return true;
        }
        return false;
    }

    public void setWenzi_xuanze(float x, float y){
        int i = 0;
        x = (x-x_juli)/beilv;
        y = (y-y_juli)/beilv;
        for (List<Integer> wz : wenzi_wz){
            float x1 = wz.get(0);
            float y1 = wz.get(1);
            float x2 = x1 + wz.get(2);
            float y2 = y1 + wz.get(3);
            if (x>x1&&x<x2){
                if (y>y1&&y<y2){
                    if (wenzi_wz.get(i).get(4) == 0){
                        wenzi_wz.get(i).remove(4);
                        wenzi_wz.get(i).add(1);
                    }else {
                        wenzi_wz.get(i).remove(4);
                        wenzi_wz.get(i).add(0);
                    }
                    shuaxin();
                    return;
                }
            }
            i++;
        }
    }

    public String get_xuanze_wenben(List<String> shibie_wenben){
        String wenben = "";
        int i = 0;
        for (List<Integer> wz : wenzi_wz){
            if (wz.get(4) == 1){
                if (wenben.equals("")){
                    wenben = shibie_wenben.get(i);
                }else {
                    wenben = wenben + "\n"+shibie_wenben.get(i);
                }
            }
            i++;
        }
        i = 0;
        if (wenben.equals("")){
            for (String s : shibie_wenben){
                if (wenben.equals("")){
                    wenben = shibie_wenben.get(i);
                }else {
                    wenben = wenben + "\n"+shibie_wenben.get(i);
                }
                i++;
            }
        }
        return wenben;
    }

    public void xuanzhuan(){
        Bitmap bitmap = Bitmap.createBitmap(tupian.getHeight(),tupian.getWidth(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.parseColor("#66ccff"));
        float width = tupian.getWidth();
        float height = tupian.getHeight();
        float max = height;
        float min = width;
        if (width > height){
            max = width;
            min = height;
        }
        float wz = (max-min)/2f;
        canvas.rotate(90,bitmap.getWidth()/2,bitmap.getHeight()/2f);
        canvas.drawBitmap(tupian,width > height ?  -wz : wz,width > height ? wz : -wz,new Paint());
        canvas.save();
        canvas.restore();
        if (tupian!=bitmap){
            if (tupian!=null && !tupian.isRecycled()){
                tupian.recycle();
                tupian = null;
            }
            tupian = bitmap;
        }
        if (wenzi_wz.size()>1){
            wenzi_wz.clear();
        }
        fuwei();
    }

    public void clear(){
        if (wenzi_wz.size()>1){
            wenzi_wz.clear();
        }
        if (tupian != null){
            if (!tupian.isRecycled()){
                tupian.recycle();
                tupian = null;
            }
        }
    }

    public void shuaxin(){
        huabu = getHolder().lockCanvas();
        huabu.drawColor(getResources().getColor(R.color.colorBeijing));
        huabu.save();
        huabu.scale(this.beilv,this.beilv);
        huabu.translate(x_juli/beilv,y_juli/beilv);
        huabu.drawBitmap(tupian,0,0,new Paint());

        if (xuanzekuang.getKeshi()){
            Paint bi= new Paint();
            bi.setStyle(Paint.Style.STROKE);
            bi.setStrokeWidth(5/beilv);
            bi.setColor(getResources().getColor(R.color.colorAccent));
            bi.setPathEffect(new DashPathEffect(new float[] {20, 10}, 0));
            float[] wz = xuanzekuang_panduan_wz(xuanzekuang.getwz());
            RectF rectF = new RectF(wz[0],wz[1],wz[2],wz[3]);
            huabu.drawRect(rectF,bi);
        }

        if (wenzi_wz.size()>1){
            huabu.save();
            Paint bi= new Paint();
            bi.setStrokeWidth(5/beilv);
            bi.setColor(getResources().getColor(R.color.colorAccent));
            bi.setPathEffect(new DashPathEffect(new float[] {20, 10}, 0));
            for (List<Integer> wz : wenzi_wz){
                if (wz.get(4) == 0){
                    bi.setStyle(Paint.Style.STROKE);
                    bi.setAlpha(255);
                }else {
                    bi.setStyle((Paint.Style.FILL));
                    bi.setAlpha(100);
                }
                RectF rectF = new RectF(wz.get(0),wz.get(1),wz.get(0)+wz.get(2),wz.get(1)+wz.get(3));
                huabu.drawRect(rectF,bi);
            }
            huabu.restore();
        }


        getHolder().unlockCanvasAndPost(huabu);
    }

    public float[] xuanzekuang_panduan_wz(float[] wz){

        if (wz[0]*beilv < 0){
            wz[0]  = 0;
        }
        if (wz[1] < 0){
            wz[1] = 0;
        }
        if (wz[2]*beilv > tupian.getWidth()*beilv){
            wz[2] = tupian.getWidth();
        }
        if (wz[0]*beilv > tupian.getWidth()*beilv){
            wz[0] = tupian.getWidth();
        }
        if (wz[3]*beilv > tupian.getHeight()*beilv){
            wz[3] = tupian.getHeight();
        }
        if (wz[1]*beilv > tupian.getHeight()*beilv){
            wz[1] = tupian.getHeight();
        }
        if (wz[0] > wz[2]){
            wz[2] = wz[0];
        }
        if (wz[1]> wz [3]){
            wz[3] = wz[1];
        }
        return wz;

    }

    public void setWeizhi(float x,float y){
        x_juli = x_juli - x;
        y_juli = y_juli - y;
        shuaxin();
    }

    public void setSize(float beilv){
        if (this.beilv <= 1){
            beilv = beilv / 1000;
        }else {
            beilv = beilv / 500;
        }
        if (this.beilv == 0.1){
            return;
        }else if (this.beilv - beilv<0.1) {
            this.beilv = 0.1f;
        }else {
            this.beilv = this.beilv - beilv;
        }
        if (this.beilv == 32){
            return;
        }else if (this.beilv - beilv > 32){
            this.beilv = 32f;
        }

        shuaxin();
    }

    public float getBeilv(){
        return beilv;
    }

    public void shihepingmu(){
        if (tupian.getWidth() >= tupian.getHeight()){
            shihepingmu_width(true);
        }else {
            shihepingmu_height(true);
        }
        int[] juli = getjuli((int)(tupian.getWidth()*beilv),(int)(tupian.getHeight()*beilv));
        x_juli = juli[0];
        y_juli = juli[1];
        shuaxin();
    }

    public void tianchongpingmu(){
        if (tupian.getWidth() <= tupian.getHeight()){
            shihepingmu_height(false);
        }else {
            shihepingmu_width(false);
        }
        int[] juli = getjuli((int)(tupian.getWidth()*beilv),(int)(tupian.getHeight()*beilv));
        x_juli = juli[0];
        y_juli = juli[1];
        shuaxin();
    }

    public float getTupian_width(){
        return tupian.getWidth();
    }

    public float getTupian_height(){
        return tupian.getHeight();
    }

    public float getTupian_X(){
        return x_juli;
    }

    public float getTupian_X2(){
        return x_juli + tupian.getWidth() * beilv;
    }

    public float getTupian_Y(){
        return y_juli;
    }

    public float getTupian_Y2(){
        return y_juli + tupian.getHeight()*beilv;
    }

    public boolean panduan_dianji(float x,float y){
        if (x>=x_juli&&y>=y_juli){
            if (x<=x_juli+tupian.getWidth()*beilv&&y<=y_juli+tupian.getHeight()*beilv){
                return true;
            }
        }
        return false;
    }

    public void xuanzekuang_kai(float x1,float y1,float x2,float y2){
        xuanzekuang.kai((x1 / beilv) - x_juli/beilv,(y1 / beilv)-y_juli/beilv,(x2/beilv) - x_juli/beilv,(y2/beilv)-y_juli/beilv);
    }

    public void xuanzekuang_yidong(float x,float y){
        xuanzekuang.yidong(x/beilv,y/beilv);
        shuaxin();
    }

    public boolean xuanzaikuang_cunzai(){
        if (xuanzekuang.cunzai()){
            return true;
        }
        return false;
    }

    private void shihepingmu_width(boolean shihe_tianchong){
        tupian_width = getWidth();
        beilv = (float)tupian_width/(float)tupian.getWidth() ;
        tupian_height = (int)(tupian.getHeight() *beilv);

        if (shihe_tianchong){
            if (tupian_height > getHeight()){
                shihepingmu_height(true);
            }
        }else {
            if (tupian_height < getHeight()){
                shihepingmu_height(false);
            }
        }
    }

    private void shihepingmu_height(boolean shihe_tianchong){
        tupian_height = getHeight();
        beilv = (float)tupian_height/(float)tupian.getHeight();
        tupian_width = (int)(tupian.getWidth() * beilv);

        if (shihe_tianchong){
            if (tupian_width > getWidth()){
                shihepingmu_width(true);
            }
        }else {
            if (tupian_width < getWidth()){
                shihepingmu_width(false);
            }
        }
    }

    public void xuanzekuang_clear(){
        xuanzekuang.clear();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        fuwei();
        Log.e("自绘;","1235");
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
