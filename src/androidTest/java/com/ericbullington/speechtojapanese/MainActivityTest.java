package com.ericbullington.speechtojapanese;

import android.test.ActivityInstrumentationTestCase2;

import com.ericbullington.speechtojapanese.ui.MainActivity;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.ericbullington.speechtojapanese.MainActivityTest \
 * com.ericbullington.speechtojapanese.tests/android.test.InstrumentationTestRunner
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {
        super("com.ericbullington.speechtojapanese", MainActivity.class);
    }

}
