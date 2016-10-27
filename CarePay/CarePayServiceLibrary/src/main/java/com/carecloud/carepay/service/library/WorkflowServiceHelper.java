package com.carecloud.carepay.service.library;

import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.WorkflowDTO;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jahirul Bhuiyan on 10/24/2016.
 * Service workflow helper a generic helper call wrapper that expose all possible API function calls.
 */

public class WorkflowServiceHelper {

    public enum ApplicationType{
        PATIENT,PRACTICE
    }

    private static WorkflowServiceHelper instance;

    private static ApplicationType applicationType;

    public static void initialization(ApplicationType applicationType){
        if (instance == null) {
            instance = new WorkflowServiceHelper();
        }
        WorkflowServiceHelper.applicationType=applicationType;
    }

    private WorkflowServiceHelper() {
    }

    public static WorkflowServiceHelper getInstance() {
        return instance;
    }

    private Map<String, String> getUserAuthenticationHeaders() {
        Map<String, String> userAuthHeaders = new HashMap<>();
        if (!isNullOrEmpty(CognitoAppHelper.getCurrUser())) {
            userAuthHeaders.put("username", CognitoAppHelper.getCurrUser());
        }
        if (CognitoAppHelper.getCurrSession() != null && !isNullOrEmpty(CognitoAppHelper.getCurrSession().getIdToken().getJWTToken())) {
            userAuthHeaders.put("Authorization", CognitoAppHelper.getCurrSession().getIdToken().getJWTToken());
        }
        return userAuthHeaders;
    }

    private Map<String, String> addCustomHeaders(Map<String, String> customHeaders) {
        if (customHeaders == null) {
            customHeaders = new HashMap<>();
        }
        if (!isNullOrEmpty(CognitoAppHelper.getCurrUser())) {
            customHeaders.put("username", CognitoAppHelper.getCurrUser());
        }
        if (CognitoAppHelper.getCurrSession() != null && !isNullOrEmpty(CognitoAppHelper.getCurrSession().getIdToken().getJWTToken())) {
            customHeaders.put("Authorization", CognitoAppHelper.getCurrSession().getIdToken().getJWTToken());
        }
        return customHeaders;
    }

    private Map<String, String> getApplicationStartHeaders() {
        Map<String, String> appStartHeaders = new HashMap<>();
        appStartHeaders.put("x-api-key", HttpConstants.X_API_KEY);
        return appStartHeaders;
    }

    public void executeApplicationStartRequest(final WorkflowServiceCallback callback) {
        executeGet(HttpConstants.API_START_URL, getApplicationStartHeaders(), callback);
    }

    public void executeGetRequest(String url, Map<String, String> customHeaders, final WorkflowServiceCallback callback) {
        executeGet(url, addCustomHeaders(customHeaders), callback);
    }

    public void executeGetRequest(String url, final WorkflowServiceCallback callback) {
        executeGet(url, getUserAuthenticationHeaders(), callback);
    }

    public void executeGetRequest(String url) {
        executeGet(url, getUserAuthenticationHeaders(), null);
    }


    private void executeGet(String url, Map<String, String> headers, final WorkflowServiceCallback callback) {
        if (callback != null) {
            callback.onPreExecute();
        }
        WorkflowService apptService = ServiceGenerator.getInstance().createService(WorkflowService.class, headers); //, String token, String searchString
        Call<WorkflowDTO> call = apptService.executeGet(url);
        call.enqueue(new Callback<WorkflowDTO>() {
            @Override
            public void onResponse(Call<WorkflowDTO> call, Response<WorkflowDTO> response) {

                if (response.code() == 200 && response.body() != null && callback != null) {
                    callback.onPostExecute(response.body());
                }
            }

            @Override
            public void onFailure(Call<WorkflowDTO> call, Throwable throwable) {
                if (callback != null) {
                    callback.onFailure(throwable.getMessage());
                }
            }
        });
    }

    private boolean isNullOrEmpty(String string) {
        return (string == null || string.trim().equals(""));
    }
}
