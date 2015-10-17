package com.chornsby.touristtracker.actionbar.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.chornsby.touristtracker.R;

public class SurveysFragment extends Fragment {

    private Button backgroundSurvey;
    private Button walkabilitySurvey;

    public SurveysFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_surveys, container, false);

        backgroundSurvey = (Button) rootView.findViewById(R.id.backgroundSurveyButton);
        walkabilitySurvey = (Button) rootView.findViewById(R.id.walkabilitySurveyButton);

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
