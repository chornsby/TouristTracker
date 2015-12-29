package com.chornsby.touristtracker.submit;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.chornsby.touristtracker.Utility;
import com.chornsby.touristtracker.data.TrackerContract.ActivityEntry;
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

    public static File generateActivityJsonFile(Context context) throws IOException, JSONException {
        JSONArray jsonArray = generateActivityJSON(context);
        return generateFile(context, jsonArray, "activity.json");
    }

    public static File generateLocationJsonFile(Context context) throws IOException, JSONException {
        JSONArray jsonArray = generateLocationJSON(context);
        return generateFile(context, jsonArray, "location.json");
    }

    public static File generateNotesJsonFile(Context context) throws IOException, JSONException {
        JSONArray jsonArray = generateNotesJSON(context);
        return generateFile(context, jsonArray, "notes.json");
    }

    public static File generateGeneralJsonFile(Context context, Intent intent) throws IOException, JSONException {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();

        String emailAddress = intent.getStringExtra(DataUploadService.EXTRA_USER_EMAIL);
        if (emailAddress == null) emailAddress = "";

        boolean participateLottery = intent.getBooleanExtra(DataUploadService.EXTRA_PARTICIPATE_LOTTERY, false);
        boolean participateTAK = intent.getBooleanExtra(DataUploadService.EXTRA_PARTICIPATE_TAK, false);
        int researchNumber = Utility.getResearchNumber(context);

        jsonObject.put("email_address", emailAddress);
        jsonObject.put("agreed_to_participate_in_lottery", participateLottery);
        jsonObject.put("agreed_to_share_email_with_tak", participateTAK);
        jsonObject.put("research_number", researchNumber);
        jsonArray.put(jsonObject);

        return generateFile(context, jsonArray, "general.json");
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

    private static JSONArray generateActivityJSON(Context context) throws JSONException {
        Uri uri = ActivityEntry.CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        final int INDEX_ID = cursor.getColumnIndex(ActivityEntry._ID);
        final int INDEX_CONFIDENCE = cursor.getColumnIndex(ActivityEntry.COLUMN_CONFIDENCE);
        final int INDEX_TIME = cursor.getColumnIndex(ActivityEntry.COLUMN_TIME);
        final int INDEX_TYPE = cursor.getColumnIndex(ActivityEntry.COLUMN_TYPE);

        JSONArray jsonArray = new JSONArray();

        while (cursor.moveToNext()) {
            JSONObject jsonObject = new JSONObject();

            int id = cursor.getInt(INDEX_ID);
            int confidence = cursor.getInt(INDEX_CONFIDENCE);
            long time = cursor.getLong(INDEX_TIME);
            int type = cursor.getInt(INDEX_TYPE);

            jsonObject.put(ActivityEntry._ID, id);
            jsonObject.put(ActivityEntry.COLUMN_CONFIDENCE, confidence);
            jsonObject.put(ActivityEntry.COLUMN_TIME, time);
            jsonObject.put(ActivityEntry.COLUMN_TYPE, type);

            jsonArray.put(jsonObject);
        }

        cursor.close();

        return jsonArray;
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
        final int INDEX_TEXT = cursor.getColumnIndex(NoteEntry.COLUMN_TEXT);
        final int INDEX_IMAGE_URI = cursor.getColumnIndex(NoteEntry.COLUMN_IMAGE_URI);
        final int INDEX_ATTITUDE = cursor.getColumnIndex(NoteEntry.COLUMN_ATTITUDE);

        JSONArray jsonArray = new JSONArray();

        while (cursor.moveToNext()) {
            JSONObject jsonObject = new JSONObject();

            int id = cursor.getInt(INDEX_ID);
            long time = cursor.getLong(INDEX_TIME);
            String text = cursor.getString(INDEX_TEXT);
            String imageUri = cursor.getString(INDEX_IMAGE_URI);
            int attitude = cursor.getInt(INDEX_ATTITUDE);

            jsonObject.put(NoteEntry._ID, id);
            jsonObject.put(NoteEntry.COLUMN_TIME, time);
            jsonObject.put(NoteEntry.COLUMN_TEXT, text);
            jsonObject.put(NoteEntry.COLUMN_IMAGE_URI, imageUri);
            jsonObject.put(NoteEntry.COLUMN_ATTITUDE, attitude);

            jsonArray.put(jsonObject);
        }

        cursor.close();

        return jsonArray;
    }
}
