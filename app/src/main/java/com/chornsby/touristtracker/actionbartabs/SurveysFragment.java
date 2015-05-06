package com.chornsby.touristtracker.actionbartabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.chornsby.touristtracker.R;
import com.chornsby.touristtracker.survey.FirstDaySurveyActivity;

public class SurveysFragment extends Fragment {

    private ListView mListView;

    public SurveysFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_surveys, container, false);

        String[] surveys = getResources().getStringArray(R.array.survey_names_array);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_list_item_1, surveys
        );

        mListView = (ListView) rootView.findViewById(R.id.survey_list_view);
        mListView.setAdapter(arrayAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getActivity(), FirstDaySurveyActivity.class));
            }
        });

        return rootView;
    }
}
