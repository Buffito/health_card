package com.theodoroskotoufos.healthcard.facetec.facetecapp;

import android.os.Handler;
import android.util.Log;

public class FacetecAppUtilities {

    private final FacetecAppActivity facetecAppActivity;
    private Handler themeTransitionTextHandler;

    public FacetecAppUtilities(FacetecAppActivity activity) {
        facetecAppActivity = activity;
    }

    public void showSessionTokenConnectionText() {
        themeTransitionTextHandler = new Handler();
        themeTransitionTextHandler.postDelayed(() -> facetecAppActivity.activityMainBinding.themeTransitionText.animate().alpha(1f).setDuration(600), 3000);
    }

    public void hideSessionTokenConnectionText() {
        themeTransitionTextHandler.removeCallbacksAndMessages(null);
        themeTransitionTextHandler = null;
        facetecAppActivity.runOnUiThread(() -> facetecAppActivity.activityMainBinding.themeTransitionText.animate().alpha(0f).setDuration(600));
    }

    // Disable buttons to prevent hammering, fade out main interface elements, and shuffle the guidance images.
    public void fadeOutMainUIAndPrepareForFaceTecSDK(final Runnable callback) {

        facetecAppActivity.runOnUiThread(() -> {

            facetecAppActivity.activityMainBinding.themeTransitionImageView.animate().alpha(1f).setDuration(600).start();
            facetecAppActivity.activityMainBinding.contentLayout.animate().alpha(0f).setDuration(600).withEndAction(callback).start();
        });
    }

    public void fadeInMainUI() {

        facetecAppActivity.runOnUiThread(() -> {
            facetecAppActivity.activityMainBinding.contentLayout.animate().alpha(1f).setDuration(600);
            facetecAppActivity.activityMainBinding.themeTransitionImageView.animate().alpha(0f).setDuration(600);
        });
    }

    public void displayStatus(final String statusString) {
        Log.d("FaceTecSDKSampleApp", statusString);
        facetecAppActivity.runOnUiThread(() -> facetecAppActivity.activityMainBinding.statusLabel.setText(statusString));
    }

    public void handleErrorGettingServerSessionToken() {
        hideSessionTokenConnectionText();
        displayStatus("Session could not be started due to an unexpected issue during the network request.");
        fadeInMainUI();
    }

}
