package com.carecloud.carepay.service.library;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.carecloud.carepay.service.library.cognito.CognitoAppHelper;
import com.carecloud.carepay.service.library.constants.HttpConstants;
import com.carecloud.carepay.service.library.dtos.TransitionDTO;
import com.carecloud.carepay.service.library.dtos.UserPracticeDTO;
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

    public enum ApplicationType{
        PATIENT,PRACTICE
    }

    private static WorkflowServiceHelper instance;

    private static ApplicationType applicationType;

    /**
     * Application type inizialization Patient or Practice
     * @param applicationType Patient or Practice
     */

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

    // use for seting practice maganement information
    private  UserPracticeDTO userPracticeDTO;

    public  UserPracticeDTO getUserPracticeDTO() {
        return userPracticeDTO;
    }

    public  void setUserPracticeDTO(UserPracticeDTO userPracticeDTO) {
        this.userPracticeDTO = userPracticeDTO;
    }

    /**
     * Default headers user information
     * @return collection user auth heaters
     */
    private Map<String, String> getUserAuthenticationHeaders() {
        Map<String, String> userAuthHeaders = new HashMap<>();

        if(WorkflowServiceHelper.applicationType==ApplicationType.PRACTICE){
            if (!isNullOrEmpty(CognitoAppHelper.getCurrUser())) {
                userAuthHeaders.put("username", CognitoAppHelper.getCurrUser());
            }
            if(userPracticeDTO!=null){
                userAuthHeaders.put("practice_mgmt", userPracticeDTO.getPracticeMgmt());
                userAuthHeaders.put("practice_id", userPracticeDTO.getPracticeId());
            }
        }else{
            if (!isNullOrEmpty(CognitoAppHelper.getCurrUser())) {
                userAuthHeaders.put("username", CognitoAppHelper.getCurrUser());
            }
            if (CognitoAppHelper.getCurrSession() != null && !isNullOrEmpty(CognitoAppHelper.getCurrSession().getIdToken().getJWTToken())) {
                userAuthHeaders.put("Authorization", CognitoAppHelper.getCurrSession().getIdToken().getJWTToken());
            }
        }



        return userAuthHeaders;
    }



    /**
     * add custom headers sened from user
     * @param customHeaders collection of custom headers
     * @return collection of headers
     */
    private Map<String, String> addCustomHeaders(Map<String, String> customHeaders) {
        customHeaders.putAll(getUserAuthenticationHeaders());
        return customHeaders;
    }

    private Map<String, String> getApplicationStartHeaders() {
        Map<String, String> appStartHeaders = new HashMap<>();
        appStartHeaders.put("x-api-key", HttpConstants.X_API_KEY);
        return appStartHeaders;
    }

    /**
     * Application Start request
     * @param callback UI callback
     */
    public void executeApplicationStartRequest(final WorkflowServiceCallback callback) {
        TransitionDTO transitionDTO=new TransitionDTO();
        transitionDTO.setMethod("GET");
        transitionDTO.setUrl(HttpConstants.API_START_URL);
        executeRequest(transitionDTO,callback,null,null, getApplicationStartHeaders());
    }

    /**
     * @Deprecated use execute
     * @param url url
     * @param callback ui callback
     */
    @Deprecated
    public void executeGetRequest(@NonNull String url, @NonNull Map<String, String> customHeaders, @NonNull final WorkflowServiceCallback callback) {
        TransitionDTO transitionDTO=new TransitionDTO();
        transitionDTO.setMethod(url);
        transitionDTO.setUrl(HttpConstants.API_START_URL);
        executeRequest(transitionDTO,callback,null,null, addCustomHeaders(customHeaders));
    }

    /**
     * @Deprecated use execute
     * @param url
     * @param callback
     */
    @Deprecated
    public void executeGetRequest(@NonNull String url, @NonNull final WorkflowServiceCallback callback) {
        TransitionDTO transitionDTO=new TransitionDTO();
        transitionDTO.setMethod(url);
        transitionDTO.setUrl(HttpConstants.API_START_URL);
        executeRequest(transitionDTO,callback,null,null, getUserAuthenticationHeaders());
    }

    public void execute(@NonNull TransitionDTO transitionDTO,@NonNull WorkflowServiceCallback callback ){

        executeRequest(transitionDTO,callback,null,null,getUserAuthenticationHeaders());
    }

    public void execute(@NonNull TransitionDTO transitionDTO, @NonNull final WorkflowServiceCallback callback, String jsonBody) {
        executeRequest(transitionDTO,callback,jsonBody,new HashMap<String, String>(),getUserAuthenticationHeaders());
    }

    public void execute(@NonNull TransitionDTO transitionDTO, @NonNull final WorkflowServiceCallback callback, Map<String,String> queryMap) {
        executeRequest(transitionDTO,callback,null,queryMap,getUserAuthenticationHeaders());
    }

    public void execute(@NonNull TransitionDTO transitionDTO, @NonNull final WorkflowServiceCallback callback, String jsonBody, Map<String,String> queryMap) {
        executeRequest(transitionDTO,callback,jsonBody,queryMap,getUserAuthenticationHeaders());
    }

    public void execute(@NonNull TransitionDTO transitionDTO, @NonNull final WorkflowServiceCallback callback, Map<String,String> queryMap, Map<String,String> customHeaders) {
        executeRequest(transitionDTO,callback,null,queryMap,addCustomHeaders(customHeaders));
    }

    public void execute(@NonNull TransitionDTO transitionDTO, @NonNull final WorkflowServiceCallback callback, String jsonBody, Map<String,String> queryMap, Map<String, String> customHeaders) {
        executeRequest(transitionDTO,callback,jsonBody,queryMap,addCustomHeaders(customHeaders));
    }

    private void executeRequest(@NonNull TransitionDTO transitionDTO, @NonNull final WorkflowServiceCallback callback, String jsonBody, Map<String,String> queryMap, Map<String, String> headers) {
        callback.onPreExecute();
        WorkflowService workflowService = ServiceGenerator.getInstance().createService(WorkflowService.class, headers); //, String token, String searchString
        Call<WorkflowDTO> call=null;

        if(transitionDTO.getMethod().equalsIgnoreCase("GET")){
            if(jsonBody!=null && queryMap==null){
                call= workflowService.executeGet(transitionDTO.getUrl(),jsonBody);
            } else if(jsonBody==null && queryMap!=null){
                call= workflowService.executeGet(transitionDTO.getUrl(),queryMap);
            }else if(jsonBody!=null && queryMap.size()>0){
                call= workflowService.executeGet(transitionDTO.getUrl(),jsonBody,queryMap);
            }else {
                call= workflowService.executeGet(transitionDTO.getUrl());
            }
        }else{
            if(jsonBody!=null && queryMap==null){
                call= workflowService.executePost(transitionDTO.getUrl(),jsonBody);
            } else if(jsonBody==null && queryMap!=null){
                call= workflowService.executePost(transitionDTO.getUrl(),queryMap);
            }else if(jsonBody!=null && queryMap.size()>0){
                call= workflowService.executePost(transitionDTO.getUrl(),jsonBody,queryMap);
            }else {
                call= workflowService.executePost(transitionDTO.getUrl());
            }
        }
       // Call<WorkflowDTO> call = transitionDTO.getMethod().equalsIgnoreCase("GET")? workflowService.executeGet(transitionDTO.getUrl()):workflowService.executePost(transitionDTO.getUrl(),jsonBody,queryMap);
        call.enqueue(new Callback<WorkflowDTO>() {
            @Override
            public void onResponse(Call<WorkflowDTO> call, Response<WorkflowDTO> response) {

                if (response.code() == 200 && response.body() != null) {
                    callback.onPostExecute(response.body());
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
