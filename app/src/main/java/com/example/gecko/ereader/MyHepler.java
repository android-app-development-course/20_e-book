package com.example.gecko.ereader;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by GecKo on 2017/12/6.
 */

 class MyHepler extends SQLiteOpenHelper {

    public MyHepler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE informatione(_id INTEGER PRIMARY KEY AUTOINCREMENT,name VARCHAR(20),time VARCHAR(20),name1 VARCHAR(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
