package com.carecloud.carepaylibray.utils;

import android.content.Intent;
import android.os.Bundle;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
     * Converts to the desire DTO object from JsonObject DTO
     *
     * @param dtoClass class to convert
     * @param workflowDTO  generic workflow to be converted
     * @return Dynamic converted class object
     */
    public static <S> S getConvertedDTO(Class<S> dtoClass, WorkflowDTO workflowDTO) {
        return getConvertedDTO(dtoClass, getStringDTO(workflowDTO));
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

    public static void putExtra(Intent intent, Object dto) {
        intent.putExtra(WorkflowDTO.class.getSimpleName(), getStringDTO(dto));
    }

    /**
     * Convenience method to repackage a DTO from activity to Fragment withoug having to serialize it
     * @param args Output Bundle
     * @param intent Original Intent
     * @param dtoClass Type of object for key name
     */
    public static void bundleBaseDTO(Bundle args, Intent intent, Class dtoClass){
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            args.putString(dtoClass.getSimpleName(), bundle.getString(WorkflowDTO.class.getSimpleName()));
        }
    }

    /**
     * Convenience method to repackage a DTO from activity to Fragment without having to serialize it
     * @param args Output Bundle
     * @param intent Original Intent
     * @param saveKey key to forward the extra
     * @param getKey key to retrieve the extra
     */
    public static void bundleBaseDTO(Bundle args, Intent intent, String saveKey, String getKey){
        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.containsKey(getKey)) {
            try{
                bundle = intent.getBundleExtra(getKey);
                if(bundle.containsKey(getKey)){
                    args.putString(saveKey, bundle.getString(getKey));
                }
            }catch (Exception e){
                args.putString(saveKey, bundle.getString(getKey));
            }
        }
    }

}
