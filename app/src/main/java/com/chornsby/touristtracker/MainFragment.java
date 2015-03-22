package com.chornsby.touristtracker;

import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chornsby.touristtracker.data.TrackerContract;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnStatusChangedListener;
import com.getbase.floatingactionbutton.FloatingActionButton;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    private static final int ADD_PHOTO_REQUEST_CODE = 1;
    private static final int ADD_NOTE_REQUEST_CODE = 2;

    private MapView mMapView;
    private LocationObserver mLocationObserver;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.setOnStatusChangedListener(new OnStatusChangedListener() {
            private static final long serialVersionUID = 1L;

            @Override
            public void onStatusChanged(Object source, STATUS status) {
                if (OnStatusChangedListener.STATUS.INITIALIZED == status && source == mMapView) {
                    // TODO: Fix bug where `centerMapAtLatestLocation` is called twice
                    centerMapAtLatestLocation();
                    mLocationObserver = new LocationObserver(null);
                    getActivity().getContentResolver().registerContentObserver(
                            TrackerContract.LocationEntry.CONTENT_URI,
                            true,
                            mLocationObserver
                    );
                }
            }
        });

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
        Intent intent = new Intent(getActivity(), TrackerService.class);
        getActivity().startService(intent);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().getContentResolver().unregisterContentObserver(mLocationObserver);
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

    private void centerMapAtLatestLocation() {
        // Retrieve relevant records from the database
        Uri uri = TrackerContract.LocationEntry.CONTENT_URI;
        String[] projection = {
                TrackerContract.LocationEntry.COLUMN_LATITUDE,
                TrackerContract.LocationEntry.COLUMN_LONGITUDE,
        };

        Cursor c = getActivity().getContentResolver().query(
                uri,
                projection,
                null,
                null,
                null
        );
        if (c.moveToLast()) {
            double latitude = c.getDouble(c.getColumnIndex(TrackerContract.LocationEntry.COLUMN_LATITUDE));
            double longitude = c.getDouble(c.getColumnIndex(TrackerContract.LocationEntry.COLUMN_LONGITUDE));
            mMapView.centerAt(latitude, longitude, true);
        }
        c.close();
    }

    private class LocationObserver extends ContentObserver {

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public LocationObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            centerMapAtLatestLocation();
        }
    }
}
