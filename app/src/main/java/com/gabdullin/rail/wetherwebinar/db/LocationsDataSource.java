package com.gabdullin.rail.wetherwebinar.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.Closeable;

public class LocationsDataSource implements Closeable {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private LocationDataReader locationDataReader;

    public LocationsDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // Открывает базу данных
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
        // Создать читателя и открыть его
        locationDataReader = new LocationDataReader(database);
        locationDataReader.open();
    }

    // Закрыть базу данных
    public void close() {
        locationDataReader.close();
        dbHelper.close();
    }

    // Добавить новую запись
    public void addNote(String location) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_LOCATION, location);
        // Добавление записи
        database.insert(DatabaseHelper.TABLE_LOCATIONS, null,
                values);
    }

    // Изменить запись
    public void editNote(String currentLocation, String newLocation) {
        ContentValues editedNote = new ContentValues();
        editedNote.put(dbHelper.COLUMN_LOCATION, newLocation);
        // Изменение записи
        database.update(dbHelper.TABLE_LOCATIONS,
                editedNote,
                dbHelper.COLUMN_LOCATION + " = '" + currentLocation + "'",
                null);
    }

    // Удалить запись
    public void deleteNote(String location) {
        database.delete(DatabaseHelper.TABLE_LOCATIONS, DatabaseHelper.COLUMN_LOCATION
                + " = '" + location + "'", null);
    }

    public void deleteNote(int id) {
        database.delete(DatabaseHelper.TABLE_LOCATIONS, DatabaseHelper.COLUMN_ID
                + " = " + id, null);
    }

    // Очистить таблицу
    public void deleteAll() {
        database.delete(DatabaseHelper.TABLE_LOCATIONS, null, null);
    }

    // Вернуть читателя (он потребуется в других местах)
    public LocationDataReader getLocationDataReader(){
        return locationDataReader;
    }

}
