package com.chornsby.touristtracker.settings;


import android.app.Activity;

import com.chornsby.touristtracker.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

public class GooglePlayServicesHelper {

    private Activity mActivity;

    public GooglePlayServicesHelper(Activity activity) {
        mActivity = activity;
    }

    public int getErrorCode() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
    }

    public boolean isAvailable() {
        return getErrorCode() == ConnectionResult.SUCCESS;
    }

    public boolean checkPlayServices() {
        if (isAvailable()) return true;

        final int errorCode = getErrorCode();

        GooglePlayServicesUtil.getErrorDialog(
                errorCode,
                mActivity,
                Constants.REQUEST_RESOLVE_ERROR
        ).show();

        return false;
    }
}
