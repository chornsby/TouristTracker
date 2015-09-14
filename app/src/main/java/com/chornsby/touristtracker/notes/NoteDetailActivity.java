package com.chornsby.touristtracker.notes;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.chornsby.touristtracker.R;
import com.chornsby.touristtracker.data.TrackerContract;

public class NoteDetailActivity extends AppCompatActivity {

    TextView mRelativeDateTime;
    EditText mEditText;

    String mSelection;
    String[] mSelectionArgs;

    Note mNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        Intent intent = getIntent();

        String rowId = intent.getStringExtra(TrackerContract.NoteEntry.TABLE_NAME);

        if (rowId.equals("")) {
            rowId = createNote();
            intent.putExtra(TrackerContract.NoteEntry.TABLE_NAME, rowId);
        }

        mSelection = TrackerContract.NoteEntry._ID + "=?";
        mSelectionArgs = new String[] {rowId};

        Cursor cursor = getContentResolver().query(
                TrackerContract.NoteEntry.CONTENT_URI,
                null,
                mSelection,
                mSelectionArgs,
                null
        );

        mNote = new Note(cursor);

        mEditText = (EditText) findViewById(R.id.edit_note);
        mEditText.append(mNote.note);

        CharSequence relativeDateTime;

        if (mNote.time != 0) {
            relativeDateTime = DateUtils.getRelativeDateTimeString(
                    this,
                    mNote.time,
                    DateUtils.MINUTE_IN_MILLIS,
                    DateUtils.WEEK_IN_MILLIS,
                    0
            );
        } else {
            relativeDateTime = "";
        }

        mRelativeDateTime = (TextView) findViewById(R.id.relative_date_time);
        mRelativeDateTime.setText(relativeDateTime);
    }

    private String createNote() {
        ContentValues contentValues = new Note().asContentValues();
        Uri uri = getContentResolver().insert(
                TrackerContract.NoteEntry.CONTENT_URI,
                contentValues
        );
        return uri.getLastPathSegment();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNote.note = mEditText.getText().toString();

        getContentResolver().update(
                TrackerContract.NoteEntry.CONTENT_URI,
                mNote.asContentValues(),
                TrackerContract.NoteEntry._ID + "=?",
                new String[] {((Long) mNote.id).toString()}
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_discard) {
            getContentResolver().delete(
                    TrackerContract.NoteEntry.CONTENT_URI,
                    mSelection,
                    mSelectionArgs
            );
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }
}
