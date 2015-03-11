package com.chornsby.touristtracker;

import android.content.ContentValues;
import android.location.Location;

import java.util.Date;

public class TestUtility {

    public static Location getTestLocation() {
        Location testLocation = new Location("test-provider");
        testLocation.setAccuracy(Float.parseFloat("50.0"));
        testLocation.setAltitude(24);
        testLocation.setLatitude(51.5010);
        testLocation.setLongitude(0.1416);
        testLocation.setTime(new Date().getTime());
        return testLocation;
    }

    public static ContentValues getTestLocationContentValues() {
        return Utility.getContentValuesFromLocation(getTestLocation());
    }

}
