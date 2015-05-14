package com.chornsby.touristtracker.actionbar.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.chornsby.touristtracker.R;
import com.chornsby.touristtracker.submit.DataUploadService;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class SubmitFragment extends Fragment {

    private EditText mEmailEditText;
    private Button mSubmitButton;

    public SubmitFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_submit, container, false);

        mEmailEditText = (EditText) rootView.findViewById(R.id.user_email_edit_text);
        mSubmitButton = (Button) rootView.findViewById(R.id.button);

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!validateUserEmail()) {
                    Crouton.makeText(
                            getActivity(), R.string.email_invalid, Style.ALERT
                    ).show();
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
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

        return rootView;
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

        Intent fileUpload = new Intent(getActivity(), DataUploadService.class);
        fileUpload.putExtra(DataUploadService.EXTRA_USER_EMAIL, userEmail);
        getActivity().startService(fileUpload);
    }
}
