package com.chornsby.touristtracker.data;


import android.provider.BaseColumns;

public class TrackerContract {
//    public static final String CONTENT_AUTHORITY = "com.chornsby.touristtracker";
//    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

//    public static final String PATH_LOCATION = "location";

    // Normalise all dates in the database to make querying easier
//    public static long normalizeDate(long startDate) {
//        // normalize the start date to the beginning of the (UTC) day
//        Time time = new Time();
//        time.set(startDate);
//        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
//        return time.setJulianDay(julianDay);
//    }

    public static final class LocationEntry implements BaseColumns {
//        public static final Uri CONTENT_URI =
//                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
//        public static final String CONTENT_TYPE =
//                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String TABLE_NAME = "location";

        public static final String COLUMN_ACCURACY = "accuracy";
        public static final String COLUMN_ALTITUDE = "altitude";
        public static final String COLUMN_BEARING = "bearing";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_PROVIDER = "provider";
        public static final String COLUMN_SPEED = "speed";
        public static final String COLUMN_TIME = "time";
    }


}
