package com.cemetery.map.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cemetery.map.model.MarkerData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import static com.cemetery.map.utils.Constants.DB_NAME;
import static com.cemetery.map.utils.Constants.DB_VERSION;

public class CacheDbHelper extends SQLiteOpenHelper {

    private static final String TABLE = "markers_cache";
    private static final String COL_ID   = "id";
    private static final String COL_JSON = "json";
    private static final String COL_TS   = "timestamp";

    public CacheDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " (" +
                COL_ID   + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COL_JSON + " TEXT NOT NULL," +
                COL_TS   + " INTEGER NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    /** Save marker list as JSON blob */
    public void saveMarkers(List<MarkerData> markers) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE, null, null); // clear old cache
        ContentValues cv = new ContentValues();
        cv.put(COL_JSON, new Gson().toJson(markers));
        cv.put(COL_TS, System.currentTimeMillis());
        db.insert(TABLE, null, cv);
        db.close();
    }

    /** Load cached markers — returns empty list if none */
    public List<MarkerData> loadMarkers() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE, null, null, null, null, null, COL_TS + " DESC", "1");
        List<MarkerData> result = new ArrayList<>();
        if (c.moveToFirst()) {
            String json = c.getString(c.getColumnIndexOrThrow(COL_JSON));
            result = new Gson().fromJson(json, new TypeToken<List<MarkerData>>(){}.getType());
        }
        c.close();
        db.close();
        return result != null ? result : new ArrayList<>();
    }

    /** Returns true if cache is older than maxAgeMs */
    public boolean isCacheStale(long maxAgeMs) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE, new String[]{COL_TS}, null, null, null, null, COL_TS + " DESC", "1");
        if (!c.moveToFirst()) { c.close(); db.close(); return true; }
        long ts = c.getLong(c.getColumnIndexOrThrow(COL_TS));
        c.close();
        db.close();
        return (System.currentTimeMillis() - ts) > maxAgeMs;
    }
}
