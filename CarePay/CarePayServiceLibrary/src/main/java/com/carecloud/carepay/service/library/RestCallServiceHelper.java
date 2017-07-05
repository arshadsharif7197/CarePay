package com.carecloud.carepay.service.library;

import android.support.annotation.NonNull;

import com.carecloud.carepay.service.library.cognito.AppAuthorizationHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by lmenendez on 6/28/17
 */

public class RestCallServiceHelper {
    private static final String HEADER_KEY_AUTHORIZATION = "Authorization";
    private static final String HEADER_KEY_USER = "username";

    private AppAuthorizationHelper appAuthorizationHelper;
    private ApplicationMode applicationMode;

    public RestCallServiceHelper(AppAuthorizationHelper appAuthorizationHelper,
                                 ApplicationMode applicationMode){
        this.appAuthorizationHelper = appAuthorizationHelper;
        this.applicationMode = applicationMode;
    }

    public void setAppAuthorizationHelper(AppAuthorizationHelper appAuthorizationHelper) {
        this.appAuthorizationHelper = appAuthorizationHelper;
    }

    public void setApplicationMode(ApplicationMode applicationMode) {
        this.applicationMode = applicationMode;
    }

    /**
     * Make Rest service call
     * @param method REST method
     * @param baseUrl base url
     * @param callback callback method
     * @param queryMap optional query params
     * @param headerMap optional headers
     * @param jsonBody optional request body
     * @param pathParams optional path params, must be passed in order
     */
    public void executeRequest(@RestDef.RestMethod String method,
                               @NonNull String baseUrl,
                               @NonNull RestCallServiceCallback callback,
                               Map<String, String> queryMap,
                               Map<String, String> headerMap,
                               String jsonBody,
                               String... pathParams){

        executeRequest(method, baseUrl, callback, false, queryMap, headerMap, jsonBody, pathParams);
    }

    /**
     * Make Rest service call with option to set authorization headers as query params
     * @param method REST method
     * @param baseUrl base url
     * @param callback callback method
     * @param authQueryParams optionally pass header auths as query params
     * @param queryMap optional query params
     * @param headerMap optional headers
     * @param jsonBody optional request body
     * @param pathParams optional path params, must be passed in order
     */
    public void executeRequest(@RestDef.RestMethod String method,
                               @NonNull String baseUrl,
                               @NonNull final RestCallServiceCallback callback,
                               boolean authQueryParams,
                               Map<String, String> queryMap,
                               Map<String, String> headerMap,
                               String jsonBody,
                               String... pathParams) {

        executeRequest(method, baseUrl, callback, authQueryParams, null, queryMap, headerMap, jsonBody, pathParams);
    }

    /**
     * Make Rest service call with option to set authorization headers as query params
     * @param method REST method
     * @param baseUrl base url
     * @param callback callback method
     * @param authQueryParams optionally pass header auths as query params
     * @param authTokenName optional name to pass for the auth token in query param
     * @param queryMap optional query params
     * @param headerMap optional headers
     * @param jsonBody optional request body
     * @param pathParams optional path params, must be passed in order
     */
    public void executeRequest(@RestDef.RestMethod String method,
                               @NonNull String baseUrl,
                               @NonNull final RestCallServiceCallback callback,
                               boolean authQueryParams,
                               String authTokenName,
                               Map<String, String> queryMap,
                               Map<String, String> headerMap,
                               String jsonBody,
                               String... pathParams){

        callback.onPreExecute();
        String urlPath = geturlPath(pathParams);
        if(authQueryParams){
            queryMap = getAuthQueryParams(queryMap, authTokenName);
        }
        Call<JsonElement> requestCall = getServiceCall(method, baseUrl, urlPath, headerMap, queryMap, jsonBody);
        requestCall.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.isSuccessful()){
                    callback.onPostExecute(response.body());
                }else{
                    callback.onFailure(parseError(response, "data", "error"));
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onFailure(throwable.getMessage());
            }
        });
    }


    private Map<String, String> getAuthHeaders(){
        Map<String, String> authHeaders = new HashMap<>();
        if(appAuthorizationHelper != null && applicationMode != null){
            authHeaders.put(HEADER_KEY_AUTHORIZATION, appAuthorizationHelper.getIdToken());
            authHeaders.put(HEADER_KEY_USER, appAuthorizationHelper.getCurrUser());
        }
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

    private Map<String, String> getAuthQueryParams(Map<String, String> queryParams, String authTokenKey){
        Map<String, String> authQueryParams = getAuthHeaders();
        if(authTokenKey != null && authTokenKey.trim().length() > 0){
            String token = authQueryParams.get(HEADER_KEY_AUTHORIZATION);
            authQueryParams.remove(HEADER_KEY_AUTHORIZATION);
            authQueryParams.put(authTokenKey, token);
        }

        if(queryParams != null){
            queryParams.putAll(authQueryParams);
            return queryParams;
        }
        return authQueryParams;
    }

    private Call<JsonElement> getServiceCall(@RestDef.RestMethod String method,
                                             String baseUrl,
                                             String urlPath,
                                             Map<String, String> headerMap,
                                             Map<String, String> queryMap,
                                             String jsonBody){
        RestCallService restCallService = RestServiceGenerator.getInstance().createService(RestCallService.class, getFullHeaders(headerMap), baseUrl);
        switch (method){
            default:
            case RestDef.GET:
                return getGetCall(restCallService, urlPath, queryMap, jsonBody);
            case RestDef.POST:
                return getPostCall(restCallService, urlPath, queryMap, jsonBody);
            case RestDef.DELETE:
                return getDeleteCall(restCallService, urlPath, queryMap, jsonBody);
            case RestDef.PUT:
                return getPutCall(restCallService, urlPath, queryMap, jsonBody);
        }
    }

    private Call<JsonElement> getGetCall(RestCallService restCallService,
                                         String urlPath,
                                         Map<String, String> queryMap,
                                         String jsonBody){
        if(jsonBody != null && queryMap != null && !queryMap.isEmpty()){
            return restCallService.executeGet(urlPath, jsonBody, queryMap);
        }

        if(queryMap != null && !queryMap.isEmpty()){
            return restCallService.executeGet(urlPath, queryMap);
        }

        if(jsonBody != null){
            return restCallService.executeGet(urlPath, jsonBody);
        }

        return restCallService.executeGet(urlPath);
    }

    private Call<JsonElement> getPostCall(RestCallService restCallService,
                                          String urlPath,
                                          Map<String, String> queryMap,
                                          String jsonBody){
        if(jsonBody != null && queryMap != null && !queryMap.isEmpty()){
            return restCallService.executePost(urlPath, jsonBody, queryMap);
        }

        if(queryMap != null && !queryMap.isEmpty()){
            return restCallService.executePost(urlPath, queryMap);
        }

        if(jsonBody != null){
            return restCallService.executePost(urlPath, jsonBody);
        }

        return restCallService.executePost(urlPath);

    }

    private Call<JsonElement> getDeleteCall(RestCallService restCallService,
                                            String urlPath,
                                            Map<String, String> queryMap,
                                            String jsonBody){
        if(jsonBody != null && queryMap != null && !queryMap.isEmpty()){
            return restCallService.executeDelete(urlPath, jsonBody, queryMap);
        }

        if(queryMap != null && !queryMap.isEmpty()){
            return restCallService.executeDelete(urlPath, queryMap);
        }

        if(jsonBody != null){
            return restCallService.executeDelete(urlPath, jsonBody);
        }

        return restCallService.executeDelete(urlPath);

    }

    private Call<JsonElement> getPutCall(RestCallService restCallService,
                                         String urlPath,
                                         Map<String, String> queryMap,
                                         String jsonBody){
        if(jsonBody != null && queryMap != null && !queryMap.isEmpty()){
            return restCallService.executePut(urlPath, jsonBody, queryMap);
        }

        if(queryMap != null && !queryMap.isEmpty()){
            return restCallService.executePut(urlPath, queryMap);
        }

        if(jsonBody != null){
            return restCallService.executePut(urlPath, jsonBody);
        }

        return restCallService.executePut(urlPath);

    }

    private static String geturlPath(String... pathParams){
        StringBuilder urlBuilder = new StringBuilder();
        if(pathParams != null){
            for (int i=0; i<pathParams.length; i++){
                urlBuilder.append("/");
                urlBuilder.append(pathParams[i]);
            }
        }
        return urlBuilder.toString();
    }

    private static String parseError(Response<?> response, @NonNull String... errorFields){
        String message = response.message();
        try{
            JsonElement jsonElement = new JsonParser().parse(response.errorBody().string());
            String error;
            for(String errorField : errorFields) {
                error = findErrorElement(jsonElement, errorField);
                if (error != null) {
                    message = error;
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return message;
    }

    private static String findErrorElement(JsonElement jsonElement, String errorFieldName){
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
