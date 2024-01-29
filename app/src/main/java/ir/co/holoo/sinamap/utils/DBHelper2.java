package ir.co.holoo.sinamap.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import ir.co.holoo.sinamap.model.Place;


public class DBHelper2 {

    public static ArrayList<Place> getPlaces(Context context) {
        final ArrayList<Place> places = new ArrayList<>();
        final DBHelper databaseHelper = new DBHelper(context);
        final SQLiteDatabase db = databaseHelper.openDatabase();
        final Cursor cursor = db.rawQuery("SELECT rowid,name,lat,lon FROM places", null);
        while (cursor.moveToNext()) {
            final int row_id = cursor.getInt(0);
            final String r_name = cursor.getString(1);
            final double r_lat = cursor.getDouble(2);
            final double r_lon = cursor.getDouble(3);

            places.add(new Place(row_id, r_name, r_lat, r_lon));
        }
        cursor.close();
        db.close();
        return places;
    }

    public static void insertPlace(Context context, Place place) {
        final DBHelper databaseHelper = new DBHelper(context);
        final SQLiteDatabase db = databaseHelper.openDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put("name", place.getName());
        contentValues.put("lat", place.getLat());
        contentValues.put("lon", place.getLon());

        db.insert("places", null, contentValues);
        db.close();
    }


}
