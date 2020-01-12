package com.gabdullin.rail.wetherwebinar.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "locationList.db"; // Название БД
    public static final int DATABASE_VERSION = 1; // Версия базы данных
    static final String TABLE_LOCATIONS = "locationTable"; // Название таблицы в БД
    // Названия столбцов
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LOCATION = "location";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Вызывается при попытке доступа к базе данных, когда она еще не создана
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_LOCATIONS + " (" + COLUMN_ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_LOCATION + " TEXT);");
    }

    // Вызывается, когда необходимо обновление базы данных
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
    }

}
