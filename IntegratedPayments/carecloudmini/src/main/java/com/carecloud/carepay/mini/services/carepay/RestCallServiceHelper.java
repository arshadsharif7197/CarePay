package com.carecloud.carepay.mini.services.carepay;

import android.support.annotation.NonNull;

import com.carecloud.carepay.mini.HttpConstants;
import com.carecloud.carepay.mini.interfaces.ApplicationHelper;
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
    private static final String HEADER_KEY_AUTH_TYPE = "x-token-type";
    private static final String HEADER_KEY_USER = "username";
    private static final String HEADER_VALUE_AUTH_TYPE_COGNITO = "jwt";
    private static final String HEADER_VALUE_AUTH_TYPE_KMS = "kms";

    private ApplicationHelper applicationHelper;

    public RestCallServiceHelper(ApplicationHelper applicationHelper){
        this.applicationHelper = applicationHelper;
    }

    public void setApplicationHelper(ApplicationHelper applicationHelper) {
        this.applicationHelper = applicationHelper;
    }


    private Map<String, String> getAuthHeaders(){
        Map<String, String> authHeaders = new HashMap<>();
        authHeaders.put("x-api-key", HttpConstants.getApiStartKey());
        if(applicationHelper != null){
            String username = applicationHelper.getApplicationPreferences().getUsername();
            if(username != null){
                authHeaders.put(HEADER_KEY_USER, username);
            }
            if(applicationHelper.getAuthentication()!= null) {
                String idToken = applicationHelper.getAuthentication().getIdToken();
                if (idToken != null) {
                    authHeaders.put(HEADER_KEY_AUTHORIZATION, idToken);
                    authHeaders.put(HEADER_KEY_AUTH_TYPE, HEADER_VALUE_AUTH_TYPE_COGNITO);
                }
            }
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

    /**
     * Make http call to signin user
     * @param callback callback
     * @param jsonBody payload
     */
    public void executeSignIn(@NonNull final RestCallServiceCallback callback, String jsonBody){

        RestCallService restCallService = RestServiceGenerator.getInstance().createService(RestCallService.class, getAuthHeaders());

        callback.onPreExecute();
        Call<JsonElement> signInCall = restCallService.getSignin(jsonBody);
        signInCall.enqueue(new Callback<JsonElement>() {
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

    /**
     * Make http call to get practice and location info
     * @param callback callback
     * @param mid merchant id retrieved from SDK
     */
    public void executePreRegister(@NonNull final RestCallServiceCallback callback, @NonNull String mid){
        RestCallService restCallService = RestServiceGenerator.getInstance().createService(RestCallService.class, getAuthHeaders());

        callback.onPreExecute();
        Call<JsonElement> signInCall = restCallService.getPreRegistration(mid);
        signInCall.enqueue(new Callback<JsonElement>() {
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

    /**
     * Make Http call to post a payment object id to carepay for processing
     * @param callback callback
     * @param token kms token from sdk
     * @param payload payment object id payload
     */
    public void executePostPayment(@NonNull final RestCallServiceCallback callback, String token, @NonNull String payload){
        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put(HEADER_KEY_AUTH_TYPE, HEADER_VALUE_AUTH_TYPE_KMS);
        customHeaders.put(HEADER_KEY_AUTHORIZATION, token);

        RestCallService restCallService = RestServiceGenerator.getInstance().createService(RestCallService.class, getFullHeaders(customHeaders));

        callback.onPreExecute();
        Call<JsonElement> postPaymentCall = restCallService.postPaymentRequest(payload);
        postPaymentCall.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.isSuccessful()){
                    callback.onPostExecute(response.body());
                }else{
                    callback.onFailure(parseError(response, "error"));
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable throwable) {
                callback.onFailure(throwable.getMessage());
            }
        });
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
