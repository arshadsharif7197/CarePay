package com.carecloud.carepaylibray.appointments.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lmenendez on 8/21/17
 */

public class ScheduleAppointmentRequestDTO {

    @SerializedName("appointment")
    private Appointment appointment = new Appointment();

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }


    public class Appointment {
        @SerializedName("start_time")
        private String startTime;

        @SerializedName("end_time")
        private String endTime;

        @SerializedName("location_id")
        private int locationId;

        private transient String locationGuid;

        @SerializedName("provider_id")
        private int providerId;

        private transient String providerGuid;

        @SerializedName("visit_reason_id")
        private int visitReasonId;

        @SerializedName("resource_id")
        private int resourceId;

        @SerializedName("chief_complaint")
        private String complaint;

        @SerializedName("comments")
        private String comments;

        @SerializedName("patient")
        private Patient patient = new Patient();

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public int getLocationId() {
            return locationId;
        }

        public void setLocationId(int locationId) {
            this.locationId = locationId;
        }

        public int getProviderId() {
            return providerId;
        }

        public void setProviderId(int providerId) {
            this.providerId = providerId;
        }

        public int getVisitReasonId() {
            return visitReasonId;
        }

        public void setVisitReasonId(int visitReasonId) {
            this.visitReasonId = visitReasonId;
        }

        public int getResourceId() {
            return resourceId;
        }

        public void setResourceId(int resourceId) {
            this.resourceId = resourceId;
        }

        public String getComplaint() {
            return complaint;
        }

        public void setComplaint(String complaint) {
            this.complaint = complaint;
        }

        public String getComments() {
            return comments;
        }

        public void setComments(String comments) {
            this.comments = comments;
        }

        public Patient getPatient() {
            return patient;
        }

        public void setPatient(Patient patient) {
            this.patient = patient;
        }

        public String getLocationGuid() {
            return locationGuid;
        }

        public void setLocationGuid(String locationGuid) {
            this.locationGuid = locationGuid;
        }

        public String getProviderGuid() {
            return providerGuid;
        }

        public void setProviderGuid(String providerGuid) {
            this.providerGuid = providerGuid;
        }


        public class Patient {
            @SerializedName("id")
            private String id;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }
        }
    }

}
