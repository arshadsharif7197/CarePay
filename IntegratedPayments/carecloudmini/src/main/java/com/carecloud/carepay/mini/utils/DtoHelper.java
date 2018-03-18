package com.carecloud.carepay.mini.utils;

import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Created by cocampo on 2/2/17
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
        if(bundle == null){
            return null;
        }
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

    /**
     * Converts to the desire DTO object from JsonObject DTO
     *
     * @param dtoClass class to convert
     * @param <S>      Dynamic class to convert
     * @return Dynamic converted class object
     */
    public static <S> S getConvertedDTO(Class<S> dtoClass, JsonObject jsonObject) {

        if (null != jsonObject) {
            Gson gson = new Gson();
            return gson.fromJson(jsonObject, dtoClass);
        }

        return null;
    }


    /**
     * Converts DTO object to String
     *
     * @param dto object to be converted to String
     * @return String represention of the DTO object
     */
    public static String getStringDTO(Object dto) {
        Gson gson = new Gson();

        return gson.toJson(dto);
    }

    public static void bundleDto(Bundle bundle, Object dto) {
        bundle.putString(dto.getClass().getSimpleName(), getStringDTO(dto));
    }


}
