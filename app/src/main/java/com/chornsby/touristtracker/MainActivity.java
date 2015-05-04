package com.chornsby.touristtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.chornsby.touristtracker.navigationdrawer.NavDrawerListAdapter;
import com.chornsby.touristtracker.settings.LocationSettingsHelper;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class MainActivity extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private MenuItem mLocationToggle;
    private ExpandableListView mDrawerList;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private NavDrawerListAdapter mAdapter;

    private LocationSettingsHelper mSettingsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MapFragment())
                    .commit();
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerList = (ExpandableListView) findViewById(R.id.nav_list);
        mDrawerList.setGroupIndicator(null);
        mDrawerList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (groupPosition != 0) {
                    return true;
                }

                switch (childPosition) {
                    case 0:
                        Crouton.cancelAllCroutons();
                        Crouton.makeText(MainActivity.this, "Survey functionality coming soon...", Style.INFO).show();
                        mDrawerLayout.closeDrawers();
                        break;
                    case 1:
                        Crouton.cancelAllCroutons();
                        Crouton.makeText(MainActivity.this, "Survey functionality coming soon...", Style.INFO).show();
                        mDrawerLayout.closeDrawers();
                        break;
                }

            return true;
        }
    });
        mDrawerList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                switch (groupPosition) {
                    case 1:
                        Crouton.cancelAllCroutons();
                        Crouton.makeText(MainActivity.this, "Submit data coming soon...", Style.INFO).show();
                        mDrawerLayout.closeDrawers();
                        break;
                    case 2:
                        Crouton.cancelAllCroutons();
                        Crouton.makeText(MainActivity.this, "Help coming soon...", Style.INFO).show();
                        mDrawerLayout.closeDrawers();
                        break;
                }
                return false;
            }
        });

        setupDrawer();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void setupDrawer() {
        mAdapter = new NavDrawerListAdapter(this);
        mDrawerList.setAdapter(mAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open,
                R.string.drawer_close) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                supportInvalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_toggle_tracking) {
            // Create an Intent to toggle tracking
            Intent intent = new Intent(this, LocationService.class);

            // Add Action if toggling should stop tracking
            if (Utility.isTracking(this)) {
                intent.setAction(LocationService.ACTION_CLOSE);
            // And request best Location accuracy if toggling starts tracking
            } else {
                mSettingsHelper = new LocationSettingsHelper(this);
                mSettingsHelper.checkSettings();
            }

            startService(intent);

            return true;
        }

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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

            // Cancel any existing alerts
            Crouton.cancelAllCroutons();

            // Inform the user of the change
            if (isTracking) {
                Crouton.makeText(this, "Location tracking started.", Style.CONFIRM).show();
            } else {
                Crouton.makeText(this, "Location tracking stopped.", Style.ALERT).show();
            }
        }
    }
}
