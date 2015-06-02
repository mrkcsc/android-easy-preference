package com.miguelgaeta.easyprefs.typetoken;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by Miguel Gaeta on 6/1/15.
 */
@SuppressWarnings("unchecked")
class TypeTokenMap extends TypeToken {

    @Override
    protected void build(Object object) {

        Set keySet = ((Map)object).keySet();

        int index = 0;

        for (Object key : keySet) {

            Object value = ((Map)object).get(key);

            // If value is not a collection, or is a non-empty collection, or is the last key in the set.
            if (!(value instanceof Collection) || isNonEmptyCollection(((Map)object).get(key)) || index == keySet.size() - 1) {

                nestedTypeTokens.add(GenericsToken.create(key));
                nestedTypeTokens.add(GenericsToken.create(((Map) object).get(key)));

                break;
            }

            index++;
        }
    }

    @Override
    protected boolean isMatchingType(Object object) {

        return object instanceof Map && !((Map)object).isEmpty();
    }

    @Override
    public Object deserialize(Gson gson, String json) {

        Map map = null;

        try {

            map = (Map)Class.forName(objectClassName).newInstance();

            JsonObject jsonObject = gson.fromJson(json, JsonObject.class);

            if (jsonObject.isJsonArray()) {

                for (final JsonElement entry: jsonObject.getAsJsonArray()) {

                    JsonArray entryValues = entry.getAsJsonArray();

                    map.put(
                        nestedTypeTokens.get(0).deserialize(gson, entryValues.get(0).toString()),
                        nestedTypeTokens.get(1).deserialize(gson, entryValues.get(1).toString()));
                }

            } else {

                for (Map.Entry<String, JsonElement>  entry : jsonObject.entrySet()) {

                    map.put(
                        nestedTypeTokens.get(0).deserialize(gson, entry.getKey()),
                        nestedTypeTokens.get(1).deserialize(gson, entry.getValue().toString()));
                }
            }

        } catch (Exception ignored) { }

        return map;
    }

    private static boolean isNonEmptyCollection(Object object) {

        return object instanceof Collection && !((Collection)object).isEmpty();
    }
}
