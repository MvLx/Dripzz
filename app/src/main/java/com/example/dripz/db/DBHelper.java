package com.example.dripz.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "places_cache.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_PLACES = "places";
    public static final String COL_ID = "id";
    public static final String COL_NAMA_TEMPAT = "nama_tempat";
    public static final String COL_NAMA_KOTA = "nama_kota";
    public static final String COL_ALAMAT_JALAN = "alamat_jalan";
    public static final String COL_DESKRIPSI = "deskripsi";
    public static final String COL_JAM_BUKA = "jam_buka";
    public static final String COL_GAMBAR_PATH = "gambar_path";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_PLACES + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_NAMA_TEMPAT + " TEXT, " +
            COL_NAMA_KOTA + " TEXT, " +
            COL_ALAMAT_JALAN + " TEXT, " +
            COL_DESKRIPSI + " TEXT, " +
            COL_JAM_BUKA + " TEXT, " +
            COL_GAMBAR_PATH + " TEXT);";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLACES);
        onCreate(db);
    }

    public void clearPlaces() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_PLACES, null, null);
        db.close();
    }

    public void insertPlace(String namaTempat, String namaKota, String alamatJalan, String deskripsi, String jamBuka, String gambarPath) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAMA_TEMPAT, namaTempat);
        values.put(COL_NAMA_KOTA, namaKota);
        values.put(COL_ALAMAT_JALAN, alamatJalan);
        values.put(COL_DESKRIPSI, deskripsi);
        values.put(COL_JAM_BUKA, jamBuka);
        values.put(COL_GAMBAR_PATH, gambarPath);
        db.insert(TABLE_PLACES, null, values);
        db.close();
    }

    public List<PlaceCache> getAllPlaces() {
        List<PlaceCache> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_PLACES, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                PlaceCache place = new PlaceCache(
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAMA_TEMPAT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_NAMA_KOTA)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_ALAMAT_JALAN)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_DESKRIPSI)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_JAM_BUKA)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_GAMBAR_PATH))
                );
                list.add(place);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // Data class untuk hasil cache
    public static class PlaceCache {
        public String namaTempat, namaKota, alamatJalan, deskripsi, jamBuka, gambarPath;

        public PlaceCache(String namaTempat, String namaKota, String alamatJalan, String deskripsi, String jamBuka, String gambarPath) {
            this.namaTempat = namaTempat;
            this.namaKota = namaKota;
            this.alamatJalan = alamatJalan;
            this.deskripsi = deskripsi;
            this.jamBuka = jamBuka;
            this.gambarPath = gambarPath;
        }
    }
}