package com.chornsby.touristtracker.data;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class TrackerContract {
    public static final String CONTENT_AUTHORITY = "com.chornsby.touristtracker";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_LOCATION = "location";

    public static final class LocationEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static final String TABLE_NAME = "location";

        public static final String COLUMN_ACCURACY = "accuracy";
        public static final String COLUMN_ALTITUDE = "altitude";
        public static final String COLUMN_BEARING = "bearing";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_PROVIDER = "provider";
        public static final String COLUMN_SPEED = "speed";
        public static final String COLUMN_TIME = "time";

        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }


}
