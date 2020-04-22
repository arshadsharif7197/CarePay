package com.carecloud.carepay.service.library;

import android.content.SharedPreferences;
import android.util.Log;

import com.carecloud.carepay.service.library.base.IApplicationSession;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.Defs;
import com.carecloud.carepay.service.library.dtos.AvailableLocationDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
import com.carecloud.carepay.service.library.platform.AndroidPlatform;
import com.carecloud.carepay.service.library.platform.Platform;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * Created by Jahirul Bhuiyan on 9/6/2016
 */
public class ApplicationPreferences {

    private static final String DEFAULT_STRING_PREFERENCES = "-";

    private static final String PREFERENCE_PATIENT_ID = "patient_id";
    private static final String PREFERENCE_PRACTICE_ID = "practice_id";
    private static final String PREFERENCE_USER_ID = "user_id";
    private static final String PREFERENCE_USERNAME = "username";
    private static final String PREFERENCE_PASSWORD = "password";
    private static final String PREFERENCE_PATIENT_PHOTO_URL = "patient_photo_url";
    private static final String PREFERENCE_IS_TUTORIAL_SHOWN = "is_tutorial_shown";
    private static final String PREFERENCE_APPOINTMENT_NAVIGATION_OPTION = "appointment_navigation_option";
    private static final String PREFERENCE_FILTERED_PROVIDERS = "filteredDoctors";
    private static final String PREFERENCE_FILTERED_LOCATIONS = "filteredLocations";
    private static final String PATIENT_USER_LANGUAGE = "practiceUserLanguage";
    private static final String PRACTICE_USER_LANGUAGE = "user_selected_language";
    private static final String PREFERENCE_LOCATION_ID = "locationId";
    private static final String PREFERENCE_LOCATION = "locations";
    private static final String BAD_COUNTER_TRANSITION = "badgeCounterTransition";
    private static final String PREFERENCE_LANDING_SCREEN = "landing_screen";
    private static final String USER_PRACTICES = "userPractices";
    private static final String PREFERENCE_LATEST_APP_VERSION = "latest_app_version";
    private static final String PREFERENCE_LAST_APP_VERSION_NUM = "last_app_version_num";
    private static final String PREFERENCE_REMIND_LATEST = "remind_latest";
    private static final String PREFERENCE_FORCE_UPDATE = "force_update";
    private static final String PREFERENCE_PROFILE_ID = "profileId";
    private static final String PREFERENCE_APPOINTMENT_COUNTS = "appointment_counts";
    private static final String PREFERENCE_LAST_DATE_RATE_DIALOG_SHOWN = "lastDateRateDialogShown";
    private static final String PREFERENCE_PATIENT_MODE_TRANSITION = "patientModeTransition";
    private static final String PREFERENCE_REFRESH_TOKEN = "refresh_token";


    private String patientId;
    private String practiceId;
    private Integer practiceLocationId;
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
    private String userPassword;
    private String fullName;
    private TransitionDTO badgeCounterTransition;
    private int messagesBadgeCounter;
    private int formsBadgeCounter;
    private TransitionDTO patientModeTransition;


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
            @Defs.AppointmentNavigationTypeDef
            int savedNavigationOption = readIntFromSharedPref(PREFERENCE_APPOINTMENT_NAVIGATION_OPTION);
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
                        CarePayConstants.DEFAULT_LANGUAGE);
            }
            return patientUserLanguage;
        }
        if (userLanguage == null) {
            userLanguage = readStringFromSharedPref(PRACTICE_USER_LANGUAGE,
                    CarePayConstants.DEFAULT_LANGUAGE);
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
     * @return practiceId
     */
    public Integer getPracticeLocationId() {
        if (practiceLocationId != null) {
            return practiceLocationId;
        }

        return readIntFromSharedPref(PREFERENCE_LOCATION_ID);
    }

    public void setPracticeLocationId(Integer practiceLocationId) {
        this.practiceLocationId = practiceLocationId;
        writeIntegerToSharedPref(PREFERENCE_LOCATION_ID, practiceLocationId);
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

        return readStringFromSharedPref(PREFERENCE_USER_ID, "");
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

        return readStringFromSharedPref(PREFERENCE_PATIENT_PHOTO_URL, "");
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
            Log.e("Application Preferences", ex.getMessage());
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

    public void setSelectedProvidersId(String practiceId, String userId, Set<String> filteredDoctorsIds) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putStringSet(practiceId + userId + ApplicationPreferences.PREFERENCE_FILTERED_PROVIDERS,
                filteredDoctorsIds).apply();
    }

    public Set<String> getSelectedLocationsIds(String practiceId, String userId) {
        return readStringSetFromSharedPref(practiceId + userId + PREFERENCE_FILTERED_LOCATIONS);
    }

    public void setSelectedLocationsId(String practiceId, String userId, Set<String> filteredLocationsIds) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putStringSet(practiceId + userId + ApplicationPreferences.PREFERENCE_FILTERED_LOCATIONS,
                filteredLocationsIds).apply();
    }

    public String getUserName() {
        if (userName == null) {
            userName = readStringFromSharedPref(PREFERENCE_USERNAME);
        }
        return userName;
    }

    public void setUserName(String userName) {
        writeStringToSharedPref(PREFERENCE_USERNAME, userName);
        this.userName = userName;
    }

    public Set<String> getPracticesWithBreezeEnabled(String practiceId) {
        return getSharedPreferences().getStringSet(PREFERENCE_LOCATION + practiceId, null);
    }

    public void setPracticesWithBreezeEnabled(List<UserPracticeDTO> practiceInformation) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        for (UserPracticeDTO practice : practiceInformation) {
            List<AvailableLocationDTO> locations = practice.getLocations();
            if (locations != null) {
                Set<String> locationsSet = new HashSet<>();
                for (AvailableLocationDTO location : locations) {
                    locationsSet.add(location.getGuid());
                }
                editor.putStringSet(PREFERENCE_LOCATION + practice.getPracticeId(), locationsSet);
            } else {
                editor.putStringSet(PREFERENCE_LOCATION + practice.getPracticeId(), null);
            }
        }
        editor.apply();
    }

    public void setUserPractices(List<UserPracticeDTO> practiceInformation) {
        Map<String, UserPracticeDTO> userPracticesMap = new HashMap<>();
        for (UserPracticeDTO userPracticeDTO : practiceInformation) {
            userPracticesMap.put(userPracticeDTO.getPracticeId(), userPracticeDTO);
        }
        Gson gson = new GsonBuilder().create();
        String jsonMap = gson.toJson(userPracticesMap);
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(USER_PRACTICES, jsonMap);
        editor.apply();
    }

    public UserPracticeDTO getUserPractice(String practiceId) {
        Gson gson = new GsonBuilder().create();
        String json = getSharedPreferences().getString(USER_PRACTICES, "");
        Type typeOfHashMap = new TypeToken<Map<String, UserPracticeDTO>>() {
        }.getType();
        Map<String, UserPracticeDTO> userPracticeMap = gson.fromJson(json, typeOfHashMap);
        return userPracticeMap.get(practiceId);
    }

    public String getUserPassword() {
        if (userPassword == null) {
            userPassword = readStringFromSharedPref(PREFERENCE_PASSWORD);
        }
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        writeStringToSharedPref(PREFERENCE_PASSWORD, userPassword);
        this.userPassword = userPassword;
    }

    public String getFullName() {
        return fullName;
    }

    public void setUserFullName(String fullName) {
        if (!fullName.isEmpty()) {
            this.fullName = fullName;
        }
    }

    public void setBadgeCounterTransition(TransitionDTO badgeCounterTransition) {
        writeObjectToSharedPreference(BAD_COUNTER_TRANSITION, badgeCounterTransition);
        this.badgeCounterTransition = badgeCounterTransition;
    }

    public TransitionDTO getBadgeCounterTransition() {
        if (badgeCounterTransition == null) {
            badgeCounterTransition = getObjectFromSharedPreferences(BAD_COUNTER_TRANSITION, TransitionDTO.class);
        }
        return badgeCounterTransition;
    }

    public void setMessagesBadgeCounter(int messagesBadgeCounter) {
        this.messagesBadgeCounter = messagesBadgeCounter;
    }

    public int getMessagesBadgeCounter() {
        return messagesBadgeCounter;
    }

    public void setFormsBadgeCounter(int formsBadgeCounter) {
        this.formsBadgeCounter = formsBadgeCounter;
    }

    public int getFormsBadgeCounter() {
        return formsBadgeCounter;
    }

    public boolean isLandingScreen() {
        return readBooleanFromSharedPref(PREFERENCE_LANDING_SCREEN, false);
    }

    public void setLandingScreen(boolean isLandingScreen) {
        writeBooleanToSharedPref(PREFERENCE_LANDING_SCREEN, isLandingScreen);
    }

    public boolean isLatestVersion() {
        return readBooleanFromSharedPref(PREFERENCE_LATEST_APP_VERSION, true);
    }

    public void setLatestVersion(boolean isLatestVersion) {
        writeBooleanToSharedPref(PREFERENCE_LATEST_APP_VERSION, isLatestVersion);
        if (!isLatestVersion) {
            //reset remind latest
            setRemindLatest(true);
        }
    }

    public int getLastVersionNum() {
        return readIntFromSharedPref(PREFERENCE_LAST_APP_VERSION_NUM);
    }

    public void setLastVersionNum(int newVersionNum) {
        writeIntegerToSharedPref(PREFERENCE_LAST_APP_VERSION_NUM, newVersionNum);
        //always reset remind latest for new updates
        setRemindLatest(true);
    }

    public boolean shouldRemindLatest() {
        return readBooleanFromSharedPref(PREFERENCE_REMIND_LATEST, true);
    }

    public void setRemindLatest(boolean shouldRemind) {
        writeBooleanToSharedPref(PREFERENCE_REMIND_LATEST, shouldRemind);
    }

    public boolean mustForceUpdate() {
        return readBooleanFromSharedPref(PREFERENCE_FORCE_UPDATE, false);
    }

    public void setForceUpdate(boolean mustForceUpdate) {
        writeBooleanToSharedPref(PREFERENCE_FORCE_UPDATE, mustForceUpdate);
    }

    public String getProfileId() {
        return readStringFromSharedPref(PREFERENCE_PROFILE_ID, null);
    }

    public void setProfileId(String profileId) {
        writeStringToSharedPref(PREFERENCE_PROFILE_ID, profileId);
    }

    public void setAppointmentCounts(Object appointmentCounts) {
        writeObjectToSharedPreference(PREFERENCE_APPOINTMENT_COUNTS, appointmentCounts);
    }

    public Object getAppointmentCounts(Class appointmentCountsClass) {
        return getObjectFromSharedPreferences(PREFERENCE_APPOINTMENT_COUNTS, appointmentCountsClass);
    }

    public boolean shouldShowRateDialog() {
        String strDate = readStringFromSharedPref(PREFERENCE_LAST_DATE_RATE_DIALOG_SHOWN, null);
        if (strDate == null) {
            return true;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        formatter.setLenient(false);
        try {
            Date lastRateDate = formatter.parse(strDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(lastRateDate);
            calendar.add(Calendar.DAY_OF_YEAR, 122);
            return calendar.after(new Date());
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setLastDateRateDialogShown() {
        String strDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        writeStringToSharedPref(PREFERENCE_LAST_DATE_RATE_DIALOG_SHOWN, strDate);
    }

    public TransitionDTO getPatientModeTransition() {
        if (patientModeTransition == null) {
            patientModeTransition = getObjectFromSharedPreferences(PREFERENCE_PATIENT_MODE_TRANSITION, TransitionDTO.class);
        }
        return patientModeTransition;
    }

    public void setPatientModeTransition(TransitionDTO patientModeTransition) {
        this.patientModeTransition = patientModeTransition;
        writeObjectToSharedPreference(PREFERENCE_PATIENT_MODE_TRANSITION, patientModeTransition);
    }

    public void setRefreshToken(String newValue) {
        writeStringToSharedPref(PREFERENCE_REFRESH_TOKEN, newValue);
    }

    public String getRefreshToken() {
        return readStringFromSharedPref(PREFERENCE_REFRESH_TOKEN, "");
    }
}
