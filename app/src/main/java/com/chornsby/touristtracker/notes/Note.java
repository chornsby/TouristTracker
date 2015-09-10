package com.chornsby.touristtracker.notes;

import android.content.ContentValues;
import android.database.Cursor;

import com.chornsby.touristtracker.data.TrackerContract.NoteEntry;

public class Note {

    public long id;
    public long time;
    public double latitude;
    public double longitude;
    public String note;

    public Note(ContentValues values) {
        time = values.getAsLong(NoteEntry.COLUMN_TIME);
        latitude = values.getAsDouble(NoteEntry.COLUMN_LATITUDE);
        longitude = values.getAsDouble(NoteEntry.COLUMN_LONGITUDE);
        note = values.getAsString(NoteEntry.COLUMN_TEXT);

        if (values.getAsLong(NoteEntry._ID) != null) {
            id = values.getAsLong(NoteEntry._ID);
        }
    }

    public Note(Cursor cursor) {

        if (!cursor.moveToFirst()) {
            throw new IllegalArgumentException("Cursor had no results!");
        }

        time = cursor.getLong(cursor.getColumnIndex(NoteEntry.COLUMN_TIME));
        latitude = cursor.getDouble(cursor.getColumnIndex(NoteEntry.COLUMN_LATITUDE));
        longitude = cursor.getDouble(cursor.getColumnIndex(NoteEntry.COLUMN_LONGITUDE));
        note = cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_TEXT));

        id = cursor.getLong(cursor.getColumnIndex(NoteEntry._ID));

        cursor.close();
    }

    public ContentValues asContentValues() {
        ContentValues contentValues = new ContentValues(4);

        contentValues.put(NoteEntry.COLUMN_TIME, time);
        contentValues.put(NoteEntry.COLUMN_LATITUDE, latitude);
        contentValues.put(NoteEntry.COLUMN_LONGITUDE, longitude);
        contentValues.put(NoteEntry.COLUMN_TEXT, note);

        return contentValues;
    }
}
