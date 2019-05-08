package com.carecloud.carepay.service.library;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.carecloud.carepay.service.library.cognito.AppAuthorizationHelper;
import com.carecloud.carepay.service.library.cognito.CognitoActionCallback;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.FaultResponseDTO;
import com.carecloud.carepay.service.library.dtos.RefreshDTO;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;
import com.carecloud.carepay.service.library.label.Label;
import com.carecloud.carepay.service.library.platform.AndroidPlatform;
import com.carecloud.carepay.service.library.platform.Platform;
import com.carecloud.carepay.service.library.unifiedauth.RefreshTokenDto;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedAuthenticationTokens;
import com.google.gson.Gson;
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
 * Service workflow helper a generic helper call wrapper that expose all possible API function calls.
 * Singleton, initialized from application class.
 * User information Dynamic headers added by default
 * Application start Header "x-api-key" added from HttpConstants
 */

public class WorkflowServiceHelper {

    private static final String TOKEN = "token";
    private static final String REVOKED = "revoked";
    private static final String EXPIRED = "expired";
    private static final String UNAUTHORIZED = "unauthorized";

    private static final int STATUS_CODE_OK = 200;
    private static final int STATUS_CODE_UNAUTHORIZED = 401;
    private static final int STATUS_BAD_REQUEST = 400;
    private static final int STATUS_CODE_UNPROCESSABLE_ENTITY = 422;

    private AppAuthorizationHelper appAuthorizationHelper;
    private ApplicationPreferences applicationPreferences;
    private ApplicationMode applicationMode;

    private Stack<Call<?>> callStack = new Stack<>();

    public WorkflowServiceHelper(ApplicationPreferences applicationPreferences,
                                 ApplicationMode applicationMode) {
        this.applicationPreferences = applicationPreferences;
        this.applicationMode = applicationMode;
    }

    public void setAppAuthorizationHelper(AppAuthorizationHelper appAuthorizationHelper) {
        this.appAuthorizationHelper = appAuthorizationHelper;
    }

    /**
     * get request headers
     *
     * @param customHeaders collection of custom headers
     * @return collection of headers
     */
    private Map<String, String> getHeaders(Map<String, String> customHeaders) {
        Map<String, String> headers = getUserAuthenticationHeaders();

        if ((applicationMode.getApplicationType() == ApplicationMode.ApplicationType.PATIENT)
                && (ApplicationPreferences.getInstance().getProfileId() != null)
                && (!ApplicationPreferences.getInstance().getProfileId().isEmpty())) {
            headers.put("username_patient", ApplicationPreferences.getInstance().getProfileId());
        }

        // Add auth headers to custom in case custom has old auth headers
        if (customHeaders != null) {
            customHeaders.remove("Authorization");

            customHeaders.putAll(headers);

            if (customHeaders.containsKey("AccessToken")) {
                customHeaders.put("Authorization", customHeaders.get("AccessToken"));
                customHeaders.remove("AccessToken");
            }

            return customHeaders;
        }

        return headers;
    }

    /**
     * Default headers user information
     *
     * @return collection user auth heaters
     */
    private Map<String, String> getUserAuthenticationHeaders() {
        Map<String, String> userAuthHeaders = new HashMap<>();

        if (appAuthorizationHelper != null) {

            if ((applicationMode.getApplicationType() == ApplicationMode.ApplicationType.PRACTICE ||
                    applicationMode.getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) &&
                    applicationMode.getUserPracticeDTO() != null) {
                String username = applicationMode.getUserPracticeDTO().getUserName();
                if (username == null) {
                    username = appAuthorizationHelper.getCurrUser();
                }
                userAuthHeaders.put("username", username);

                if (applicationMode.getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
                    userAuthHeaders.put("username_patient", appAuthorizationHelper.getCurrUser());
                }

            } else {
                userAuthHeaders.put("username", appAuthorizationHelper.getCurrUser());
            }

            userAuthHeaders.put("Authorization", appAuthorizationHelper.getIdToken());

        }

        userAuthHeaders.putAll(getPreferredLanguageHeader());
        return userAuthHeaders;
    }

    /**
     * @return app start headers
     */
    public Map<String, String> getApplicationStartHeaders() {
        Map<String, String> appStartHeaders = new HashMap<>();
        appStartHeaders.put("x-api-key", HttpConstants.getApiStartKey());
        if (applicationPreferences.getUserLanguage().isEmpty()) {
            appStartHeaders.put("Accept-Language", CarePayConstants.DEFAULT_LANGUAGE);
        } else {
            appStartHeaders.put("Accept-Language", applicationPreferences.getUserLanguage());
        }
        return appStartHeaders;
    }

    /**
     * @return map with language header
     */
    public Map<String, String> getPreferredLanguageHeader() {
        Map<String, String> prefredLanguage = new HashMap<>();
        if (applicationPreferences.getUserLanguage().isEmpty()) {
            prefredLanguage.put("Accept-Language", CarePayConstants.DEFAULT_LANGUAGE);
        } else {
            prefredLanguage.put("Accept-Language", applicationPreferences.getUserLanguage());
        }
        return prefredLanguage;
    }

    /**
     * Application Start request
     *
     * @param callback UI callback
     */
    public void executeApplicationStartRequest(final WorkflowServiceCallback callback) {
        TransitionDTO transitionDTO = new TransitionDTO();
        transitionDTO.setMethod("GET");
        transitionDTO.setUrl(HttpConstants.getApiStartUrl());
        Map<String, String> query = new HashMap<>();
        query.put("language", applicationPreferences.getUserLanguage());
        execute(transitionDTO, callback, null, query, getApplicationStartHeaders());
    }

    public void execute(@NonNull TransitionDTO transitionDTO,
                        @NonNull WorkflowServiceCallback callback) {
        execute(transitionDTO, callback, null, null, null);
    }

    public void execute(@NonNull TransitionDTO transitionDTO,
                        @NonNull final WorkflowServiceCallback callback,
                        String jsonBody) {
        execute(transitionDTO, callback, jsonBody, null);
    }

    public void execute(@NonNull TransitionDTO transitionDTO,
                        @NonNull final WorkflowServiceCallback callback,
                        Map<String, String> queryMap) {
        execute(transitionDTO, callback, null, queryMap, null);
    }

    public void execute(@NonNull TransitionDTO transitionDTO,
                        @NonNull final WorkflowServiceCallback callback,
                        String jsonBody,
                        Map<String, String> queryMap) {
        execute(transitionDTO, callback, jsonBody, queryMap, null);
    }

    public void execute(@NonNull TransitionDTO transitionDTO,
                        @NonNull final WorkflowServiceCallback callback,
                        Map<String, String> queryMap,
                        Map<String, String> customHeaders) {
        execute(transitionDTO, callback, null, queryMap, customHeaders);
    }

    public void execute(@NonNull TransitionDTO transitionDTO,
                        @NonNull final WorkflowServiceCallback callback,
                        String jsonBody,
                        Map<String, String> queryMap,
                        Map<String, String> customHeaders) {
        executeRequest(transitionDTO, callback, jsonBody, queryMap, getHeaders(customHeaders), 0);
    }

    private Map<String, String> updateQueryMapWithDefault(Map<String, String> queryMap) {
        if (queryMap == null) {
            queryMap = new HashMap<>();
        }
        if (applicationMode.getUserPracticeDTO() != null && !queryMap.containsKey("practice_mgmt")) {
            queryMap.put("practice_mgmt", applicationMode.getUserPracticeDTO().getPracticeMgmt());
            queryMap.put("practice_id", applicationMode.getUserPracticeDTO().getPracticeId());
        }

        return queryMap;
    }

    private void executeRequest(@NonNull TransitionDTO transitionDTO,
                                @NonNull final WorkflowServiceCallback callback,
                                String jsonBody,
                                Map<String, String> queryMap,
                                Map<String, String> headers,
                                int attemptCount) {

        callback.onPreExecute();
        queryMap = updateQueryMapWithDefault(queryMap);
        //, String token, String searchString
        WorkflowService workflowService = ServiceGenerator.getInstance().createService(WorkflowService.class, headers);
        if (transitionDTO != null) {
            Call<WorkflowDTO> call;
            if (transitionDTO.isGet()) {
                if (jsonBody != null && queryMap == null) {
                    call = workflowService.executeGet(transitionDTO.getUrl(), jsonBody);
                } else if (jsonBody == null && queryMap != null) {
                    call = workflowService.executeGet(transitionDTO.getUrl(), queryMap);
                } else if (jsonBody != null && queryMap.size() > 0) {
                    call = workflowService.executeGet(transitionDTO.getUrl(), jsonBody, queryMap);
                } else {
                    call = workflowService.executeGet(transitionDTO.getUrl());
                }
            } else if (transitionDTO.isPost()) {
                if (jsonBody != null && queryMap == null) {
                    call = workflowService.executePost(transitionDTO.getUrl(), jsonBody);
                } else if (jsonBody == null && queryMap != null) {
                    call = workflowService.executePost(transitionDTO.getUrl(), queryMap);
                } else if (jsonBody != null && queryMap.size() > 0) {
                    call = workflowService.executePost(transitionDTO.getUrl(), jsonBody, queryMap);
                } else if (jsonBody != null) {
                    call = workflowService.executePost(transitionDTO.getUrl(), jsonBody, queryMap);
                } else {
                    call = workflowService.executePost(transitionDTO.getUrl());
                }
            } else if (transitionDTO.isDelete()) {
                if (jsonBody != null && queryMap == null) {
                    call = workflowService.executeDelete(transitionDTO.getUrl(), jsonBody);
                } else if (jsonBody == null && queryMap != null) {
                    call = workflowService.executeDelete(transitionDTO.getUrl(), queryMap);
                } else if (jsonBody != null && queryMap.size() > 0) {
                    call = workflowService.executeDelete(transitionDTO.getUrl(), jsonBody, queryMap);
                } else {
                    call = workflowService.executeDelete(transitionDTO.getUrl());
                }
            } else {
                if (jsonBody != null && queryMap == null) {
                    call = workflowService.executePut(transitionDTO.getUrl(), jsonBody);
                } else if (jsonBody == null && queryMap != null) {
                    call = workflowService.executePut(transitionDTO.getUrl(), queryMap);
                } else if (jsonBody != null && queryMap.size() > 0) {
                    call = workflowService.executePut(transitionDTO.getUrl(), jsonBody, queryMap);
                } else if (jsonBody != null) {
                    call = workflowService.executePut(transitionDTO.getUrl(), jsonBody, queryMap);
                } else {
                    call = workflowService.executePut(transitionDTO.getUrl());
                }
            }
            executeCallback(transitionDTO, callback, jsonBody, queryMap, headers, attemptCount, call);
        } else {
            Log.e("Carepay Error", "null Transition");
        }
    }

    private void executeCallback(@NonNull final TransitionDTO transitionDTO,
                                 @NonNull final WorkflowServiceCallback callback,
                                 final String jsonBody,
                                 final Map<String, String> queryMap,
                                 final Map<String, String> headers,
                                 final int attemptCount,
                                 final Call<WorkflowDTO> call) {

        call.enqueue(new Callback<WorkflowDTO>() {
            boolean shouldRetryRequest = false;
            final String retryErrorCodes = "403|408|409";

            @Override
            public void onResponse(Call<WorkflowDTO> call, Response<WorkflowDTO> response) {
                callStack.remove(call);
                try {
                    shouldRetryRequest = retryErrorCodes.contains(String.valueOf(response.code())) || response.code() > 422;
                    switch (response.code()) {
                        case STATUS_CODE_OK:
                            onResponseOk(response);
                            break;
                        case STATUS_CODE_UNAUTHORIZED:
                            onResponseUnauthorized(response);
                            break;
                        case STATUS_BAD_REQUEST:
                            onResponseBadRequest(response);
                            break;
                        case STATUS_CODE_UNPROCESSABLE_ENTITY:
                            onFailure(response);
                            break;
                        default:
                            onFailure(response);

                    }
                } catch (Exception exception) {
                    handleException(exception);
                }
            }

            private void handleException(Exception exception) {
                if (exception.getMessage() != null) {
                    callback.onFailure(capitalizeMessage(exception.getMessage()));
                    Log.e("WorkflowServiceHelper", exception.getMessage(), exception);
                } else {
                    callback.onFailure(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
                }
            }

            private void onResponseOk(Response<WorkflowDTO> response) throws IOException {
                WorkflowDTO workflowDTO = response.body();
                if (workflowDTO != null && !isNullOrEmpty(workflowDTO.getState())) {
                    callback.onPostExecute(response.body());
                } else {
                    onFailure(response);
                }
            }


            private void onResponseUnauthorized(Response<WorkflowDTO> response) throws IOException {
                String message = response.message().toLowerCase();
                String errorBodyString = "";
                try {
                    errorBodyString = response.errorBody().string().toLowerCase();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if (!(message.contains(TOKEN) && message.contains(EXPIRED)) && !message.contains(UNAUTHORIZED) &&
                        !(errorBodyString.contains(TOKEN) && errorBodyString.contains(EXPIRED))) {
                    onFailure(response);
                } else {
                    executeRefreshTokenRequest(getRefreshTokenCallback(transitionDTO, callback, jsonBody, queryMap, headers));
                }
            }

            private void onResponseBadRequest(Response<WorkflowDTO> response) throws IOException {
                String message = response.message().toLowerCase();
                String errorBodyString = "";
                try {
                    errorBodyString = response.errorBody().string();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                if ((message.contains(TOKEN) && message.contains(REVOKED))
                        || (errorBodyString.toLowerCase().contains(TOKEN)
                        && errorBodyString.toLowerCase().contains(REVOKED))) {
                    atomicAppRestart();
                } else {
                    onFailure(parseError(message, errorBodyString, "message", "exception", "error"));
                }
            }

            private void onValidationError(Response<WorkflowDTO> response) throws IOException {
                if (null != response.errorBody()) {
                    try {
                        FaultResponseDTO fault = getConvertedDTO(FaultResponseDTO.class, response.errorBody().string());
                        onFailure(fault.getException().getBody().getError().getMessage());
                    } catch (Exception e) {
                        onFailure(response.errorBody().string());
                    }
                } else {
                    onFailure("");
                }
            }

            /**
             * Converts to the desire DTO object from String DTO
             *
             * @param dtoClass class to convert
             * @param <S>      Dynamic class to convert
             * @return Dynamic converted class object
             */
            private <S> S getConvertedDTO(Class<S> dtoClass, String jsonString) {

                Gson gson = new Gson();
                return gson.fromJson(jsonString, dtoClass);

            }

            private void onFailure(Response<WorkflowDTO> response) throws IOException {
                if (response.errorBody() != null) {
                    String errorBodyString = "";
                    try {
                        errorBodyString = response.errorBody().string();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    onFailure(parseError(response.message(), errorBodyString, "message", "data", "error", "exception"));
                } else {
                    onFailure("");
                }
            }

            @Override
            public void onFailure(Call<WorkflowDTO> call, Throwable throwable) {
                shouldRetryRequest = true;
                callStack.remove(call);
                onFailure(throwable.getMessage());
            }

            void onFailure(String errorMessage) {
                if (attemptCount < 2 && shouldRetryRequest) {
                    // Re-try failed request with increased attempt count
                    executeRequest(transitionDTO, callback, jsonBody, queryMap, headers, attemptCount + 1);
                } else {
                    callback.onFailure(capitalizeMessage(errorMessage));
                }
            }
        });

        callStack.push(call);
    }

    private CognitoActionCallback getCognitoActionCallback(@NonNull final TransitionDTO transitionDTO,
                                                           @NonNull final WorkflowServiceCallback callback,
                                                           final String jsonBody,
                                                           final Map<String, String> queryMap,
                                                           final Map<String, String> headers) {
        return new CognitoActionCallback() {
            @Override
            public void onBeforeLogin() {

            }

            @Override
            public void onLoginSuccess() {
                // Re-try failed request with new auth headers
                execute(transitionDTO, callback, jsonBody, queryMap, headers);
            }

            @Override
            public void onLoginFailure(String exceptionMessage) {
                callback.onFailure(capitalizeMessage(exceptionMessage));
            }
        };
    }

    private void executeRefreshTokenRequest(@NonNull final WorkflowServiceCallback callback) {
        if (appAuthorizationHelper == null) {
            atomicAppRestart();
        }

        Gson gson = new Gson();
        String jsonBody = gson.toJson(new RefreshDTO(appAuthorizationHelper.getRefreshToken()));

        execute(appAuthorizationHelper.getRefreshTransition(), callback, jsonBody, null, getApplicationStartHeaders());
    }

    private WorkflowServiceCallback getRefreshTokenCallback(@NonNull final TransitionDTO transitionDTO,
                                                            @NonNull final WorkflowServiceCallback callback,
                                                            final String jsonBody,
                                                            final Map<String, String> queryMap,
                                                            final Map<String, String> headers) {

        return new WorkflowServiceCallback() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onPostExecute(WorkflowDTO workflowDTO) {
                Gson gson = new Gson();
                String signInResponseString = gson.toJson(workflowDTO);
                RefreshTokenDto refreshTokenDto = gson.fromJson(signInResponseString, RefreshTokenDto.class);
                if (refreshTokenDto != null) {
                    UnifiedAuthenticationTokens authTokens = refreshTokenDto.getPayload()
                            .getAuthorizationModel().getCognito().getAuthenticationTokens();
                    appAuthorizationHelper.setAuthorizationTokens(authTokens);
                }

                // Re-try failed request with new auth headers
                execute(transitionDTO, callback, jsonBody, queryMap, headers);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                callback.onFailure(capitalizeMessage(exceptionMessage));
            }
        };
    }

    private static boolean isNullOrEmpty(String string) {
        return (string == null || string.trim().equals(""));
    }

    /**
     * Stop all calls in progress
     */
    public void interrupt() {
        while (!callStack.isEmpty()) {
            Call<?> call = callStack.peek();
            call.cancel();
            callStack.pop();
        }
    }


    private void atomicAppRestart() {
        if (applicationMode != null) {
            applicationMode.clearUserPracticeDTO();
            applicationMode.setApplicationType(ApplicationMode.ApplicationType.PRACTICE);
        }
        Intent intent = new Intent();
        intent.setAction("com.carecloud.carepay.restart");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);


        Context context = ((AndroidPlatform) Platform.get()).getContext();
        Toast.makeText(context, "Login Authorization has Expired!\nPlease Login to Application again",
                Toast.LENGTH_LONG).show();
        context.startActivity(intent);
    }

    /**
     * Persist all Labels contained in Workflow DTO
     *
     * @param labels the JsonObject containing all the labels
     */
    public void saveLabels(JsonObject labels) {
        saveLabels(labels, "");
    }

    /**
     * Persist all Labels contained in Workflow DTO
     *
     * @param labels the JsonObject containing all the labels
     * @param prefix the prefix added in the key (most used for patient labels)
     */
    public void saveLabels(JsonObject labels, String prefix) {
        if (labels != null) {
            Set<Map.Entry<String, JsonElement>> set = labels.entrySet();
            for (Map.Entry<String, JsonElement> entry : set) {
                Label.putLabelAsync(prefix + entry.getKey(), entry.getValue().getAsString());
            }
            Label.applyAsyncLabels();
        }
    }

    private static String parseError(String message, String errorBodyString, @NonNull String... errorFields) {
        try {
            JsonElement jsonElement = new JsonParser().parse(errorBodyString);
            String error;
            for (String errorField : errorFields) {
                error = findErrorElement(jsonElement, errorField);
                if (error != null) {
                    message = error;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    private static String findErrorElement(JsonElement jsonElement, String errorFieldName) {
        if (jsonElement instanceof JsonObject) {
            JsonObject jsonObject = (JsonObject) jsonElement;
            Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
            for (Map.Entry<String, JsonElement> entry : entrySet) {
                if (entry.getKey().equals(errorFieldName) && entry.getValue() instanceof JsonPrimitive) {
                    return entry.getValue().getAsString();
                }
                if (entry.getValue() instanceof JsonObject) {
                    String error = findErrorElement(entry.getValue(), errorFieldName);
                    if (error != null) {
                        return error;
                    }
                }
            }
        } else if (jsonElement instanceof JsonArray) {
            JsonArray array = (JsonArray) jsonElement;
            Iterator<JsonElement> iterator = array.iterator();
            while (iterator.hasNext()) {
                String error = findErrorElement(iterator.next(), errorFieldName);
                if (error != null) {
                    return error;
                }
            }
        }
        return null;
    }


    private static @NonNull
    String capitalizeMessage(String message) {
        if (message == null || message.length() == 0) {
            return "";
        }
        return message.substring(0, 1).toUpperCase() + message.substring(1);
    }
}