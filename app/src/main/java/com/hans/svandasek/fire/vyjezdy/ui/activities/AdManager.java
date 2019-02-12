package com.hans.svandasek.fire.vyjezdy.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.AdRequest;

public class AdManager {
    // Static fields are shared between all instances.
    private static InterstitialAd interstitialAd;

    private static boolean isInterAdsShowed = false;
    private Activity activity;
    private String AD_UNIT_ID;

    AdManager(Activity activity, String AD_UNIT_ID) {

        this.activity = activity;
        this.AD_UNIT_ID = AD_UNIT_ID;
        createAd();
    }

    public void createAd() {
        // Create an ad.
        interstitialAd = new InterstitialAd(activity);
        interstitialAd.setAdUnitId(AD_UNIT_ID);

        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("819BF3BEF14F000525869D2645FB83F3")
                .build();

        // Load the interstitial ad.
        interstitialAd.loadAd(adRequest);


        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Log.e("ADVERTISEMENT", "Ad closed");
                AdRequest adRequest = new AdRequest.Builder()
                        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .addTestDevice("819BF3BEF14F000525869D2645FB83F3")
                        .build();

                // Load the interstitial ad.
                interstitialAd.loadAd(adRequest);
            }
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                Log.e("ADVERTISEMENT", "Ad LOADED");

            }

        });
    }

    public static InterstitialAd getAd() {
        if(interstitialAd != null && interstitialAd.isLoaded() && !isInterAdsShowed) {
            isInterAdsShowed = true;
            return interstitialAd;
        }
        else return null;
    }
}
