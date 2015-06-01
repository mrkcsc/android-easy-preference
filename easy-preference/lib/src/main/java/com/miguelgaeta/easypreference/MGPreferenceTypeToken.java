package com.miguelgaeta.easypreference;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by Miguel Gaeta on 4/15/15.
 */
@SuppressWarnings("UnusedDeclaration") @Getter @ToString @EqualsAndHashCode
class MGPreferenceTypeToken {

    private enum ObjectType {

        TYPE_OBJECT,
        TYPE_MAP,
        TYPE_COLLECTION
    }

    // Signifies type of object.
    private ObjectType typeClassifier;

    // Raw type token.
    private String typeToken;

    // Store type token for collection values.
    private MGPreferenceTypeToken typeTokenCollectionElement;

    // Type type tokens for map key/value pairs.
    private MGPreferenceTypeToken typeTokenMapKeyElement;
    private MGPreferenceTypeToken typeTokenMapValueElement;

    /**
     * Generates a complex type token object that captures
     * the normally unobtainable type tokens of objects
     * defined by generics.
     *
     * Currently only supports collections and maps.
     */
    public static MGPreferenceTypeToken create(Object object) {

        MGPreferenceTypeToken typeToken = new MGPreferenceTypeToken();

        typeToken.typeToken = object.getClass().getName();
        typeToken.typeClassifier = ObjectType.TYPE_OBJECT;

        if (isNonEmptyCollection(object)) {

            typeToken.typeClassifier = ObjectType.TYPE_COLLECTION;
            typeToken.typeTokenCollectionElement = MGPreferenceTypeToken.create(((Collection)object).iterator().next());
        }

        if (object instanceof Map && ((Map)object).size() > 0) {

            typeToken.typeClassifier = ObjectType.TYPE_MAP;

            // Fetch keys in the map.
            Set keySet = ((Map)object).keySet();

            // Initialize index.
            int index = 0;

            for (Object key: keySet) {

                Object value = ((Map)object).get(key);

                // If value is not a collection, or is a non-empty collection, or is the last key in the set.
                if (!(value instanceof Collection) || isNonEmptyCollection(((Map)object).get(key)) || index == keySet.size() - 1) {

                    typeToken.typeTokenMapKeyElement = MGPreferenceTypeToken.create(key);
                    typeToken.typeTokenMapValueElement = MGPreferenceTypeToken.create(((Map)object).get(key));

                    break;
                }

                index++;
            }
        }

        return typeToken;
    }

    /**
     * Serialize from json object.
     */
    public static MGPreferenceTypeToken createFromJson(Gson gson, String json) {

        return gson.fromJson(json, MGPreferenceTypeToken.class);
    }

    /**
     * Given some arbitrary json string, serialize it into
     * the corresponding object using the type token
     * information stored in this instance.
     */
    @SuppressWarnings("unchecked")
    public Object fromJson(Gson gson, String json) {

        try {

            switch (typeClassifier) {

                case TYPE_COLLECTION:

                    // Fetch an instance of the iterable class.
                    Collection collection = (Collection)Class.forName(typeToken).newInstance();

                    for (final JsonElement element: gson.fromJson(json, JsonArray.class)) {

                        // Serialize each element into the collection.
                        collection.add(typeTokenCollectionElement.fromJson(gson, element));
                    }

                    return collection;

                case TYPE_MAP:

                    Map map = (Map)Class.forName(typeToken).newInstance();

                    JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

                    if (jsonObject.isJsonArray()) {

                        for (final JsonElement entry: jsonObject.getAsJsonArray()) {

                            JsonArray entryValues = entry.getAsJsonArray();

                            map.put(
                                typeTokenMapKeyElement.fromJson(gson, entryValues.get(0)),
                                typeTokenMapValueElement.fromJson(gson, entryValues.get(1)));
                        }

                    } else {

                        for (Map.Entry<String, JsonElement>  entry : jsonObject.entrySet()) {

                            map.put(
                                typeTokenMapKeyElement.fromJson(gson, entry.getKey()),
                                typeTokenMapValueElement.fromJson(gson, entry.getValue()));
                        }
                    }

                    return map;

                case TYPE_OBJECT:

                    // Serialize with type token.
                    return gson.fromJson(json, Class.forName(typeToken));

                default:

                    // Should not happen, implementation error.
                    throw new RuntimeException("Unknown type classifier.");
            }

        } catch (Exception e) {

            // Should not happen unless input json is invalid.
            throw new RuntimeException("Deserialization error: " + e);
        }
    }

    public Object fromJson(Gson gson, JsonElement jsonElement) {

        return fromJson(gson, jsonElement.toString());
    }

    /**
     * Test if an object is an instance of a collection
     * and is not empty.
     */
    private static boolean isNonEmptyCollection(Object object) {

        return object instanceof Collection && ((Collection)object).size() > 0;
    }
}
