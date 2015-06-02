package com.miguelgaeta.easyprefs.typetoken;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;
import lombok.ToString;

/**
 * Created by Miguel Gaeta on 6/1/15.
 */
@ToString
public abstract class TypeToken {

    protected @NonNull List<TypeToken> nestedTypeTokens = new ArrayList<>();

    @NonNull
    protected String objectClassName;

    void set(Object object) {

        this.objectClassName = object.getClass().getName();

        build(object);
    }

    protected abstract void build(Object object);

    /**
     * Given an arbitrary object does it match
     * this type token.
     */
    protected abstract boolean isMatchingType(Object object);

    /**
     * Given a string attempt to de-serialize it
     * into the associated object for this
     * type token.
     */
    public abstract Object deserialize(Gson gson, String json);
}
