package com.chornsby.touristtracker;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.chornsby.touristtracker.data.TrackerContract;
import com.getbase.floatingactionbutton.FloatingActionButton;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapDataStore;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment {

    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    private static final int ADD_PHOTO_REQUEST_CODE = 1;
    private static final int ADD_NOTE_REQUEST_CODE = 2;

    private MapView mMapView;
    private TileCache mTileCache;
    private TileRendererLayer mTileRendererLayer;

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
    public void onStart() {
        super.onStart();

        centerMapAtLatestLocation();
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
                TrackerContract.LocationEntry.COLUMN_ACCURACY,
        };

        // Select only Location entries that are accurate to within 30m
        String selection = TrackerContract.LocationEntry.COLUMN_ACCURACY + " < ?";
        String[] selectionArgs = {"30"};

        Cursor c = getActivity().getContentResolver().query(
                uri,
                projection,
                selection,
                selectionArgs,
                null
        );

        final int LATITUDE_INDEX = c.getColumnIndex(TrackerContract.LocationEntry.COLUMN_LATITUDE);
        final int LONGITUDE_INDEX = c.getColumnIndex(TrackerContract.LocationEntry.COLUMN_LONGITUDE);

        if (c.moveToLast()) {
            double latitude = c.getDouble(LATITUDE_INDEX);
            double longitude = c.getDouble(LONGITUDE_INDEX);

            mMapView.getModel().mapViewPosition.setCenter(new LatLong(latitude, longitude));
        }

        c.close();
    }
}
