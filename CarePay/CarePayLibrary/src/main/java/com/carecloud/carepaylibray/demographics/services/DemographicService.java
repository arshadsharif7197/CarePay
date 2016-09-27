package com.carecloud.carepaylibray.demographics.services;

import com.carecloud.carepaylibray.demographics.models.DemographicModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadModel;
import com.carecloud.carepaylibray.googleapis.Models.GoogleAddressModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Jahirul Bhuiyan on 9/20/2016.
 */
public interface DemographicService {
    @GET(value = "dev/workflow/carepay/patient_checkin/demographics/information")
    Call<DemographicModel> fetchDemographicInformation( );
    @POST(value = "dev/workflow/carepay/patient_checkin/demographics/confirm")
    Call<DemographicModel> confirmDemographicInformation(@Body DemographicPayloadModel demographicModel);


}
