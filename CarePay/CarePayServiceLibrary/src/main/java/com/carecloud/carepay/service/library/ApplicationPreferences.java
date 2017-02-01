package com.carecloud.carepay.service.library;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;


/**
 * Created by Jahirul Bhuiyan on 9/6/2016.
 */
public class ApplicationPreferences {
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;
    private static Context context;
    public static ApplicationPreferences Instance;
    private String patientId = null;
    private String practiceId = null;
    private String practiceManagement = null;
    private String prefix = null;
    private String userId = null;

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

    /**
     *
     * @param language language
     */
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

    public void saveObjectToSharedPreference(String key, Object object){
        editor.putString(key, new Gson().toJson(object));
        editor.apply();
    }

    /**
     *
     * @param patientId patientId
     */
    public void setPatientId(String patientId) {
        this.patientId=patientId;
        editor.putString(CarePayConstants.PREFERENCE_PATIENT_ID, patientId);
        editor.apply();
    }

    /**
     *
     * @return patientId
     */
    public String getPatientId() {
        return sharedPreferences.getString(CarePayConstants.PREFERENCE_PATIENT_ID, patientId);
    }

    /**
     *
     * @param practiceId practiceId
     */
    public void setPracticeId(String practiceId) {
        this.practiceId=practiceId;
        editor.putString(CarePayConstants.PREFERENCE_PRACTICE_ID, practiceId);
        editor.apply();
    }

    /**
     *
     * @return practiceId
     */
    public String getPracticeId() {
        return sharedPreferences.getString(CarePayConstants.PREFERENCE_PRACTICE_ID, practiceId);
    }

    /**
     *
     * @param practiceManagement practiceManagement
     */
    public void setPracticeManagement(String practiceManagement) {
        this.practiceManagement=practiceManagement;
        editor.putString(CarePayConstants.PREFERENCE_PRACTICE_MANAGEMENT, practiceManagement);
        editor.apply();
    }

    /**
     *
     * @return practiceManagement
     */
    public String getPracticeManagement() {
        return sharedPreferences.getString(CarePayConstants.PREFERENCE_PRACTICE_MANAGEMENT, practiceManagement);
    }

    /**
     *
     * @param userId the userId
     */
    public void setUserId(String userId) {
        this.userId=userId;
        editor.putString(CarePayConstants.PREFERENCE_USER_ID, userId);
        editor.apply();
    }

    /**
     *
     * @return userId
     */
    public String getUserId() {
        return sharedPreferences.getString(CarePayConstants.PREFERENCE_USER_ID, userId);
    }

    /**
     *
     * @param prefix the prefix
     */
    public void setPrefix(String prefix) {
        this.prefix=prefix;
        editor.putString(CarePayConstants.PREFERENCE_PREFIX, prefix);
        editor.apply();
    }

    /**
     *
     * @return prefix
     */
    public String getPrefix() {
        return sharedPreferences.getString(CarePayConstants.PREFERENCE_PREFIX, prefix);
    }
}
