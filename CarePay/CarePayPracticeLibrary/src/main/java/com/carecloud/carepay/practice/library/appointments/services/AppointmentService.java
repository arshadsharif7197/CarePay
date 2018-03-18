package com.carecloud.carepay.practice.library.appointments.services;


import retrofit2.Call;
import retrofit2.http.GET;

public interface AppointmentService {

    @GET(value = "dev/workflow/carepay/patient_mode/appointments")
    Call<com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel> fetchAppointmentInformation();
}
