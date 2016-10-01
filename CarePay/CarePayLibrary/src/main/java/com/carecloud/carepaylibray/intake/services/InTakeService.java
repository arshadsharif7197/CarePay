package com.carecloud.carepaylibray.intake.services;

import com.carecloud.carepaylibray.intake.models.IntakeResponseModel;

import retrofit2.Call;
import retrofit2.http.POST;

/**
 * Created by arpit_jain1 on 9/29/2016.
 */

public interface IntakeService {
    @POST(value = "dev/workflow/carepay/patient_checkin/intake_forms/confirm")
    Call<IntakeResponseModel> confirmInTakeInformation();
}
