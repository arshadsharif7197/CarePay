package com.carecloud.carepay.mini.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.carecloud.carepay.mini.models.response.UserPracticeDTO;

/**
 * Created by lmenendez on 6/24/17
 */

public class ApplicationPreferences {
    private static final String APP_PREF = "application_preferences";
    private static final String KEY_USERNAME = "key_username";
    private static final String KEY_PRACTICE_ID = "key_practice_id";
    private static final String KEY_LOCATION_ID = "key_location_id";
    private static final String KEY_DEVICE_NAME = "key_device_name";
    private static final String KEY_WELCOME_MSG = "key_welcome_msg";
    private static final String KEY_IMAGE_STYLE = "key_icon_style";
    private static final String KEY_PRACTICE_INFO = "key_practice_info";
    private static final String KEY_DEVICE_ID = "key_device_id";
    private static final String KEY_SUPPORT_MID = "key_support_mid";

    private SharedPreferences sharedPreferences;

    public ApplicationPreferences(Context context){
        sharedPreferences = context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
    }

    public String getDeviceId(){
        return sharedPreferences.getString(KEY_DEVICE_ID, "");
    }

    public void setDeviceId(String deviceId){
        sharedPreferences.edit().putString(KEY_DEVICE_ID, deviceId).apply();
    }

    public String getUsername(){
        return sharedPreferences.getString(KEY_USERNAME, "");
    }

    public void setUsername(String username){
        sharedPreferences.edit().putString(KEY_USERNAME, username).apply();
    }

    public String getPracticeId(){
        return sharedPreferences.getString(KEY_PRACTICE_ID, "");
    }

    public void setPracticeId(String practiceId){
        sharedPreferences.edit().putString(KEY_PRACTICE_ID, practiceId).apply();
    }

    public String getLocationId(){
        return sharedPreferences.getString(KEY_LOCATION_ID, "");
    }

    public void setLocationId(String locationId){
        sharedPreferences.edit().putString(KEY_LOCATION_ID, locationId).apply();
    }

    public String getDeviceName(){
        return sharedPreferences.getString(KEY_DEVICE_NAME, "");
    }

    public void setDeviceName(String deviceName){
        sharedPreferences.edit().putString(KEY_DEVICE_NAME, deviceName).apply();
    }

    public String getWelcomeMessage(){
        return sharedPreferences.getString(KEY_WELCOME_MSG, "");
    }

    public void setWelcomeMessage(String welcomeMessage){
        sharedPreferences.edit().putString(KEY_WELCOME_MSG, welcomeMessage).apply();
    }

    public @Defs.ImageStyles int getImageStyle(){
        @Defs.ImageStyles int imageStyle =  sharedPreferences.getInt(KEY_IMAGE_STYLE, Defs.IMAGE_STYLE_CARECLOUD_LOGO);
        return imageStyle;
    }

    public void setImageStyle(@Defs.ImageStyles int imageStyle){
        sharedPreferences.edit().putInt(KEY_IMAGE_STYLE, imageStyle).apply();
    }

    public String getSupportMid(){
        return sharedPreferences.getString(KEY_SUPPORT_MID, "");
    }

    public void setSupportMid(String supportMid){
        sharedPreferences.edit().putString(KEY_SUPPORT_MID, supportMid).apply();
    }

    /**
     * Get Saved practice Info
     * @return saved practice info
     */
    public UserPracticeDTO getUserPracticeDTO(){
        String practiceString = sharedPreferences.getString(KEY_PRACTICE_INFO, null);
        if(practiceString == null){
            return null;
        }
        return DtoHelper.getConvertedDTO(UserPracticeDTO.class, practiceString);
    }

    /**
     * Save practice info
     * @param userPracticeDTO practice info
     */
    public void setUserPracticeDTO(UserPracticeDTO userPracticeDTO){
        String practiceString = DtoHelper.getStringDTO(userPracticeDTO);
        sharedPreferences.edit().putString(KEY_PRACTICE_INFO, practiceString).apply();
    }

    /**
     * Convinience method for checking if device is registered
     * @return true if registered
     */
    public boolean isDeviceRegistered(){
        return !StringUtil.isNullOrEmpty(getDeviceId());
    }
}
