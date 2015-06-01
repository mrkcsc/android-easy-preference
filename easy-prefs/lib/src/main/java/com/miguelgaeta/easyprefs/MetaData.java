package com.miguelgaeta.easyprefs;

import android.util.Pair;

import com.google.gson.Gson;

import java.util.Arrays;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

/**
 * Created by Miguel Gaeta on 3/9/15.
 */
@SuppressWarnings("unchecked")
class MetaData<T> {

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
    MetaData(String key, T defaultValue, boolean cacheBreaker) {

        if (cacheBreaker) {

            // Append cache breaker.
            key += "_" + Config.getApplicationVersionCode();
        }

        // Set gson.
        this.gson = Config.getGson();

        // Set key.
        this.key = key;
        this.keyTypeToken = key + "_TYPE_TOKEN";

        // Set default value.
        this.defaultValue = defaultValue;
    }

    /**
     * Clear out any value stored.
     */
    void clear() {

        SharedPreferences.removeString(key);

        locallyCachedValue = null;
    }

    /**
     * Get the preference value.
     */
    T get() {

        if (locallyCachedValue == null) {

            // First fetch complex type token for preference.
            String typeTokenJson = SharedPreferences.getString(keyTypeToken);

            if (typeTokenJson != null) {

                // Serialize type token into object.
                TypeToken typeToken = TypeToken.createFromJson(gson, typeTokenJson);

                // Fetch raw json associated with this preference key.
                String keyJson = SharedPreferences.getString(key);

                if (keyJson != null) {

                    // Deserialize with type token and update local cache.
                    locallyCachedValue = (T)typeToken.fromJson(gson, keyJson);
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

                String key = gson.toJson(value);
                String keyTypeToken = gson.toJson(TypeToken.create(value));

                subscriber.onNext(new Pair<>(key, keyTypeToken));
                subscriber.onCompleted();
            });

            serializationSubscription = serializationObservable

                .subscribeOn(Schedulers.computation())
                .subscribe(keyTypeTokenPair -> {

                    SharedPreferences.setString(Arrays.asList(

                        Pair.create(key, keyTypeTokenPair.first),
                        Pair.create(keyTypeToken, keyTypeTokenPair.second)));
                });
        }
    }
}
