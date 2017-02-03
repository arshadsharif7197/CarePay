package com.carecloud.carepaylibray.utils;

import android.os.Bundle;

import com.google.gson.Gson;

/**
 * Created by cocampo on 2/2/17.
 */

public class DtoHelper {

    /**
     * Common WorkflowDTO which will converted to the desire DTO with dtoClass params
     *
     * @param dtoClass class to convert
     * @param <S>      Dynamic class to convert
     * @return Dynamic converted class object
     */
    public static <S> S getConvertedDTO(Class<S> dtoClass, Bundle bundle) {
        String jsonString = bundle.getString(dtoClass.getSimpleName());

        return getConvertedDTO(dtoClass, jsonString);
    }

    /**
     * Converts to the desire DTO object from String DTO
     *
     * @param dtoClass class to convert
     * @param <S>      Dynamic class to convert
     * @return Dynamic converted class object
     */
    public static <S> S getConvertedDTO(Class<S> dtoClass, String jsonString) {

        if (!StringUtil.isNullOrEmpty(jsonString)) {
            Gson gson = new Gson();
            return gson.fromJson(jsonString, dtoClass);
        }

        return null;
    }

    public static String getStringDTO(Object dto) {
        Gson gson = new Gson();

        return gson.toJson(dto);
    }

    public static void bundleDto(Bundle bundle, Object dto) {
        bundle.putString(dto.getClass().getSimpleName(), getStringDTO(dto));
    }
}
