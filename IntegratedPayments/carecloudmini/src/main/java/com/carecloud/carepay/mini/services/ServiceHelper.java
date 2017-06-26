package com.carecloud.carepay.mini.services;

import android.support.annotation.NonNull;

import com.carecloud.carepay.mini.HttpConstants;
import com.carecloud.carepay.mini.utils.ApplicationPreferences;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kkannan on 5/23/17
 */
public class ServiceHelper {

    private final ApplicationPreferences applicationPreferences;

    private static final int STATUS_CODE_OK = 200;
    private static final int STATUS_CODE_UNAUTHORIZED = 401;
    private static final int STATUS_BAD_REQUEST = 400;
    private static final int STATUS_CODE_UNPROCESSABLE_ENTITY = 422;

    private Stack<Call<?>> callStack = new Stack<>();


    public ServiceHelper(ApplicationPreferences applicationPreferences){
        this.applicationPreferences = applicationPreferences;
    }

    /**
     * Gets application start headers.
     *
     * @return app start headers
     */
    private Map<String, String> getApplicationStartHeaders() {
        Map<String, String> appStartHeaders = new HashMap<>();
        appStartHeaders.put("x-api-key", HttpConstants.getApiStartKey());
        if(applicationPreferences != null){
            String username = applicationPreferences.getUsername();
            if(username != null){
                appStartHeaders.put("username", username);
            }
        }
        return appStartHeaders;
    }

    private Map<String, String>getRequiredHeaders(Map<String, String> userHeaders){
        Map<String, String> requiredHeaders = getApplicationStartHeaders();
        if(userHeaders!=null) {
            requiredHeaders.putAll(userHeaders);
        }
        return requiredHeaders;
    }

    /**
     * Execute.
     *
     * @param serviceRequestDTO the service request dto
     * @param callback          the callback
     */
    public void execute(@NonNull ServiceRequestDTO serviceRequestDTO, @NonNull ServiceCallback callback) {
        execute(serviceRequestDTO, callback, null, null, null);
    }

    /**
     * Execute.
     *
     * @param serviceRequestDTO the service request dto
     * @param callback          the callback
     * @param jsonBody          the json body
     */
    public void execute(@NonNull ServiceRequestDTO serviceRequestDTO, @NonNull final ServiceCallback callback, String jsonBody) {
        execute(serviceRequestDTO, callback, jsonBody, null);
    }

    /**
     * Execute.
     *
     * @param serviceRequestDTO the service request dto
     * @param callback          the callback
     * @param queryMap          the query map
     */
    public void execute(@NonNull ServiceRequestDTO serviceRequestDTO, @NonNull final ServiceCallback callback, Map<String, String> queryMap) {
        execute(serviceRequestDTO, callback, null, queryMap, null);
    }

    /**
     * Execute.
     *
     * @param serviceRequestDTO the service request dto
     * @param callback          the callback
     * @param jsonBody          the json body
     * @param queryMap          the query map
     */
    public void execute(@NonNull ServiceRequestDTO serviceRequestDTO, @NonNull final ServiceCallback callback, String jsonBody, Map<String, String> queryMap) {
        execute(serviceRequestDTO, callback, jsonBody, queryMap, null);
    }

    /**
     * Execute.
     *
     * @param serviceRequestDTO the service request dto
     * @param callback          the callback
     * @param queryMap          the query map
     * @param customHeaders     the custom headers
     */
    public void execute(@NonNull ServiceRequestDTO serviceRequestDTO, @NonNull final ServiceCallback callback, Map<String, String> queryMap, Map<String, String> customHeaders) {
        execute(serviceRequestDTO, callback, null, queryMap, customHeaders);
    }

    /**
     * Execute.
     *
     * @param serviceRequestDTO the service request dto
     * @param callback          the callback
     * @param jsonBody          the json body
     * @param queryMap          the query map
     * @param customHeaders     the custom headers
     */
    public void execute(@NonNull ServiceRequestDTO serviceRequestDTO, @NonNull final ServiceCallback callback, String jsonBody, Map<String, String> queryMap, Map<String, String> customHeaders) {
        executeRequest(serviceRequestDTO, callback, jsonBody, queryMap, customHeaders, 0);
    }

    private void executeRequest(@NonNull ServiceRequestDTO serviceRequestDTO,
                                @NonNull final ServiceCallback callback,
                                String jsonBody,
                                Map<String, String> queryMap,
                                Map<String, String> headers,
                                int attemptCount) {

        callback.onPreExecute();
        headers = getRequiredHeaders(headers);
//        queryMap = updateQueryMapWithDefault(queryMap);
        ServiceInterface workflowService = ServiceGenerator.getInstance().createService(ServiceInterface.class, headers); //, String token, String searchString
        Call<ServiceResponseDTO> call;

        if (serviceRequestDTO.isGet()) {
            if (jsonBody != null && queryMap == null) {
                call = workflowService.executeGet(serviceRequestDTO.getUrl(), jsonBody);
            } else if (jsonBody == null && queryMap != null) {
                call = workflowService.executeGet(serviceRequestDTO.getUrl(), queryMap);
            } else if (jsonBody != null && queryMap.size() > 0) {
                call = workflowService.executeGet(serviceRequestDTO.getUrl(), jsonBody, queryMap);
            } else {
                call = workflowService.executeGet(serviceRequestDTO.getUrl());
            }
        } else if (serviceRequestDTO.isPost()) {
            if (jsonBody != null && queryMap == null) {
                call = workflowService.executePost(serviceRequestDTO.getUrl(), jsonBody);
            } else if (jsonBody == null && queryMap != null) {
                call = workflowService.executePost(serviceRequestDTO.getUrl(), queryMap);
            } else if (jsonBody != null && queryMap.size() > 0) {
                call = workflowService.executePost(serviceRequestDTO.getUrl(), jsonBody, queryMap);
            } else if (jsonBody != null) {
                call = workflowService.executePost(serviceRequestDTO.getUrl(), jsonBody, queryMap);
            } else {
                call = workflowService.executePost(serviceRequestDTO.getUrl());
            }
        } else if (serviceRequestDTO.isDelete()) {
            if (jsonBody != null && queryMap == null) {
                call = workflowService.executeDelete(serviceRequestDTO.getUrl(), jsonBody);
            } else if (jsonBody == null && queryMap != null) {
                call = workflowService.executeDelete(serviceRequestDTO.getUrl(), queryMap);
            } else if (jsonBody != null && queryMap.size() > 0) {
                call = workflowService.executeDelete(serviceRequestDTO.getUrl(), jsonBody, queryMap);
            } else {
                call = workflowService.executeDelete(serviceRequestDTO.getUrl());
            }
        } else {
            if (jsonBody != null && queryMap == null) {
                call = workflowService.executePut(serviceRequestDTO.getUrl(), jsonBody);
            } else if (jsonBody == null && queryMap != null) {
                call = workflowService.executePut(serviceRequestDTO.getUrl(), queryMap);
            } else if (jsonBody != null && queryMap.size() > 0) {
                call = workflowService.executePut(serviceRequestDTO.getUrl(), jsonBody, queryMap);
            } else if (jsonBody != null) {
                call = workflowService.executePut(serviceRequestDTO.getUrl(), jsonBody, queryMap);
            } else {
                call = workflowService.executePut(serviceRequestDTO.getUrl());
            }
        }

        executeCallback(serviceRequestDTO, callback, jsonBody, queryMap, headers, attemptCount, call);
    }

    private void executeCallback(@NonNull final ServiceRequestDTO serviceRequestDTO,
                                 @NonNull final ServiceCallback callback,
                                 final String jsonBody,
                                 final Map<String, String> queryMap,
                                 final Map<String, String> headers,
                                 final int attemptCount,
                                 final Call<ServiceResponseDTO> call) {
        call.enqueue(new Callback<ServiceResponseDTO>() {

            @Override
            public void onResponse(Call<ServiceResponseDTO> call, Response<ServiceResponseDTO> response) {
                if(response.isSuccessful()) {
                    callback.onPostExecute(response.body());
                }else{
                    callback.onFailure(parseError(response));
                }
            }

            @Override
            public void onFailure(Call<ServiceResponseDTO> call, Throwable t) {
                callback.onFailure(t.getMessage());
            }
        });
    }


    private String parseError(Response<?> response){
        String message = response.message();
        try{
            JsonElement jsonElement = new JsonParser().parse(response.errorBody().string());
            String error = getErrorElement(jsonElement, "data");
            if(error != null){
                message = error;
            }
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
        return message;
    }

    private String getErrorElement(JsonElement jsonElement, String name){
        if(jsonElement instanceof JsonObject){
            JsonObject jsonObject = (JsonObject) jsonElement;
            Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
            for(Map.Entry<String, JsonElement> entry : entrySet){
                if(entry.getKey().equals(name) && entry.getValue() instanceof JsonPrimitive){
                    return entry.getValue().getAsString();
                }
                if(entry.getValue() instanceof JsonObject){
                    String error = getErrorElement(entry.getValue(), name);
                    if(error != null){
                        return error;
                    }
                }
            }
        }else if(jsonElement instanceof JsonArray){
            JsonArray array = (JsonArray) jsonElement;
            Iterator<JsonElement> iterator = array.iterator();
            while (iterator.hasNext()){
                String error = getErrorElement(iterator.next(), name);
                if(error != null){
                    return error;
                }
            }
        }
        return null;
    }
}
