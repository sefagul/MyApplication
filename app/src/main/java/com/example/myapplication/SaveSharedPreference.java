package com.example.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import static com.example.myapplication.PreferencesUtility.LOGGED_IN_PREF;
import static com.example.myapplication.PreferencesUtility.URL;
import static com.example.myapplication.PreferencesUtility.USER_ID;
import static com.example.myapplication.PreferencesUtility.TOKEN;
import static com.example.myapplication.PreferencesUtility.VALIDATION_TOKEN;

class SaveSharedPreference{

    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    static void setLoggedIn(Context context, boolean loggedIn) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(LOGGED_IN_PREF, loggedIn);
        editor.apply();
    }

    static boolean getLoggedStatus(Context context) {
        return getPreferences(context).getBoolean(LOGGED_IN_PREF, false);
    }

    static void setUserId(Context context, int UserId) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putInt(USER_ID, UserId);
        editor.apply();
    }

    static int getUserId(Context context) {
        return getPreferences(context).getInt(USER_ID, 0);
    }

    static void setUrl(Context context, String url) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(URL, url);
        editor.apply();
    }

    static String getUrl(Context context) {
        return getPreferences(context).getString(URL,"");
    }

    static void setToken(Context context, String token) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(TOKEN, token);
        editor.apply();
    }

    static String getToken(Context context) {
        return getPreferences(context).getString(TOKEN,"");
    }

    static String getValidationToken(Context context) {
        return getPreferences(context).getString(VALIDATION_TOKEN,"");
    }

    static void setValidationToken(Context context, String token) {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putString(VALIDATION_TOKEN, token);
        editor.apply();
    }

    static void deleteAccount(Context context){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.clear().apply();
    }
}
