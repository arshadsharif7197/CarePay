package com.carecloud.carepay.patient.consentforms.services;



import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ConsentFormService {
    @GET(value = "dev/workflow/carepay/patient_checkin/consent_forms/information")
    Call<ConsentFormDTO> fetchConnsentFormInformation();


}