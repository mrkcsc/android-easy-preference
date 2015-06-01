package com.miguelgaeta.easyprefs;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by mrkcsc on 3/9/15.
 */
@SuppressWarnings("UnusedDeclaration")
public class Config {

    private Context context;

    @Getter(AccessLevel.PACKAGE) @Setter
    private Gson gson = new GsonBuilder().create();

    public void init(Context context) {

        if (context instanceof Application) {

            this.context = context;

        } else {

            throw new RuntimeException("An application context is required.");
        }
    }

    static Context getContext() {

        return EasyPrefs.getConfig().context;
    }

    /**
     * Application version code is used as an optional
     * cache breaker for preferences.  This can be
     * helpful for making breaking changes to
     * the underlying structures.
     */
    public static int getApplicationVersionCode() {

        PackageManager manager = EasyPrefs.getConfig().context.getPackageManager();

        try {

            // Fetch current version code from the current package.
            return manager.getPackageInfo(EasyPrefs.getConfig().context.getPackageName(), 0).versionCode;

        } catch (PackageManager.NameNotFoundException e) {

            return 0;
        }
    }
}
