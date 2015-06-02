package com.miguelgaeta.easyprefs.app;

import android.app.Activity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by Miguel Gaeta on 6/2/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class TestActivityTest {

    @Test
    public void onCreate_shouldInflateTheMenu() throws Exception {

        Activity activity = Robolectric.setupActivity(TestActivity.class);

        // mock creation
        List mockedList = mock(List.class);

            // using mock object - it does not throw any "unexpected interaction" exception
        mockedList.add("one");
        mockedList.clear();

        // selective, explicit, highly readable verification
        verify(mockedList).add("one");
        verify(mockedList).clear();

        //final Menu menu = shadowOf(activity).getOptionsMenu();
        //assertThat(menu.findItem(R.id.item1).getTitle()).isEqualTo("First menu item");
        //assertThat(menu.findItem(R.id.item2).getTitle()).isEqualTo("Second menu item");
    }
}
