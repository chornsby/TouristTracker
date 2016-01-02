package com.chornsby.touristtracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class TutorialActivity extends AppIntro {

    private static final int MATERIAL_INDIGO = Color.parseColor("#3F51B5");

    @Override
    public void init(@Nullable Bundle bundle) {
        // TODO: Extract strings for translation
        addSlide(AppIntroFragment.newInstance(
                "Welcome",
                "In this study I would like to learn about your experience travelling in Helsinki, especially using public transport and walking",
                R.drawable.ic_video,
                MATERIAL_INDIGO
        ));
        addSlide(AppIntroFragment.newInstance(
                "Tracking",
                "Tap the location button to turn location tracking on and off whenever you like",
                R.drawable.ic_location,
                MATERIAL_INDIGO
        ));
        addSlide(AppIntroFragment.newInstance(
                "Notes",
                "Use the Notes section to record information about problems and positive things you face while moving in the city. Please pay special attention to walkability and the use of public transport.",
                R.drawable.ic_pencil,
                MATERIAL_INDIGO
        ));
        addSlide(AppIntroFragment.newInstance(
                "Notes",
                "Please add a comment and take a picture when you experience a problem or positive thing while in Helsinki.",
                R.drawable.ic_camera,
                MATERIAL_INDIGO
        ));
        addSlide(AppIntroFragment.newInstance(
                "Survey",
                "Use the Survey tab to answer questions on your experience walking and using public transport as a tourist",
                R.drawable.ic_notes,
                MATERIAL_INDIGO
        ));
        addSlide(AppIntroFragment.newInstance(
                "Submit",
                "When you are ready, submit your data to us for further study",
                R.drawable.ic_upload,
                MATERIAL_INDIGO
        ));
        addSlide(AppIntroFragment.newInstance(
                "Let's get started",
                "Thank you for helping out!",
                R.drawable.ic_heart,
                MATERIAL_INDIGO
        ));
    }

    @Override
    public void onSkipPressed() {
        launchMainActivity();
    }

    @Override
    public void onNextPressed() {
    }

    @Override
    public void onDonePressed() {
        launchMainActivity();
    }

    @Override
    public void onSlideChanged() {
    }

    private void launchMainActivity() {
        setTutorialFinished();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void setTutorialFinished() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        String prefTutorialFinished = getString(R.string.pref_tutorial_finished);
        editor.putBoolean(prefTutorialFinished, true);
        editor.apply();
    }
}
