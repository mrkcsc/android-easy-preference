package com.miguelgaeta.easyprefs;

import android.util.Pair;

import com.google.gson.Gson;
import com.miguelgaeta.easyprefs.typetoken.GenericsToken;

import java.util.Arrays;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by Miguel Gaeta on 3/9/15.
 */
@SuppressWarnings("unchecked")
class EasyPrefsMetaData<T> {

    // A memory cache of the preference value
    // so we do not have to go into the native
    // android preferences every fetch.
    private T locallyCachedValue;

    // Default value of this item.
    private T defaultValue;

    // The key used by the android preferences
    // underlying system (should be unique).
    private final String key;
    private final String keyTypeToken;

    // Gson serializer.
    private final Gson gson;

    // Holds any active serialization subscription.
    private Subscription serializationSubscription;

    /**
     * Creates a new preference object that is backed
     * by the android shared preferences object.
     */
    EasyPrefsMetaData(Gson gson, String key, T defaultValue, boolean cacheBreaker) {

        if (cacheBreaker) {

            // Append cache breaker.
            key += "_" + EasyPrefsConfig.getApplicationVersionCode();
        }

        this.gson = gson;

        this.key = key;
        this.keyTypeToken = key + "_TYPE_TOKEN";

        this.defaultValue = defaultValue;
    }

    /**
     * Clear out any value stored.
     */
    void clear() {

        EasyPrefsSharedPreferences.removeString(key);

        locallyCachedValue = null;
    }

    /**
     * Get the preference value.
     */
    T get() {

        if (locallyCachedValue == null) {

            // First fetch complex type token for preference.
            String typeTokenJson = EasyPrefsSharedPreferences.getString(keyTypeToken);

            if (typeTokenJson != null) {

                // Fetch raw json associated with this preference key.
                String keyJson = EasyPrefsSharedPreferences.getString(key);

                if (keyJson != null) {

                    // Deserialize with type token and update local cache.
                    locallyCachedValue = (T)GenericsToken.deserialize(GenericsToken.deserializeToken(typeTokenJson), gson, keyJson);
                }
            }
        }

        if (locallyCachedValue == null && defaultValue != null) {

            return defaultValue;
        }

        return locallyCachedValue;
    }

    /**
     * Set the preference value.
     */
    void set(T value) {

        if (value == null) {

            clear();

        } else {

            // Locally cache value.
            locallyCachedValue = value;

            if (serializationSubscription != null) {
                serializationSubscription.unsubscribe();
            }

            // Create an observable that serializes the value and generates its type token.
            Observable<Pair<String, String>> serializationObservable = Observable.create(subscriber -> {

                subscriber.onNext(Pair.create(gson.toJson(value), GenericsToken.serializeToken(GenericsToken.create(value))));
                subscriber.onCompleted();
            });

            serializationSubscription = serializationObservable.subscribeOn(Schedulers.computation()).subscribe(keyTypeTokenPair -> {

                EasyPrefsSharedPreferences.setString(Arrays.asList(

                    Pair.create(key, keyTypeTokenPair.first),
                    Pair.create(keyTypeToken, keyTypeTokenPair.second)));
            });
        }
    }
}
