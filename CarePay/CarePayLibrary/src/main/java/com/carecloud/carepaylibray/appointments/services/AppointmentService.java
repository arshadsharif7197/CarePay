package com.carecloud.carepaylibray.appointments.services;

import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.AppointmentModel;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AppointmentService {

    @GET(value = "dev/workflow/carepay/patient_checkin/appointments")
    Call<AppointmentsResultModel> fetchAppointmentInformation();

    @POST(value = "dev/workflow/carepay/patient_checkin/appointments/checkin")
    Call<JsonObject> confirmAppointment(@Body String appointmentsModel);

    @GET(value = "dev/workflow/carepay/patient_checkin/appointments/practice")
    Call<AppointmentsResultModel> fetchCheckedInAppointments();
}
