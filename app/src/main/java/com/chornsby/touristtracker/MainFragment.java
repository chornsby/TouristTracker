package com.chornsby.touristtracker;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    private static final int ADD_PHOTO_REQUEST_CODE = 1;
    private static final int ADD_NOTE_REQUEST_CODE = 2;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        FloatingActionButton addPhoto = (FloatingActionButton) rootView.findViewById(R.id.add_photo_fab);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, ADD_PHOTO_REQUEST_CODE);
            }
        });

        FloatingActionButton addNote = (FloatingActionButton) rootView.findViewById(R.id.add_note_fab);
        addNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Currently not implemented.", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        // Start Service without defined action in order to respect SharedPreferences
        Intent intent = new Intent(getActivity(), LocationService.class);
        getActivity().startService(intent);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_PHOTO_REQUEST_CODE) {
            Toast.makeText(
                    getActivity(), "Returned from the camera app.", Toast.LENGTH_SHORT
            ).show();
        } else if (requestCode == ADD_NOTE_REQUEST_CODE) {
            Toast.makeText(
                    getActivity(), "Received some text!", Toast.LENGTH_SHORT
            ).show();
        }
    }
}
