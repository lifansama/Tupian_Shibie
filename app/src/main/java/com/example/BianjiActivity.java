package com.example;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.GeneralBasicParams;
import com.baidu.ocr.sdk.model.GeneralParams;
import com.baidu.ocr.sdk.model.GeneralResult;
import com.baidu.ocr.sdk.model.Location;
import com.baidu.ocr.sdk.model.Word;
import com.baidu.ocr.sdk.model.WordSimple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BianjiActivity extends AppCompatActivity {
    private String path ;
    //滑动时的x历史坐标
    private float x;
    //滑动时的y历史坐标
    private float y;
    //滑动时的x2历史坐标
    private float x2;
    //滑动时的y2历史坐标
    private float y2;
    //记录两点间的距离
    private float juli;
    //判断点击的位置是否在图片上
    private boolean dianji = false;
    //记录是否为双指滑动时换成单指
    private boolean shuazhi_danzhi = false;
    //判断动画是否进行
    private boolean donghua = false;
    //工具栏开关
    private boolean gongjulan_kg = true;
    //当前选择的工具
    private int gongju = -1;
    //工具栏组件集合
    private List<Integer> gongju_list = new ArrayList<>();
    //记录工具设置栏的布局view
    private View layout;
    //画布组件
    private My_huabu huaban;
    //记录当前是选择工具时，是否点击在以选择的区域内
    private boolean xuanzekuang = false;
    //记录带位置识别返回的文本
    private List<String> shibie_wenben = new ArrayList<>();
    //记录为带位置识别图片时的手指点击坐标
    private float weizhi_x= 0;
    private float weizhi_y = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bianji);

        //状态栏半透明
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == View.VISIBLE){
                    setSystemUIVisible(false);
                }
            }
        });

        //初始化工具组件集合
        gongju_list.add(R.id.zhuashou_Id);
        gongju_list.add(R.id.xuxian_Id);
        gongju_list.add(R.id.wenzi_Id);
        gongju_list.add(R.id.saomiao_Id);

        path = getIntent().getStringExtra("path");
        huaban = findViewById(R.id.huabu_Id);
        huaban.setTupian(path);

        huaban.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        x = event.getX();
                        y = event.getY();
                        dianji = huaban.panduan_dianji(x,y);
                        if (gongju == 1){
                            if (!xuanzekuang){
                                if (!dianji){
                                    if (x<huaban.getTupian_X()){
                                        x = huaban.getTupian_X();
                                    }else if (x > huaban.getTupian_X2()){
                                        x = huaban.getTupian_X2();
                                    }
                                    if (y<huaban.getTupian_Y()){
                                        y = huaban.getTupian_Y();
                                    }else if (y > huaban.getTupian_Y2()){
                                        y = huaban.getTupian_Y2();
                                    }
                                }
                            }
                        }
                        if (MainActivity.moshi == 3 || MainActivity.moshi == 4){
                            if (gongju == 2){
                                if (huaban.getWenzi_wz_kesih()){
                                    weizhi_x = x;
                                    weizhi_y = y;
                                }
                            }
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_2_DOWN:
                        x2 = event.getX(1);
                        y2 = event.getY(1);
                        juli = (float) Math.sqrt((x - x2)*(x - x2) + (y - y2) * (y - y2));
                        break;
                    case MotionEvent.ACTION_MOVE:
                        switch (gongju){
                            case 0:
                            case 2:
                                if (event.getPointerCount() == 2){
                                    //求两点间距离公式
                                    float juli_new = (float) Math.sqrt((x - x2)*(x - x2) + (y - y2) * (y - y2));
                                    float jl = juli - juli_new;
                                    juli = juli_new;
                                    x2 = event.getX(1);
                                    y2 = event.getY(1);
                                    float  b   =  (float)(Math.round(jl*100))/100;
                                    huaban.setSize(b);
                                    shuazhi_danzhi = true;
                                }else if (event.getPointerCount() == 1){
                                    if (shuazhi_danzhi){
                                        x = event.getX();
                                        y = event.getY();
                                        shuazhi_danzhi = false;
                                    }
                                    huaban.setWeizhi(x - event.getX(),y-event.getY());
                                }
                                x = event.getX();
                                y = event.getY();
                                break;
                            case 1:     //选择  选择工具滑动时
                                if (!xuanzekuang){      //没点击在已选择的区域内
                                    float x2 = event.getX();
                                    float y2 = event.getY();
                                    if (!huaban.panduan_dianji(event.getX(),event.getY())) {
                                        if (event.getX() < huaban.getTupian_X()) {
                                            x2 = huaban.getTupian_X();
                                        }else if (event.getX() > huaban.getTupian_X2()){
                                            x2 = huaban.getTupian_X2();
                                        }
                                        if (event.getY() < huaban.getTupian_Y()) {
                                            y2 = huaban.getTupian_Y();
                                        }else if (event.getY() > huaban.getTupian_Y2()){
                                            y2 = huaban.getTupian_Y2();
                                        }
                                    }
                                    huaban.xuanzekuang_kai(x,y,x2,y2);
                                    huaban.shuaxin();
                                }else {     //点击在已选择的区域内
                                    huaban.xuanzekuang_yidong(x - event.getX(),y-event.getY());
                                    x = event.getX();
                                    y = event.getY();
                                }

                                break;
                        }
                        break;
                    case  MotionEvent.ACTION_UP:
                        if (gongju == 1){
                            xuanzekuang = huaban.xuanzaikuang_cunzai();
                        }
                        if (MainActivity.moshi == 3 || MainActivity.moshi == 4){
                            if (gongju == 2){
                                if (huaban.getWenzi_wz_kesih()){
                                    if (Math.abs(weizhi_x-event.getX())<10){
                                        if (Math.abs(weizhi_y-event.getY())<10){
                                            huaban.setWenzi_xuanze(event.getX(),event.getY());
                                            EditText wenben = findViewById(R.id.wenben_Id);
                                            wenben.setText(huaban.get_xuanze_wenben(shibie_wenben));
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        dianji = false;
//                        tupian_fangda_gone();
                        break;
                }

                return true;
            }
        });

        gongju_onclick(findViewById(R.id.zhuashou_Id));

        if (MainActivity.moshi == 3 || MainActivity.moshi == 4){
            findViewById(R.id.xuxian_Id).setVisibility(View.GONE);
        }

        setSystemUIVisible(false);
    }

    private void setSystemUIVisible(boolean show) {
        if (show) {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            uiFlags |= 0x00001000;
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        } else {
            int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            uiFlags |= 0x00001000;
            getWindow().getDecorView().setSystemUiVisibility(uiFlags);
        }
    }

    private void file_clear(File file,Bitmap tupian){
        file.delete();
        if (tupian != null && !tupian.isRecycled()){
            tupian.recycle();
            tupian = null;
        }
    }

    public void gongju_shezhi(View view){
        switch (gongju){
            case 0:     //移动工具
                switch (view.getId()){
                    case R.id.fuwu_Id:
                        huaban.fuwei();
                        break;
                    case R.id.shihepingmu_Id:
                        huaban.shihepingmu();
                        break;
                    case R.id.tianchongpingmu_Id:
                        huaban.tianchongpingmu();
                        break;
                    case R.id.xuanzhuan_Id:
                        huaban.xuanzekuang_clear();
                        xuanzekuang = false;
                        huaban.shuaxin();
                        huaban.xuanzhuan();
                        if (shibie_wenben.size()>1){
                            shibie_wenben.clear();
                            EditText wenben = findViewById(R.id.wenben_Id);
                            wenben.setText("");
                        }
                        break;
                }
                break;
            case 1:     //选择工具
                switch (view.getId()){
                    case R.id.quxiao_xuanze_Id:
                        huaban.xuanzekuang_clear();
                        xuanzekuang = false;
                        huaban.shuaxin();
                        break;
                }
                break;
            case 3:
                if (view.getId() == R.id.kaishi_shibie_Id){
                    if (MainActivity.token == 0){
                        Snackbar.make(huaban,"token尚未获取！请稍等",Snackbar.LENGTH_LONG).show();
                        return;
                    }else if (MainActivity.token == 2){
                        Snackbar.make(huaban,"token获取失败，请退出重试！",Snackbar.LENGTH_LONG).show();
                        return;
                    }
                    if (shibie_wenben.size()>1){
                        shibie_wenben.clear();
                    }
                    final TextView tv = (TextView)view;
                    tv.setText("识别中...");
                    tv.setEnabled(false);

                    final GeneralParams qingqiu = new GeneralParams();
                    qingqiu.setDetectDirection(true);

                    File file = new File(Environment.getExternalStorageDirectory().getPath() + "/M3h/hc/hc.jpg");
                    Bitmap tupian = huaban.getTupian();
                    try {
                        FileOutputStream baocun = new FileOutputStream(file);
                        tupian.compress(Bitmap.CompressFormat.JPEG,100,baocun);
                        baocun.flush();
                        baocun.close();

                        qingqiu.setImageFile(file);

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    switch (MainActivity.moshi){
                        case 0:
                        case 4:
                            Spinner spinner = layout.findViewById(R.id.saomiao_yuyan_Id);
                            String yuyan = spinner.getSelectedItem().toString();
                            if (yuyan.equals("自动识别")){
                                qingqiu.setDetectLanguage(true);
                            }else if (yuyan.equals("中英混合")){
                                qingqiu.setLanguageType(GeneralBasicParams.CHINESE_ENGLISH);
                            }else if (yuyan.equals("英语")){
                                qingqiu.setLanguageType(GeneralBasicParams.ENGLISH);
                            }else if (yuyan.equals("葡萄牙语")){
                                qingqiu.setLanguageType(GeneralBasicParams.PORTUGUESE);
                            }else if (yuyan.equals("法语")){
                                qingqiu.setLanguageType(GeneralBasicParams.FRENCH);
                            }else if (yuyan.equals("德语")){
                                qingqiu.setLanguageType(GeneralBasicParams.GERMAN);
                            }else if (yuyan.equals("意大利语")){
                                qingqiu.setLanguageType(GeneralBasicParams.ITALIAN);
                            }else if (yuyan.equals("西班牙语")){
                                qingqiu.setLanguageType(GeneralBasicParams.SPANISH);
                            }else if (yuyan.equals("俄语")){
                                qingqiu.setLanguageType(GeneralBasicParams.RUSSIAN);
                            }else if (yuyan.equals("日语")){
                                qingqiu.setLanguageType(GeneralBasicParams.JAPANESE);
                            }
                            break;
                    }

                    switch (MainActivity.moshi){
                        case 0:         //文字识别
                            OCR.getInstance().recognizeGeneralBasic(qingqiu, new OnResultListener<GeneralResult>() {
                                @Override
                                public void onResult(GeneralResult generalResult) {
                                    Log.e("请求：","成功");
                                    String s = "";
                                    for (WordSimple wordSimple : generalResult.getWordList()) {
                                        // wordSimple不包含位置信息
                                        WordSimple word = wordSimple;
                                        if (!s.equals("")){
                                            s = s+"\n";
                                        }
                                        s = s + word.getWords();
                                    }
                                    gongju_onclick(findViewById(R.id.wenzi_Id));
                                    EditText editText = findViewById(R.id.wenben_Id);
                                    editText.setText(s);
                                    tv.setText("开始识别");
                                    tv.setEnabled(true);

                                }

                                @Override
                                public void onError(OCRError ocrError) {
                                    Log.e("size",ocrError.getMessage());
                                    shibie_cuowu(ocrError.getErrorCode());
                                    tv.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv.setText("开始识别");
                                            tv.setEnabled(true);
                                        }
                                    });
                                }
                            });
                            break;
                        case 1:             //特殊字体
                            qingqiu.setDetectLanguage(true);
                            OCR.getInstance().recognizeWebimage(qingqiu, new OnResultListener<GeneralResult>() {
                                @Override
                                public void onResult(GeneralResult generalResult) {
                                    Log.e("请求：","成功");
                                    String s = "";
                                    for (WordSimple wordSimple : generalResult.getWordList()) {
                                        // wordSimple不包含位置信息
                                        WordSimple word = wordSimple;
                                        if (!s.equals("")){
                                            s = s+"\n";
                                        }
                                        s = s + word.getWords();
                                    }
                                    gongju_onclick(findViewById(R.id.wenzi_Id));
                                    EditText editText = findViewById(R.id.wenben_Id);
                                    editText.setText(s);
                                    tv.setText("开始识别");
                                    tv.setEnabled(true);
                                }

                                @Override
                                public void onError(OCRError ocrError) {
                                    Log.e("size",ocrError.getMessage());
                                    shibie_cuowu(ocrError.getErrorCode());
                                    tv.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv.setText("开始识别");
                                            tv.setEnabled(true);
                                        }
                                    });
                                }
                            });
                            break;
                        case 2:
                            OCR.getInstance().recognizeAccurateBasic(qingqiu, new OnResultListener<GeneralResult>() {
                                @Override
                                public void onResult(GeneralResult generalResult) {
                                    Log.e("请求：","成功");
                                    String s = "";
                                    for (WordSimple wordSimple : generalResult.getWordList()) {
                                        // wordSimple不包含位置信息
                                        WordSimple word = wordSimple;
                                        if (!s.equals("")){
                                            s = s+"\n";
                                        }
                                        s = s + word.getWords();
                                    }
                                    gongju_onclick(findViewById(R.id.wenzi_Id));
                                    EditText editText = findViewById(R.id.wenben_Id);
                                    editText.setText(s);
                                    tv.setText("开始识别");
                                    tv.setEnabled(true);
                                }

                                @Override
                                public void onError(OCRError ocrError) {
                                    Log.e("size",ocrError.getMessage());
                                    shibie_cuowu(ocrError.getErrorCode());
                                    tv.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv.setText("开始识别");
                                            tv.setEnabled(true);
                                        }
                                    });
                                }
                            });
                            break;
                        case 3:
                            qingqiu.setVertexesLocation(true);
                            OCR.getInstance().recognizeAccurate(qingqiu, new OnResultListener<GeneralResult>() {
                                @Override
                                public void onResult(GeneralResult generalResult) {
                                    String s = "";
                                    List<List<Integer>> wenzi_wz = new ArrayList<>();
                                    float wd = huaban.getTupian_width();
                                    float he = huaban.getTupian_height();
                                    float max = wd > he ? wd : he;
                                    float beilv = 1;
                                    if (max >2048){
                                        beilv = 2048f / max;
                                    }
                                    for (WordSimple wordSimple : generalResult.getWordList()) {
                                        Word word = (Word)wordSimple;
                                        Location location= word.getLocation();
                                        List<Integer> wz_wz = new ArrayList<>();
                                        wz_wz.add((int)(location.getLeft() / beilv));
                                        wz_wz.add((int)(location.getTop() / beilv));
                                        wz_wz.add((int)(location.getWidth() / beilv));
                                        wz_wz.add((int)(location.getHeight() / beilv));
                                        wz_wz.add(0);
                                        wenzi_wz.add(wz_wz);
                                        if (!s.equals("")){
                                            s = s+"\n";
                                        }
                                        s = s + word.getWords();
                                        shibie_wenben.add(word.getWords());
                                    }
                                    huaban.setWenzi_wz(wenzi_wz);
                                    gongju_onclick(findViewById(R.id.wenzi_Id));
                                    EditText editText = findViewById(R.id.wenben_Id);
                                    editText.setText(s);
                                    tv.setText("开始识别");
                                    tv.setEnabled(true);
                                }

                                @Override
                                public void onError(OCRError ocrError) {
                                    Log.e("size",ocrError.getMessage());
                                    shibie_cuowu(ocrError.getErrorCode());
                                    tv.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv.setText("开始识别");
                                            tv.setEnabled(true);
                                        }
                                    });
                                }
                            });
                            break;
                        case 4:
                            qingqiu.setVertexesLocation(true);
                            qingqiu.setRecognizeGranularity(GeneralParams.GRANULARITY_SMALL);
                            OCR.getInstance().recognizeGeneral(qingqiu, new OnResultListener<GeneralResult>() {
                                @Override
                                public void onResult(GeneralResult generalResult) {
                                    String s = "";
                                    List<List<Integer>> wenzi_wz = new ArrayList<>();
                                    float wd = huaban.getTupian_width();
                                    float he = huaban.getTupian_height();
                                    float max = wd > he ? wd : he;
                                    float beilv = 1;
                                    if (max >2048){
                                        beilv = 2048f / max;
                                    }
                                    for (WordSimple wordSimple : generalResult.getWordList()) {
                                        Word word = (Word)wordSimple;
                                        Location location= word.getLocation();
                                        List<Integer> wz_wz = new ArrayList<>();
                                        wz_wz.add((int)(location.getLeft() / beilv));
                                        wz_wz.add((int)(location.getTop() / beilv));
                                        wz_wz.add((int)(location.getWidth() / beilv));
                                        wz_wz.add((int)(location.getHeight() / beilv));
                                        wz_wz.add(0);
                                        wenzi_wz.add(wz_wz);
                                        if (!s.equals("")){
                                            s = s+"\n";
                                        }
                                        s = s + word.getWords();
                                        shibie_wenben.add(word.getWords());
                                    }
                                    huaban.setWenzi_wz(wenzi_wz);
                                    gongju_onclick(findViewById(R.id.wenzi_Id));
                                    EditText editText = findViewById(R.id.wenben_Id);
                                    editText.setText(s);
                                    tv.setText("开始识别");
                                    tv.setEnabled(true);
                                }

                                @Override
                                public void onError(OCRError ocrError) {
                                    Log.e("size",ocrError.getMessage());
                                    shibie_cuowu(ocrError.getErrorCode());
                                    tv.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            tv.setText("开始识别");
                                            tv.setEnabled(true);
                                        }
                                    });
                                }
                            });
                            break;
                    }
                    file_clear(file,tupian);
                }
                break;
        }

    }

    public void wenben_xuanxiang(View view){
        EditText wenben = findViewById(R.id.wenben_Id);
        if (view.getId() == R.id.wenben_fuzhi_Id){
            //获取剪贴板管理器：
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("ShiBie", wenben.getText().toString());
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
        }else {
            Intent intent=new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT,wenben.getText().toString());
            intent.setType("text/plain");
            startActivity(Intent.createChooser(intent,"分享到："));
        }
    }

    private void shibie_cuowu(int dm){
        Snackbar.make(huaban,new cuowu_dm().getString(dm),Snackbar.LENGTH_LONG).show();
    }

    public void kong(View view){

    }

    private int getgongju_wz(int gongju){
        for (int wz = 0;wz<gongju_list.size();wz++){
            if (gongju_list.get(wz) == gongju){
                return wz;
            }
        }
        return -1;
    }

    private void gongju_shiqujiaodian(){
        ImageView imageView = findViewById(gongju_list.get(gongju));
        imageView.setBackgroundColor(0);
    }

    private void gongju_huodejiaodian(){
        ImageView imageView = findViewById(gongju_list.get(gongju));
        imageView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    public void gongju_onclick(View view){
        int wz = getgongju_wz(view.getId());
        if (wz == gongju){
            return;
        }

        LinearLayout gongju_shezhilan = findViewById(R.id.gongju_shezhilan_Id);
        gongju_shezhilan.removeAllViews();

        //工具栏工具失去焦点
        switch (gongju){
            case 2:
                findViewById(R.id.wenzi_fu_gongju_Id).setVisibility(View.GONE);
                break;
        }

        //工具栏工具获得焦点
        switch (wz){
            case 0:     //移动工具
                layout = getLayoutInflater().inflate(R.layout.yidong_shezhi, gongju_shezhilan,false);
                gongju_shezhilan.addView(layout);
                break;
            case 1:     //选择工具
                layout = getLayoutInflater().inflate(R.layout.xuanzekuang_shezhi, gongju_shezhilan,false);
                gongju_shezhilan.addView(layout);
                break;
            case 2:
                findViewById(R.id.wenzi_fu_gongju_Id).setVisibility(View.VISIBLE);
                break;
            case 3:
                layout = getLayoutInflater().inflate(R.layout.saomiao_shezhi, gongju_shezhilan,false);
                gongju_shezhilan.addView(layout);

                List<String> wenzifangxiang = new ArrayList<>();
                switch (MainActivity.moshi){
                    case 0:
                    case 4:
                        wenzifangxiang.add("中英混合");
                        wenzifangxiang.add("英语");
                        wenzifangxiang.add("葡萄牙语");
                        wenzifangxiang.add("法语");
                        wenzifangxiang.add("德语");
                        wenzifangxiang.add("意大利语");
                        wenzifangxiang.add("西班牙语");
                        wenzifangxiang.add("俄语");
                        wenzifangxiang.add("日语");
                        wenzifangxiang.add("自动识别");
                        break;
                    case 1:
                    case 2:
                    case 3:
                        wenzifangxiang.add("自动识别");
                        break;
                }



                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, wenzifangxiang);
                Spinner spinner = findViewById(R.id.saomiao_yuyan_Id);
                spinner.setAdapter(adapter);
                switch (MainActivity.moshi){
                    case 0:
                        spinner.setSelection(MainActivity.yuyan);
                        break;
                }


                break;
        }

        if (gongju != -1){
            gongju_shiqujiaodian();
        }
        gongju = wz;
        gongju_huodejiaodian();
        Log.e("当前工具:","当前选择的是"+gongju);
    }

    public void gongjulan_kg(View view){
        if (!donghua) {
            donghua = true;
            final ImageView gongjulan_kaiguan = (ImageView)view;
            if (gongjulan_kg) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(findViewById(R.id.gongjulan_layout_Id), "TranslationX", 0, -findViewById(R.id.gongjulan_Id).getWidth());
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(findViewById(R.id.gongju_shezhilan_Id), "TranslationY", 0, findViewById(R.id.gongju_shezhilan_Id).getHeight());
                AnimatorSet zuhe = new AnimatorSet();
                zuhe.playTogether(animator,animator1);
                zuhe.setDuration(200);
                zuhe.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        gongjulan_kg = false;
                        gongjulan_kaiguan.setImageResource(R.drawable.ic_you);
                        donghua = false;
                        findViewById(R.id.gongjulan_Id).setTranslationX(0);
                    }
                });
                zuhe.start();

            } else {
                findViewById(R.id.gongjulan_Id).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                ObjectAnimator animator = ObjectAnimator.ofFloat(findViewById(R.id.gongjulan_layout_Id), "TranslationX", -findViewById(R.id.gongjulan_Id).getWidth(), 0);
                ObjectAnimator animator1 = ObjectAnimator.ofFloat(findViewById(R.id.gongju_shezhilan_Id), "TranslationY", findViewById(R.id.gongju_shezhilan_Id).getHeight(), 0);
                AnimatorSet zuhe = new AnimatorSet();
                zuhe.playTogether(animator,animator1);
                zuhe.setDuration(200);
                zuhe.addListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        gongjulan_kg = true;
                        gongjulan_kaiguan.setImageResource(R.drawable.ic_zuo);
                        donghua = false;
                    }
                });
                zuhe.start();

            }
        }
    }

    @Override
    public void onBackPressed() {
        gongju_list.clear();
        huaban.clear();
        super.onBackPressed();
    }
}
