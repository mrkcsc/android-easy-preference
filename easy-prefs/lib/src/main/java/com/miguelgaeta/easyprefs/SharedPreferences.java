package com.miguelgaeta.easyprefs;

import android.preference.PreferenceManager;
import android.util.Pair;

import java.util.List;

import lombok.NonNull;

/**
 * Simple wrapper over the standard shared
 * preferences android object that
 * performs a subset of operations needed
 * by easy prefs.
 */
public class SharedPreferences {

    private static android.content.SharedPreferences get() {

        return PreferenceManager.getDefaultSharedPreferences(EasyPrefs.getConfig().getContext());
    }

    private static android.content.SharedPreferences.Editor getEditor() {

        return get().edit();
    }

    public static String getString(@NonNull String key) {

        return get().getString(key, null);
    }

    public static void setString(@NonNull List<Pair<String, String>> keyValuePairs) {

        android.content.SharedPreferences.Editor editor = getEditor();

        for (Pair<String, String> keyValuePair : keyValuePairs) {

            editor.putString(keyValuePair.first, keyValuePair.second);
        }

        editor.apply();
    }

    public static void removeString(@NonNull String key) {

        getEditor().remove(key).apply();
    }
}
