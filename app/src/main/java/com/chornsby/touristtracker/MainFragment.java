package com.chornsby.touristtracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.esri.android.map.MapOptions;
import com.esri.android.map.MapView;
import com.esri.android.map.event.OnStatusChangedListener;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment
        implements ConnectionCallbacks, OnConnectionFailedListener{

    private static final int ADD_PHOTO_REQUEST_CODE = 1;
    private static final int ADD_NOTE_REQUEST_CODE = 2;

    public MainFragment() {
    }

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    MapView mMapView;
    FloatingActionButton mAddPhoto;
    FloatingActionButton mAddNote;
    LocationManager mLocationManager;

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
                    mGoogleApiClient.connect();
                }
            }
        });

        mAddPhoto = (FloatingActionButton) rootView.findViewById(R.id.add_photo_fab);
        mAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, ADD_PHOTO_REQUEST_CODE);
            }
        });

        mAddNote = (FloatingActionButton) rootView.findViewById(R.id.add_note_fab);
        mAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Currently not implemented.", Toast.LENGTH_SHORT)
                        .show();
            }
        });

        buildGoogleApiClient();

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

    @Override
    public void onResume() {
        super.onResume();
        promptToActivateGPS();
    }

    private void promptToActivateGPS() {
        mLocationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Location Services Not Active");
            builder.setMessage("Please enable Location Services and GPS");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    // Show location settings when the user acknowledges the alert dialog
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    getActivity().finish();
                }
            });
            builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    getActivity().finish();
                }
            });
            Dialog alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            MapOptions mapOptions = new MapOptions(
                    MapOptions.MapType.STREETS,
                    mLastLocation.getLatitude(),
                    mLastLocation.getLongitude(),
                    18
            );
            mMapView.setMapOptions(mapOptions);
        }

        Toast.makeText(getActivity(), "Updated location", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getActivity(), "suspended", Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(getActivity(), "failed", Toast.LENGTH_SHORT).show();
    }
}
