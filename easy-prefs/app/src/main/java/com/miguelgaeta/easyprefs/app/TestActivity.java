package com.miguelgaeta.easyprefs.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.miguelgaeta.easyprefs.EasyPrefs;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class TestActivity extends AppCompatActivity {

    private static final EasyPrefs<List<TestClass>> pref1 = EasyPrefs.create("DA_KEY", new ArrayList<>());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("TEST: ", "Lol: " + pref1.get());

        List<TestClass> testClass = new ArrayList<>();

        testClass.add(TestClass.create("one"));
        testClass.add(TestClass.create("two"));

        pref1.set(testClass);


        Log.e("TEST: ", "Lol: " + pref1.get());
    }

    @ToString @Getter @Setter @AllArgsConstructor(staticName = "create")
    private static class TestClass {

        public String test;
    }
}
