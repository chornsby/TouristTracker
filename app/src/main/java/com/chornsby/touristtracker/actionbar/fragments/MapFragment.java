package com.chornsby.touristtracker.actionbar.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chornsby.touristtracker.R;
import com.chornsby.touristtracker.Utility;
import com.chornsby.touristtracker.data.TrackerService;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.model.MapViewPosition;
import org.mapsforge.map.reader.MapDataStore;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import java.io.File;

public class MapFragment extends Fragment {

    private static final String LOG_TAG = MapFragment.class.getSimpleName();

    private static final String PREF_MAP_LATITUDE = "pref_map_latitude";
    private static final String PREF_MAP_LONGITUDE = "pref_map_longitude";
    private static final String PREF_MAP_ZOOM_LEVEL = "pref_map_zoom_level";

    private MapView mMapView;
    private TileCache mTileCache;
    private TileRendererLayer mTileRendererLayer;

    public MapFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialise the GraphicsFactory for Mapsforge
        AndroidGraphicFactory.createInstance(getActivity().getApplication());
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = (MapView) rootView.findViewById(R.id.map_view);
        mMapView.setClickable(true);
        mMapView.setBuiltInZoomControls(false);
        mMapView.getMapScaleBar().setVisible(true);

        loadMapViewPosition();

        mTileCache = AndroidUtil.createTileCache(
                getActivity(),
                "mapcache",
                mMapView.getModel().displayModel.getTileSize(),
                1f,
                mMapView.getModel().frameBufferModel.getOverdrawFactor()
        );

        File mapFile = Utility.getMapFile(getActivity());

        if (mapFile == null) {
            throw new NullPointerException("MapFile was null!");
        }

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

        if (Utility.isTracking(getActivity())) {
            // Start Service without defined action in order to respect SharedPreferences
            Intent intent = new Intent(getActivity(), TrackerService.class);
            getActivity().startService(intent);
        }

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMapViewPosition();
    }

    @Override
    public void onPause() {
        super.onPause();
        saveMapViewPosition();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mTileRendererLayer.onDestroy();
        mTileCache.destroy();
        mMapView.destroy();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        AndroidGraphicFactory.clearResourceMemoryCache();
    }

    private void loadMapViewPosition() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        double latitude = preferences.getFloat(PREF_MAP_LATITUDE, 60.1708f);
        double longitude = preferences.getFloat(PREF_MAP_LONGITUDE, 24.9375f);
        byte zoomLevel = (byte) preferences.getInt(PREF_MAP_ZOOM_LEVEL, 16);

        LatLong latLong = new LatLong(latitude, longitude);

        MapViewPosition mapViewPosition = mMapView.getModel().mapViewPosition;
        mapViewPosition.setCenter(latLong);
        mapViewPosition.setZoomLevel(zoomLevel);
    }

    private void saveMapViewPosition() {
        MapViewPosition mapViewPosition = mMapView.getModel().mapViewPosition;

        LatLong latLong = mapViewPosition.getCenter();
        byte zoomLevel = mapViewPosition.getZoomLevel();

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();

        editor.putFloat(PREF_MAP_LATITUDE, (float) latLong.latitude);
        editor.putFloat(PREF_MAP_LONGITUDE, (float) latLong.longitude);
        editor.putInt(PREF_MAP_ZOOM_LEVEL, (int) zoomLevel);

        editor.apply();
    }
}
