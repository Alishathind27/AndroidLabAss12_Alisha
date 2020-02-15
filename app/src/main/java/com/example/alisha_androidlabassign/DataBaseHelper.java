package com.example.alisha_androidlabassign;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.print.PrinterId;

import androidx.annotation.Nullable;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "MLocation";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "location";
    private static final String LOCATION_ID = "id";
    private static final String LATITUDE = "lat";
    private static final String LONGITUDE = "lng";
    private static final String ADDRESS = "address";
    private static final String DATE_TIME = "dateTime";



  public DataBaseHelper(@Nullable Context context){
      super(context, DATABASE_NAME, null,DATABASE_VERSION);

  }

    @Override
    public void onCreate(SQLiteDatabase db) {

      String sql = "CREATE TABLE " + TABLE_NAME + "(" +
            LOCATION_ID + " INTEGER NOT NULL CONSTRAINT location_pk PRIMARY KEY AUTOINCREMENT," +
            LATITUDE + " double NOT NULL," +
            LONGITUDE + " double NOT NULL," +
              ADDRESS + " varchar(200) NOT NULL," +
              DATE_TIME + " varchar(200) NOT NULL);";
      db.execSQL(sql);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

      String sql = "DROP TABLE IF EXISTS " + TABLE_NAME+ ";";
      db.execSQL(sql);
      onCreate(db);
    }

    boolean addLocation(double lat, double lng, String addr, String dateT){

      SQLiteDatabase sqLiteDatabase = getWritableDatabase();

      ContentValues cv = new ContentValues();

      cv.put(LATITUDE, String.valueOf(lat));
      cv.put(LONGITUDE, String.valueOf(lng));
      cv.put(ADDRESS, addr);
      cv.put(DATE_TIME, dateT);

        System.out.println("addLocation");
        return sqLiteDatabase.insert(TABLE_NAME, null,cv) != 1;
    }

    Cursor getAllLocations(){
      SQLiteDatabase sqLiteDatabase = getReadableDatabase();
      return  sqLiteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME,null);
    }

    boolean updateLocation(int id, String lat,String lng, String addr, String dateT){
        SQLiteDatabase sqLiteDatabase  = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(LATITUDE,String.valueOf(lat));
        cv.put(LONGITUDE, String.valueOf(lng));
        cv.put(ADDRESS, addr);
        cv.put(DATE_TIME, dateT);
        System.out.println("print");
        return sqLiteDatabase.update(TABLE_NAME,cv,LOCATION_ID + "=?", new String[]{String.valueOf(id)}) > 0;
    }

    boolean deleteLocation(int id){
      SQLiteDatabase sqLiteDatabase = getWritableDatabase();

      return sqLiteDatabase.delete(TABLE_NAME, LOCATION_ID + "=?", new String[]{String.valueOf(id)}) >0;
    }
}
