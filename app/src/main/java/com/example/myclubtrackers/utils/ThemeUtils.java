package com.example.myclubtrackers.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeUtils {

    public static void applyTheme(Context context) {
        boolean isDarkMode = SharedPrefManager.getInstance(context).isDarkMode();

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public static boolean isNightModeActive(Context context) {
        return (context.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    }

    public static void toggleTheme(Activity activity) {
        boolean currentMode = SharedPrefManager.getInstance(activity).isDarkMode();
        SharedPrefManager.getInstance(activity).setDarkMode(!currentMode);
        activity.recreate();
    }
}