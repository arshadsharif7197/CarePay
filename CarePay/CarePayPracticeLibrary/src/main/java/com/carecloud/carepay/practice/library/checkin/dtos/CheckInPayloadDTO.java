package com.carecloud.carepay.practice.library.checkin.dtos;

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
    private List<PatientBalanceDTO> patientBalances = new ArrayList<PatientBalanceDTO>();
    @SerializedName("appointments")
    @Expose
    private List<AppointmentDTO> appointments = new ArrayList<AppointmentDTO>();
    @SerializedName("provider_index")
    @Expose
    private List<ProviderIndexDTO> providerIndex;
    @SerializedName("location_index")
    @Expose
    private List<LocationIndexDTO> locationIndex;
    @SerializedName("appointment_counts")
    @Expose
    private AppointmentCountsDTO appointmentCounts;

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

    /**
     * Gives total balance.
     * @return total balance
     */
    public double getTotalBalance() {
        double total = 0.00;
        if (patientBalances != null && patientBalances.size() > 0) {
            for (PatientBalanceDTO patientBalanceDTO : patientBalances) {
                if (patientBalanceDTO.getPayload().get(0).getBalanceType().equalsIgnoreCase("Account")) {
                    total = total + patientBalanceDTO.getPayload().get(0).getTotal();
                } else if (patientBalanceDTO.getPayload().get(0).getBalanceType().equalsIgnoreCase("Copay")) {
                    total = total + patientBalanceDTO.getPayload().get(0).getTotal();
                }
            }
        }

        // using for demo. will remove this after the demo.
        if (total == 0.00) {
            total = 20.00;
        }
        return total;
    }

    /**
     * Gives responsibility account.
     * @return responsibility account
     *//*
    public double getResponsibilityAccount() {
        double total;
        if (patientBalances != null && patientBalances.size() > 0) {
            for (PatientResponsibilityDTO patientResponsibilityDto : patientBalances) {
                if (patientResponsibilityDto.getBalanceType().equalsIgnoreCase("Account")) {
                    return patientResponsibilityDto.getTotal();
                }
            }
        }

        // using for demo. will remove this after the demo.
        total = 20.00;
        return total;
    }

    *//**
     * Gives responsibility copay.
     * @return responsibility copay
     *//*
    public double getResponsibilityCopay() {
        double total = 0.00;
        if (patientBalances != null && patientBalances.size() > 0) {
            for (PatientResponsibilityDTO patientResponsibilityDto : patientBalances) {
                if (patientResponsibilityDto.getBalanceType().equalsIgnoreCase("Copay")) {
                    return patientResponsibilityDto.getTotal();
                }
            }
        }
        return total;
    }*/
}
