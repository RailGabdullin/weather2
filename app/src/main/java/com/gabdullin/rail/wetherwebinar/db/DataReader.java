package com.gabdullin.rail.wetherwebinar.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.gabdullin.rail.wetherwebinar.Note;

import java.io.Closeable;
import java.io.IOException;

public class DataReader implements Closeable {
    private final SQLiteDatabase database;
    private String[] all = {
            DataHelper.TABLE_ID,
            DataHelper.TABLE_DATE,
            DataHelper.TABLE_CITY,
            DataHelper.TABLE_TEMP,
            DataHelper.TABLE_DESC,
    };
    private Cursor cursor;

    public DataReader(SQLiteDatabase database) {
        this.database = database;
    }

    public void open(){
        query();
        cursor.moveToFirst();
    }

    private void query() {
        cursor = database.query(DataHelper.TABLE_NAME, all,
                null,
                null,
                null,
                null,
                null);
    }

    public void refresh(){
        int position = cursor.getPosition();
        query();
        cursor.moveToPosition(position);
    }

    @Override
    public void close() throws IOException {
        cursor.close();
    }

    private Note cursorToNote(){
        Note note = new Note();
        note.setDate(cursor.getString(1));
        note.setCity(cursor.getString(2));
        note.setTemperature(cursor.getInt(3));
        note.setDescription(cursor.getString(4));
        return note;
    }

    public Note getPosition(int position){
        cursor.moveToPosition(position);
        return cursorToNote();
    }

    public int getCount() {
        return cursor.getCount();
    }
}
