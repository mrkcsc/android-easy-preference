package com.miguelgaeta.easyprefs.app;

import android.app.Application;

import com.miguelgaeta.easyprefs.EasyPrefs;

/**
 * Created by mrkcsc on 3/28/15.
 */
public class TestApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        EasyPrefs.getConfig().init(this);
    }
}
