package com.chornsby.touristtracker.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class TrackerProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private TrackerDbHelper mOpenHelper;

    static final int ACTIVITY = 50;
    static final int LOCATION = 100;
    static final int NOTE = 200;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(TrackerContract.CONTENT_AUTHORITY, TrackerContract.PATH_ACTIVITY, ACTIVITY);
        matcher.addURI(TrackerContract.CONTENT_AUTHORITY, TrackerContract.PATH_LOCATION, LOCATION);
        matcher.addURI(TrackerContract.CONTENT_AUTHORITY, TrackerContract.PATH_NOTE, NOTE);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new TrackerDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;

        switch (sUriMatcher.match(uri)) {
            case ACTIVITY:
                cursor = mOpenHelper.getReadableDatabase().query(
                        TrackerContract.ActivityEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case LOCATION:
                cursor = mOpenHelper.getReadableDatabase().query(
                        TrackerContract.LocationEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case NOTE:
                cursor = mOpenHelper.getReadableDatabase().query(
                        TrackerContract.NoteEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ACTIVITY:
                return TrackerContract.ActivityEntry.CONTENT_TYPE;
            case LOCATION:
                return TrackerContract.LocationEntry.CONTENT_TYPE;
            case NOTE:
                return TrackerContract.NoteEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long id;

        switch (match) {
            case ACTIVITY:
                id = db.insert(TrackerContract.ActivityEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = TrackerContract.ActivityEntry.buildActivityUri(id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case LOCATION:
                id = db.insert(TrackerContract.LocationEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = TrackerContract.LocationEntry.buildLocationUri(id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case NOTE:
                id = db.insert(TrackerContract.NoteEntry.TABLE_NAME, null, values);
                if (id > 0) {
                    returnUri = TrackerContract.NoteEntry.buildNoteUri(id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        if (selection == null) selection = "1";

        switch (match) {
            case ACTIVITY:
                rowsDeleted = db.delete(
                        TrackerContract.ActivityEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case LOCATION:
                rowsDeleted = db.delete(
                        TrackerContract.LocationEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case NOTE:
                rowsDeleted = db.delete(
                        TrackerContract.NoteEntry.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case ACTIVITY:
                rowsUpdated = db.update(
                        TrackerContract.ActivityEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            case LOCATION:
                rowsUpdated = db.update(
                        TrackerContract.LocationEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            case NOTE:
                rowsUpdated = db.update(
                        TrackerContract.NoteEntry.TABLE_NAME,
                        values,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
