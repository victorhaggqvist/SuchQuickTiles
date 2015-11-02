package com.snilius.suchquick.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.snilius.aboutit.AboutIt;
import com.snilius.aboutit.L;
import com.snilius.suchquick.BuildConfig;
import com.snilius.suchquick.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new AboutIt(this).app(R.string.app_name)
                .buildInfo(BuildConfig.DEBUG, BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME)
                .description(R.string.description)
                .copyright("Snilius")
                .year(2015)
                .libLicense("AboutIt", "Victor Häggqvist", L.AP2, "https://github.com/victorhaggqvist/aboutit")
                .libLicense("BroadcastTileSupport", "kcoppock", L.AP2, "https://github.com/kcoppock/BroadcastTileSupport")
                .libLicense("CyanogenMod Platform SDK", "The CyanogenMod Project", L.AP2, "https://github.com/CyanogenMod/cm_platform_sdk")
                .libLicense("greenDAO", "greenrobot", L.AP2, "https://github.com/greenrobot/greenDAO")
                .libLicense("Timber", "Jake Wharton", L.AP2, "https://github.com/JakeWharton/timber")
                .libLicense("Butter Knife", "Jake Wharton", L.AP2, "https://github.com/JakeWharton/butterknife")
                .libLicense("Android Support Library", "The Android Open Source Project", L.AP2, "https://android.googlesource.com/platform/frameworks/support/")

                .toTextView(R.id.text);
    }
}
