package com.carecloud.shamrocksdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.carecloud.shamrocksdk.constants.AppConstants;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;


import static android.content.Context.MODE_PRIVATE;

/**
 * Helper class for obtaining an Authorization token for accessing Shamrock Payments
 */

public class AuthorizationUtil {
    private static final String AUTH_TOKEN_TYPE_KEY = "token_type";
    private static final String AUTH_TOKEN_KEY = "token";
    private static final String AUTH_USER_KEY = "userLogin";
    private static final String AUTH_TOKEN_TYPE = "kms";


    /**
     * Get DeepStream Authorization JSON payload for DeepStream Token stored in Shared Pref
     * after device registration
     * @param context context
     * @return Authorization JSON
     */
    public static JsonElement getAuthorization(Context context) {

        SharedPreferences sharedPref = context.getSharedPreferences(
                AppConstants.APP_PREFERENCES, MODE_PRIVATE);

        JsonElement authorization = new JsonObject();
        ((JsonObject) authorization).addProperty(AUTH_TOKEN_TYPE_KEY, AUTH_TOKEN_TYPE);
        ((JsonObject) authorization).addProperty(AUTH_TOKEN_KEY, sharedPref.getString(AppConstants.DEEPSTREAM_AUTH_KEY, "") );
        return authorization;
    }

    /**
     * Get Authorization as Map for making HTTP calls to Shamrock Payments for DeepStream Token
     * stored in Shared Pref after device registration
     * @param context context
     * @return Map containing token_type and token
     */
    public static Map<String, String> getAuthorizationMap(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                AppConstants.APP_PREFERENCES, MODE_PRIVATE);

        Map<String, String> authMap = new HashMap<>();
        authMap.put(AUTH_TOKEN_TYPE_KEY, AUTH_TOKEN_TYPE);
        authMap.put(AUTH_TOKEN_KEY, sharedPref.getString(AppConstants.DEEPSTREAM_AUTH_KEY, ""));

        return authMap;
    }

    /**
     * Get Authorization Token String for DeepStream Token stored in Shared Pref
     * after device registration
     * @param context context
     * @return Auth token
     */
    public static String getAuthorizationToken(Context context){
        SharedPreferences sharedPref = context.getSharedPreferences(
                AppConstants.APP_PREFERENCES, MODE_PRIVATE);

        return sharedPref.getString(AppConstants.DEEPSTREAM_AUTH_KEY, "");
    }

    /**
     * Create Authorization based on User id and Auth Token this is intended for use by client
     * implementations for Android clients that are not necessarily Clover devices.
     * @param userId user id
     * @param authToken auth token
     * @return authorization JSON
     */
    public static JsonElement getAuthorization(String userId, String authToken){
        JsonElement authorization = new JsonObject();
        ((JsonObject) authorization).addProperty(AUTH_USER_KEY, userId);
        ((JsonObject) authorization).addProperty(AUTH_TOKEN_KEY, authToken);
        return authorization;
    }

}
