package com.carecloud.shamrocksdk.services;


import android.util.Log;

import androidx.annotation.NonNull;

import com.carecloud.shamrocksdk.constants.HttpConstants;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import org.json.JSONException;
import org.json.JSONObject;

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
 * Helper for executing http requests
 */
public class ServiceHelper {


    private static final int STATUS_CODE_OK = 200;
    private static final int STATUS_CODE_UNAUTHORIZED = 401;
    private static final int STATUS_BAD_REQUEST = 400;
    private static final int STATUS_CODE_UNPROCESSABLE_ENTITY = 422;
    private static final int STATUS_CODE_BAD_GATEWAY = 502;

    private Stack<Call<?>> callStack = new Stack<>();


    private Map<String, String> getAuthHeaders(){
        Map<String, String> authHeaders = new HashMap<>();
        authHeaders.put("x-api-key", HttpConstants.getxApiKey());

        return authHeaders;
    }


    private Map<String, String> getFullHeaders(Map<String, String > customHeaders){
        Map <String, String> fullHeaders = getAuthHeaders();
        if(customHeaders != null) {
            customHeaders.putAll(fullHeaders);
            return customHeaders;
        }
        return fullHeaders;
    }

    /**
     * Execute.
     *
     * @param serviceRequest the service request dto
     * @param callback       the callback
     */
    public void execute(@NonNull ServiceRequest serviceRequest, @NonNull ServiceCallback callback) {
        execute(serviceRequest, callback, null, null, null);
    }

    /**
     * Execute.
     *
     * @param serviceRequest the service request dto
     * @param callback       the callback
     * @param jsonBody       the json body
     */
    public void execute(@NonNull ServiceRequest serviceRequest, @NonNull final ServiceCallback callback, String jsonBody) {
        execute(serviceRequest, callback, jsonBody, null);
    }

    /**
     * Execute.
     *
     * @param serviceRequest the service request dto
     * @param callback       the callback
     * @param queryMap       the query map
     */
    public void execute(@NonNull ServiceRequest serviceRequest, @NonNull final ServiceCallback callback, Map<String, String> queryMap) {
        execute(serviceRequest, callback, null, queryMap, null);
    }

    /**
     * Execute.
     *
     * @param serviceRequest the service request dto
     * @param callback       the callback
     * @param jsonBody       the json body
     * @param queryMap       the query map
     */
    public void execute(@NonNull ServiceRequest serviceRequest, @NonNull final ServiceCallback callback, String jsonBody, Map<String, String> queryMap) {
        execute(serviceRequest, callback, jsonBody, queryMap, null);
    }

    /**
     * Execute.
     *
     * @param serviceRequest the service request dto
     * @param callback       the callback
     * @param queryMap       the query map
     * @param customHeaders  the custom headers
     */
    public void execute(@NonNull ServiceRequest serviceRequest, @NonNull final ServiceCallback callback, Map<String, String> queryMap, Map<String, String> customHeaders) {
        execute(serviceRequest, callback, null, queryMap, customHeaders);
    }

    /**
     * Execute.
     *
     * @param serviceRequest the service request dto
     * @param callback       the callback
     * @param jsonBody       the json body
     * @param queryMap       the query map
     * @param customHeaders  the custom headers
     */
    public void execute(@NonNull ServiceRequest serviceRequest, @NonNull final ServiceCallback callback, String jsonBody, Map<String, String> queryMap, Map<String, String> customHeaders) {
        executeRequest(serviceRequest, callback, jsonBody, queryMap, getFullHeaders(customHeaders), 0);
    }

    private void executeRequest(@NonNull ServiceRequest serviceRequest,
                                @NonNull final ServiceCallback callback,
                                String jsonBody,
                                Map<String, String> queryMap,
                                Map<String, String> headers,
                                int attemptCount) {

        callback.onPreExecute();
//        queryMap = updateQueryMapWithDefault(queryMap);
        ServiceInterface workflowService = ServiceGenerator.getInstance().createService(ServiceInterface.class, headers); //, String token, String searchString
        Call<JsonElement> call = null;

        if (serviceRequest.isGet()) {
            if (jsonBody != null && queryMap == null) {
                call = workflowService.executeGet(serviceRequest.getUrl(), jsonBody);
            } else if (jsonBody == null && queryMap != null) {
                call = workflowService.executeGet(serviceRequest.getUrl(), queryMap);
            } else if (jsonBody != null && queryMap.size() > 0) {
                call = workflowService.executeGet(serviceRequest.getUrl(), jsonBody, queryMap);
            } else {
                call = workflowService.executeGet(serviceRequest.getUrl());
            }
        } else if (serviceRequest.isPost()) {
            if (jsonBody != null && queryMap == null) {
                call = workflowService.executePost(serviceRequest.getUrl(), jsonBody);
            } else if (jsonBody == null && queryMap != null) {
                call = workflowService.executePost(serviceRequest.getUrl(), queryMap);
            } else if (jsonBody != null && queryMap.size() > 0) {
                call = workflowService.executePost(serviceRequest.getUrl(), jsonBody, queryMap);
            } else if (jsonBody != null) {
                call = workflowService.executePost(serviceRequest.getUrl(), jsonBody, queryMap);
            } else {
                call = workflowService.executePost(serviceRequest.getUrl());
            }
        } else if (serviceRequest.isDelete()) {
            if (jsonBody != null && queryMap == null) {
                call = workflowService.executeDelete(serviceRequest.getUrl(), jsonBody);
            } else if (jsonBody == null && queryMap != null) {
                call = workflowService.executeDelete(serviceRequest.getUrl(), queryMap);
            } else if (jsonBody != null && queryMap.size() > 0) {
                call = workflowService.executeDelete(serviceRequest.getUrl(), jsonBody, queryMap);
            } else {
                call = workflowService.executeDelete(serviceRequest.getUrl());
            }
        } else {
            if (jsonBody != null && queryMap == null) {
                call = workflowService.executePut(serviceRequest.getUrl(), jsonBody);
            } else if (jsonBody == null && queryMap != null) {
                call = workflowService.executePut(serviceRequest.getUrl(), queryMap);
            } else if (jsonBody != null && queryMap.size() > 0) {
                call = workflowService.executePut(serviceRequest.getUrl(), jsonBody, queryMap);
            } else if (jsonBody != null) {
                call = workflowService.executePut(serviceRequest.getUrl(), jsonBody, queryMap);
            } else {
                call = workflowService.executePut(serviceRequest.getUrl());
            }
        }

        callStack.add(call);
        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                try {
                    switch (response.code()) {
                        case STATUS_CODE_OK:
                            callback.onPostExecute(response.body());
                            break;
                        case STATUS_CODE_BAD_GATEWAY:
                            onBadGateway(response);
                            break;
                        default:
                            onFailure(response);

                    }
                } catch (Exception exception) {
                    handleException(exception);
                }

            }

            private void onBadGateway(Response<JsonElement> response) throws IOException {
                String message = response.message().toLowerCase();
                String errorBodyString = "";
                try {
                    errorBodyString = response.errorBody().string();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                try {
                    if(errorBodyString.contains("html"))
                    {
                        errorBodyString = "502 Bad Gateway" ;
                    }else{
                        JSONObject json = new JSONObject(errorBodyString);
                        if (json.has("exception")) {
                            JSONObject exceptionJson = json.getJSONObject("exception");
                            if (exceptionJson.has("body")) {
                                JSONObject bodyJson = exceptionJson.getJSONObject("body");
                                if (bodyJson.has("error")) {
                                    errorBodyString = bodyJson.getString("error");
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                onFailure(errorBodyString);
            }

            private void onFailure(Response<JsonElement> response) throws IOException {
                if (null != response.errorBody()) {
                    onFailure(response.errorBody().string());
                } else {
                    onFailure("");
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callStack.remove(call);
                onFailure(throwable.getMessage());
            }

            void onFailure(String errorMessage) {
                callback.onFailure(errorMessage);
            }

            private void handleException(Exception exception) {
                if (exception.getMessage() != null) {
                    callback.onFailure(exception.getMessage());
                    Log.e("WorkflowServiceHelper", exception.getMessage(), exception);
                } else {
                    callback.onFailure(HttpConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
                }
            }
        });
    }

    public static String findErrorElement(JsonElement jsonElement, String errorFieldName){
        if(jsonElement instanceof JsonObject){
            JsonObject jsonObject = (JsonObject) jsonElement;
            Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
            for(Map.Entry<String, JsonElement> entry : entrySet){
                if(entry.getKey().equals(errorFieldName) && entry.getValue() instanceof JsonPrimitive){
                    return entry.getValue().getAsString();
                }
                if(entry.getValue() instanceof JsonObject){
                    String error = findErrorElement(entry.getValue(), errorFieldName);
                    if(error != null){
                        return error;
                    }
                }
            }
        }else if(jsonElement instanceof JsonArray){
            JsonArray array = (JsonArray) jsonElement;
            Iterator<JsonElement> iterator = array.iterator();
            while (iterator.hasNext()){
                String error = findErrorElement(iterator.next(), errorFieldName);
                if(error != null){
                    return error;
                }
            }
        }
        return null;
    }

}