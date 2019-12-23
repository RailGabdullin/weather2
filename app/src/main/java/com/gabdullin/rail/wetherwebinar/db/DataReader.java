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
    private Cursor cursore;

    public DataReader(SQLiteDatabase database) {
        this.database = database;
    }

    public void open(){
        query();
        cursore.moveToFirst();
    }

    private void query() {
        cursore = database.query(DataHelper.TABLE_NAME, all,
                null,
                null,
                null,
                null,
                null);
    }

    public void refresh(){
        int position = cursore.getPosition();
        query();
        cursore.moveToPosition(position);
    }

    @Override
    public void close() throws IOException {
        cursore.close();
    }

    private Note cursoreToNote(){
        Note note = new Note();
        note.setDate(cursore.getString(1));
        note.setCity(cursore.getString(2));
        note.setTemperature(cursore.getInt(3));
        note.setDescription(cursore.getString(4));
        return note;
    }

    public Note getPosition(int position){
        cursore.moveToPosition(position);
        return cursoreToNote();
    }

    public int getCount() {
        return cursore.getCount();
    }
}
