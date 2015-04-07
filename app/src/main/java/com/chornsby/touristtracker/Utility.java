package com.chornsby.touristtracker;


import android.content.ContentValues;
import android.location.Location;

import com.chornsby.touristtracker.data.TrackerContract.LocationEntry;
import com.google.android.gms.location.LocationRequest;

public class Utility {

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
}
