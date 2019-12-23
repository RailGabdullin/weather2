package com.gabdullin.rail.wetherwebinar.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "notes.db";
    private static final int DB_VERSION = 2;
    static final String TABLE_NAME = "weatherHistory";
    static final String TABLE_ID = "_id";
    static final String TABLE_DATE = "date";
    static final String TABLE_CITY = "city";
    static final String TABLE_TEMP = "temperature";
    static final String TABLE_DESC = "description";

    public DataHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                TABLE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                TABLE_DATE + " TEXT," +
                TABLE_CITY + " TEXT," +
                TABLE_TEMP + " INTEGER," +
                TABLE_DESC + " TEXT );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
