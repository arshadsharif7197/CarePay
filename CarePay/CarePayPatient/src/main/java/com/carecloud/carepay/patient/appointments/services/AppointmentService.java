package com.carecloud.carepay.patient.appointments.services;

import com.carecloud.carepaylibray.appointments.models.AppointmentsResultModel;
import com.carecloud.carepaylibray.appointments.models.ScanQRCodeDTO;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface AppointmentService {

    @GET(value = "dev/workflow/shamrock/patient_app/appointments/information")
    Call<AppointmentsResultModel> getAppointmentsInformation();

    @GET(value = "dev/workflow/shamrock/patient_app/appointments")
    Call<AppointmentsResultModel> getAppointmentsList();

    @POST(value = "dev/workflow/shamrock/patient_app/appointments/checkin")
    Call<JsonObject> confirmAppointment(@Body String appointmentsModel);

    @GET(value = "dev/workflow/shamrock/patient_app/appointments/practice")
    Call<AppointmentsResultModel> getCheckedInAppointments();

    @GET(value = "dev/workflow/shamrock/patient_app/appointments/providers_schedule")
    Call<AppointmentsResultModel> getProvidersList();

    @POST
    Call<ScanQRCodeDTO> getQRCode(@Url String url);
}
