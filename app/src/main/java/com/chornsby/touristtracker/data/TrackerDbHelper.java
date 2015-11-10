package com.chornsby.touristtracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.chornsby.touristtracker.data.TrackerContract.ActivityEntry;
import com.chornsby.touristtracker.data.TrackerContract.LocationEntry;
import com.chornsby.touristtracker.data.TrackerContract.NoteEntry;

public class TrackerDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;

    static final String DATABASE_NAME = "tracker.db";

    public TrackerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_ACTIVITY_TABLE = "CREATE TABLE " + ActivityEntry.TABLE_NAME + " (" +
                ActivityEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                ActivityEntry.COLUMN_CONFIDENCE + " INTEGER NOT NULL, " +
                ActivityEntry.COLUMN_TIME + " INTEGER NOT NULL, " +
                ActivityEntry.COLUMN_TYPE + " INTEGER NOT NULL);";

        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + LocationEntry.TABLE_NAME + " (" +
                LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                LocationEntry.COLUMN_ACCURACY + " REAL NOT NULL, " +
                LocationEntry.COLUMN_ALTITUDE + " REAL, " +
                LocationEntry.COLUMN_BEARING + " REAL NOT NULL, " +
                LocationEntry.COLUMN_LATITUDE + " REAL NOT NULL, " +
                LocationEntry.COLUMN_LONGITUDE + " REAL NOT NULL, " +
                LocationEntry.COLUMN_PROVIDER + " TEXT, " +
                LocationEntry.COLUMN_SPEED + " REAL NOT NULL, " +
                LocationEntry.COLUMN_TIME + " INTEGER NOT NULL);";

        final String SQL_CREATE_NOTE_TABLE = "CREATE TABLE " + NoteEntry.TABLE_NAME + " (" +
                NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                NoteEntry.COLUMN_TIME + " INTEGER NOT NULL, " +
                NoteEntry.COLUMN_TEXT + " TEXT, " +
                NoteEntry.COLUMN_IMAGE_URI + " TEXT);";

        db.execSQL(SQL_CREATE_ACTIVITY_TABLE);
        db.execSQL(SQL_CREATE_LOCATION_TABLE);
        db.execSQL(SQL_CREATE_NOTE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For now, just drop and recreate
        db.execSQL("DROP TABLE IF EXISTS " + ActivityEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + LocationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + NoteEntry.TABLE_NAME);
        onCreate(db);
    }
}
