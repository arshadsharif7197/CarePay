package com.carecloud.carepay.service.library;

import com.google.gson.JsonElement;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;


public interface RestCallService {

    @GET
    Call<JsonElement> executeGet(@Url String url);

    @GET
    Call<JsonElement> executeGet(@Url String url, @Body String jsonString);

    @GET
    Call<JsonElement> executeGet(@Url String url, @Body RequestBody requestBody);

    @GET
    Call<JsonElement> executeGet(@Url String url, @QueryMap Map<String, String> queryMap);

    @GET
    Call<JsonElement> executeGet(@Url String url, @Body String jsonString, @QueryMap Map<String, String> queryMap);

    @GET
    Call<JsonElement> executeGet(@Url String url, @Body RequestBody requestBody, @QueryMap Map<String, String> queryMap);

    @GET
    @FormUrlEncoded
    Call<JsonElement> executeGet(@Url String url, @FieldMap Map<String, String> fieldMap, @QueryMap Map<String, String> queryMap);


    @POST
    Call<JsonElement> executePost(@Url String url);

    @POST
    Call<JsonElement> executePost(@Url String url, @Body String jsonString);

    @POST
    Call<JsonElement> executePost(@Url String url, @Body RequestBody requestBody);

    @POST
    Call<JsonElement> executePost(@Url String url, @QueryMap Map<String, String> queryMap);

    @POST
    Call<JsonElement> executePost(@Url String url, @Body String jsonString, @QueryMap Map<String, String> queryMap);

    @POST
    Call<JsonElement> executePost(@Url String url, @Body RequestBody requestBody, @QueryMap Map<String, String> queryMap);

    @POST
    @FormUrlEncoded
    Call<JsonElement> executePost(@Url String url, @FieldMap Map<String, String> fieldMap, @QueryMap Map<String, String> queryMap);



    @PUT
    Call<JsonElement> executePut(@Url String url);

    @PUT
    Call<JsonElement> executePut(@Url String url, @Body String jsonString);

    @PUT
    Call<JsonElement> executePut(@Url String url, @Body RequestBody requestBody);

    @PUT
    Call<JsonElement> executePut(@Url String url, @QueryMap Map<String, String> queryMap);

    @PUT
    Call<JsonElement> executePut(@Url String url, @Body String jsonString, @QueryMap Map<String, String> queryMap);

    @PUT
    Call<JsonElement> executePut(@Url String url, @Body RequestBody requestBody, @QueryMap Map<String, String> queryMap);

    @PUT
    @FormUrlEncoded
    Call<JsonElement> executePut(@Url String url, @FieldMap Map<String, String> fieldMap, @QueryMap Map<String, String> queryMap);



    @DELETE
    Call<JsonElement> executeDelete(@Url String url);

    @DELETE
    Call<JsonElement> executeDelete(@Url String url, @Body String jsonString);

    @DELETE
    Call<JsonElement> executeDelete(@Url String url, @Body RequestBody requestBody);

    @DELETE
    Call<JsonElement> executeDelete(@Url String url, @QueryMap Map<String, String> queryMap);

    @DELETE
    Call<JsonElement> executeDelete(@Url String url, @Body String jsonString, @QueryMap Map<String, String> queryMap);

    @DELETE
    Call<JsonElement> executeDelete(@Url String url, @Body RequestBody requestBody, @QueryMap Map<String, String> queryMap);

    @DELETE
    @FormUrlEncoded
    Call<JsonElement> executeDelete(@Url String url, @FieldMap Map<String, String> fieldMap, @QueryMap Map<String, String> queryMap);

}
