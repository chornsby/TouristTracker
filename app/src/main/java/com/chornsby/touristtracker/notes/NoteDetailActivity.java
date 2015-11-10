package com.chornsby.touristtracker.notes;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chornsby.touristtracker.R;
import com.chornsby.touristtracker.Utility;
import com.chornsby.touristtracker.data.TrackerContract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class NoteDetailActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_PICTURE = 2;

    TextView mRelativeDateTime;
    EditText mEditText;
    FloatingActionButton mAddPhoto;
    ImageView mImageView;

    Uri mCurrentPhotoUri;
    Uri mSelectedPhotoUri;

    String mSelection;
    String[] mSelectionArgs;

    Note mNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

        mAddPhoto = (FloatingActionButton) findViewById(R.id.add_image_fab);
        mAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        mImageView = (ImageView) findViewById(R.id.edit_photo);

        if (!TextUtils.isEmpty(mNote.imageUri)) {
            mSelectedPhotoUri = Uri.parse(mNote.imageUri);
            mImageView.setImageURI(null);
            mImageView.setImageURI(mSelectedPhotoUri);
        }
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Verify permissions on Android 23
                if (Utility.isStoragePermissionRequired(NoteDetailActivity.this)) {
                    Utility.requestStoragePermissions(NoteDetailActivity.this);
                    return;
                }

                if (items[which].equals("Take Photo")) {
                    takePhoto();
                } else if (items[which].equals("Choose from Library")) {
                    choosePhoto();
                } else {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Check if there is an app capable of resolving the intent
        if (intent.resolveActivity(getPackageManager()) == null) {
            Snackbar.make(
                    findViewById(R.id.activity_note_detail),
                    "Could not find a camera app",
                    Snackbar.LENGTH_SHORT
            ).show();
            return;
        }

        boolean isSuccess = tryCreateFileForImage();
        if (!isSuccess) return;

        intent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoUri);

        startActivityForResult(intent, REQUEST_CAMERA);
    }

    private void choosePhoto() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        intent.setType("image/*");

        if (intent.resolveActivity(getPackageManager()) == null) {
            Snackbar.make(
                    findViewById(R.id.activity_note_detail),
                    "Could not find a gallery app",
                    Snackbar.LENGTH_SHORT
            ).show();
            return;
        }

        startActivityForResult(intent, SELECT_PICTURE);
    }

    private Uri createFileForImage() throws IOException {
        // Create a file for the image
        File imageFile = Utility.createImageFile(NoteDetailActivity.this);
        return Uri.fromFile(imageFile);
    }

    private boolean tryCreateFileForImage() {
        try {
            mCurrentPhotoUri = createFileForImage();
        } catch (IOException e) {
            e.printStackTrace();
            Snackbar.make(
                    findViewById(R.id.activity_note_detail),
                    "Problem finding storage for the image",
                    Snackbar.LENGTH_SHORT
            ).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CAMERA:
                Utility.addToGallery(this, mCurrentPhotoUri);
                mSelectedPhotoUri = mCurrentPhotoUri;
                break;
            case SELECT_PICTURE:
                mSelectedPhotoUri = data.getData();
                break;
            default:
                return;
        }

        InputStream imageStream;

        try {
            imageStream = getContentResolver().openInputStream(mSelectedPhotoUri);
        } catch (FileNotFoundException e) {
            Snackbar.make(
                    findViewById(R.id.activity_note_detail),
                    "Could not access the image",
                    Snackbar.LENGTH_SHORT
            );
            return;
        }

        if (requestCode == SELECT_PICTURE) {
            File imageFile;

            try {
                imageFile = Utility.createImageFile(NoteDetailActivity.this);
            } catch (IOException e) {
                Snackbar.make(
                        findViewById(R.id.activity_note_detail),
                        "Problem finding storage for the image",
                        Snackbar.LENGTH_SHORT
                ).show();
                return;
            }

            try {
                Utility.copyInputStreamToFile(imageStream, imageFile);
            } catch (IOException e) {
                Snackbar.make(
                        findViewById(R.id.activity_note_detail),
                        "Problem finding storage for the image",
                        Snackbar.LENGTH_SHORT
                ).show();
                return;
            }

            mSelectedPhotoUri = Uri.fromFile(imageFile);
        }

        try {
            imageStream = getContentResolver().openInputStream(mSelectedPhotoUri);
        } catch (FileNotFoundException e) {
            Snackbar.make(
                    findViewById(R.id.activity_note_detail),
                    "Could not access the image",
                    Snackbar.LENGTH_SHORT
            );
            return;
        }

        Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

        mImageView.setImageBitmap(selectedImage);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        boolean isReadGranted = false;
        boolean isWriteGranted = false;

        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            boolean isGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;

            if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                isReadGranted = isGranted;
            } else if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                isWriteGranted = isGranted;
            }
        }

        if (isReadGranted && isWriteGranted) {
            takePhoto();
        }
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

        if (mSelectedPhotoUri != null) {
            mNote.imageUri = mSelectedPhotoUri.toString();
        }

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
