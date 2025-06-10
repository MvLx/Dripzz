package com.example.dripz.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dripz.model.Place;
import com.example.dripz.model.PlaceCache;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "dripz.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_PLACE = "places";
    public static final String TABLE_FAVORITE = "favorite";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PLACE + " (" +
                "namaTempat TEXT, namaKota TEXT, alamatJalan TEXT, deskripsi TEXT, jamBuka TEXT, gambarPath TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_FAVORITE + " (" +
                "fsq_id TEXT PRIMARY KEY, " +
                "namaTempat TEXT, namaKota TEXT, alamatJalan TEXT, deskripsi TEXT, jamBuka TEXT, gambarPath TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_FAVORITE + " (" +
                    "fsq_id TEXT PRIMARY KEY, " +
                    "namaTempat TEXT, namaKota TEXT, alamatJalan TEXT, deskripsi TEXT, jamBuka TEXT, gambarPath TEXT)");
        }
    }

    public void clearPlaces() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_PLACE);
        db.close();
    }

    public void addFavorite(Place place, String namaKota) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("fsq_id", place.fsq_id);
        values.put("namaTempat", place.name);
        values.put("namaKota", namaKota);
        values.put("alamatJalan", (place.location != null) ? place.location.address : "");
        values.put("deskripsi", (place.categories != null && !place.categories.isEmpty()) ? place.categories.get(0).name : "");
        values.put("jamBuka", (place.hours != null) ? place.hours.display : "");
        values.put("gambarPath", place.offlineImagePath != null ? place.offlineImagePath : "");
        db.insertWithOnConflict(TABLE_FAVORITE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public void removeFavorite(String fsq_id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_FAVORITE, "fsq_id=?", new String[]{fsq_id});
        db.close();
    }

    public boolean isFavorite(String fsq_id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_FAVORITE + " WHERE fsq_id=?", new String[]{fsq_id});
        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();
        return exists;
    }

    public List<Place> getAllFavorites() {
        List<Place> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_FAVORITE, null);
        while (cursor.moveToNext()) {
            Place place = new Place();
            place.fsq_id = cursor.getString(cursor.getColumnIndexOrThrow("fsq_id"));
            place.name = cursor.getString(cursor.getColumnIndexOrThrow("namaTempat"));

            place.location = new Place.Location();
            place.location.address = cursor.getString(cursor.getColumnIndexOrThrow("alamatJalan"));

            place.categories = new ArrayList<>();
            Place.Category cat = new Place.Category();
            cat.name = cursor.getString(cursor.getColumnIndexOrThrow("deskripsi"));
            place.categories.add(cat);

            place.hours = new Place.Hours();
            place.hours.display = cursor.getString(cursor.getColumnIndexOrThrow("jamBuka"));

            place.offlineImagePath = cursor.getString(cursor.getColumnIndexOrThrow("gambarPath"));
            list.add(place);
        }
        cursor.close();
        db.close();
        return list;
    }

    public List<PlaceCache> getAllPlaces() {
        List<PlaceCache> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PLACE, null);
        while (cursor.moveToNext()) {
            PlaceCache placeCache = new PlaceCache();
            placeCache.namaTempat = cursor.getString(cursor.getColumnIndexOrThrow("namaTempat"));
            placeCache.namaKota = cursor.getString(cursor.getColumnIndexOrThrow("namaKota"));
            placeCache.alamatJalan = cursor.getString(cursor.getColumnIndexOrThrow("alamatJalan"));
            placeCache.deskripsi = cursor.getString(cursor.getColumnIndexOrThrow("deskripsi"));
            placeCache.jamBuka = cursor.getString(cursor.getColumnIndexOrThrow("jamBuka"));
            placeCache.gambarPath = cursor.getString(cursor.getColumnIndexOrThrow("gambarPath"));
            list.add(placeCache);
        }
        cursor.close();
        db.close();
        return list;
    }

    public void insertPlace(String namaTempat, String namaKota, String alamatJalan, String deskripsi, String jamBuka, String gambarPath) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("namaTempat", namaTempat);
        values.put("namaKota", namaKota);
        values.put("alamatJalan", alamatJalan);
        values.put("deskripsi", deskripsi);
        values.put("jamBuka", jamBuka);
        values.put("gambarPath", gambarPath);
        db.insert(TABLE_PLACE, null, values);
        db.close();
    }
}