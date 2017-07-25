package com.carecloud.carepay.mini.utils;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Created by kkannan on 7/10/17
 */

public class JsonHelper {

    /**
     * Wraps plain string into JSON formatter string.
     * @param jsonString JSON String to format
     * @return formatted json string or original string if exception occurs
     */
    public static String getJSONFormattedString(String jsonString) {
        try {
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(jsonString).getAsJsonObject();

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            return gson.toJson(json);
        } catch (Exception ex) {
            return jsonString;
        }
    }

    /**
     * Converts to the desired DTO object from String DTO
     *
     * @param gsonClass class to convert
     * @param bundle bundle containing JSON
     * @param <S> Dynamic class to convert
     * @return Dynamic converted class object
     */
    public static <S> S getConvertedGSON(Class<S> gsonClass, Bundle bundle) {
        if(bundle == null){
            return null;
        }
        String jsonString = bundle.getString(gsonClass.getSimpleName());

        return getConvertedGSON(gsonClass, jsonString);
    }

    /**
     * Converts to the desired DTO object from String DTO
     *
     * @param gsonClass class to convert
     * @param <S>      Dynamic class to convert
     * @return Dynamic converted class object
     */
    public static <S> S getConvertedGSON(Class<S> gsonClass, String jsonString) {

        if (jsonString != null && !jsonString.trim().equals("")) {
            Gson gson = new Gson();
            return gson.fromJson(jsonString, gsonClass);
        }

        return null;
    }

}
