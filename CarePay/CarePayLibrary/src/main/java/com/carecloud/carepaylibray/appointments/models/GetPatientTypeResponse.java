package com.carecloud.carepaylibray.appointments.models;

import com.carecloud.carepaylibray.intake.models.AppointmentPayloadModel;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetPatientTypeResponse {


        @SerializedName("patient_type")
        @Expose
        private PatientType patientType;
        @SerializedName("last_appointment")
        @Expose
        private AppointmentPayloadModel lastAppointment;

        public PatientType getPatientType() {
            return patientType;
        }

        public void setPatientType(PatientType patientType) {
            this.patientType = patientType;
        }

        public AppointmentPayloadModel getLastAppointment() {
            return lastAppointment;
        }

        public void setLastAppointment(AppointmentPayloadModel lastAppointment) {
            this.lastAppointment = lastAppointment;
        }




}
