package com.carecloud.carepay.service.library;

import android.support.annotation.Nullable;

import com.carecloud.carepay.service.library.dtos.WorkflowDTO;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

/**
 * Created by Jahirul Bhuiyan on 10/24/2016.
 * Workflow service interface that handle all possible HTTP request using Retrofit
 */

interface WorkflowService {

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

}
