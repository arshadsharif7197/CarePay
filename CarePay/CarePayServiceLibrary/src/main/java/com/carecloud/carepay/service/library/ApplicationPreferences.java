package com.carecloud.carepay.service.library;

import android.content.Context;
import android.content.SharedPreferences;


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
        return sharedPreferences.getString(CarePayConstants.PREFERENCE_USER_SELECTED_LANGUAGE, "en");
    }

    public void setPracticeLanguage(String language) {
        editor.putString(CarePayConstants.PREFERENCE_PRACTICE_SELECTED_LANGUAGE, language);
        editor.apply();
    }

    public String getPracticeLanguage() {
        return sharedPreferences.getString(CarePayConstants.PREFERENCE_PRACTICE_SELECTED_LANGUAGE, "en");
    }

    public void writeStringToSharedPref(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    public String readStringFromSharedPref(String key) {
        return sharedPreferences.getString(key, CarePayConstants.DEFAULT_STRING_PREFERENCES);
    }


}
