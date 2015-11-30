package com.chornsby.touristtracker.actionbar.tabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.chornsby.touristtracker.actionbar.fragments.MapFragment;
import com.chornsby.touristtracker.actionbar.fragments.NotesFragment;
import com.chornsby.touristtracker.actionbar.fragments.SurveyFragment;

public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

    public static final int MAP_FRAGMENT = 0;
    public static final int NOTES_FRAGMENT = 1;
    public static final int SURVEY_FRAGMENT = 2;

    public TabFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case MAP_FRAGMENT:
                return new MapFragment();
            case NOTES_FRAGMENT:
                return new NotesFragment();
            case SURVEY_FRAGMENT:
                return new SurveyFragment();
        }
        throw new IllegalArgumentException("Tab position out of range");
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // TODO: Don't hardcode these strings
        switch (position) {
            case MAP_FRAGMENT:
                return "MAP";
            case NOTES_FRAGMENT:
                return "NOTES";
            case SURVEY_FRAGMENT:
                return "SURVEYS";
        }
        throw new IllegalArgumentException("Tab position out of range");
    }

    @Override
    public int getCount() {
        return 3;
    }
}
