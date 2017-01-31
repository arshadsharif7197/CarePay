package com.carecloud.carepay.patient.intakeforms.services;

import com.carecloud.carepaylibray.intake.models.IntakeResponseModel;

import retrofit2.Call;
import retrofit2.http.POST;

/**
 * Created by arpit_jain1 on 9/29/2016.
 */

public interface InTakeService {
    @POST(value = "dev/workflow/shamrock/patient_app/intake_forms/confirm")
    Call<IntakeResponseModel> confirmInTakeInformation();
}