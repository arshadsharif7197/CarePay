package com.carecloud.carepaylibray.payments.services;


import com.carecloud.carepaylibray.payments.models.PaymentsModel;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Kavin Kannan on 10/01/2016.
 */

public interface PaymentsService {
    @POST(value = "dev/workflow/shamrock/patient_app/payments")
    Call<Object> updateCarePayPayment(@Body JSONObject carePayPaymentsModel);

    @GET(value = "dev/workflow/shamrock/patient_app/payments/information")
    Call<PaymentsModel> fetchPaymentInformation( );

}
