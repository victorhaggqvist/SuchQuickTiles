package com.snilius.suchquick;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.snilius.suchquick.data.DbConnection;

/**
 * @author Victor Häggqvist
 * @since 10/31/15
 */
public class SuchQuick extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG)
            Stetho.initializeWithDefaults(this);

        DbConnection.setContext(this);

    }
}
