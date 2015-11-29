package com.chornsby.touristtracker;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
                "Here is a message from the researcher (video coming soon!)",
                R.drawable.ic_instructor,
                MATERIAL_INDIGO
        ));
        addSlide(AppIntroFragment.newInstance(
                "Tracking",
                "Tap the location button to turn location tracking on and off whenever you like",
                R.drawable.ic_world,
                MATERIAL_INDIGO
        ));
        addSlide(AppIntroFragment.newInstance(
                "Notes",
                "Use the Notes tab to record information about your trip to Helsinki",
                R.drawable.ic_city,
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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
