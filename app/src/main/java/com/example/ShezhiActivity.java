package com.example;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.example.gongju.sql_zsgc;

import java.util.ArrayList;
import java.util.List;

public class ShezhiActivity extends AppCompatActivity {
    private View layout;
    private Dialog dhk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shezhi);

        List<String> wenzifangxiang = new ArrayList<>();
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


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, wenzifangxiang);
        Spinner spinner = findViewById(R.id.shezhi_morenyuyan_Id);
        spinner.setAdapter(adapter);
        spinner.setSelection(MainActivity.yuyan);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences pz = getSharedPreferences("pz", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pz.edit();
                editor.putInt("yuyan",position);
                editor.commit();
                MainActivity.yuyan = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        TextView api_key = findViewById(R.id.API_key_Id);
        api_key.setText(MainActivity.API_key);

    }

    public void shezhi_onclick(View view){
        switch (view.getId()){
            case R.id.shezhi_genghuan_key_Id:
                AlertDialog.Builder duihuakuang = new AlertDialog.Builder(this);
                layout = getLayoutInflater().inflate(R.layout.loge, null);
                TextView quxiao = layout.findViewById(R.id.tuichu_Id);
                quxiao.setText("取消");
                duihuakuang.setView(layout);
                duihuakuang.setCancelable(false);
                dhk = duihuakuang.show();
                break;
        }
    }

    public void Loge_onclick(View view){
        if (view.getId() == R.id.tuichu_Id){
            dhk.dismiss();
            dhk = null;
            layout = null;
        }else {
            EditText ak = layout.findViewById(R.id.ak_Id);
            EditText sk = layout.findViewById(R.id.sk_Id);
            if (ak.getText().toString().equals("")){
                Snackbar.make(layout,"API Key不能为空",Snackbar.LENGTH_LONG).show();
            }else if (sk.getText().toString().equals("")){
                Snackbar.make(layout,"Secret Key不能为空",Snackbar.LENGTH_LONG).show();
            }else if (ak.getText().toString().equals(MainActivity.API_key)){
                Snackbar.make(layout,"API Key与目前的相同！",Snackbar.LENGTH_LONG).show();
            } else {
                initAccessTokenWithAkSk(ak.getText().toString(),sk.getText().toString());
            }

        }
    }

    private void initAccessTokenWithAkSk(final String ak , final String sk) {
        Log.e("ak：", ak + "    " + sk);
        OCR.getInstance().initAccessTokenWithAkSk(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                dhk.dismiss();
                layout = null;
                dhk = null;
                sql_zsgc sh = new sql_zsgc(ShezhiActivity.this,"m3h");
                SQLiteDatabase sdb = sh.getWritableDatabase();
                String sql = "update token set ak=? , sk=? where id=?";
                try{
                    sdb.beginTransaction();
                    sdb.execSQL(sql,new Object[]{ak,sk,"1"});
                    sdb.setTransactionSuccessful();
                    sdb.endTransaction();
                    Log.e("修改","成功");
                    MainActivity.API_key = ak;
                    MainActivity.token = 1;
                    TextView api_key = findViewById(R.id.API_key_Id);
                    api_key.setText(MainActivity.API_key);
                }catch (SQLException e){
                    e.printStackTrace();
                    Log.e("修改","失败");
                }finally {
                    sdb.close();
                }
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
                if(dhk != null){
                    Snackbar.make(layout,new cuowu_dm().getString(error.getErrorCode()),Snackbar.LENGTH_LONG).show();
                }

            }
        }, getApplicationContext(), ak, sk);
    }

}
