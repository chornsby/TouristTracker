package com.chornsby.touristtracker;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.chornsby.touristtracker.actionbar.tabs.TabFragmentPagerAdapter;
import com.chornsby.touristtracker.actionbar.tabs.NonDraggableViewPager;
import com.chornsby.touristtracker.data.TrackerService;
import com.chornsby.touristtracker.submit.SubmitActivity;

public class MainActivity extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private MenuItem mLocationToggle;

    private TabFragmentPagerAdapter mTabFragmentPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

        setContentView(R.layout.activity_main);

        final ActionBar actionBar = getSupportActionBar();

        if (actionBar == null) {
            throw new IllegalStateException("Action bar is missing!");
        }

        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mTabFragmentPagerAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager());

        mViewPager = (NonDraggableViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mTabFragmentPagerAdapter);

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {

            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {}

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {}
        };

        actionBar.addTab(actionBar.newTab()
                .setText(getString(R.string.tab_label_map).toUpperCase())
                .setTabListener(tabListener));

        actionBar.addTab(actionBar.newTab()
                .setText(getString(R.string.tab_label_notes).toUpperCase())
                .setTabListener(tabListener));

        actionBar.addTab(actionBar.newTab()
                .setText(getString(R.string.tab_label_survey).toUpperCase())
                .setTabListener(tabListener));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        mLocationToggle = menu.findItem(R.id.action_toggle_tracking);

        updateLocationToggleIcon();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_submit) {
            startActivity(new Intent(this, SubmitActivity.class));
            return true;
        }

        if (id == R.id.action_launch_tutorial) {
            startActivity(new Intent(this, TutorialActivity.class));
            return true;
        }

        if (id == R.id.action_toggle_tracking) {

            if (!Utility.isTracking(this)) {

                if (Utility.isLocationPermissionRequired(this)) {
                    Utility.requestLocationPermissions(this);
                } else {
                    startLocationTracking();
                }

            } else {
                stopLocationTracking();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int i = 0; i < permissions.length; i++) {
            String permission = permissions[i];
            boolean isGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;

            if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION) && isGranted) {
                startLocationTracking();
            }
        }
    }

    private void startLocationTracking() {
        Intent intent = new Intent(this, TrackerService.class);
        startService(intent);
    }

    private void stopLocationTracking() {
        Intent intent = new Intent(this, TrackerService.class);
        intent.setAction(TrackerService.ACTION_CLOSE);
        startService(intent);
    }

    private void updateLocationToggleIcon() {
        updateLocationToggleIcon(Utility.isTracking(this));
    }

    private void updateLocationToggleIcon(boolean isTracking) {
        // Do nothing if the menu is not initialised
        if (mLocationToggle == null) {
            return;
        }

        if (isTracking) {
            mLocationToggle.setIcon(R.drawable.ic_action_location_on);
        } else {
            mLocationToggle.setIcon(R.drawable.ic_action_location_off);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Update the button icon depending on the value of the SharedPreference
        if (key.equals(getString(R.string.pref_track_location))) {

            boolean isTracking = sharedPreferences.getBoolean(key, true);
            updateLocationToggleIcon(isTracking);

            int resourceId;

            if (isTracking) {
                resourceId = R.string.notif_tracking_started;
            } else {
                resourceId = R.string.notif_tracking_stopped;
            }

            // Inform the user of the change
            Snackbar.make(findViewById(R.id.pager), resourceId, Snackbar.LENGTH_SHORT).show();
        }
    }
}
