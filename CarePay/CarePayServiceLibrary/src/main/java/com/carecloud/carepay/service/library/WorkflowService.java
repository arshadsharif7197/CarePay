package com.carecloud.carepay.service.library;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by Jahirul Bhuiyan on 10/24/2016.
 * Workflow service interface that handle all possible HTTP request using Retrofit
 */

public interface WorkflowService {

    @GET
    Call<WorkflowDTO> executeGet(@Url String url);

    @GET
    Call<WorkflowDTO> executeGet(@Url String url,  @Body String jsonString);

    @GET
    Call<WorkflowDTO> executeGet(@Url String url,  @QueryMap Map<String,String> queryMap);

    @GET
    Call<WorkflowDTO> executeGet(@Url String url,  @Body String jsonString, @QueryMap Map<String,String> queryMap);

    @POST
    Call<WorkflowDTO> executePost(@Url String url);

    @POST
    Call<WorkflowDTO> executePost(@Url String url, @Body String jsonString);

    @POST
    Call<WorkflowDTO> executePost(@Url String url, @QueryMap Map<String,String> queryMap);

    @POST
    Call<WorkflowDTO> executePost(@Url String url, @Body String jsonString, @QueryMap Map<String,String> queryMap);
    

    @PUT
    Call<WorkflowDTO> executePut(@Url String url);

    @PUT
    Call<WorkflowDTO> executePut(@Url String url, @Body String jsonString);

    @PUT
    Call<WorkflowDTO> executePut(@Url String url, @QueryMap Map<String,String> queryMap);

    @PUT
    Call<WorkflowDTO> executePut(@Url String url, @Body String jsonString, @QueryMap Map<String,String> queryMap);

    @DELETE
    Call<WorkflowDTO> executeDelete(@Url String url);

    @DELETE
    Call<WorkflowDTO> executeDelete(@Url String url, @Body String jsonString);

    @DELETE
    Call<WorkflowDTO> executeDelete(@Url String url, @QueryMap Map<String,String> queryMap);

    @DELETE
    Call<WorkflowDTO> executeDelete(@Url String url, @Body String jsonString, @QueryMap Map<String,String> queryMap);
}
