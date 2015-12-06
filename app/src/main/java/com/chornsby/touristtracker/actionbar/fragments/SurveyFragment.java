package com.chornsby.touristtracker.actionbar.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chornsby.touristtracker.R;

import java.util.Random;

public class SurveyFragment extends Fragment {

    private TextView mResearchNumber;
    private Button copy;
    private Button respondButton;

    public SurveyFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_surveys, container, false);

        mResearchNumber = (TextView) rootView.findViewById(R.id.research_number);
        copy = (Button) rootView.findViewById(R.id.copy);
        respondButton = (Button) rootView.findViewById(R.id.response_button);

        setApplicantId();

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager)
                        getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("research_number", mResearchNumber.getText());
                clipboard.setPrimaryClip(clip);

                Snackbar.make(
                        getActivity().findViewById(R.id.pager),
                        R.string.notif_copied_to_clipboard,
                        Snackbar.LENGTH_SHORT
                ).show();
            }
        });

        respondButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage("https://elomake.helsinki.fi/lomakkeet/65716/lomake.html");
            }
        });

        return rootView;
    }

    private void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void setApplicantId() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        int applicantId = sharedPreferences.getInt(getContext().getString(R.string.pref_applicant_id), 0);
        boolean isUnset = applicantId == 0;

        if (isUnset) {
            Random r = new Random();
            applicantId = r.nextInt(10000) + 10000;

            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
            editor.putInt(getString(R.string.pref_applicant_id), applicantId);
            editor.apply();
        }

        mResearchNumber.setText(Integer.toString(applicantId));
    }
}
