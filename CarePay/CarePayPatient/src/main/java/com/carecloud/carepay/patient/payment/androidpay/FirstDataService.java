package com.carecloud.carepay.patient.payment.androidpay;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by kkannan on 5/19/16.
 */
public interface FirstDataService {

    @POST(value = "/v1/transactions")
    Call<String> postPayment(
            @Body String payment
    );

    @POST("v1/transactions")
    Call<ResponseBody> performRequest(@Header("apikey") String apikey,
                                      @Header("Authorization") String authorization,
                                      @Header("nonce") String nonce,
                                      @Header("timestamp") String timestamp,
                                      @Header("token") String token,
                                      @Body RequestBody transaction);



}
