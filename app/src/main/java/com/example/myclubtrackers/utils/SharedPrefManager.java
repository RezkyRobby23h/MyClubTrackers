package com.example.myclubtrackers.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String PREF_NAME = "football_app_pref";
    private static final String KEY_THEME = "theme_key";
    private static final String KEY_LAST_UPDATE = "last_update_key";
    private static final String KEY_FAVORITE_LEAGUE = "favorite_league";

    private final SharedPreferences preferences;
    private static SharedPrefManager instance;

    private SharedPrefManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    public void setDarkMode(boolean isDarkMode) {
        preferences.edit().putBoolean(KEY_THEME, isDarkMode).apply();
    }

    public boolean isDarkMode() {
        return preferences.getBoolean(KEY_THEME, false);
    }

    public void setLastUpdateTime(long time) {
        preferences.edit().putLong(KEY_LAST_UPDATE, time).apply();
    }

    public long getLastUpdateTime() {
        return preferences.getLong(KEY_LAST_UPDATE, 0);
    }

    public void setFavoriteLeague(int leagueId) {
        preferences.edit().putInt(KEY_FAVORITE_LEAGUE, leagueId).apply();
    }

    public int getFavoriteLeague() {
        // Default to English Premier League (ID: 39)
        return preferences.getInt(KEY_FAVORITE_LEAGUE, 39);
    }
}