// SQLite database to hold location data.
// Consists of one table (locations) with four columns (id, latitude, longitude, address).

package com.example.assignment2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

import java.util.ArrayList;

public class LocationDatabase extends SQLiteOpenHelper {

    private	static final int DATABASE_VERSION =	1;
    private	static final String	DATABASE_NAME = "Locations.db";
    private	static final String TABLE_LOCATIONS = "locations";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_ADDRESS = "address";

    public LocationDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String	CREATE_LOCATION_TABLE = "CREATE	TABLE " + TABLE_LOCATIONS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY," + COLUMN_LATITUDE + " DOUBLE," + COLUMN_LONGITUDE + " DOUBLE," + COLUMN_ADDRESS + " TEXT" + ")";
        db.execSQL(CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        onCreate(db);
    }

    // Returns all entries as an ArrayList
    public ArrayList<Location> listLocations(){
        String sql = "select * from " + TABLE_LOCATIONS;
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Location> storeLocations = new ArrayList<>();
        Cursor cursor = db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do{
                int id = Integer.parseInt(cursor.getString(0));
                double latitude = cursor.getDouble(1);
                double longitude = cursor.getDouble(2);
                String address = cursor.getString(3);
                storeLocations.add(new Location(id, latitude, longitude, address));
            }while (cursor.moveToNext());
        }
        cursor.close();
        return storeLocations;
    }

    // Adds Location
    public void addLocation(Location location){
        ContentValues values = new ContentValues();
        values.put(COLUMN_LATITUDE, location.getLatitude());
        values.put(COLUMN_LONGITUDE, location.getLongitude());
        values.put(COLUMN_ADDRESS, location.getAddress());
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_LOCATIONS, null, values);
    }

    // Updates Location
    public void updateLocation(Location location){
        ContentValues values = new ContentValues();
        values.put(COLUMN_LATITUDE, location.getLatitude());
        values.put(COLUMN_LONGITUDE, location.getLongitude());
        values.put(COLUMN_ADDRESS, location.getAddress());
        SQLiteDatabase db = this.getWritableDatabase();
        db.update(TABLE_LOCATIONS, values, COLUMN_ID	+ "	= ?", new String[] { String.valueOf(location.getId())});
    }

    // Deletes Location with given id
    public void deleteLocation(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCATIONS, COLUMN_ID	+ "	= ?", new String[] { String.valueOf(id)});
    }

    // Deletes all entries
    public void deleteAllLocations(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_LOCATIONS);
    }
}