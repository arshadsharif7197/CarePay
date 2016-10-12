package com.carecloud.carepaylibray.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.carecloud.carepaylibray.constants.CarePayConstants;

/**
 * Created by Jahirul Bhuiyan on 9/6/2016.
 */
public class ApplicationPreferences {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static Context context;
    public static ApplicationPreferences Instance;

    private ApplicationPreferences() {

    }

    public static void createPreferences(Context mContext){
        if (Instance == null) {
            context = mContext;
            Instance = new ApplicationPreferences();
            sharedPreferences = context.getSharedPreferences(CarePayConstants.PREFERENCE_CAREPAY, Context.MODE_PRIVATE);
            editor=sharedPreferences.edit();
        }
    }

    public void setUserLanguage(String language) {
        editor.putString(CarePayConstants.PREFERENCE_USER_SELECTED_LANGUAGE, language);
        editor.apply();
    }

    public String getUserLanguage() {
        return sharedPreferences.getString(CarePayConstants.PREFERENCE_USER_SELECTED_LANGUAGE, "");
    }

    public void writeStringToSharedPref(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String readStringFromSharedPref(String key) {
        return sharedPreferences.getString(key, CarePayConstants.DEFAULT_STRING_PREFERENCES);
    }
}
