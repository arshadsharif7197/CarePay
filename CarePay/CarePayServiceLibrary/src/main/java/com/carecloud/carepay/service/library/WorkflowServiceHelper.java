package com.carecloud.carepay.service.library;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.carecloud.carepay.service.library.unifiedauth.UnifiedAuthenticationTokens;
import com.carecloud.carepay.service.library.unifiedauth.UnifiedSignInResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
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

    private static final String TOKEN_HAS_EXPIRED = "Identity token has expired";
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

                userAuthHeaders.put("username", applicationMode.getUserPracticeDTO().getUserName());

                if (applicationMode.getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
                    userAuthHeaders.put("username_patient", appAuthorizationHelper.getCurrUser());
                }

            } else {
                userAuthHeaders.put("username", appAuthorizationHelper.getCurrUser());
            }

            if (HttpConstants.isUseUnifiedAuth()) {
                userAuthHeaders.put("Authorization", appAuthorizationHelper.getIdToken());
            } else {
                userAuthHeaders.put("Authorization", appAuthorizationHelper.getCurrSession().getIdToken().getJWTToken());
            }
        }

        userAuthHeaders.putAll(getPreferredLanguageHeader());
        return userAuthHeaders;
    }

    /**
     * get request headers
     *
     * @param customHeaders collection of custom headers
     * @return collection of headers
     */
    private Map<String, String> getHeaders(Map<String, String> customHeaders) {
        Map<String, String> headers = getUserAuthenticationHeaders();

        // Add auth headers to custom in case custom has old auth headers
        if (customHeaders != null) {
            customHeaders.putAll(headers);

            return customHeaders;
        }

        return headers;
    }

    /**
     * @return app start headers
     */
    public Map<String, String> getApplicationStartHeaders() {
        Map<String, String> appStartHeaders = new HashMap<>();
        appStartHeaders.put("x-api-key", HttpConstants.getApiStartKey());
        if (applicationPreferences.getUserLanguage().isEmpty()) {
            appStartHeaders.put("Accept-Language", "en");
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
            prefredLanguage.put("Accept-Language", "en");
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
        execute(transitionDTO, callback, null, null, getApplicationStartHeaders());
    }

    public void execute(@NonNull TransitionDTO transitionDTO, @NonNull WorkflowServiceCallback callback) {
        execute(transitionDTO, callback, null, null, null);
    }

    public void execute(@NonNull TransitionDTO transitionDTO, @NonNull final WorkflowServiceCallback callback, String jsonBody) {
        execute(transitionDTO, callback, jsonBody, null);
    }

    public void execute(@NonNull TransitionDTO transitionDTO, @NonNull final WorkflowServiceCallback callback, Map<String, String> queryMap) {
        execute(transitionDTO, callback, null, queryMap, null);
    }

    public void execute(@NonNull TransitionDTO transitionDTO, @NonNull final WorkflowServiceCallback callback, String jsonBody, Map<String, String> queryMap) {
        execute(transitionDTO, callback, jsonBody, queryMap, null);
    }

    public void execute(@NonNull TransitionDTO transitionDTO, @NonNull final WorkflowServiceCallback callback, Map<String, String> queryMap, Map<String, String> customHeaders) {
        execute(transitionDTO, callback, null, queryMap, customHeaders);
    }

    public void execute(@NonNull TransitionDTO transitionDTO, @NonNull final WorkflowServiceCallback callback, String jsonBody, Map<String, String> queryMap, Map<String, String> customHeaders) {
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
        WorkflowService workflowService = ServiceGenerator.getInstance().createService(WorkflowService.class, headers); //, String token, String searchString
        Call<WorkflowDTO> call = null;

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
    }

    private void executeCallback(@NonNull final TransitionDTO transitionDTO,
                                 @NonNull final WorkflowServiceCallback callback,
                                 final String jsonBody,
                                 final Map<String, String> queryMap,
                                 final Map<String, String> headers,
                                 final int attemptCount,
                                 final Call<WorkflowDTO> call) {

        call.enqueue(new Callback<WorkflowDTO>() {
            @Override
            public void onResponse(Call<WorkflowDTO> call, Response<WorkflowDTO> response) {
                callStack.remove(call);
                try {
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
                            onValidationError(response);
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
                    callback.onFailure(exception.getMessage());
                    Log.e("WorkflowServiceHelper", exception.getMessage(), exception);
                } else {
                    callback.onFailure(CarePayConstants.CONNECTION_ISSUE_ERROR_MESSAGE);
                }
            }

            private void onResponseOk(Response<WorkflowDTO> response) throws IOException {
                WorkflowDTO workflowDTO = response.body();
                // This is temporary. We should use an exclusive service for labels
                // in order to retrieve them and save them only once.
                // TODO: erase this code when that service exists
                saveLabels(workflowDTO);
                if (workflowDTO != null && !isNullOrEmpty(workflowDTO.getState())) {
                    callback.onPostExecute(response.body());
                } else {
                    onFailure(response);
                }
            }

            private void saveLabels(WorkflowDTO workflowDTO) {
                //TODO: this should change after the creation of the Label service
                JsonObject labels = workflowDTO.getMetadata().getAsJsonObject("labels");
                String state = workflowDTO.getState();
                boolean contains = ((AndroidPlatform) Platform.get()).openSharedPreferences(AndroidPlatform.LABELS_FILE_NAME).contains("labelFor" + state);
                if (labels != null && !contains) {
                    Set<Map.Entry<String, JsonElement>> set = labels.entrySet();
                    for (Map.Entry<String, JsonElement> entry : set) {
                        Label.putLabel(entry.getKey(), entry.getValue().getAsString());
                    }
                    SharedPreferences.Editor editor = ((AndroidPlatform) Platform.get()).openDefaultSharedPreferences().edit();
                    editor.putBoolean("labelFor" + state, true).apply();
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
                } else if (!HttpConstants.isUseUnifiedAuth() && !appAuthorizationHelper.refreshToken(getCognitoActionCallback(transitionDTO, callback, jsonBody, queryMap, headers))) {
                    callback.onFailure("No User found to refresh token");
                } else if (HttpConstants.isUseUnifiedAuth()) {
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
                        || (errorBodyString.toLowerCase().contains(TOKEN) && errorBodyString.toLowerCase().contains(REVOKED))) {
                    atomicAppRestart();
                } else {
                    try {
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    onFailure(errorBodyString);
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
            public <S> S getConvertedDTO(Class<S> dtoClass, String jsonString) {

                Gson gson = new Gson();
                return gson.fromJson(jsonString, dtoClass);

            }

            private void onFailure(Response<WorkflowDTO> response) throws IOException {
                if (null != response.errorBody()) {
                    onFailure(response.errorBody().string());
                } else {
                    onFailure("");
                }
            }

            @Override
            public void onFailure(Call<WorkflowDTO> call, Throwable throwable) {
                callStack.remove(call);
                onFailure(throwable.getMessage());
            }

            void onFailure(String errorMessage) {
                if (attemptCount < 2) {
                    // Re-try failed request with increased attempt count
                    executeRequest(transitionDTO, callback, jsonBody, queryMap, headers, attemptCount + 1);
                } else {
                    callback.onFailure(errorMessage);
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
                callback.onFailure(exceptionMessage);
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
                UnifiedSignInResponse signInResponse = gson.fromJson(signInResponseString, UnifiedSignInResponse.class);
                if (signInResponse != null) {
                    UnifiedAuthenticationTokens authTokens = signInResponse.getPayload().getAuthorizationModel().getCognito().getAuthenticationTokens();
                    appAuthorizationHelper.setAuthorizationTokens(authTokens);
                }

                // Re-try failed request with new auth headers
                execute(transitionDTO, callback, jsonBody, queryMap, headers);
            }

            @Override
            public void onFailure(String exceptionMessage) {
                callback.onFailure(exceptionMessage);
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
        }
        Intent intent = new Intent();
        intent.setAction("com.carecloud.carepay.restart");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        Context context = applicationPreferences.getContext();
        Toast.makeText(context, "Login Authorization has Expired!\nPlease Login to Application again", Toast.LENGTH_LONG).show();
        context.startActivity(intent);
    }

}