package com.miguelgaeta.easyprefs.typetoken;

import com.google.gson.Gson;

/**
 * Fallback token that just tries to do
 * generic deserialization on any object.
 */
class TypeTokenObject extends TypeToken {

    @Override
    protected void build(Object object) {

    }

    @Override
    protected boolean isMatchingType(Object object) {

        return true;
    }

    @Override
    public Object deserialize(Gson gson, String json) {

        try {

            return gson.fromJson(json, Class.forName(objectClassName));

        } catch (Exception e) {

            return null;
        }
    }
}
