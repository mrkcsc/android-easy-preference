package com.miguelgaeta.easyprefs;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Created by mrkcsc on 3/9/15.
 */
@SuppressWarnings("UnusedDeclaration")
public class EasyPrefsConfig {

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

    /**
     * Fetch the native android shared
     * preferences object.
     */
    private static SharedPreferences getSharedPreferences() {

        return PreferenceManager.getDefaultSharedPreferences(EasyPrefs.getConfig().context);
    }

    public static String getSharedPreferencesString(@NonNull String key) {

        return getSharedPreferences().getString(key, null);
    }

    public static void setSharedPreferencesString(@NonNull List<Pair<String, String>> keyValuePairs) {

        SharedPreferences.Editor editor = getSharedPreferences().edit();

        for (Pair<String, String> keyValuePair : keyValuePairs) {

            editor.putString(keyValuePair.first, keyValuePair.second);
        }

        editor.apply();
    }

    public static void removeSharedPreferencesKey(@NonNull String key) {

        getSharedPreferences().edit().remove(key).apply();
    }
}
