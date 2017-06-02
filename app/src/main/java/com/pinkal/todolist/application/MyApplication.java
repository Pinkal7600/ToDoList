package com.pinkal.todolist.application;

import android.app.Application;

import com.pinkal.todolist.customfont.TypefaceUtil;

/**
 * Created by Pinkal Daliya on 21-Oct-16.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/avenir.ttf");
    }
}
