package com.carecloud.carepaylibray.appointments.services;

import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface AppointmentService {

    @GET(value = "dev/workflow/carepay/patient_checkin/appointments")
    Call<AppointmentsResultModel> fetchAppointmentInformation();

    @POST(value = "dev/workflow/carepay/patient_checkin/appointments/checkin")
    Call<JsonObject> confirmAppointment(@Body String appointmentsModel);

    @GET(value = "dev/workflow/carepay/patient_checkin/appointments/practice")
    Call<AppointmentsResultModel> fetchCheckedInAppointments();

    @GET(value = "dev/workflow/carepay/patient_checkin/appointments/providers_schedule")
    Call<AppointmentsResultModel> fetchProvidersInformation();
}
