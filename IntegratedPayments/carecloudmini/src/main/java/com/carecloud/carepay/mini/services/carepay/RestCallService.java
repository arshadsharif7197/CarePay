package com.carecloud.carepay.mini.services.carepay;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface RestCallService {

    @POST("carepay/sessions")
    Call<JsonElement> getSignin(@Body String jsonString);


    @GET("carepay/merchants/{mid}/devices/pre_registration")
    Call<JsonElement> getPreRegistration(@Path(value = "mid") String mid);

    @POST("carepay/payments")
    Call<JsonElement> postPaymentRequest(@Body String jsonString);

}
