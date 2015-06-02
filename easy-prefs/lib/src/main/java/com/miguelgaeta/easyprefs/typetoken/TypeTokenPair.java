package com.miguelgaeta.easyprefs.typetoken;

import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;

/**
 * Created by Miguel Gaeta on 6/1/15.
 */
public class TypeTokenPair extends TypeToken {

    @Override
    protected void build(Object object) {

        nestedTypeTokens.add(GenericsToken.create(((Pair) object).first));
        nestedTypeTokens.add(GenericsToken.create(((Pair) object).second));
    }

    @Override
    protected boolean isMatchingType(Object object) {

        return object instanceof Pair;
    }

    @Override
    public Object deserialize(Gson gson, String json) {

        Log.e("TEST: ", json);

        return null;
    }
}
