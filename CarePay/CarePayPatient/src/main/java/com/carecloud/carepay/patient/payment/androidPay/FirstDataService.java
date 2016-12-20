package com.carecloud.carepay.patient.payment.androidPay;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by kkannan on 5/19/16.
 */
public interface FirstDataService {

    @POST(value = "/v1/transactions")
    Call<String> postPayment(
            @Body String payment
    );



}
