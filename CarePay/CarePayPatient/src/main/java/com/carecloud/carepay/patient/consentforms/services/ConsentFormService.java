package com.carecloud.carepay.patient.consentforms.services;



import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface ConsentFormService {
    @GET(value = "dev/workflow/shamrock/patient_app/consent_forms/information")
    Call<ConsentFormDTO> fetchConnsentFormInformation();

    @GET(value = "dev/workflow/shamrock/patient_app/consent_forms/information")
    Call<ConsentFormDTO> fetchConnsentFormInformation(@QueryMap Map<String, String> queries);

}