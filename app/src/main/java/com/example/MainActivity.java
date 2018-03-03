package com.example;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.example.gongju.sql_zsgc;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    //登陆框的布局
    private View Loge_dhk = null;
    //登陆框实例
    private AlertDialog dhk = null;
    //记录当前的apikey
    public static String API_key = null;
    //判断当前token是否获取成功
    public static int token = 0;
    //默认识别语言
    public static int yuyan;
    //当前选择的识别模式
    public static int moshi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            int permission = ActivityCompat.checkSelfPermission(this,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE",
                        "android.permission.WRITE_EXTERNAL_STORAGE"},1);
            }
        }

        SharedPreferences pz = getSharedPreferences("pz", Context.MODE_PRIVATE);
        if (!pz.getBoolean("cunzai",false)){
            Log.e("配置文件","走了");
            SharedPreferences.Editor editor = pz.edit();
            editor.putBoolean("cunzai",true);
            editor.putInt("yuyan",0);
            editor.commit();
        }
        yuyan = pz.getInt("yuyan",0);

        String ak = "";
        String sk = "";

        sql_zsgc sql = new sql_zsgc(this,"m3h");
        SQLiteDatabase sdb = sql.getReadableDatabase();
        String s = "select * from token";
        Cursor cu = sdb.rawQuery(s,null);
        while (cu.moveToNext()){
            ak = cu.getString(cu.getColumnIndex("ak"));
            sk = cu.getString(cu.getColumnIndex("sk"));
        }
        cu.close();
        sdb.close();

        if (ak.equals("")){
            AlertDialog.Builder duihuakuang = new AlertDialog.Builder(MainActivity.this);
            Loge_dhk = getLayoutInflater().inflate(R.layout.loge, null);
            duihuakuang.setView(Loge_dhk);
            duihuakuang.setCancelable(false);
            dhk = duihuakuang.show();
        }else {
            //初始化
            initAccessTokenWithAkSk(ak,sk);
        }

        String Sd_dizhi = Environment.getExternalStorageDirectory().getPath() + "/";
        File file = new File(Sd_dizhi+"M3h/hc");
        if (!file.exists()){
            file.mkdirs();
        }


    }

    public void Loge_onclick(View view){
        if (view.getId() == R.id.tuichu_Id){
            super.onBackPressed();
        }else {
            EditText ak = Loge_dhk.findViewById(R.id.ak_Id);
            EditText sk = Loge_dhk.findViewById(R.id.sk_Id);
            if (ak.getText().toString().equals("")){
                Snackbar.make(Loge_dhk,"API Key不能为空",Snackbar.LENGTH_LONG).show();
            }else if (sk.getText().toString().equals("")){
                Snackbar.make(Loge_dhk,"Secret Key不能为空",Snackbar.LENGTH_LONG).show();
            }else {
                initAccessTokenWithAkSk(ak.getText().toString(),sk.getText().toString());
            }

        }
    }

    private void initAccessTokenWithAkSk(final String ak , final String sk) {
        Log.e("ak：",ak+"    "+sk);
        OCR.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                if (dhk != null){
                    sql_zsgc sql = new sql_zsgc(MainActivity.this,"m3h");
                    SQLiteDatabase sdb = sql.getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put("ak",ak);
                    cv.put("sk",sk);
                    cv.put("id","1");
                    Long rowId = sdb.insert("token",null,cv);
                    if (rowId < 0){
                        Snackbar.make(Loge_dhk,"添加失败！",Snackbar.LENGTH_LONG).show();
                    }else {
                        dhk.dismiss();
                        Loge_dhk = null;
                        dhk = null;
                    }
                }
                API_key = ak;
                token = 1;
            }
            @Override
            public void onError(OCRError error) {
                token = 2;
                if (dhk != null){
                    Snackbar.make(Loge_dhk,new cuowu_dm().getString(error.getErrorCode()),Snackbar.LENGTH_LONG).show();
                }
            }
        }, getApplicationContext(), ak, sk);
    }

    public void leixing(View view){
        switch (view.getId()){
            case R.id.Wenzi_shibie_Id:
                moshi = 0;
                break;
            case R.id.teshuziti_shibie_Id:
                moshi = 1;
                break;
            case R.id.gaojingdu_shibie_Id:
                moshi = 2;
                break;
            case R.id.gaojingdu_weizhi_shibie_Id:
                moshi = 3;
                break;
            case R.id.wenzishibie_weizhi_shibie_Id:
                moshi = 4;
                break;
            case R.id.yinhangka_shibie_Id:
                moshi = 5;
                break;
            case R.id.shenfenzheng_shibie_Id:
                moshi = 6;
                break;
        }
        Tuku();
    }

    private void Tuku(){
        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data!=null){
            Uri uri = data.getData();
            String scheme = uri.getScheme();
            Log.e("路径",ContentResolver.SCHEME_CONTENT+"   "+scheme+"   "+data.getDataString()+"   "+data.getClipData());
            String dizhi = "";
            if (scheme.equals(ContentResolver.SCHEME_CONTENT)){
                Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    // 取出文件路径
                    dizhi = cursor.getString(columnIndex);

                    // Android 4.1 更改了SD的目录，sdcard映射到/storage/sdcard0
                    if (!dizhi.startsWith("/storage") && !dizhi.startsWith("/mnt")) {
                        // 检查是否有"/mnt"前缀
                        dizhi = "/mnt" + dizhi;
                    }
                    //关闭游标
                    cursor.close();
                    Log.e("文件地址：",dizhi);
                }

            }else if (scheme.equals(ContentResolver.SCHEME_FILE)){
                dizhi = data.getDataString();
                dizhi = dizhi.substring(7,dizhi.length());
            }
            Intent intent = new Intent();
            intent.setClass(this,BianjiActivity.class);
            intent.putExtra("path",dizhi);
            startActivity(intent);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE,0,0,"设置");
        menu.add(Menu.NONE,1,1,"关于");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent();
        switch (item.getItemId()){
            case 0:
                intent.setClass(this,ShezhiActivity.class);
                startActivity(intent);
                break;
            case 1:
                intent.setClass(this,GuanyuActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_DENIED){
            Log.e("拒绝了","走了");
            super.onBackPressed();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
