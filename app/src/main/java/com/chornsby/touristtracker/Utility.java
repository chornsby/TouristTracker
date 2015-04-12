package com.chornsby.touristtracker;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.chornsby.touristtracker.data.TrackerContract.LocationEntry;
import com.google.android.gms.location.LocationRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utility {

    public static final String LOG_TAG = Utility.class.getSimpleName();

    public static ContentValues getContentValuesFromLocation(Location location) {
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_ACCURACY, location.getAccuracy());
        values.put(LocationEntry.COLUMN_ALTITUDE, location.getAltitude());
        values.put(LocationEntry.COLUMN_BEARING, location.getBearing());
        values.put(LocationEntry.COLUMN_LATITUDE, location.getLatitude());
        values.put(LocationEntry.COLUMN_LONGITUDE, location.getLongitude());
        values.put(LocationEntry.COLUMN_PROVIDER, location.getProvider());
        values.put(LocationEntry.COLUMN_SPEED, location.getSpeed());
        values.put(LocationEntry.COLUMN_TIME, location.getTime());
        return values;
    }

    public static LocationRequest createNewLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(15000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

    public static boolean isExternalStorageAvailable() {
        // Return true if External Storage is readable and writable
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static File getMapFile(Context context) {
        // Return null if the ExternalStorage is not available
        if (!isExternalStorageAvailable()) {
            Log.d(LOG_TAG, "External storage unavailable to retrieve helsinki.map data.");
            return null;
        }

        // Get a reference to the required File object
        File mapFile = new File(
                context.getExternalFilesDir(null),
                context.getString(R.string.map_file_name)
        );

        // Return the MapFile if already available
        if (mapFile.exists()) return mapFile;

        // Else generate it
        Log.d(LOG_TAG, "Creating the helsinki.map File object from asset data.");

        try {
            InputStream in = context.getAssets().open("helsinki.map");
            copyInputStreamToFile(in, mapFile);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return mapFile;

    }

    private static void copyInputStreamToFile(InputStream in, File file) throws IOException {
        OutputStream out = new FileOutputStream(file);

        byte[] buffer = new byte[102400]; // Buffer of 100 kb
        int length;

        while ((length = in.read(buffer)) > -1) {
            out.write(buffer, 0, length);
        }

        in.close();
        out.close();
    }

    public static boolean isTracking(Context context) {
        String TRACKING_PREFERENCE_KEY = context.getString(R.string.pref_track_location);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(TRACKING_PREFERENCE_KEY, true);
    }

    public static File createImageFile(Context context) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(new Date());
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + context.getString(R.string.photo_subdirectory_path)
        );

        // Check if the directory was created or already exists
        if (storageDir.mkdirs() || storageDir.isDirectory()) {
            return File.createTempFile(timeStamp, ".jpg", storageDir);
        } else {
            throw new IOException("Problem creating an image file on external storage.");
        }
    }

    public static void addToGallery(Context context, Uri photoUri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(photoUri);
        context.sendBroadcast(mediaScanIntent);
    }
}
