package com.carecloud.carepay.practice.library.checkin.dtos;

import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016.
 */

public class CheckInPayloadDTO {
    @SerializedName("patient_balances")
    @Expose
    private List<PatientBalanceDTO> patientBalances = new ArrayList<>();
    @SerializedName("appointments")
    @Expose
    private List<AppointmentDTO> appointments = new ArrayList<>();
    @SerializedName("provider_index")
    @Expose
    private List<ProviderIndexDTO> providerIndex = new ArrayList<>();
    @SerializedName("location_index")
    @Expose
    private List<LocationIndexDTO> locationIndex = new ArrayList<>();
    @SerializedName("appointment_counts")
    @Expose
    private AppointmentCountsDTO appointmentCounts = new AppointmentCountsDTO();

    /**
     *
     * @return
     * The patientBalances
     */
    public List<PatientBalanceDTO> getPatientBalances() {
        return patientBalances;
    }

    /**
     *
     * @param patientBalances
     * The patient_balances
     */
    public void setPatientBalances(List<PatientBalanceDTO> patientBalances) {
        this.patientBalances = patientBalances;
    }

    /**
     *
     * @return
     * The appointments
     */
    public List<AppointmentDTO> getAppointments() {
        return appointments;
    }

    /**
     *
     * @param appointments
     * The appointments
     */
    public void setAppointments(List<AppointmentDTO> appointments) {
        this.appointments = appointments;
    }

    /**
     *
     * @return
     * The providerIndex
     */
    public List<ProviderIndexDTO> getProviderIndex() {
        return providerIndex;
    }

    /**
     *
     * @param providerIndex
     * The provider_index
     */
    public void setProviderIndex(List<ProviderIndexDTO> providerIndex) {
        this.providerIndex = providerIndex;
    }

    /**
     *
     * @return
     * The locationIndex
     */
    public List<LocationIndexDTO> getLocationIndex() {
        return locationIndex;
    }

    /**
     *
     * @param locationIndex
     * The location_index
     */
    public void setLocationIndex(List<LocationIndexDTO> locationIndex) {
        this.locationIndex = locationIndex;
    }

    /**
     *
     * @return
     * The appointmentCounts
     */
    public AppointmentCountsDTO getAppointmentCounts() {
        return appointmentCounts;
    }

    /**
     *
     * @param appointmentCounts
     * The appointment_counts
     */
    public void setAppointmentCounts(AppointmentCountsDTO appointmentCounts) {
        this.appointmentCounts = appointmentCounts;
    }
}
