package com.chornsby.touristtracker.submit;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.chornsby.touristtracker.R;
import com.chornsby.touristtracker.Utility;

public class SubmitActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private EditText mEmailEditText;
    private Button mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        mEmailEditText = (EditText) findViewById(R.id.user_email_edit_text);
        mSubmitButton = (Button) findViewById(R.id.button);
        mSubmitButton.setEnabled(!Utility.isUploading(this));

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateUserEmail()) {
                    Snackbar.make(
                            SubmitActivity.this.findViewById(R.id.submit_activity),
                            R.string.notif_email_invalid,
                            Snackbar.LENGTH_SHORT
                    ).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(SubmitActivity.this)
                        .setTitle(R.string.confirm_data_upload)
                        .setMessage(R.string.confirm_data_upload_extra)
                        .setPositiveButton(R.string.action_upload, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startUploadService();
                            }
                        })
                        .setNegativeButton(R.string.action_cancel, null);

                builder.show();
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    private boolean validateUserEmail() {
        String userInput = mEmailEditText.getText().toString();

        if (TextUtils.isEmpty(userInput)) {
            return false;
        }

        return Patterns.EMAIL_ADDRESS.matcher(userInput).matches();
    }

    private void startUploadService() {
        String userEmail = mEmailEditText.getText().toString();

        Intent fileUpload = new Intent(this, DataUploadService.class);
        fileUpload.putExtra(DataUploadService.EXTRA_USER_EMAIL, userEmail);
        startService(fileUpload);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_uploading_data))) {
            boolean isUploading = sharedPreferences.getBoolean(key, false);
            mSubmitButton.setEnabled(!isUploading);
        }
    }
}
