package com.carecloud.carepay.mini.services.carepay;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface RestCallService {

    @POST("/carepay/sessions")
    Call<JsonElement> getSignin(@Body String jsonString);



}
