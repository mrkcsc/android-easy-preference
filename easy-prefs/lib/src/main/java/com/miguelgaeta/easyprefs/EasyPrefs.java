package com.miguelgaeta.easyprefs;

import lombok.Getter;
import lombok.NonNull;

/**
 * Created by mrkcsc on 12/5/14.
 */
@SuppressWarnings("unchecked, unused")
public class EasyPrefs<T> {

    @Getter(lazy = true)
    private static final EasyPrefsConfig config = new EasyPrefsConfig();

    // Meta data object to do the persisting.
    private EasyPrefsMetaData<T> metaData;

    /**
     * Creates a new preference object that is backed
     * by the android shared preferences object.
     */
    private EasyPrefs(String key, T defaultValue, boolean cacheBreaker) {

        // Meta data does all the heavy lifting.
        metaData = new EasyPrefsMetaData<>(EasyPrefsConfig.getGson(), key, defaultValue, cacheBreaker);
    }

    /**
     * Create a new preference object backed by
     * a concrete class object.
     */
    public static <T> EasyPrefs<T> create(@NonNull String key) {

        return new EasyPrefs<>(key, null, true);
    }

    /**
     * Create a new preference object backed by
     * a concrete class object. Provide a default
     * initial value.
     */
    public static <T> EasyPrefs<T> create(@NonNull String key, T defaultValue) {

        return new EasyPrefs<>(key, defaultValue, true);
    }

    /**
     * Create a new preference object backed by
     * a concrete class object. Provide a default
     * initial value.  Specify whether cache
     * breaking behavior is desired.
     */
    public static <T> EasyPrefs<T> create(@NonNull String key, T defaultValue, boolean cacheBreaker) {

        return new EasyPrefs<>(key, defaultValue, cacheBreaker);
    }

    /**
     * Public preference setter, abstracts away the
     * native commit type and allows for persisting
     * more complex types such as dictionaries.
     */
    public void set(T value) {

        metaData.set(value);
    }

    /**
     * Public preference getter.  Supports expanded commit types
     * backed by the native commit types supported by android.
     */
    public T get() {

        return metaData.get();
    }

    /**
     * Clear out the preference value from local
     * and native stores.
     */
    public void clear() {

        metaData.clear();
    }
}
