package com.gabdullin.rail.wetherwebinar.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.gabdullin.rail.wetherwebinar.Note;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

public class DataSource implements Closeable {

    private static DataSource dataSource;
    private final DataHelper dbHelper;
    private SQLiteDatabase database;
    private DataReader reader;

    private DataSource(Context context) {
        dbHelper = new DataHelper(context);
    }

    public static DataSource getDataSource(Context context) {
        if(dataSource == null) dataSource = new DataSource(context);
        return dataSource;
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        reader = new DataReader(database);
        reader.open();
    }

    @Override
    public void close() throws IOException {
        database.close();
        reader.close();
    }

    public Note add(String data, String city, int temp, String desc){
        Note note = new Note();
        ContentValues values = new ContentValues();
        values.put(DataHelper.TABLE_DATE, data);
        values.put(DataHelper.TABLE_CITY, city);
        values.put(DataHelper.TABLE_TEMP, temp);
        values.put(DataHelper.TABLE_DESC, desc);
        database.insert(DataHelper.TABLE_NAME, null, values);
        note.setDate(data);
        note.setCity(city);
        note.setTemperature(temp);
        note.setDescription(desc);
        return note;
    }

    public DataReader getReader() {
        return reader;
    }
}
