package com.carecloud.carepaylibray.payment.services;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Kavin Kannan on 10/01/2016.
 */

public interface PaymentsService {
    @POST(value = "dev/workflow/carepay/patient_checkin/payments")
    Call<Object> updateCarePayPayment(@Body JSONObject carePayPaymentsModel);
}
