package com.carecloud.carepaylibray.demographics.services;

import com.carecloud.carepaylibray.demographics.models.DemographicModel;
import com.carecloud.carepaylibray.demographics.models.DemographicPayloadModel;
import com.google.gson.JsonObject;

import okhttp3.ResponseBody;
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

    @GET(value = "dev/workflow/carepay/patient_checkin/demographics")
    Call<DemographicModel> fetchDemographics( );


    @POST(value = "dev/workflow/carepay/patient_checkin/demographics/confirm")
    Call<ResponseBody> confirmDemographicInformation(
            @Body DemographicPayloadModel demographicModel);

    @POST(value = "dev/workflow/carepay/patient_checkin/demographics")
    Call<ResponseBody> updateDemographicInformation(
            @Body DemographicPayloadModel demographicModel);
}