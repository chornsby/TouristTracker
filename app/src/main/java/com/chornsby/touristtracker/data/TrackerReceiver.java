package com.chornsby.touristtracker.data;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;

import com.chornsby.touristtracker.Utility;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationResult;

public class TrackerReceiver extends BroadcastReceiver {

    private static final String TAG = TrackerReceiver.class.getSimpleName();

    private static final int ACTIVITY_RESULT = 1;
    private static final int LOCATION_RESULT = 2;
    private static final int UNKNOWN_RESULT = 0;

    public TrackerReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (getIntentType(intent)) {
            case ACTIVITY_RESULT:
                handleActivityRecognitionResult(context, intent);
                break;
            case LOCATION_RESULT:
                handleLocationResult(context, intent);
                break;
        }
    }

    private static int getIntentType(Intent intent) {
        if (ActivityRecognitionResult.hasResult(intent)) {
            return ACTIVITY_RESULT;
        } else if (LocationResult.hasResult(intent)) {
            return LOCATION_RESULT;
        }
        return UNKNOWN_RESULT;
    }

    private static void handleActivityRecognitionResult(Context context, Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        ContentResolver contentResolver = context.getContentResolver();

        for (DetectedActivity activity: result.getProbableActivities()) {
            // Parse DetectedActivity into database-friendly ContentValues
            ContentValues values = Utility.getContentValues(result, activity);

            // Store Activity data in the database
            contentResolver.insert(TrackerContract.ActivityEntry.CONTENT_URI, values);
        }
    }

    private static void handleLocationResult(Context context, Intent intent) {
        LocationResult result = LocationResult.extractResult(intent);

        // Parse Location into database-friendly ContentValues
        ContentValues values = Utility.getContentValues(result);

        // Nothing to insert
        if (values == null) return;

        // Store Location data in the database
        context.getContentResolver().insert(TrackerContract.LocationEntry.CONTENT_URI, values);
    }
}
