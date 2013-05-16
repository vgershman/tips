package com.expelabs.tips.app;

import android.app.Application;

/**
 * Created with IntelliJ IDEA.
 * User: vadimgershman
 * Date: 14.05.13
 * Time: 23:53
 * To change this template use File | Settings | File Templates.
 */

public class DailyTipsApp extends Application {

    public static final String PREFERENCES_NAME = "tips_preferences";

    @Override
    public void onCreate() {
        super.onCreate();
    }
}

