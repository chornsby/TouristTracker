package com.chornsby.touristtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.chornsby.touristtracker.actionbar.tabs.TabFragmentPagerAdapter;
import com.chornsby.touristtracker.actionbar.tabs.NonDraggableViewPager;
import com.chornsby.touristtracker.settings.LocationSettingsHelper;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class MainActivity extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private MenuItem mLocationToggle;

    private TabFragmentPagerAdapter mTabFragmentPagerAdapter;
    private ViewPager mViewPager;

    private LocationSettingsHelper mSettingsHelper;

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
                .setText(getString(R.string.tab_label_surveys).toUpperCase())
                .setTabListener(tabListener));

        actionBar.addTab(actionBar.newTab()
                .setText(getString(R.string.tab_label_submit).toUpperCase())
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
