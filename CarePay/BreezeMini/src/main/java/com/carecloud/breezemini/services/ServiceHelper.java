package com.carecloud.breezemini.services;

import android.support.annotation.NonNull;

import com.carecloud.breezemini.HttpConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by kkannan on 5/23/17.
 */

public class ServiceHelper {


    private static final int STATUS_CODE_OK = 200;
    private static final int STATUS_CODE_UNAUTHORIZED = 401;
    private static final int STATUS_BAD_REQUEST = 400;
    private static final int STATUS_CODE_UNPROCESSABLE_ENTITY = 422;

    private Stack<Call<?>> callStack = new Stack<>();


    /**
     * @return app start headers
     */
    public Map<String, String> getApplicationStartHeaders() {
        Map<String, String> appStartHeaders = new HashMap<>();
        appStartHeaders.put("x-api-key", HttpConstants.getApiStartKey());
        return appStartHeaders;
    }

    /**
     * Application Start request
     *
     * @param callback UI callback
     */
    public void executeApplicationStartRequest(final ServiceCallback callback) {
        ServiceRequestDTO serviceRequestDTO = new ServiceRequestDTO();
        serviceRequestDTO.setMethod("GET");
        serviceRequestDTO.setUrl(HttpConstants.getApiStartUrl());
        execute(serviceRequestDTO, callback, null, null, getApplicationStartHeaders());
    }

    public void execute(@NonNull ServiceRequestDTO serviceRequestDTO, @NonNull ServiceCallback callback) {
        execute(serviceRequestDTO, callback, null, null, null);
    }

    public void execute(@NonNull ServiceRequestDTO serviceRequestDTO, @NonNull final ServiceCallback callback, String jsonBody) {
        execute(serviceRequestDTO, callback, jsonBody, null);
    }

    public void execute(@NonNull ServiceRequestDTO serviceRequestDTO, @NonNull final ServiceCallback callback, Map<String, String> queryMap) {
        execute(serviceRequestDTO, callback, null, queryMap, null);
    }

    public void execute(@NonNull ServiceRequestDTO serviceRequestDTO, @NonNull final ServiceCallback callback, String jsonBody, Map<String, String> queryMap) {
        execute(serviceRequestDTO, callback, jsonBody, queryMap, null);
    }

    public void execute(@NonNull ServiceRequestDTO serviceRequestDTO, @NonNull final ServiceCallback callback, Map<String, String> queryMap, Map<String, String> customHeaders) {
        execute(serviceRequestDTO, callback, null, queryMap, customHeaders);
    }

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
//        queryMap = updateQueryMapWithDefault(queryMap);
        ServiceInterface workflowService = ServiceGenerator.getInstance().createService(ServiceInterface.class, headers); //, String token, String searchString
        Call<ServiceResponseDTO> call = null;

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

            }

            @Override
            public void onFailure(Call<ServiceResponseDTO> call, Throwable t) {

            }
        });
    }
}
