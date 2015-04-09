package com.chornsby.touristtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;


public class MainActivity extends ActionBarActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private MenuItem mLocationToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.registerOnSharedPreferenceChangeListener(this);

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new MainFragment())
                    .commit();
        }
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
