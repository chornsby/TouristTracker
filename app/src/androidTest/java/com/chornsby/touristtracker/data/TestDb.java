package com.chornsby.touristtracker.data;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.chornsby.touristtracker.data.TrackerContract.LocationEntry;

public class TestDb extends AndroidTestCase {

    void deleteTheDatabase() {
        mContext.deleteDatabase(TrackerDbHelper.DATABASE_NAME);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteTheDatabase();
    }

    public void testCreateDatabase() {
        SQLiteDatabase db = new TrackerDbHelper(this.mContext).getWritableDatabase();

        assertTrue(db.isOpen());
    }

    public void testCreateCorrectTable() {
        SQLiteDatabase db = new TrackerDbHelper(this.mContext).getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue(cursor.moveToFirst());

        boolean foundTable = false;

        do {
            if (LocationEntry.TABLE_NAME.equals(cursor.getString(0))) {
                foundTable = true;
            }
        } while (cursor.moveToNext());


        assertTrue(foundTable);

        cursor.close();
    }


}
