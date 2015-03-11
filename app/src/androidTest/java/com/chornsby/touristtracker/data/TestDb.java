package com.chornsby.touristtracker.data;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.chornsby.touristtracker.TestUtility;
import com.chornsby.touristtracker.data.TrackerContract.LocationEntry;

public class TestDb extends AndroidTestCase {

    void deleteTheDatabase() {
        mContext.deleteDatabase(TrackerDbHelper.DATABASE_NAME);
    }

    SQLiteDatabase getWritableDatabase() {
        return new TrackerDbHelper(this.mContext).getWritableDatabase();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteTheDatabase();
    }

    public void testCreateDatabase() {
        SQLiteDatabase db = getWritableDatabase();

        assertTrue(db.isOpen());

        db.close();
    }

    public void testCreateCorrectTable() {
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue(cursor.moveToFirst());

        boolean foundTable = false;

        while (cursor.moveToNext()) {
            if (LocationEntry.TABLE_NAME.equals(cursor.getString(0))) {
                foundTable = true;
                break;
            }
        }

        assertTrue(foundTable);

        cursor.close();
        db.close();
    }

    public void testInsertLocationData() {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues testLocationValues = TestUtility.getTestLocationContentValues();

        long id = db.insert(LocationEntry.TABLE_NAME, null, testLocationValues);

        assertTrue(id != -1);

        Cursor cursor = db.query(
                LocationEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertTrue(cursor.moveToFirst());

        cursor.close();
        db.close();
    }

}
