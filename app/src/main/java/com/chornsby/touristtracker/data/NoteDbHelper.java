package com.chornsby.touristtracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "note.db";

    public NoteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_NOTE_TABLE = "CREATE TABLE " + NoteContract.NoteEntry.TABLE_NAME + " (" +
                NoteContract.NoteEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                NoteContract.NoteEntry.COLUMN_TIME + " INTEGER NOT NULL, " +
                NoteContract.NoteEntry.COLUMN_TEXT + " TEXT);";

        db.execSQL(SQL_CREATE_NOTE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For now, just drop and recreate
        db.execSQL("DROP TABLE IF EXISTS " + NoteContract.NoteEntry.TABLE_NAME);
        onCreate(db);
    }
}
