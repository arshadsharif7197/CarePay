package com.carecloud.carepaylibray.demographics.services;

import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;

import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicsInsurancesListDTO;

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
    Call<DemographicDTO> fetchDemographicInformation( );

    @GET(value = "dev/workflow/carepay/patient_checkin/demographics")
    Call<DemographicDTO> fetchDemographics( );

    @GET(value = "dev/workflow/carepay/patient_checkin/demographics_verify")
    Call<DemographicDTO> fetchDemographicsVerify( );


    @POST(value = "dev/workflow/carepay/patient_checkin/demographics/confirm")
    Call<ResponseBody> confirmDemographicInformation(
            @Body DemographicPayloadDTO demographicModel);

    @POST(value = "dev/workflow/carepay/patient_checkin/demographics/update_demographics")
    Call<ResponseBody> updateDemographicInformation(
            @Body DemographicPayloadDTO demographicModel);

    @POST(value = "dev/workflow/carepay/patient_checkin/demographics/update_demographics")
    Call<ResponseBody> updateDemographicInsurances(
            @Body DemographicsInsurancesListDTO demInsuranceList);
}