package com.pahuza.pahuza.views;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by baryariv on 26/12/2016.
 */

public class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}