package com.gabdullin.rail.wetherwebinar.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Closeable;

public class LocationDataReader implements Closeable {

    private Cursor cursor;              // Курсор (фактически, подготовленный запрос),
    // но сами данные подсчитываются только по необходимости
    private SQLiteDatabase database;

    private String[] notesAllColumn = {
            DatabaseHelper.COLUMN_ID,
            DatabaseHelper.COLUMN_LOCATION,
    };

    public LocationDataReader(SQLiteDatabase database){
        this.database = database;
    }

    // Подготовить к чтению таблицу
    public void open(){
        query();
        cursor.moveToFirst();
    }

    public void close(){
        cursor.close();
    }

    // Перечитать таблицу (если точно – обновить курсор)
    public void Refresh(){
        int position = cursor.getPosition();
        query();
        cursor.moveToPosition(position);
    }

    // Создание запроса на курсор
    private void query(){
        cursor = database.query(DatabaseHelper.TABLE_LOCATIONS,
                notesAllColumn, null, null, null, null, null);
    }

    // Прочитать данные по определенной позиции
    public LocationNote getPosition(int position){
        cursor.moveToPosition(position);
        return cursorToNote();
    }

    // Получить количество строк в таблице
    public int getCount(){
        return cursor.getCount();
    }

    // Преобразователь данных курсора в объект
    private LocationNote cursorToNote() {
        LocationNote note = new LocationNote();
        note.setId(cursor.getLong(0));
        note.setCityName(cursor.getString(1));
        return note;
    }

}
