package com.miguelgaeta.easyprefs.typetoken;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.util.Collection;

/**
 * Created by Miguel Gaeta on 6/1/15.
 */
@SuppressWarnings("unchecked")
class TypeTokenCollection extends TypeToken {

    @Override
    public void build(Object object) {

        nestedTypeTokens.add(GenericsToken.create(((Collection) object).iterator().next()));
    }

    @Override
    public boolean isMatchingType(Object object) {

        return object instanceof Collection && !((Collection)object).isEmpty();
    }

    @Override
    public Object deserialize(Gson gson, String json) {

        try {

            Collection collection = (Collection)Class.forName(objectClassName).newInstance();

            for (final JsonElement element: gson.fromJson(json, JsonArray.class)) {

                collection.add(nestedTypeTokens.get(0).deserialize(gson, element.toString()));
            }

            return collection;

        } catch (Exception e) {

            return null;
        }
    }
}
