package com.example.gongju;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by 李旭 on 2018/2/27  22:01
 */

public class sql_zsgc extends SQLiteOpenHelper {

    public sql_zsgc(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public sql_zsgc(Context context, String name, int version) {
        super(context, name, null, version);
    }

    public sql_zsgc(Context context, String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建数据表
        String sql = "create table token(ak varchar(30) not null,sk varchar(40) not null,id varchar(1) not null)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
