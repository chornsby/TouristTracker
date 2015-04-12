package com.chornsby.touristtracker;

import android.app.Activity;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
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

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;

import org.mapsforge.core.graphics.Bitmap;
import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.Layers;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.overlay.Marker;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapDataStore;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;
import java.io.IOException;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_TAKE_NOTE = 2;

    private MapView mMapView;
    private TileCache mTileCache;
    private TileRendererLayer mTileRendererLayer;

    private LocationObserver mLocationObserver;

    private Uri mCurrentPhotoUri;

    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Initialise the GraphicsFactory for Mapsforge
        AndroidGraphicFactory.createInstance(getActivity().getApplication());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.map_view);
        mMapView.setClickable(true);
        mMapView.setBuiltInZoomControls(false);
        mMapView.getMapScaleBar().setVisible(true);

        mTileCache = AndroidUtil.createTileCache(
                getActivity(),
                "mapcache",
                mMapView.getModel().displayModel.getTileSize(),
                1f,
                mMapView.getModel().frameBufferModel.getOverdrawFactor()
        );

        FloatingActionButton addPhoto = (FloatingActionButton) rootView.findViewById(R.id.add_photo_fab);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile;
                try {
                    photoFile = Utility.createImageFile(getActivity());
                } catch (IOException e) {
                    Crouton.makeText(
                            getActivity(), R.string.error_camera_access, Style.ALERT
                    );
                    return;
                }
                mCurrentPhotoUri = Uri.fromFile(photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCurrentPhotoUri);
                startActivityForResult(intent, REQUEST_TAKE_PHOTO);
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

        if (Utility.isTracking(getActivity())) {
            // Start Service without defined action in order to respect SharedPreferences
            Intent intent = new Intent(getActivity(), LocationService.class);
            getActivity().startService(intent);
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

        mMapView.getModel().mapViewPosition.setZoomLevel((byte) 16);

        File mapFile = Utility.getMapFile(getActivity());
        if (mapFile == null) return;

        MapDataStore mapDataStore = new MapFile(mapFile);

        mTileRendererLayer = new TileRendererLayer(
                mTileCache,
                mapDataStore,
                mMapView.getModel().mapViewPosition,
                false,
                true,
                AndroidGraphicFactory.INSTANCE
        );
        mTileRendererLayer.setXmlRenderTheme(InternalRenderTheme.OSMARENDER);

        mMapView.getLayerManager().getLayers().add(mTileRendererLayer);

        centerMapAtLatestLocation();
    }

    @Override
    public void onResume() {
        super.onResume();

        mLocationObserver = new LocationObserver(new Handler());

        getActivity().getContentResolver().registerContentObserver(
                TrackerContract.BASE_CONTENT_URI, true, mLocationObserver
        );
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().getContentResolver().unregisterContentObserver(mLocationObserver);

        mLocationObserver = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.getLayerManager().getLayers().remove(mTileRendererLayer);
        mTileRendererLayer.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTileCache.destroy();
        mMapView.getModel().mapViewPosition.destroy();
        mMapView.destroy();
        AndroidGraphicFactory.clearResourceMemoryCache();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            Utility.addToGallery(getActivity(), mCurrentPhotoUri);
            Crouton.makeText(
                    getActivity(), R.string.photo_saved, Style.CONFIRM
            ).show();
        }
    }

    private LatLong getLatestLatLong() {
        // Retrieve relevant records from the database
        Uri uri = TrackerContract.LocationEntry.CONTENT_URI;
        String[] projection = {
                TrackerContract.LocationEntry.COLUMN_LATITUDE,
                TrackerContract.LocationEntry.COLUMN_LONGITUDE,
                TrackerContract.LocationEntry.COLUMN_ACCURACY,
        };

        Cursor c = getActivity().getContentResolver().query(
                uri,
                projection,
                null,
                null,
                null
        );

        final int LATITUDE_INDEX = c.getColumnIndex(TrackerContract.LocationEntry.COLUMN_LATITUDE);
        final int LONGITUDE_INDEX = c.getColumnIndex(TrackerContract.LocationEntry.COLUMN_LONGITUDE);

        LatLong latLong;
        double latitude;
        double longitude;

        // Use latest stored data
        if (c.moveToLast()) {
            latitude = c.getDouble(LATITUDE_INDEX);
            longitude = c.getDouble(LONGITUDE_INDEX);

        // Or the coordinates for Helsinki
        } else {
            latitude = 60.1708;
            longitude = 24.9375;
        }

        latLong = new LatLong(latitude, longitude);

        c.close();

        return latLong;
    }

    private void centerMapAtLatestLocation() {
        LatLong latLong = getLatestLatLong();
        mMapView.getModel().mapViewPosition.setCenter(latLong);

        Drawable drawable = getActivity().getResources().getDrawable(R.drawable.ic_action_close_red);

        if (drawable == null) return;

        Bitmap marker = AndroidGraphicFactory.convertToBitmap(drawable);

        Layers layers = mMapView.getLayerManager().getLayers();

        // Remove the marker if it has already been placed
        if (layers.size() == 2) {
            layers.remove(1);
        }

        layers.add(new Marker(
                latLong,
                marker,
                0,
                -marker.getHeight() / 2
        ));
    }

    private class LocationObserver extends ContentObserver {

        public LocationObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            centerMapAtLatestLocation();
        }
    }
}
