package com.chornsby.touristtracker.actionbartabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabFragmentPagerAdapter extends FragmentPagerAdapter {

    public static final int MAP_FRAGMENT = 0;
    public static final int SURVEY_FRAGMENT = 1;
    public static final int SUBMIT_FRAGMENT = 2;

    public TabFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case MAP_FRAGMENT:
                return new MapFragment();
            case SURVEY_FRAGMENT:
                return new SurveysFragment();
            case SUBMIT_FRAGMENT:
                return new SubmitFragment();
        }
        throw new IllegalArgumentException("Tab position out of range");
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // TODO: Don't hardcode these strings
        switch (position) {
            case MAP_FRAGMENT:
                return "MAP";
            case SURVEY_FRAGMENT:
                return "SURVEYS";
            case SUBMIT_FRAGMENT:
                return "SUBMIT";
        }
        throw new IllegalArgumentException("Tab position out of range");
    }

    @Override
    public int getCount() {
        return 3;
    }
}
