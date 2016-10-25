package com.carecloud.carepaylibray.consentforms.services;



import com.carecloud.carepaylibray.consentforms.models.ConsentFormDTO;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ConsentFormService {
    @GET(value = "dev/workflow/carepay/patient_checkin/consent_forms/information")
    Call<ConsentFormDTO> fetchConnsentFormInformation();


}