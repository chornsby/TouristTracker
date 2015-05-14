package com.chornsby.touristtracker.submit;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.chornsby.touristtracker.Utility;
import com.chornsby.touristtracker.data.TrackerContract.LocationEntry;
import com.chornsby.touristtracker.data.TrackerContract.NoteEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class JsonGenerator {

    private static final String LOG_TAG = JsonGenerator.class.getSimpleName();

    public static File generateLocationJsonFile(Context context) throws IOException, JSONException {
        JSONArray jsonArray = generateLocationJSON(context);
        return generateFile(context, jsonArray, "location.json");
    }

    public static File generateNotesJsonFile(Context context) throws IOException, JSONException {
        JSONArray jsonArray = generateNotesJSON(context);
        return generateFile(context, jsonArray, "notes.json");
    }

    private static File generateFile(Context context, JSONArray jsonArray, String filename) throws IOException {

        if (!Utility.isExternalStorageAvailable()) {
            throw new IOException("External storage is not available.");
        }

        File output = new File(context.getExternalFilesDir(null), filename);
        FileWriter writer = new FileWriter(output);
        writer.write(jsonArray.toString());
        writer.flush();
        writer.close();

        return output;
    }

    private static JSONArray generateLocationJSON(Context context) throws JSONException {
        Uri uri = LocationEntry.CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        final int INDEX_ID = cursor.getColumnIndex(LocationEntry._ID);
        final int INDEX_ACCURACY = cursor.getColumnIndex(LocationEntry.COLUMN_ACCURACY);
        final int INDEX_ALTITUDE = cursor.getColumnIndex(LocationEntry.COLUMN_ALTITUDE);
        final int INDEX_BEARING = cursor.getColumnIndex(LocationEntry.COLUMN_BEARING);
        final int INDEX_LATITUDE = cursor.getColumnIndex(LocationEntry.COLUMN_LATITUDE);
        final int INDEX_LONGITUDE = cursor.getColumnIndex(LocationEntry.COLUMN_LONGITUDE);
        final int INDEX_PROVIDER = cursor.getColumnIndex(LocationEntry.COLUMN_PROVIDER);
        final int INDEX_SPEED = cursor.getColumnIndex(LocationEntry.COLUMN_SPEED);
        final int INDEX_TIME = cursor.getColumnIndex(LocationEntry.COLUMN_TIME);

        JSONArray jsonArray = new JSONArray();

        while (cursor.moveToNext()) {
            JSONObject jsonObject = new JSONObject();

            int id = cursor.getInt(INDEX_ID);
            float accuracy = cursor.getFloat(INDEX_ACCURACY);
            float altitude = cursor.getFloat(INDEX_ALTITUDE);
            float bearing = cursor.getFloat(INDEX_BEARING);
            float latitude = cursor.getFloat(INDEX_LATITUDE);
            float longitude = cursor.getFloat(INDEX_LONGITUDE);
            String provider = cursor.getString(INDEX_PROVIDER);
            float speed = cursor.getFloat(INDEX_SPEED);
            long time = cursor.getLong(INDEX_TIME);

            jsonObject.put(LocationEntry._ID, id);
            jsonObject.put(LocationEntry.COLUMN_ACCURACY, accuracy);
            jsonObject.put(LocationEntry.COLUMN_ALTITUDE, altitude);
            jsonObject.put(LocationEntry.COLUMN_BEARING, bearing);
            jsonObject.put(LocationEntry.COLUMN_LATITUDE, latitude);
            jsonObject.put(LocationEntry.COLUMN_LONGITUDE, longitude);
            jsonObject.put(LocationEntry.COLUMN_PROVIDER, provider);
            jsonObject.put(LocationEntry.COLUMN_SPEED, speed);
            jsonObject.put(LocationEntry.COLUMN_TIME, time);

            jsonArray.put(jsonObject);
        }

        cursor.close();

        return jsonArray;
    }
    
    private static JSONArray generateNotesJSON(Context context) throws JSONException {
        Uri uri = NoteEntry.CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        final int INDEX_ID = cursor.getColumnIndex(NoteEntry._ID);
        final int INDEX_TIME = cursor.getColumnIndex(NoteEntry.COLUMN_TIME);
        final int INDEX_LATITUDE = cursor.getColumnIndex(NoteEntry.COLUMN_LATITUDE);
        final int INDEX_LONGITUDE = cursor.getColumnIndex(NoteEntry.COLUMN_LONGITUDE);
        final int INDEX_TEXT = cursor.getColumnIndex(NoteEntry.COLUMN_TEXT);

        JSONArray jsonArray = new JSONArray();

        while (cursor.moveToNext()) {
            JSONObject jsonObject = new JSONObject();

            int id = cursor.getInt(INDEX_ID);
            long time = cursor.getLong(INDEX_TIME);
            float latitude = cursor.getFloat(INDEX_LATITUDE);
            float longitude = cursor.getFloat(INDEX_LONGITUDE);
            String text = cursor.getString(INDEX_TEXT);

            jsonObject.put(NoteEntry._ID, id);
            jsonObject.put(NoteEntry.COLUMN_TIME, time);
            jsonObject.put(NoteEntry.COLUMN_LATITUDE, latitude);
            jsonObject.put(NoteEntry.COLUMN_LONGITUDE, longitude);
            jsonObject.put(NoteEntry.COLUMN_TEXT, text);

            jsonArray.put(jsonObject);
        }

        cursor.close();

        return jsonArray;
    }
}
