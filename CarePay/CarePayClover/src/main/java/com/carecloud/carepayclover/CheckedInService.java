package com.carecloud.carepayclover;

import com.carecloud.carepayclover.models.AppointmentsCheckedInModel;
import com.carecloud.carepaylibray.demographics.models.DemographicModel;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Jahirul Bhuiyan on 9/28/2016.
 */

public interface CheckedInService {
    @GET(value = "dev/workflow/carepay/patient_checkin/appointments/practice")
    Call<AppointmentsCheckedInModel> fetchCheckedInAppointments();
}
