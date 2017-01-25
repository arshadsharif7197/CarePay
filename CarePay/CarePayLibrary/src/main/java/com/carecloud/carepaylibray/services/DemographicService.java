package com.carecloud.carepaylibray.services;

import com.carecloud.carepaylibray.demographics.dtos.DemographicDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicPayloadDTO;
import com.carecloud.carepaylibray.demographics.dtos.payload.DemographicsInsurancesListDTO;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by Jahirul Bhuiyan on 9/20/2016.
 */
public interface DemographicService {
    @GET(value = "dev/workflow/shamrock/patient_app/demographics/information")
    Call<DemographicDTO> fetchDemographicInformation( );

    @GET(value = "dev/workflow/shamrock/patient_app/demographics")
    Call<DemographicDTO> fetchDemographics( );

    @GET(value = "dev/workflow/shamrock/patient_app/demographics_verify")
    Call<DemographicDTO> fetchDemographicsVerify( );

    @GET(value = "dev/workflow/shamrock/patient_app/demographics_verify")
    Call<DemographicDTO> fetchDemographicsVerify(@QueryMap Map<String, String> queries);


    @POST(value = "dev/workflow/shamrock/patient_app/demographics/confirm")
    Call<ResponseBody> confirmDemographicInformation(
            @Body DemographicPayloadDTO demographicModel);

    @POST(value = "dev/workflow/shamrock/patient_app/demographics/update_demographics")
    Call<ResponseBody> updateDemographicInformation(
            @Body DemographicPayloadDTO demographicModel);

    @POST(value = "dev/workflow/shamrock/patient_app/demographics/update_demographics")
    Call<ResponseBody> updateDemographicInsurances(
            @Body DemographicsInsurancesListDTO demInsuranceList);

}