package com.miguelgaeta.easyprefs.typetoken;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Miguel Gaeta on 6/1/15.
 */
public class GenericsToken {

    private static final List<Class<?>> knownTokenClasses = Arrays.asList(TypeTokenCollection.class, TypeTokenMap.class, TypeTokenPair.class, TypeTokenObject.class);

    public static TypeToken create(Object object) {

        try {

            for (Class<?> knownTypeTokenClass : knownTokenClasses) {

                TypeToken typeToken = (TypeToken) knownTypeTokenClass.newInstance();

                if (typeToken.isMatchingType(object)) {
                    typeToken.set(object);

                    return typeToken;
                }
            }

        } catch (Exception ignored) { }

        return null;
    }

    /**
     * Create a type token from a json string.
     */
    public static TypeToken createFromJson(String json) {

        Gson gson = new GsonBuilder()

            .registerTypeAdapter(TypeToken.class, new GenericsTokenSerializer())
            .registerTypeAdapter(TypeToken.class, new GenericsTokenSerializer()).create();

        return gson.fromJson(json, TypeToken.class);
    }
}
