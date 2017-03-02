package com.carecloud.carepay.service.library;

import android.support.annotation.NonNull;

import com.carecloud.carepay.service.library.cognito.CognitoActionCallback;
import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    private static final int STATUS_CODE_OK = 200;
    private static final int STATUS_CODE_UNAUTHORIZED = 401;

    private CognitoAppHelper cognitoAppHelper;
    private ApplicationPreferences applicationPreferences;
    private ApplicationMode applicationMode;
//    private static int retryAttempts = 0;

    public WorkflowServiceHelper(ApplicationPreferences applicationPreferences,
                                 ApplicationMode applicationMode) {
        this.applicationPreferences = applicationPreferences;
        this.applicationMode = applicationMode;
    }

    public void setCognitoAppHelper(CognitoAppHelper cognitoAppHelper) {
        this.cognitoAppHelper = cognitoAppHelper;
    }

    /**
     * Default headers user information
     *
     * @return collection user auth heaters
     */
    private Map<String, String> getUserAuthenticationHeaders() {
        Map<String, String> userAuthHeaders = new HashMap<>();

        if (null != cognitoAppHelper) {

            if ((applicationMode.getApplicationType() == ApplicationMode.ApplicationType.PRACTICE
                    || applicationMode.getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE)
                    && applicationMode.getUserPracticeDTO() != null) {
                userAuthHeaders.put("username", applicationMode.getUserPracticeDTO().getUserName());
                userAuthHeaders.put("Authorization", cognitoAppHelper.getCurrSession().getIdToken().getJWTToken());
                if (applicationMode.getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
                    userAuthHeaders.put("username_patient", cognitoAppHelper.getCurrUser());
                }
            } else if (!isNullOrEmpty(cognitoAppHelper.getCurrUser())) {
                userAuthHeaders.put("username", cognitoAppHelper.getCurrUser());
                if (cognitoAppHelper.getCurrSession() != null && !isNullOrEmpty(cognitoAppHelper.getCurrSession().getIdToken().getJWTToken())) {
                    userAuthHeaders.put("Authorization", cognitoAppHelper.getCurrSession().getIdToken().getJWTToken());
                }
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
        if( applicationPreferences.getUserLanguage().isEmpty()) {
            appStartHeaders.put("Accept-Language", "en");
        } else {
            appStartHeaders.put("Accept-Language", applicationPreferences.getUserLanguage());
        }
        return appStartHeaders;
    }

    /**
     * @return map with language header
     */
    public  Map<String, String> getPreferredLanguageHeader(){
        Map<String, String> prefredLanguage = new HashMap<>();
        if( applicationPreferences.getUserLanguage().isEmpty()) {
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
        executeRequest(transitionDTO, callback, null, null, getApplicationStartHeaders());
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
        executeRequest(transitionDTO, callback, jsonBody, queryMap, getHeaders(customHeaders));
    }

    private void updateQueryMapWithDefault(Map<String, String> queryMap) {
        if (queryMap == null) {
            queryMap = new HashMap<>();
        }
        if (applicationMode.getUserPracticeDTO() != null && !queryMap.containsKey("practice_mgmt")) {
            queryMap.put("practice_mgmt", applicationMode.getUserPracticeDTO().getPracticeMgmt());
            queryMap.put("practice_id", applicationMode.getUserPracticeDTO().getPracticeId());
        }
    }

    private void executeRequest(@NonNull TransitionDTO transitionDTO, @NonNull final WorkflowServiceCallback callback, String jsonBody, Map<String, String> queryMap, Map<String, String> headers) {
        callback.onPreExecute();
        updateQueryMapWithDefault(queryMap);
        WorkflowService workflowService = ServiceGenerator.getInstance().createService(WorkflowService.class, headers); //, String token, String searchString
        Call<WorkflowDTO> call = null;

        if (transitionDTO.getMethod().equalsIgnoreCase("GET")) {
            if (jsonBody != null && queryMap == null) {
                call = workflowService.executeGet(transitionDTO.getUrl(), jsonBody);
            } else if (jsonBody == null && queryMap != null) {
                call = workflowService.executeGet(transitionDTO.getUrl(), queryMap);
            } else if (jsonBody != null && queryMap.size() > 0) {
                call = workflowService.executeGet(transitionDTO.getUrl(), jsonBody, queryMap);
            } else {
                call = workflowService.executeGet(transitionDTO.getUrl());
            }
        } else if (transitionDTO.getMethod().equalsIgnoreCase("POST")){
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
        } else if (transitionDTO.getMethod().equalsIgnoreCase("DELETE")) {
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

        executeCallback(transitionDTO, callback, jsonBody, queryMap, headers, call);
    }

    private void executeCallback(@NonNull final TransitionDTO transitionDTO,
                                 @NonNull final WorkflowServiceCallback callback,
                                 final String jsonBody,
                                 final Map<String, String> queryMap,
                                 final Map<String, String> headers,
                                 Call<WorkflowDTO> call) {

        call.enqueue(new Callback<WorkflowDTO>() {
            @Override
            public void onResponse(Call<WorkflowDTO> call, Response<WorkflowDTO> response) {
                try {
                    switch (response.code()) {
                        case STATUS_CODE_OK:
                            onResponseOk(response);
                            break;
                        case STATUS_CODE_UNAUTHORIZED:
                            onResponseUnauthorized(response);
                            break;
                        default:
                            onFailure(response);

                    }
                } catch (Exception exception) {
                    onFailure(call, exception);
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
                if (!response.errorBody().string().contains(TOKEN_HAS_EXPIRED)) {
                    onFailure(response);
                } else if (!cognitoAppHelper.refreshToken(getCognitoActionCallback(transitionDTO, callback, jsonBody, queryMap, headers))) {
                    callback.onFailure("No User found to refresh token");
                }
            }

            private void onFailure(Response<WorkflowDTO> response) throws IOException {
                if (null != response.errorBody()) {
                    callback.onFailure(response.errorBody().string());
                } else {
                    callback.onFailure("");
                }
            }

            @Override
            public void onFailure(Call<WorkflowDTO> call1, Throwable throwable) {
                callback.onFailure(throwable.getMessage());
            }
        });
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

    private static boolean isNullOrEmpty(String string) {
        return (string == null || string.trim().equals(""));
    }
}