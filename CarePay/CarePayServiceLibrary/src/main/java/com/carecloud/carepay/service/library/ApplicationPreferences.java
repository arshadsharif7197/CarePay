package com.carecloud.carepay.service.library;

import android.content.SharedPreferences;

import com.carecloud.carepay.service.library.base.IApplicationSession;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.Defs;
import com.carecloud.carepay.service.library.platform.AndroidPlatform;
import com.carecloud.carepay.service.library.platform.Platform;
import com.google.gson.Gson;

import java.util.Set;


/**
 * Created by Jahirul Bhuiyan on 9/6/2016
 */
public class ApplicationPreferences {

    private static final String DEFAULT_STRING_PREFERENCES = "-";

    private static final String PREFERENCE_PATIENT_ID = "patient_id";
    private static final String PREFERENCE_PRACTICE_ID = "practice_id";
    private static final String PREFERENCE_PREFIX = "prefix";
    private static final String PREFERENCE_USER_ID = "user_id";
    private static final String PREFERENCE_PATIENT_PHOTO_URL = "patient_photo_url";
    private static final String PREFERENCE_IS_TUTORIAL_SHOWN = "is_tutorial_shown";
    private static final String PREFERENCE_APPOINTMENT_NAVIGATION_OPTION = "appointment_navigation_option";
    public static final String PREFERENCE_FILTERED_PROVIDERS = "filteredDoctors";
    public static final String PREFERENCE_FILTERED_LOCATIONS = "filteredLocations";
    public static final String PATIENT_USER_LANGUAGE = "practiceUserLanguage";
    public static final String PRACTICE_USER_LANGUAGE = "user_selected_language";

    private String patientId;
    private String practiceId;
    private String prefix;
    private String userId;
    private String userLanguage;
    private String patientUserLanguage;
    private Boolean isTutorialShown;
    private String photoUrl;
    private
    @Defs.AppointmentNavigationTypeDef
    Integer navigationOption;
    private String userName;

    private static ApplicationPreferences instance;


    public static ApplicationPreferences getInstance() {
        if (instance == null) {
            instance = new ApplicationPreferences();
        }
        return instance;
    }

    /**
     * Set the selected Appointment Navigation Option & Store in Shared Pref
     *
     * @param navigationOption valid navigationOption
     */
    public void setAppointmentNavigationOption(@Defs.AppointmentNavigationTypeDef int navigationOption) {
        this.navigationOption = navigationOption;
        writeIntegerToSharedPref(PREFERENCE_APPOINTMENT_NAVIGATION_OPTION, navigationOption);
    }

    /**
     * Get the last selected Appointment Navigation Option
     *
     * @return selected navigation option
     */
    public
    @Defs.AppointmentNavigationTypeDef
    int getAppointmentNavigationOption() {
        if (navigationOption == null) {
            @Defs.AppointmentNavigationTypeDef int savedNavigationOption = readIntFromSharedPref(PREFERENCE_APPOINTMENT_NAVIGATION_OPTION);
            this.navigationOption = savedNavigationOption;
        }
        return navigationOption;
    }

    public void setUserLanguage(String newValue) {
        if (((IApplicationSession) ((AndroidPlatform) Platform.get()).getContext())
                .getApplicationMode().getApplicationType()
                .equals(ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE)) {
            patientUserLanguage = newValue;
            writeStringToSharedPref(PATIENT_USER_LANGUAGE, userLanguage);
            return;
        }
        userLanguage = newValue;
        patientUserLanguage = newValue;
        writeStringToSharedPref(PRACTICE_USER_LANGUAGE, userLanguage);
    }

    /**
     * @return user preferred language. Returns default value if not set.
     */
    public String getUserLanguage() {
        if (((IApplicationSession) ((AndroidPlatform) Platform.get()).getContext())
                .getApplicationMode().getApplicationType()
                .equals(ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE)) {
            if (patientUserLanguage == null) {
                patientUserLanguage = readStringFromSharedPref(PATIENT_USER_LANGUAGE,
                        "en");//Resources.getSystem().getConfiguration().locale.getLanguage());
            }
            return patientUserLanguage;
        }
        if (userLanguage == null) {
            userLanguage = readStringFromSharedPref(PRACTICE_USER_LANGUAGE,
                    "en");//Resources.getSystem().getConfiguration().locale.getLanguage());
        }
        return userLanguage;
    }

    /**
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
     * @param newValue practiceId
     */
    public void setPracticeId(String newValue) {
        practiceId = newValue;
        writeStringToSharedPref(PREFERENCE_PRACTICE_ID, practiceId);
    }

    /**
     * @return practiceId
     */
    public String getPracticeId() {
        if (practiceId != null) {
            return practiceId;
        }

        return readStringFromSharedPref(PREFERENCE_PRACTICE_ID);
    }

    /**
     * @param newValue the userId
     */
    public void setUserId(String newValue) {
        userId = newValue;
        writeStringToSharedPref(PREFERENCE_USER_ID, userId);
    }

    /**
     * @return userId
     */
    public String getUserId() {
        if (userId != null) {
            return userId;
        }

        return readStringFromSharedPref(PREFERENCE_USER_ID);
    }

    /**
     * @param photoUrl the user photoUrl
     */
    public void setUserPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
        writeStringToSharedPref(PREFERENCE_PATIENT_PHOTO_URL, photoUrl);
    }

    /**
     * @return photoUrl
     */
    public String getUserPhotoUrl() {
        if (photoUrl != null) {
            return photoUrl;
        }

        return readStringFromSharedPref(PREFERENCE_PATIENT_PHOTO_URL);
    }

    /**
     * @param newValue the prefix
     */
    public void setPrefix(String newValue) {
        prefix = newValue;
        writeStringToSharedPref(PREFERENCE_PREFIX, prefix);
    }

    /**
     * @return prefix
     */
    public String getPrefix() {
        if (null != prefix) {
            return prefix;
        }

        return readStringFromSharedPref(PREFERENCE_PREFIX);
    }

    public void writeBooleanToSharedPref(String key, Boolean value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void writeStringToSharedPref(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void writeIntegerToSharedPref(String key, int value) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(key, value);
        editor.apply();
    }

    /**
     * Save object to Shared Preferences. Object will be stored as a JSON String
     *
     * @param key    preference key
     * @param object object to save
     */
    public void writeObjectToSharedPreference(String key, Object object) {
        writeStringToSharedPref(key, object != null ? new Gson().toJson(object) : null);
    }

    /**
     * Retrieve a previously saved JSON object from Shared Prefferences
     *
     * @param key         preference key
     * @param objectClass S Type Object class for deserializing
     * @param <S>         Type
     * @return S Type object
     */
    public <S> S getObjectFromSharedPreferences(String key, Class<S> objectClass) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(readStringFromSharedPref(key), objectClass);
        } catch (Exception ex) {
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

    private Set<String> readStringSetFromSharedPref(String key) {
        return getSharedPreferences().getStringSet(key, null);
    }

    private int readIntFromSharedPref(String key) {
        return readIntFromSharedPref(key, -1);
    }

    private int readIntFromSharedPref(String key, int defaultValue) {
        return getSharedPreferences().getInt(key, defaultValue);
    }

    private SharedPreferences getSharedPreferences() {
        return ((AndroidPlatform) Platform.get()).openDefaultSharedPreferences();
    }

    /**
     * @param tutorialShown a boolean indicating if the tutorial has been shown
     */
    public void setTutorialShown(Boolean tutorialShown) {
        isTutorialShown = tutorialShown;
        writeBooleanToSharedPref(PREFERENCE_IS_TUTORIAL_SHOWN, tutorialShown);
    }

    /**
     * @return a boolean indicating if the tutorial has been shown
     */
    public boolean isTutorialShown() {
        if (isTutorialShown != null) {
            return isTutorialShown;
        }

        return readBooleanFromSharedPref(PREFERENCE_IS_TUTORIAL_SHOWN);
    }

    public Set<String> getSelectedProvidersIds(String practiceId, String userId) {
        return readStringSetFromSharedPref(practiceId + userId + PREFERENCE_FILTERED_PROVIDERS);
    }

    public Set<String> getSelectedLocationsIds(String practiceId, String userId) {
        return readStringSetFromSharedPref(practiceId + userId + PREFERENCE_FILTERED_LOCATIONS);
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
