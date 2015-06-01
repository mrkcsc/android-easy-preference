package com.miguelgaeta.easypreference;

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
public class MGPreferenceConfig {

    @Getter(AccessLevel.PACKAGE)
    private Context context;

    @Getter(AccessLevel.PACKAGE) @Setter
    private Gson gson = new GsonBuilder().create();

    @Getter(AccessLevel.PACKAGE)
    private int applicationVersionCode;

    public void init(Context context) {

        if (context instanceof Application) {

            this.context = context;

            this.applicationVersionCode = getApplicationVersionCode(context);

        } else {

            throw new RuntimeException("An application context is required.");
        }
    }

    private static int getApplicationVersionCode(Context context) {

        PackageManager manager = context.getPackageManager();

        try {

            // Fetch current version code from the current package.
            return manager.getPackageInfo(context.getPackageName(), 0).versionCode;

        } catch (PackageManager.NameNotFoundException e) {

            return 0;
        }
    }
}
