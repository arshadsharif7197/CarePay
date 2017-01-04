package com.carecloud.carepay.service.library;

import android.support.annotation.NonNull;

import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.constants.ApplicationMode;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Jahirul Bhuiyan on 10/24/2016.
 * Service workflow helper a generic helper call wrapper that expose all possible API function calls.
 * Singleton, initialized from application class.
 * User information Dynamic headers added by default
 * Application start Header "x-api-key" added from HttpConstants
 */

public class WorkflowServiceHelper {


    private static WorkflowServiceHelper instance;


    private WorkflowServiceHelper() {
    }

    /**
     * Return singleton object
     *
     * @return singleton object
     */
    public static WorkflowServiceHelper getInstance() {
        if (instance == null) {
            instance = new WorkflowServiceHelper();
        }
        return instance;
    }

    /**
     * Default headers user information
     *
     * @return collection user auth heaters
     */
    private Map<String, String> getUserAuthenticationHeaders() {
        Map<String, String> userAuthHeaders = new HashMap<>();

        if ((ApplicationMode.getInstance().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE
                || ApplicationMode.getInstance().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE)
                && ApplicationMode.getInstance().getUserPracticeDTO() != null) {
            userAuthHeaders.put("username", ApplicationMode.getInstance().getUserPracticeDTO().getUserName());
           if (ApplicationMode.getInstance().getApplicationType() == ApplicationMode.ApplicationType.PRACTICE_PATIENT_MODE) {
               userAuthHeaders.put("username_patient", CognitoAppHelper.getCurrUser());
            }
        } else if (!isNullOrEmpty(CognitoAppHelper.getCurrUser())) {
            userAuthHeaders.put("username", CognitoAppHelper.getCurrUser());
            if (CognitoAppHelper.getCurrSession() != null && !isNullOrEmpty(CognitoAppHelper.getCurrSession().getIdToken().getJWTToken())) {
                userAuthHeaders.put("Authorization", CognitoAppHelper.getCurrSession().getIdToken().getJWTToken());
            }
        }
        userAuthHeaders.putAll(getPreferredLanguageHeader());
        return userAuthHeaders;
    }


    /**
     * add custom headers sened from user
     *
     * @param customHeaders collection of custom headers
     * @return collection of headers
     */
    private Map<String, String> addCustomHeaders(Map<String, String> customHeaders) {
        customHeaders.putAll(getUserAuthenticationHeaders());
        return customHeaders;
    }

    /**
     * @return app start headers
     */
    public static Map<String, String> getApplicationStartHeaders() {
        Map<String, String> appStartHeaders = new HashMap<>();
        appStartHeaders.put("x-api-key", HttpConstants.getApiStartKey());
        if( ApplicationPreferences.Instance.getUserLanguage().isEmpty()) {
            appStartHeaders.put("Accept-Language", "en");
        } else {
            appStartHeaders.put("Accept-Language", ApplicationPreferences.Instance.getUserLanguage());
        }
        return appStartHeaders;
    }

    public static  Map<String, String> getPreferredLanguageHeader(){
        Map<String, String> prefredLanguage = new HashMap<>();
        if( ApplicationPreferences.Instance.getUserLanguage().isEmpty()) {
            prefredLanguage.put("Accept-Language", "en");
        } else {
            prefredLanguage.put("Accept-Language", ApplicationPreferences.Instance.getUserLanguage());
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

        executeRequest(transitionDTO, callback, null, null, getUserAuthenticationHeaders());
    }

    public void execute(@NonNull TransitionDTO transitionDTO, @NonNull final WorkflowServiceCallback callback, String jsonBody) {
        executeRequest(transitionDTO, callback, jsonBody, new HashMap<String, String>(), getUserAuthenticationHeaders());
    }

    public void execute(@NonNull TransitionDTO transitionDTO, @NonNull final WorkflowServiceCallback callback, Map<String, String> queryMap) {
        executeRequest(transitionDTO, callback, null, queryMap, getUserAuthenticationHeaders());
    }

    public void execute(@NonNull TransitionDTO transitionDTO, @NonNull final WorkflowServiceCallback callback, String jsonBody, Map<String, String> queryMap) {
        executeRequest(transitionDTO, callback, jsonBody, queryMap, getUserAuthenticationHeaders());
    }

    public void execute(@NonNull TransitionDTO transitionDTO, @NonNull final WorkflowServiceCallback callback, Map<String, String> queryMap, Map<String, String> customHeaders) {
        executeRequest(transitionDTO, callback, null, queryMap, addCustomHeaders(customHeaders));
    }

    public void execute(@NonNull TransitionDTO transitionDTO, @NonNull final WorkflowServiceCallback callback, String jsonBody, Map<String, String> queryMap, Map<String, String> customHeaders) {
        executeRequest(transitionDTO, callback, jsonBody, queryMap, addCustomHeaders(customHeaders));
    }

    private void updateQueryMapWithDefault(Map<String, String> queryMap) {
        if (queryMap == null) {
            queryMap = new HashMap<>();
        }
        if (ApplicationMode.getInstance().getUserPracticeDTO() != null && !queryMap.containsKey("practice_mgmt")) {
            queryMap.put("practice_mgmt", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeMgmt());
            queryMap.put("practice_id", ApplicationMode.getInstance().getUserPracticeDTO().getPracticeId());
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
        } else {
            if (jsonBody != null && queryMap == null) {
                call = workflowService.executePost(transitionDTO.getUrl(), jsonBody);
            } else if (jsonBody == null && queryMap != null) {
                call = workflowService.executePost(transitionDTO.getUrl(), queryMap);
            } else if (jsonBody != null && queryMap.size() > 0) {
                call = workflowService.executePost(transitionDTO.getUrl(), jsonBody, queryMap);
            } else if (jsonBody != null) {
                call = workflowService.executePost(transitionDTO.getUrl(), jsonBody, queryMap);
            } else if (jsonBody != null) {
                call = workflowService.executePost(transitionDTO.getUrl(), jsonBody, queryMap);
            } else {
                call = workflowService.executePost(transitionDTO.getUrl());
            }
        }
        executeCallback(callback, call);
    }

    private void executeCallback(@NonNull final WorkflowServiceCallback callback, Call<WorkflowDTO> call) {
        call.enqueue(new Callback<WorkflowDTO>() {
            @Override
            public void onResponse(Call<WorkflowDTO> call, Response<WorkflowDTO> response) {
                WorkflowDTO workflowDTO = response.body();
                if (response.code() == 200 && workflowDTO != null && !isNullOrEmpty(workflowDTO.getState())) {
                    callback.onPostExecute(response.body());
                } else {
                    try {
                        callback.onFailure(response.errorBody().string());
                    } catch (Exception exception) {
                        callback.onFailure(exception.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<WorkflowDTO> call, Throwable throwable) {
                callback.onFailure(throwable.getMessage());
            }
        });
    }

    private boolean isNullOrEmpty(String string) {
        return (string == null || string.trim().equals(""));
    }


}
