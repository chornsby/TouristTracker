package com.chornsby.touristtracker;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.chornsby.touristtracker.data.TrackerContract.ActivityEntry;
import com.chornsby.touristtracker.data.TrackerContract.LocationEntry;
import com.chornsby.touristtracker.reminders.AlarmReceiver;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class Utility {

    public static final String LOG_TAG = Utility.class.getSimpleName();

    public static ContentValues getContentValues(ActivityRecognitionResult result, DetectedActivity activity) {
        ContentValues values = new ContentValues();
        values.put(ActivityEntry.COLUMN_CONFIDENCE, activity.getConfidence());
        values.put(ActivityEntry.COLUMN_TIME, result.getTime());
        values.put(ActivityEntry.COLUMN_TYPE, activity.getType());
        return values;
    }

    @Nullable
    public static ContentValues getContentValues(LocationResult result) {
        Location location = result.getLastLocation();

        // No last-known location
        if (location == null) return null;

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
        locationRequest.setInterval(60000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        return locationRequest;
    }

    public static boolean isExternalStorageAvailable() {
        // Return true if External Storage is readable and writable
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static File getFileFromAssets(Context context, String filename) {
        // Return null if the ExternalStorage is not available
        if (!isExternalStorageAvailable()) {
            Log.d(LOG_TAG, "External storage unavailable to retrieve file: " + filename);
            return null;
        }

        // Get a reference to the required File object
        File file = new File(
                context.getExternalFilesDir(null),
                filename
        );

        // Return the file if already available
        if (file.exists()) return file;

        // Else generate it
        Log.d(LOG_TAG, "Creating " + filename + " from asset data");

        try {
            InputStream in = context.getAssets().open(filename);
            copyInputStreamToFile(in, file);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return file;
    }

    public static File getKeyFile(Context context) {
        return getFileFromAssets(context, context.getString(R.string.key_file_name));
    }

    public static File getMapFile(Context context) {
        return getFileFromAssets(context, context.getString(R.string.map_file_name));
    }

    public static void copyInputStreamToFile(InputStream in, File file) throws IOException {
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
        return preferences.getBoolean(TRACKING_PREFERENCE_KEY, false);
    }

    public static boolean isUploading(Context context) {
        String UPLOADING_PREFERENCE_KEY = context.getString(R.string.pref_uploading_data);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(UPLOADING_PREFERENCE_KEY, false);
    }

    public static void setIsUploading(Context context, boolean isUploading) {
        String UPLOADING_PREFERENCE_KEY = context.getString(R.string.pref_uploading_data);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(UPLOADING_PREFERENCE_KEY, isUploading);
        editor.apply();
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

    public static boolean isLocationPermissionRequired(Context context) {
        return isPermissionRequired(context, Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public static boolean isStoragePermissionRequired(Context context) {
        if (Build.VERSION.SDK_INT > 15) {
            final boolean isReadRequired = isPermissionRequired(context, Manifest.permission.READ_EXTERNAL_STORAGE);
            final boolean isWriteRequired = isPermissionRequired(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return isReadRequired || isWriteRequired;
        } else {
            return isPermissionRequired(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @TargetApi(23)
    public static void requestLocationPermissions(Activity activity) {
        activity.requestPermissions(
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                0
        );
    }

    @TargetApi(23)
    public static void requestStoragePermissions(Activity activity, int requestCode) {
        activity.requestPermissions(
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                },
                requestCode
        );
    }

    public static boolean isPermissionRequired(Context context, String permission) {
        int permissionState = ContextCompat.checkSelfPermission(context, permission);
        return permissionState != PackageManager.PERMISSION_GRANTED;
    }

    public static int getResearchNumber(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        int researchNumber = sharedPreferences.getInt(context.getString(R.string.pref_applicant_id), 0);
        boolean isUnset = researchNumber == 0;

        if (isUnset) {
            Random r = new Random();
            researchNumber = r.nextInt(10000) + 10000;

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(context.getString(R.string.pref_applicant_id), researchNumber);
            editor.apply();
        }

        return researchNumber;
    }

    public static void setNotifications(Context context, SharedPreferences sharedPreferences) {
        // Do not set alarms again if they have already been set
        if (sharedPreferences.getBoolean(context.getString(R.string.pref_notifications_set), false)) {
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        calendar.add(Calendar.DATE, 1);

        alarmManager.set(
                AlarmManager.RTC,
                calendar.getTimeInMillis(),
                PendingIntent.getBroadcast(context, Constants.FIRST_NOTIFICATION_ID, alarmIntent, 0)
        );

        Log.d(LOG_TAG, "First notification set for: " + calendar.getTimeInMillis());

        calendar.add(Calendar.DATE, 1);

        alarmManager.set(
                AlarmManager.RTC,
                calendar.getTimeInMillis(),
                PendingIntent.getBroadcast(context, Constants.SECOND_NOTIFICATION_ID, alarmIntent, 0)
        );

        Log.d(LOG_TAG, "Second notification set for: " + calendar.getTimeInMillis());

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(context.getString(R.string.pref_notifications_set), true);
        editor.apply();
    }
}
