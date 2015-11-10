package com.chornsby.touristtracker.notes;

import android.content.ContentValues;
import android.database.Cursor;

import com.chornsby.touristtracker.data.TrackerContract.NoteEntry;

public class Note {

    public long id;
    public long time;
    public String note;
    public String imageUri;

    public Note() {
        time = System.currentTimeMillis();
        note = "";
        imageUri = "";
    }

    public Note(ContentValues values) {
        time = values.getAsLong(NoteEntry.COLUMN_TIME);
        note = values.getAsString(NoteEntry.COLUMN_TEXT);
        imageUri = values.getAsString(NoteEntry.COLUMN_IMAGE_URI);

        if (values.getAsLong(NoteEntry._ID) != null) {
            id = values.getAsLong(NoteEntry._ID);
        }
    }

    public Note(Cursor cursor) {

        if (!cursor.moveToFirst()) {
            throw new IllegalArgumentException("Cursor had no results!");
        }

        time = cursor.getLong(cursor.getColumnIndex(NoteEntry.COLUMN_TIME));
        note = cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_TEXT));
        imageUri = cursor.getString(cursor.getColumnIndex(NoteEntry.COLUMN_IMAGE_URI));

        id = cursor.getLong(cursor.getColumnIndex(NoteEntry._ID));

        cursor.close();
    }

    public ContentValues asContentValues() {
        ContentValues contentValues = new ContentValues(2);

        contentValues.put(NoteEntry.COLUMN_TIME, time);
        contentValues.put(NoteEntry.COLUMN_TEXT, note);
        contentValues.put(NoteEntry.COLUMN_IMAGE_URI, imageUri);

        return contentValues;
    }
}
