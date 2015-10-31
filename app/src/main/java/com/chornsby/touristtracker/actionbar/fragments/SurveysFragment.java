package com.chornsby.touristtracker.actionbar.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.chornsby.touristtracker.R;

public class SurveysFragment extends Fragment {

    private TextView applicantId;
    private Button copy;
    private Button backgroundSurvey;
    private Button walkabilitySurvey;

    public SurveysFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_surveys, container, false);

        applicantId = (TextView) rootView.findViewById(R.id.applicant_id);
        copy = (Button) rootView.findViewById(R.id.copy);
        backgroundSurvey = (Button) rootView.findViewById(R.id.backgroundSurveyButton);
        walkabilitySurvey = (Button) rootView.findViewById(R.id.walkabilitySurveyButton);

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager)
                        getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("applicant id", applicantId.getText());
                clipboard.setPrimaryClip(clip);

                Snackbar.make(
                        getActivity().findViewById(R.id.pager),
                        R.string.notif_copied_to_clipboard,
                        Snackbar.LENGTH_SHORT
                ).show();
            }
        });

        backgroundSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage("https://elomake.helsinki.fi/lomakkeet/63793/lomake.html");
            }
        });
        walkabilitySurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWebPage("https://elomake.helsinki.fi/lomakkeet/63802/lomake.html");
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
}
