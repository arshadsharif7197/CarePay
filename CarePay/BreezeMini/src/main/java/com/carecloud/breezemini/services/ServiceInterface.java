package com.carecloud.breezemini.services;



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
 * Created by Kavin Kannan on 10/24/2016.
 * Workflow service interface that handle all possible HTTP request using Retrofit
 */

public interface ServiceInterface {

    @GET
    Call<ServiceResponseDTO> executeGet(@Url String url);

    @GET
    Call<ServiceResponseDTO> executeGet(@Url String url, @Body String jsonString);

    @GET
    Call<ServiceResponseDTO> executeGet(@Url String url, @QueryMap Map<String, String> queryMap);

    @GET
    Call<ServiceResponseDTO> executeGet(@Url String url, @Body String jsonString, @QueryMap Map<String, String> queryMap);

    @POST
    Call<ServiceResponseDTO> executePost(@Url String url);

    @POST
    Call<ServiceResponseDTO> executePost(@Url String url, @Body String jsonString);

    @POST
    Call<ServiceResponseDTO> executePost(@Url String url, @QueryMap Map<String, String> queryMap);

    @POST
    Call<ServiceResponseDTO> executePost(@Url String url, @Body String jsonString, @QueryMap Map<String, String> queryMap);
    

    @PUT
    Call<ServiceResponseDTO> executePut(@Url String url);

    @PUT
    Call<ServiceResponseDTO> executePut(@Url String url, @Body String jsonString);

    @PUT
    Call<ServiceResponseDTO> executePut(@Url String url, @QueryMap Map<String, String> queryMap);

    @PUT
    Call<ServiceResponseDTO> executePut(@Url String url, @Body String jsonString, @QueryMap Map<String, String> queryMap);

    @DELETE
    Call<ServiceResponseDTO> executeDelete(@Url String url);

    @DELETE
    Call<ServiceResponseDTO> executeDelete(@Url String url, @Body String jsonString);

    @DELETE
    Call<ServiceResponseDTO> executeDelete(@Url String url, @QueryMap Map<String, String> queryMap);

    @DELETE
    Call<ServiceResponseDTO> executeDelete(@Url String url, @Body String jsonString, @QueryMap Map<String, String> queryMap);
}
