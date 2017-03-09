package com.carecloud.carepay.service.library;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;


/**
 * Created by Jahirul Bhuiyan on 9/6/2016.
 */
public class ApplicationPreferences {

    private static final String DEFAULT_STRING_PREFERENCES = "-";

    private static final String PREFERENCE_CAREPAY = "Preference_CarePay";

    private static final String PREFERENCE_USER_SELECTED_LANGUAGE = "user_selected_language";

    private static final String PREFERENCE_PRACTICE_SELECTED_LANGUAGE = "practice_user_selected_language";

    private static final String PREFERENCE_PATIENT_ID = "patient_id";

    private static final String PREFERENCE_PRACTICE_ID = "practice_id";

    private static final String PREFERENCE_PRACTICE_MANAGEMENT = "practice_management";

    private static final String PREFERENCE_PREFIX = "prefix";

    private static final String PREFERENCE_USER_ID = "user_id";

    private static final String PREFERENCE_IS_PATIENT_MODE_APPOINTMENTS = "is_patient_mode_appointments";

    private Context context;

    private String patientId;
    private String practiceId;
    private String practiceManagement;
    private String prefix;
    private String userId;
    private String userLanguage;
    private String practiceLanguage;
    private Boolean navigateToAppointments;

    public ApplicationPreferences(Context context) {
        this.context = context;
    }

    public void setNavigateToAppointments(boolean newValue) {
        navigateToAppointments = newValue;
        writeStringToSharedPref(PREFERENCE_USER_SELECTED_LANGUAGE, userLanguage);
    }

    /**
     * @return true if app is navigating to appointments
     */
    public boolean isNavigatingToAppointments() {
        if (null != navigateToAppointments) {
            return navigateToAppointments;
        }

        return readBooleanFromSharedPref(PREFERENCE_IS_PATIENT_MODE_APPOINTMENTS);
    }

    public void setUserLanguage(String newValue) {
        userLanguage = newValue;
        writeStringToSharedPref(PREFERENCE_USER_SELECTED_LANGUAGE, userLanguage);
    }

    /**
     * @return user preferred language. Returns default value if not set.
     */
    public String getUserLanguage() {
        if (null != userLanguage) {
            return userLanguage;
        }

        return readStringFromSharedPref(PREFERENCE_USER_SELECTED_LANGUAGE, "en");
    }

    /**
     * @return user preferred language. Returns null if not set.
     *
     */
    public String getUserLanguageRaw() {
        if (null != userLanguage) {
            return userLanguage;
        }

        return readStringFromSharedPref(PREFERENCE_USER_SELECTED_LANGUAGE);
    }

    public void setPracticeLanguage(String newValue) {
        practiceLanguage = newValue;
        writeStringToSharedPref(PREFERENCE_PRACTICE_SELECTED_LANGUAGE, userLanguage);
    }

    /**
     * @return practice preferred language
     */
    public String getPracticeLanguage() {
        if (null != practiceLanguage) {
            return practiceLanguage;
        }

        return readStringFromSharedPref(PREFERENCE_PRACTICE_SELECTED_LANGUAGE, "en");
    }

    /**
     *
     * @param newValue patientId
     */
    public void setPatientId(String newValue) {
        patientId = newValue;
        writeStringToSharedPref(PREFERENCE_PATIENT_ID, patientId);
    }

    /**
     * @return patientId
     */
    public String getPatientId() {
        if (null != patientId) {
            return patientId;
        }

        return readStringFromSharedPref(PREFERENCE_PATIENT_ID);
    }

    /**
     *
     * @param newValue practiceId
     */
    public void setPracticeId(String newValue) {
        practiceId = newValue;
        writeStringToSharedPref(PREFERENCE_PRACTICE_ID, practiceId);
    }

    /**
     *
     * @return practiceId
     */
    public String getPracticeId() {
        if (null != practiceId) {
            return practiceId;
        }

        return readStringFromSharedPref(PREFERENCE_PRACTICE_ID);
    }

    /**
     *
     * @param newValue practiceManagement
     */
    public void setPracticeManagement(String newValue) {
        practiceManagement = newValue;
        writeStringToSharedPref(PREFERENCE_PRACTICE_MANAGEMENT, practiceManagement);
    }

    /**
     *
     * @return practiceManagement
     */
    public String getPracticeManagement() {
        if (null != practiceManagement) {
            return practiceManagement;
        }

        return readStringFromSharedPref(PREFERENCE_PRACTICE_MANAGEMENT);
    }

    /**
     *
     * @param newValue the userId
     */
    public void setUserId(String newValue) {
        userId = newValue;
        writeStringToSharedPref(PREFERENCE_USER_ID, userId);
    }

    /**
     *
     * @return userId
     */
    public String getUserId() {
        if (null != userId) {
            return userId;
        }

        return readStringFromSharedPref(PREFERENCE_USER_ID);
    }

    /**
     *
     * @param newValue the prefix
     */
    public void setPrefix(String newValue) {
        prefix = newValue;
        writeStringToSharedPref(PREFERENCE_PREFIX, prefix);
    }

    /**
     *
     * @return prefix
     */
    public String getPrefix() {
        if (null != prefix) {
            return prefix;
        }

        return readStringFromSharedPref(PREFERENCE_PREFIX);
    }

    private void writeStringToSharedPref(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void writeObjectToSharedPreference(String key, Object object){
        writeStringToSharedPref(key, new Gson().toJson(object));
    }

    public <S> S getObjectFromSharedPreferences(String key, Class<S> objectClass){
        Gson gson = new Gson();
        try{
            return gson.fromJson(readStringFromSharedPref(key), objectClass);
        }catch (Exception ex){
            return null;
        }
    }

    public String readStringFromSharedPref(String key) {
        return readStringFromSharedPref(key, DEFAULT_STRING_PREFERENCES);
    }

    public String readStringFromSharedPref(String key, String defaultValue) {
        return getSharedPreferences().getString(key, defaultValue);
    }

    private boolean readBooleanFromSharedPref(String key) {
        return readBooleanFromSharedPref(key, false);
    }

    private boolean readBooleanFromSharedPref(String key, boolean defaultValue) {
        return getSharedPreferences().getBoolean(key, defaultValue);
    }

    private SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences(PREFERENCE_CAREPAY, Context.MODE_PRIVATE);
    }
}
