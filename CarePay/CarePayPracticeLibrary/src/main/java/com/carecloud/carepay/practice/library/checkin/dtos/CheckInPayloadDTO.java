package com.carecloud.carepay.practice.library.checkin.dtos;

import com.carecloud.carepay.practice.library.signin.dtos.PracticeSelectionUserPractice;
import com.carecloud.carepaylibray.appointments.models.AppointmentDTO;
import com.carecloud.carepaylibray.appointments.models.LocationDTO;
import com.carecloud.carepaylibray.appointments.models.ProviderDTO;
import com.carecloud.carepaylibray.base.models.UserAuthModel;
import com.carecloud.carepaylibray.payments.models.LocationIndexDTO;
import com.carecloud.carepaylibray.payments.models.PatientBalanceDTO;
import com.carecloud.carepaylibray.payments.models.ProviderIndexDTO;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jahirul Bhuiyan on 10/27/2016
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
    @SerializedName("providers")
    @Expose
    private List<ProviderDTO> providers = new ArrayList<>();
    @SerializedName("locations")
    @Expose
    private List<LocationDTO> locations = new ArrayList<>();
    @SerializedName("page_messages")
    @Expose
    private List<String> pageMessages = new ArrayList<>();
    @SerializedName("auth")
    @Expose
    private UserAuthModel userAuthModel = new UserAuthModel();
    @SerializedName("user_practices")
    @Expose
    private List<PracticeSelectionUserPractice> userPracticesList = new ArrayList<>();

    /**
     * @return The patientBalances
     */
    public List<PatientBalanceDTO> getPatientBalances() {
        return patientBalances;
    }

    /**
     * @param patientBalances The patient_balances
     */
    public void setPatientBalances(List<PatientBalanceDTO> patientBalances) {
        this.patientBalances = patientBalances;
    }

    /**
     * @return The appointments
     */
    public List<AppointmentDTO> getAppointments() {
        return appointments;
    }

    /**
     * @param appointments The appointments
     */
    public void setAppointments(List<AppointmentDTO> appointments) {
        this.appointments = appointments;
    }

    /**
     * @return The providerIndex
     */
    public List<ProviderIndexDTO> getProviderIndex() {
        return providerIndex;
    }

    /**
     * @param providerIndex The provider_index
     */
    public void setProviderIndex(List<ProviderIndexDTO> providerIndex) {
        this.providerIndex = providerIndex;
    }

    /**
     * @return The locationIndex
     */
    public List<LocationIndexDTO> getLocationIndex() {
        return locationIndex;
    }

    /**
     * @param locationIndex The location_index
     */
    public void setLocationIndex(List<LocationIndexDTO> locationIndex) {
        this.locationIndex = locationIndex;
    }

    /**
     * @return The appointmentCounts
     */
    public AppointmentCountsDTO getAppointmentCounts() {
        return appointmentCounts;
    }

    /**
     * @param appointmentCounts The appointment_counts
     */
    public void setAppointmentCounts(AppointmentCountsDTO appointmentCounts) {
        this.appointmentCounts = appointmentCounts;
    }

    public List<ProviderDTO> getProviders() {
        return providers;
    }

    public void setProviders(List<ProviderDTO> providers) {
        this.providers = providers;
    }

    public List<LocationDTO> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationDTO> locations) {
        this.locations = locations;
    }

    public List<String> getPageMessages() {
        return pageMessages;
    }

    public void setPageMessages(List<String> pageMessages) {
        this.pageMessages = pageMessages;
    }

    public UserAuthModel getUserAuthModel() {
        return userAuthModel;
    }

    public void setUserAuthModel(UserAuthModel userAuthModel) {
        this.userAuthModel = userAuthModel;
    }

    public List<PracticeSelectionUserPractice> getUserPracticesList() {
        return userPracticesList;
    }

    public void setUserPracticesList(List<PracticeSelectionUserPractice> userPracticesList) {
        this.userPracticesList = userPracticesList;
    }
}
