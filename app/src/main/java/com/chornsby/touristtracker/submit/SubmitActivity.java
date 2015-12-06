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
import android.widget.CheckBox;
import android.widget.EditText;

import com.chornsby.touristtracker.R;
import com.chornsby.touristtracker.Utility;

public class SubmitActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private EditText mEmailEditText;
    private CheckBox mConfirmParticipationLottery;
    private CheckBox mConfirmParticipationTAK;
    private Button mSubmitButton;

    private static final String CONFIRM_PARTICIPATION_LOTTERY = "confirm_participation_lottery";
    private static final String CONFIRM_PARTICIPATION_TAK = "confirm_participation_tak";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);

        mEmailEditText = (EditText) findViewById(R.id.user_email_edit_text);
        mConfirmParticipationLottery = (CheckBox) findViewById(R.id.confirm_participation_lottery);
        mConfirmParticipationTAK = (CheckBox) findViewById(R.id.confirm_participation_tak);
        mSubmitButton = (Button) findViewById(R.id.button);

        final boolean isUploading = Utility.isUploading(this);

        mConfirmParticipationLottery.setEnabled(!isUploading);
        mConfirmParticipationTAK.setEnabled(!isUploading);
        mSubmitButton.setEnabled(!isUploading);

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
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(CONFIRM_PARTICIPATION_LOTTERY, mConfirmParticipationLottery.isChecked());
        outState.putBoolean(CONFIRM_PARTICIPATION_TAK, mConfirmParticipationTAK.isChecked());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mConfirmParticipationLottery.setChecked(savedInstanceState.getBoolean(CONFIRM_PARTICIPATION_LOTTERY, false));
        mConfirmParticipationTAK.setChecked(savedInstanceState.getBoolean(CONFIRM_PARTICIPATION_TAK, false));
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
        boolean participateLottery = mConfirmParticipationLottery.isChecked();
        boolean participateTAK = mConfirmParticipationTAK.isChecked();

        Intent fileUpload = new Intent(this, DataUploadService.class);
        fileUpload.putExtra(DataUploadService.EXTRA_USER_EMAIL, userEmail);
        fileUpload.putExtra(DataUploadService.EXTRA_PARTICIPATE_LOTTERY, participateLottery);
        fileUpload.putExtra(DataUploadService.EXTRA_PARTICIPATE_TAK, participateTAK);
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
