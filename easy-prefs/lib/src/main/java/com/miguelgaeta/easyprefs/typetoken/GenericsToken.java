package com.miguelgaeta.easyprefs.typetoken;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Miguel Gaeta on 6/1/15.
 */
public class GenericsToken {

    private static final List<Class<?>> knownTokenClasses = Arrays.asList
        (TypeTokenCollection.class, TypeTokenMap.class, TypeTokenPair.class, TypeTokenObject.class);

    /**
     * Use a special gson instance that knows how to handle
     * the various abstract class implementations of
     * type token.
     */
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(TypeToken.class, new GenericsTokenSerializer()).create();

    /**
     * Creates a type token assuming a match
     * is found with one of the known
     * registered classes.
     */
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
     * Deserialization will only work using the internal gson instance.
     */
    public static TypeToken deserializeToken(String json) {

        return gson.fromJson(json, TypeToken.class);
    }

    /**
     * Serialize will only work using the internal gson instance.
     */
    public static String serializeToken(TypeToken typeToken) {

        return gson.toJson(typeToken, TypeToken.class);
    }

    /**
     * Deserializing a raw object however is allowed
     * to use an external gson instance.  Proxied
     * through the TypeToken class to keep visibility
     * scoped at the package level.
     */
    public static Object deserialize(TypeToken typeToken, Gson gson, String json) {

        return typeToken.deserialize(gson, json);
    }
}
